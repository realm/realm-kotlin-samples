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

import kotlinx.serialization.Serializable
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Serializable
class SerializableSecretKey(
    val encoded: ByteArray,
    val cipherSpec: CipherSpec
) {
    constructor(key: SecretKey, cipherSpec: CipherSpec) : this(
        encoded = key.encoded,
        cipherSpec = cipherSpec
    )

    fun asSecretKey(): SecretKey = SecretKeySpec(encoded, cipherSpec.algorithm)
}
