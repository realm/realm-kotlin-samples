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
package io.realm.appservicesusagesamples.fieldencryption.models

import java.lang.Exception
import java.security.Key
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

/**
 * Field level encryption cypher spec.
 *
 * It is a global variable because we cannot reference the user out from a RealmObject yet.
 */
lateinit var FLECipherSpec: SerializableCipherSpec

/**
 * Field level encryption key.
 *
 * It is a global variable because we cannot reference the user out from a RealmObject yet.
 */
lateinit var FLEKey: Key


/**
 * Delegates that encapsulates the logic to encrypt/decrypt a String.
 *
 * It uses the global variables [FLECipherSpec] and [FLEKey] to encrypt/decrypt the
 * contents from [backingProperty].
 */
class SecureStringDelegate(private val backingProperty: KMutableProperty0<ByteArray>) {
    operator fun getValue(
        thisRef: Any,
        property: KProperty<*>,
    ): String =
        try {
            String(bytes = FLECipherSpec.decrypt(backingProperty.get(), FLEKey))
        } catch (e: Exception) {
            "Data could not be decrypted"
        }

    operator fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: String,
    ) {
        backingProperty.set(
            FLECipherSpec.encrypt(
                input = value.toByteArray(),
                key = FLEKey
            )
        )
    }
}