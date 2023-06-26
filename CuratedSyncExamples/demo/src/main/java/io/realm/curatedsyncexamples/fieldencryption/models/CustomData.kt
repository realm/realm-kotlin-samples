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

/**
 * App services user custom data. It contains the resources required for FLE:
 * 1. Encryption key, it is stored in a password protected keystore.
 * 2. Field encryption cipher spec,
 */
@Serializable
class CustomData(
    /**
     * Defines the FLE algorithm.
     */
    @SerialName("fle_cipher_spec")
    val FLECipherSpec: SerializableCipherSpec?,

    /**
     * BKS keystore containing the key.
     */
    @SerialName("key_store")
    val keyStore: ByteArray?
)
