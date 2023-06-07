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

@Serializable
data class CustomData(
    @SerialName("field_encryption_cipher_spec")
    val fieldEncryptionCipherSpec: CipherSpec?,
    @SerialName("key_store")
    val keyStore: UserKeyStore
)
