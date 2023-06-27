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
package io.realm.appservicesusagesamples.fieldencryption.ext

import io.realm.appservicesusagesamples.fieldencryption.models.SerializableCipherSpec
import io.realm.appservicesusagesamples.fieldencryption.models.CustomData
import io.realm.kotlin.annotations.ExperimentalRealmSerializerApi
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.ext.call
import io.realm.kotlin.mongodb.ext.customData
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore

@OptIn(ExperimentalRealmSerializerApi::class)
fun User.hasKeyStore() = customData<CustomData>()?.keyStore != null

@OptIn(ExperimentalRealmSerializerApi::class)
fun User.getRemoteKeyStore(password: String): KeyStore =
    KeyStore.getInstance("BKS").apply {
        // Load any user keystore if available
        customData<CustomData>()?.keyStore?.let { keyStoreBlob ->
            ByteArrayInputStream(keyStoreBlob).use { keyStoreStream ->
                load(keyStoreStream, password.toCharArray())
            }
        } ?: load(null)
    }

@OptIn(ExperimentalRealmSerializerApi::class)
suspend fun User.updateRemoteKeyStore(keyStore: KeyStore, password: String) {
    functions.call<Boolean>("updateKeyStore") {
        ByteArrayOutputStream().use { outputStream ->
            keyStore.store(outputStream, password.toCharArray())
            add(outputStream.toByteArray())
        }
    }
}

@OptIn(ExperimentalRealmSerializerApi::class)
fun User.fieldEncryptionCipherSpec(): SerializableCipherSpec {
    return customData<CustomData>()?.FLECipherSpec!!
}
