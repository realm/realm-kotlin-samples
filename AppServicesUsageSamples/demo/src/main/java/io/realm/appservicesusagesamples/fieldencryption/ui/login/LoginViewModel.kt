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
package io.realm.appservicesusagesamples.fieldencryption.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception

data class LoginUiState(
    val loggingIn: Boolean = false,
    val loggedIn: Boolean = false,
    val errorMessage: String? = null,
)

class LoginViewModel(
    private val app: App
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState(false))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, register: Boolean = false) {
        _uiState.update {
            it.copy(loggingIn = true, errorMessage = null)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (register) {
                    app.emailPasswordAuth.registerUser(email, password)
                }

                app.login(Credentials.emailPassword(email, password))
                _uiState.update {
                    it.copy(loggedIn = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loggingIn = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}
