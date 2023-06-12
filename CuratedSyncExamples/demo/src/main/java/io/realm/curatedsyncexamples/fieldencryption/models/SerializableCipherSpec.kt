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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.lang.Exception
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

private const val IV_SIZE: Int = 16  // should be ok for most of the cases

/**
 * Class that contains all the information required to instantiate a [Cipher], it also provides
 * with methods to encrypt/decrypt data.
 */
@Serializable
data class SerializableCipherSpec(
    val algorithm: String,
    val block: String,
    val padding: String,
    @SerialName("key_length")
    val keyLength: Int
) {
    @Transient
    private val transformation = "$algorithm/$block/$padding"

    /**
     * Encrypts [input] using [key]
     */
    fun encrypt(input: ByteArray, key: Key): ByteArray =
        with(Cipher.getInstance(transformation)) {
            init(Cipher.ENCRYPT_MODE, key)
            iv + doFinal(input)
        }
    /**
     * Decrypts [encryptedData] using [key]
     */
    fun decrypt(encryptedData: ByteArray, key: Key): ByteArray =
        with(Cipher.getInstance(transformation)) {
            init(
                /* opmode = */ Cipher.DECRYPT_MODE,
                /* key = */ key,
                /* params = */ IvParameterSpec(encryptedData, 0, IV_SIZE)
            )
            try {
                doFinal(
                    /* input = */ encryptedData,
                    /* inputOffset = */ IV_SIZE,
                    /* inputLen = */ encryptedData.size - IV_SIZE
                )
            } catch (e: Exception) {
                byteArrayOf()
            }
        }
}
