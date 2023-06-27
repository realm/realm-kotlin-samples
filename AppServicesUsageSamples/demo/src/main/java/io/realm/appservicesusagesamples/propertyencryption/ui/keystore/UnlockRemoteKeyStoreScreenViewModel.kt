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
package io.realm.appservicesusagesamples.propertyencryption.ui.keystore

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.appservicesusagesamples.propertyencryption.ext.getPropertyEncryptionCipherSpec
import io.realm.appservicesusagesamples.propertyencryption.ext.generateKey
import io.realm.appservicesusagesamples.propertyencryption.ext.getRemoteKeyStore
import io.realm.appservicesusagesamples.propertyencryption.ext.hasKeyStore
import io.realm.appservicesusagesamples.propertyencryption.ext.updateRemoteKeyStore
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStore.SecretKeyEntry
import javax.crypto.SecretKey

/**
 * UI state of the KeyStore screen.
 */
data class KeyStoreUiState(
    val isInitialized: Boolean = false,
    val isUnlocking: Boolean = false,
    val isUnlocked: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * View model for the [UnlockRemoteKeyStoreScreen].
 */
class KeyStoreViewModel(
    val app: App,
    private val keyAlias: String,
    private val localKeyStore: KeyStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        KeyStoreUiState(
            isInitialized = app.currentUser?.hasKeyStore() == true
        )
    )
    val uiState: StateFlow<KeyStoreUiState> = _uiState.asStateFlow()

    fun importRemoteKey(password: String) {
        _uiState.update {
            it.copy(isUnlocking = true, isUnlocked = false, errorMessage = null)
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                doImportRemoteKey(password)
            } catch (exception: IOException) {
                notifyError("Wrong password")
            }
        }
    }

    private fun notifyError(errorMessage: String) {
        _uiState.update {
            it.copy(
                isUnlocking = false,
                isUnlocked = false,
                errorMessage = errorMessage
            )
        }
    }

    /**
     * Imports a key from a remote keystore to a local
     */
    private suspend fun doImportRemoteKey(
        password: String
    ) {
        app.currentUser?.let { user ->
            val remoteKeyStore = user.getRemoteKeyStore(password)

            if (!remoteKeyStore.isKeyEntry(keyAlias)) {
                // key is missing, generate and store a new key
                generateAndStoreKey(user, remoteKeyStore, password)
            }
            // Now we can safely retrieve the key from the remote keystore
            val remoteKey = remoteKeyStore.getKey(keyAlias, null) as SecretKey
            val cipherSpec = user.getPropertyEncryptionCipherSpec()

            // now we can add it in the secure local keystore
            localKeyStore.setEntry(
                /* alias = */ keyAlias,
                /* entry = */ SecretKeyEntry(remoteKey),
                /* protParam = */ KeyProtection
                    .Builder(
                        /* purposes = */ KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                    .setBlockModes(cipherSpec.block)
                    .setEncryptionPaddings(cipherSpec.padding)
                    .build()
            )

            _uiState.update {
                it.copy(isUnlocked = true)
            }
        } ?: notifyError("No user found") // TODO navigate to login page?
    }

    private suspend fun generateAndStoreKey(user: User, keyStore: KeyStore, password: String) {
        val cipherSpec = user.getPropertyEncryptionCipherSpec()
        val key = cipherSpec.generateKey()

        keyStore.setEntry(
            keyAlias,
            SecretKeyEntry(key),
            null
        )

        // update the remote keystore with the new key
        user.updateRemoteKeyStore(
            keyStore = keyStore,
            password = password
        )
    }
}
