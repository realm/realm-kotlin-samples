package io.realm.appservicesusagesamples.errorhandling.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.appservicesusagesamples.errorhandling.models.Entry
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.SyncException
import io.realm.kotlin.mongodb.sync.ConnectionState
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.SyncSession
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ErrorHandlingUIStatus(
    val loading: Boolean = true,
    val loggingOut: Boolean = false,
    val loggedOut: Boolean = false,
    val errorMessage: String? = null,
    val connectionState: ConnectionState = ConnectionState.CONNECTING,
)

class ErrorHandlingViewModel(
    private val app: App,
) : ViewModel() {
    private lateinit var realm: Realm
    private lateinit var user: User
    private val _uiState = MutableStateFlow(ErrorHandlingUIStatus())
    val uiState: StateFlow<ErrorHandlingUIStatus> = _uiState.asStateFlow()

    val entries: MutableLiveData<List<Entry>> by lazy {
        MutableLiveData<List<Entry>>()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            app.login(Credentials.anonymous())
                .let { user ->
                    this@ErrorHandlingViewModel.user = user

                    val syncConfig = SyncConfiguration
                        .Builder(app.currentUser!!, setOf(Entry::class))
                        .initialSubscriptions {
                            // Subscribe to all user statuses
                            add(it.query<Entry>())
                        }
                        .errorHandler{ session: SyncSession, exception: SyncException ->
                            _uiState.update {
                                it.copy(
                                    errorMessage = exception.message
                                )
                            }
                        }
                        .waitForInitialRemoteData()
                        .build()

                    realm = Realm.open(syncConfig)

                    val job = async {
                        realm.query<Entry>()
                            .sort("_id", Sort.DESCENDING)
                            .asFlow()
                            .collect {
                                entries.postValue(it.list)
                            }
                    }

                    val job2 = async {
                        realm.syncSession
                            .connectionStateAsFlow()
                            .collect {connectionStateChange ->
                                _uiState.update {
                                    it.copy(
                                        connectionState = connectionStateChange.newState
                                    )
                                }
                            }
                    }

                    addCloseable {
                        job.cancel()
                        job2.cancel()
                        realm.close()
                    }

                    _uiState.update {
                        it.copy(
                            loading = false,
                            connectionState = realm.syncSession.connectionState
                        )
                    }
                }
        }
    }

    fun addEntry() {
        viewModelScope.launch {
            realm.write {
                copyToRealm(Entry())
            }
        }
    }

    fun connect() {
        _uiState.update {
            it.copy(connectionState = ConnectionState.CONNECTING)
        }

        realm.syncSession.resume()
    }

    fun disconnect() {
        _uiState.update {
            it.copy(connectionState = ConnectionState.DISCONNECTED)
        }

        realm.syncSession.pause()
    }

    fun logout() {
        _uiState.update {
            it.copy(loggingOut = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            app.currentUser?.logOut()

            _uiState.update {
                it.copy(loggedOut = true)
            }
        }
    }
}