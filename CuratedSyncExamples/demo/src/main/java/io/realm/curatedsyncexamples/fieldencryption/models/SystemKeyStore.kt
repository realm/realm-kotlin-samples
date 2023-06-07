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
package io.realm.curatedsyncexamples.fieldencryption.models

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import io.realm.curatedsyncexamples.fieldencryption.ext.generateKey
import io.realm.curatedsyncexamples.fieldencryption.ext.getKeyOrGenerate
import io.realm.curatedsyncexamples.fieldencryption.ext.keyStore
import io.realm.curatedsyncexamples.fieldencryption.ext.updateKeyStore
import io.realm.kotlin.mongodb.User
import java.security.Key
import java.security.KeyStore

object SystemKeyStore {
    private val keyStore: KeyStore =
        KeyStore.getInstance("AndroidKeyStore")
            .apply {
                load(null)
            }

    fun containsKey(keyAlias: String) = keyStore.isKeyEntry(keyAlias)

    fun removeKey(keyAlias: String) = keyStore.deleteEntry(keyAlias)

    /**
     * Suspend as it key computation can take some time.
     */
    suspend fun getKey(
        keyAlias: String,
        generateKey: suspend SystemKeyStore.() -> SerializableSecretKey
    ): Key {
        if (!keyStore.isKeyEntry(keyAlias))
            storeKey(keyAlias, generateKey())

        return keyStore
            .getKey(keyAlias, null)
    }

    private fun storeKey(
        keyAlias: String,
        key: SerializableSecretKey
    ) {
        keyStore.setEntry(
            keyAlias,
            KeyStore.SecretKeyEntry(key.asSecretKey()),
            KeyProtection
                .Builder(
                    /* purposes = */ KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(key.cipherSpec.block)
                .setEncryptionPaddings(key.cipherSpec.padding)
                .build()
        )
    }
}

suspend fun getFieldLevelEncryptionKey(keyAlias: String, user: User, password: String) =
    SystemKeyStore
        .getKey(keyAlias) {
            // Key is missing in the Android keystore, retrieve it from the keystore
            val keyStore = user.keyStore()

            keyStore.getKeyOrGenerate(keyAlias, password) {
                // Key is missing in the User keystore, generate a new one
                user.generateKey()
            }.also {
                // We might have modified the user keystore, lets propagate the changes to the server
                if (keyStore.hasChanges) user.updateKeyStore(keyStore)
            }
        }
