package io.realm.kotlin.demo.javacompatibility.data.kotlin

import android.util.Log
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.demo.javacompatibility.TAG
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmObject

class KotlinEntity : RealmObject {
    var name: String = "KOTLIN"
}

class KotlinRepository {

    val realm = Realm.open(RealmConfiguration.Builder(setOf(KotlinEntity::class)).name("kotlin.realm").build())

    init {
        realm.writeBlocking { copyToRealm(KotlinEntity()) }
        val entities = realm.query<KotlinEntity>().find()
        Log.w(TAG, "KOTLIN: ${entities.size}")
    }
}
