package io.realm.appservicesusagesamples.dynamicdata.models

import io.realm.kotlin.types.RealmAny
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class DynamicDataEntity: RealmObject {
    @PersistedName("_id")
    @PrimaryKey
    var id: ObjectId = BsonObjectId()

    var name: String = "<name>"

    var configuration: RealmAny? = null
}
