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
package io.realm.appservicesusagesamples.presence.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.appservicesusagesamples.presence.models.UserStatus
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.ConnectionState
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserListUiStatus(
    val loading: Boolean = true,
    val loggingOut: Boolean = false,
    val loggedOut: Boolean = false,
    val userId: String = "",
    val connectionState: ConnectionState = ConnectionState.CONNECTING,
)

class UserStatusListViewModel(
    private val app: App,
) : ViewModel() {
    private lateinit var realm: Realm
    lateinit var user: User

    private val _uiState = MutableStateFlow(UserListUiStatus())
    val uiState: StateFlow<UserListUiStatus> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            app.login(Credentials.anonymous())
                .let { user ->
                    this@UserStatusListViewModel.user = user

                    val syncConfig = SyncConfiguration
                        .Builder(app.currentUser!!, setOf(UserStatus::class))
                        .initialSubscriptions {
                            // Subscribe to all user statuses
                            add(it.query<UserStatus>("owner_id != $0", user.id))
                        }
                        .waitForInitialRemoteData()
                        .build()

                    realm = Realm.open(syncConfig)

                    val job = async {
                        realm.query<UserStatus>()
                            .sort("present", Sort.DESCENDING)
                            .asFlow()
                            .collect {
                                userStatus.postValue(it.list)
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
                            userId = user.id,
                            loading = false,
                            connectionState = realm.syncSession.connectionState
                        )
                    }
                }
        }
    }

    val userStatus: MutableLiveData<List<UserStatus>> by lazy {
        MutableLiveData<List<UserStatus>>()
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
