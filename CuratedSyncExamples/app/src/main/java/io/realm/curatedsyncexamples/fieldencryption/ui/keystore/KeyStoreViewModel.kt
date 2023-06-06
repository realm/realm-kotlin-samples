package io.realm.curatedsyncexamples.fieldencryption.ui.keystore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.curatedsyncexamples.fieldencryption.fieldEncryptionCipherSpec
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