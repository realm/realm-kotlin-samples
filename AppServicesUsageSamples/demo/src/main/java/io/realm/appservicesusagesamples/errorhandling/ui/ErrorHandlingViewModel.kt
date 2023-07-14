/*
 * Copyright 2023 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import io.realm.kotlin.mongodb.exceptions.CompensatingWriteException
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

    val _uiState: UiStateFlow = MutableStateFlow(ErrorHandlingUIStatus())
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

            // Loads the client reset strategy selected by the user in the demo
            // selection screen
            val syncClientResetStrategy: SyncClientResetStrategy = when (clientResetAction) {
                ClientResetAction.RECOVER -> automaticUnsyncedDataRecovery
                ClientResetAction.DISCARD -> discardUnsyncedData
                ClientResetAction.MANUAL -> manualUnsyncedDataRecovery
                ClientResetAction.BACKUP -> backupRealm
            }

            // This is the error handler that would be used by the sync session.
            // It will filter and show any compensating write
            val errorHandler =
                SyncSession.ErrorHandler { _: SyncSession, exception: SyncException ->
                    val errorMessage = when (exception) {
                        is CompensatingWriteException -> "The server undid ${exception.writes.count()} change"
                        else -> exception.message
                    }

                    _uiState.update {
                        it.copy(
                            errorMessage = errorMessage
                        )
                    }
                }

            val syncConfig = SyncConfiguration
                .Builder(user, setOf(Entry::class))
                .initialSubscriptions {
                    add(it.query<Entry>())
                }
                .errorHandler(errorHandler)
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
                                connectionState = connectionStateChange.newState,
                                errorMessage = null,
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
                    connectionState = realm.syncSession.connectionState,
                    errorMessage = null,
                )
            }
        }
    }

    fun triggerClientReset() {
        _uiState.update {
            it.copy(
                loading = true,
                errorMessage = null,
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val user = app.currentUser!!

            // Tell the server force a reset for this client
            doClientReset(user)
            // Client reset would be triggered once connected
            connect()
        }
    }

    private suspend fun doClientReset(user: User) {
        val count = user.functions.call<Int>("triggerClientReset")
        Log.println(INFO, "CLIENT_RESET", "count: $count")
    }

    fun addEntry() {
        _uiState.update {
            it.copy(
                errorMessage = null,
            )
        }
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

    fun triggerCompensatingWriteError() {
        viewModelScope.launch {
            realm.write {
                copyToRealm(
                    Entry().apply {
                        ownerId = "invalid user id"
                    }
                )
            }
        }
    }

    fun connect() {
        _uiState.update {
            it.copy(
                errorMessage = null,
            )
        }
        realm.syncSession.resume()
    }

    fun disconnect() {
        _uiState.update {
            it.copy(connectionState = ConnectionState.DISCONNECTED, errorMessage = null)
        }

        realm.syncSession.pause()
    }
}
