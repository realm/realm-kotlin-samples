package io.realm.kotlin.demo.javacompatibility.data.java

import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmModel
import io.realm.annotations.RealmClass
import io.realm.kotlin.demo.javacompatibility.TAG

// Realm Kotlin will try to process this class if using io.realm.RealmObject so use
// io.realm.RealmModel/@RealmClass approach instead
@RealmClass
open class JavaEntity : RealmModel {
    var name: String = "JAVA"
}

class JavaRepository {

    var realm: Realm = Realm.getInstance(
        RealmConfiguration.Builder()
            .name("java.realm")
            .allowWritesOnUiThread(true)
            .build()
    )

    init {
        realm.executeTransaction {
            realm.createObject(JavaEntity::class.java)
            val entities = realm.where(JavaEntity::class.java).findAll()
            Log.d(TAG, "JAVA: ${entities.size}")
        }
    }
}
