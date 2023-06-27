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

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

/**
 * Object with an encrypted field.
 */
@PersistedName("secret_record")
class SecretRecord : RealmObject {
    @PersistedName("owner_id")
    var ownerId: String = ""
    @PrimaryKey
    var _id: BsonObjectId = BsonObjectId()

    /**
     * Contains encrypted data.
     */
    var securedContent: ByteArray = byteArrayOf()

    /**
     * Helper that automatically encrypts/decrypts data.
     */
    @Ignore
    var content: String by SecureStringDelegate(::securedContent)
}
