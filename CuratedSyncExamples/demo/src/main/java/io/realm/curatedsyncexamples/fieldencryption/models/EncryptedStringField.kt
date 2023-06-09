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

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.annotations.Ignore
import java.security.Key
import kotlin.reflect.KProperty


lateinit var cipherSpec: CipherSpec
lateinit var key: Key

class EncryptedStringField : EmbeddedRealmObject {
    var encryptedValue: ByteArray = byteArrayOf()

    /**
     * This delegated property provides seamless access to the encrypted data.
     */
    @Ignore
    var value: String by DecryptionDelegate()

    inner class DecryptionDelegate {
        operator fun getValue(thisRef: EncryptedStringField, property: KProperty<*>): String =
            String(
                bytes = cipherSpec.decrypt(thisRef.encryptedValue, key)
            )

        operator fun setValue(
            thisRef: EncryptedStringField,
            property: KProperty<*>,
            value: String
        ) {
            thisRef.encryptedValue = cipherSpec.encrypt(
                input = value.toByteArray(),
                key = key
            )
        }
    }
}