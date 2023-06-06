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
package io.realm.curatedsyncexamples.fieldencryption.ext

import io.realm.curatedsyncexamples.fieldencryption.models.CipherSpec
import io.realm.curatedsyncexamples.fieldencryption.models.SerializableSecretKey
import io.realm.curatedsyncexamples.fieldencryption.models.UserKeyStore
import io.realm.kotlin.mongodb.User
import java.security.Key
import java.security.MessageDigest
import javax.crypto.KeyGenerator

const val HASH_ALGORITHM = "SHA-256"

fun Key.computeHash(): ByteArray =
    MessageDigest.getInstance(HASH_ALGORITHM).digest(encoded)

suspend fun UserKeyStore.getKeyOrGenerate(
    alias: String,
    password: String,
    generateNewKey: suspend () -> SerializableSecretKey
): SerializableSecretKey = use(password) {
    if (!contains(alias)) {
        set(alias, generateNewKey())
            .also {
                hasChanges = true
            }
    }

    get(alias)!!
}

fun CipherSpec.newKey(): SerializableSecretKey =
    KeyGenerator
        .getInstance(
            /* algorithm = */ algorithm
        ).apply {
            init(keyLength)
        }
        .generateKey()
        .let { key->
            SerializableSecretKey(
                key = key,
                cipherSpec = this@newKey
            )
        }

fun User.generateKey(): SerializableSecretKey = fieldEncryptionCipherSpec().newKey()
