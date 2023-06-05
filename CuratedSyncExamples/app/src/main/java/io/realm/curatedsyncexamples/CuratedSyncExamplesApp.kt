package io.realm.curatedsyncexamples

import android.app.Application
import io.realm.kotlin.mongodb.App

class CuratedSyncExamplesApp: Application() {

    lateinit var app: App

    override fun onCreate() {
        super.onCreate()
        app = App.create("cypher-scjvs")
    }
}

fun Application.app(): App = (this as CuratedSyncExamplesApp).app
