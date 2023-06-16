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
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Class that contains all the information required to instantiate a [PBEKeySpec]. It contains a
 * helper method to generate keys.
 */
@Serializable
class SerializablePBEKeySpec(
    val algorithm: String,
    val salt: ByteArray,
    @SerialName("iterations_count")
    val iterationsCount: Int,
    @SerialName("key_length")
    val keyLength: Int,
) {
    fun generateKey(password: String): SecretKey =
        PBEKeySpec(
            /* password = */ password.toCharArray(),
            /* salt = */ salt,
            /* iterationCount = */ iterationsCount,
            /* keyLength = */ keyLength
        ).let {
            SecretKeyFactory.getInstance(algorithm).generateSecret(it)
        }
}