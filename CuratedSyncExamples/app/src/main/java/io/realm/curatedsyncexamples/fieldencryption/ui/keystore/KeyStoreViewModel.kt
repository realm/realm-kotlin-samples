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
package io.realm.curatedsyncexamples.fieldencryption.ui.keystore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.curatedsyncexamples.fieldencryption.ext.fieldEncryptionCipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.cipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.getFieldLevelEncryptionKey
import io.realm.curatedsyncexamples.fieldencryption.models.key
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KeyStoreUiState(
    val unlocking: Boolean = false,
    val unlocked: Boolean = false,
    val errorMessage: String? = null,
)

class KeyStoreViewModel(
    app: App,
    private val keyAlias: String
) : ViewModel() {
    private var user: User

    private val _uiState = MutableStateFlow(KeyStoreUiState())
    val uiState: StateFlow<KeyStoreUiState> = _uiState.asStateFlow()

    init {
        user = app.currentUser!!
        cipherSpec = user.fieldEncryptionCipherSpec()
    }

    fun unlock(password: String) {
        _uiState.update {
            it.copy(unlocking = true, unlocked = false, errorMessage = null)
        }
        viewModelScope.launch {
            try {
                key = getFieldLevelEncryptionKey(keyAlias, user, password)

                _uiState.update {
                    it.copy(unlocked = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(unlocking = false, errorMessage = e.message)
                }
            }
        }
    }
}