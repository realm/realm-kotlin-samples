package io.realm.curatedsyncexamples.fieldencryption.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
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

    fun login(email: String, password: String) {
        _uiState.update {
            it.copy(loggingIn = true, errorMessage = null)
        }

        viewModelScope.launch {
            try {
                app.emailPasswordAuth.registerUser(email, password)
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