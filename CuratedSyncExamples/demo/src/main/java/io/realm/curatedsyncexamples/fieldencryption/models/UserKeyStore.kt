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

import io.realm.curatedsyncexamples.fieldencryption.ext.computeHash
import io.realm.kotlin.internal.platform.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.security.Key

/**
 * Serializable class that models a password protected key store.
 */
@Serializable
class UserKeyStore(
    /**
     * Specifies the algorithm used to generate the key that would secure this keystore contents.
     */
    @SerialName("encryption_key_spec")
    val encryptionKeySpec: SerializablePBEKeySpec,

    /**
     * Cipher algorithm used to secure this keystore contents.
     */
    @SerialName("cipher_spec")
    val cipherSpec: SerializableCipherSpec,

    /**
     * Encrypted contents. The type of the unencrypted data is a Map<String, SerializableSecretKey>.
     */
    @SerialName("secure_contents")
    var secureContents: ByteArray?,

    /**
     * Computed hash for the encryption key used to encode the contents. It allows to
     * identify if the right key was used to decode the contents.
     */
    @SerialName("key_hash")
    var keyHash: ByteArray?,

    /**
     * Indicates if the keystore has been modified.
     */
    @Transient
    var hasChanges: Boolean = false
) {

    private fun loadContents(key: Key): MutableMap<String, SerializableSecretKey> =
        secureContents?.let { byteArray ->
            keyHash?.let {
                require(keyHash.contentEquals(key.computeHash())) { "Wrong password" }
            }

            val serializedKeyStore = String(
                cipherSpec.decrypt(byteArray, key),
                StandardCharsets.UTF_8
            )

            Json.decodeFromString<Map<String, SerializableSecretKey>>(serializedKeyStore)
                .toMutableMap()
        } ?: mutableMapOf()

    private fun saveContents(contents: MutableMap<String, SerializableSecretKey>, key: Key) {
        val updatedKeyStore = Json.encodeToString(contents)
            .toByteArray(StandardCharsets.UTF_8)

        keyHash = key.computeHash()

        secureContents = cipherSpec.encrypt(updatedKeyStore, key)
    }

    suspend fun <T> use(
        password: String,
        update: suspend MutableMap<String, SerializableSecretKey>.() -> T
    ): T =
        // Encryption/decryption can take a while
        runBlocking(Dispatchers.IO) {
            val key = encryptionKeySpec.generateKey(password)

            loadContents(key).let { contents ->
                try {
                    contents.update()
                } finally {
                    saveContents(contents, key)
                }
            }
        }
}
