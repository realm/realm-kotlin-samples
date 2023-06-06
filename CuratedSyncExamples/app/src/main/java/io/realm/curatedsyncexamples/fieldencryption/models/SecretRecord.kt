package io.realm.curatedsyncexamples.fieldencryption.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class SecretRecord : RealmObject {
    @PersistedName("owner_id")
    var ownerId: String = ""
    @PrimaryKey
    var _id: BsonObjectId = BsonObjectId()
    var content: EncryptedStringField? = EncryptedStringField()
}
