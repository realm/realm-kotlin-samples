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
package io.realm.appservicesusagesamples.propertyencryption.ui.records

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.appservicesusagesamples.propertyencryption.ext.getPropertyEncryptionCipherSpec
import io.realm.appservicesusagesamples.propertyencryption.models.PLECipherSpec
import io.realm.appservicesusagesamples.propertyencryption.models.PLEKey
import io.realm.appservicesusagesamples.propertyencryption.models.SecretRecord
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.KeyStore

/**
 * Holds the UI state for the secret records screen.
 */
data class SecretRecordsUiState(
    val loading: Boolean = true,
    val loggingOut: Boolean = false,
    val loggedOut: Boolean = false
)

/**
 * View model for the secret records screen.
 */
class SecretRecordsViewModel(
    private val app: App,
    private val keyAlias: String,
    private val localKeyStore: KeyStore,
) : ViewModel() {
    private lateinit var realm: Realm
    private lateinit var user: User

    private val _uiState = MutableStateFlow(SecretRecordsUiState())
    val uiState: StateFlow<SecretRecordsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            app.currentUser?.let { user ->
                this@SecretRecordsViewModel.user = user

                // Import key and cipher spec required to access the secret contents
                PLECipherSpec = user.getPropertyEncryptionCipherSpec()
                PLEKey = localKeyStore.getKey(keyAlias, null)

                val syncConfig = SyncConfiguration
                    .Builder(app.currentUser!!, setOf(SecretRecord::class))
                    .initialSubscriptions {
                        // Subscribe to all secret records
                        add(it.query<SecretRecord>())
                    }
                    .build()

                realm = Realm.open(syncConfig)

                val job = async {
                    realm.query<SecretRecord>()
                        .sort("_id", Sort.DESCENDING)
                        .asFlow()
                        .collect {
                            records.postValue(it.list)
                        }
                }

                addCloseable {
                    job.cancel()
                    realm.close()
                }

                _uiState.update {
                    it.copy(loading = false)
                }
            }
        }
    }

    val records: MutableLiveData<List<SecretRecord>> by lazy {
        MutableLiveData<List<SecretRecord>>()
    }

    fun logout() {
        _uiState.update {
            it.copy(loggingOut = true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            PLECipherSpec = null
            PLEKey = null
            app.currentUser?.logOut()

            localKeyStore.deleteEntry(keyAlias)

            _uiState.update {
                it.copy(loggedOut = true)
            }
        }
    }

    fun addRecord(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                copyToRealm(
                    SecretRecord().apply {
                        this.ownerId = user.id
                        this.content = content
                    }
                )
            }
        }
    }
}
