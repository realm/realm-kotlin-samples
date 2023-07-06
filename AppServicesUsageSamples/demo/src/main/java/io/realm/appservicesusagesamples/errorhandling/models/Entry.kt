package io.realm.appservicesusagesamples.errorhandling.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class Entry: RealmObject {
    @PrimaryKey
    @PersistedName("_id")
    var id = BsonObjectId()

    @PersistedName("owner_id")
    var ownerId: String = ""
}