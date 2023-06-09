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
import io.realm.curatedsyncexamples.fieldencryption.models.getFieldLevelEncryptionKey
import io.realm.curatedsyncexamples.fieldencryption.models.key
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KeyStoreUiState(
    val isInitialized: Boolean = false,
    val isUnlocking: Boolean = false,
    val isUnlocked: Boolean = false,
    val errorMessage: String? = null,
)

class KeyStoreViewModel(
    val app: App? = null,
    private val keyAlias: String,
    uiState: KeyStoreUiState = KeyStoreUiState(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(uiState)
    val uiState: StateFlow<KeyStoreUiState> = _uiState.asStateFlow()

    init {

    }
    fun unlock(password: String) {
        _uiState.update {
            it.copy(isUnlocking = true, isUnlocked = false, errorMessage = null)
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                key = getFieldLevelEncryptionKey(keyAlias, app!!.currentUser!!, password)

                _uiState.update {
                    it.copy(isUnlocked = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isUnlocking = false, errorMessage = e.message)
                }
            }
        }
    }
}