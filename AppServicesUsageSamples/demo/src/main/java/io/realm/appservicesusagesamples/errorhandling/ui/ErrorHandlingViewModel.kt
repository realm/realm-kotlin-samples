package io.realm.appservicesusagesamples.errorhandling.ui

import android.util.Log
import android.util.Log.INFO
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.appservicesusagesamples.errorhandling.models.Entry
import io.realm.appservicesusagesamples.errorhandling.strategies.automaticUnsyncedDataRecovery
import io.realm.appservicesusagesamples.errorhandling.strategies.backupRealm
import io.realm.appservicesusagesamples.errorhandling.strategies.discardUnsyncedData
import io.realm.appservicesusagesamples.errorhandling.strategies.manualUnsyncedDataRecovery
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.ext.call
import io.realm.kotlin.mongodb.sync.ConnectionState
import io.realm.kotlin.mongodb.sync.SyncClientResetStrategy
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

enum class ClientResetAction {
    RECOVER, DISCARD, MANUAL, BACKUP
}

data class ErrorHandlingUIStatus(
    val loading: Boolean = true,
    val restart: Boolean = false,
    val errorMessage: String? = null,
    val connectionState: ConnectionState = ConnectionState.CONNECTING,
)

typealias UiStateFlow = MutableStateFlow<ErrorHandlingUIStatus>

class ErrorHandlingViewModel(
    private val app: App,
    private val clientResetAction: ClientResetAction,
) : ViewModel() {
    private lateinit var user: User
    private lateinit var realm: Realm

    private val _uiState: UiStateFlow = MutableStateFlow(ErrorHandlingUIStatus())
    val uiState: StateFlow<ErrorHandlingUIStatus> = _uiState.asStateFlow()

    val entries: MutableLiveData<List<Entry>> by lazy {
        MutableLiveData<List<Entry>>()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            openRealm()
        }
    }

    private suspend fun openRealm() {
        coroutineScope {
            user = app.login(Credentials.anonymous(reuseExisting = false))

            val syncClientResetStrategy: SyncClientResetStrategy = when (clientResetAction) {
                ClientResetAction.RECOVER -> automaticUnsyncedDataRecovery(_uiState)
                ClientResetAction.DISCARD -> discardUnsyncedData(_uiState)
                ClientResetAction.MANUAL -> manualUnsyncedDataRecovery(_uiState)
                ClientResetAction.BACKUP -> backupRealm(_uiState)
            }

            val syncConfig = SyncConfiguration
                .Builder(user, setOf(Entry::class))
                .initialSubscriptions {
                    add(it.query<Entry>())
                }
                .errorHandler { _: SyncSession, exception: SyncException ->
                    _uiState.update {
                        it.copy(
                            errorMessage = exception.message
                        )
                    }
                }
                .syncClientResetStrategy(syncClientResetStrategy)
                .waitForInitialRemoteData()
                .build()

            realm = Realm.open(syncConfig)

            val entriesQueryJob = async {
                realm.query<Entry>()
                    .sort("_id", Sort.DESCENDING)
                    .asFlow()
                    .collect {
                        entries.postValue(it.list)
                    }
            }

            val syncSessionStateJob = async {
                realm.syncSession
                    .connectionStateAsFlow()
                    .collect { connectionStateChange ->
                        _uiState.update {
                            it.copy(
                                connectionState = connectionStateChange.newState
                            )
                        }
                    }
            }

            addCloseable {
                entriesQueryJob.cancel()
                syncSessionStateJob.cancel()

                realm.close()

                runBlocking {
                    user.delete()
                }
            }

            _uiState.update {
                it.copy(
                    loading = false,
                    connectionState = realm.syncSession.connectionState
                )
            }
        }
    }

    fun triggerClientReset() {
        _uiState.update {
            it.copy(loading = true, errorMessage = null)
        }

        viewModelScope.launch(Dispatchers.IO) {
            val user = app.currentUser!!

            doClientReset(user)
            connect()
        }
    }

    private suspend fun doClientReset(user: User) {
        val count = user.functions.call<Int>("triggerClientReset")
        Log.println(INFO, "CLIENT_RESET", "count: $count")
    }

    fun addEntry() {
        viewModelScope.launch {
            realm.write {
                copyToRealm(
                    Entry().apply {
                        ownerId = user.id
                    }
                )
            }
        }
    }

    fun connect() {
        realm.syncSession.resume()
    }

    fun disconnect() {
        _uiState.update {
            it.copy(connectionState = ConnectionState.DISCONNECTED)
        }

        realm.syncSession.pause()
    }
}
