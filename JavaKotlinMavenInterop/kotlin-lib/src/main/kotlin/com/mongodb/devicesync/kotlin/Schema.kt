package com.mongodb.devicesync.kotlin

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Person: RealmObject {
    @PrimaryKey
    @PersistedName("_id")
    var id = ObjectId()
    var name: String = ""
    var age: Int = 0
    var children: RealmList<Child> = realmListOf()
    var favoriteChild: Child? = null

    // Expose method making it easier to use from Java
    fun setChildren(children: ArrayList<Child>) {
        children.addAll(children)
    }

    override fun toString(): String {
        return "Person(id=$id, name='$name', age=$age, children=$children, favoriteChild=$favoriteChild)"
    }
}

class Child(s: String) : RealmObject {
    // No-arg constructor required by Realm
    constructor(): this("")
    @PrimaryKey
    @PersistedName("_id")
    var id = ObjectId()
    var name: String = s
}