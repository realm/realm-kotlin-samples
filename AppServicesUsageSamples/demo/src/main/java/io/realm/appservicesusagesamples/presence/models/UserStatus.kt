package io.realm.appservicesusagesamples.presence.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

@PersistedName("user_status")
class UserStatus : RealmObject {
    @PersistedName("_id")
    @PrimaryKey
    var id: ObjectId = ObjectId()
    @PersistedName("owner_id")
    var ownerId: String = ""
    var present: Boolean = false
}