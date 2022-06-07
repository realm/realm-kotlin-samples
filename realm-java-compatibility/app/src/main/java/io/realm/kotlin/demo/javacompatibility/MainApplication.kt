package io.realm.kotlin.demo.javacompatibility

import android.app.Application
import io.realm.Realm
import io.realm.kotlin.demo.javacompatibility.data.java.JavaRepository
import io.realm.kotlin.demo.javacompatibility.data.kotlin.KotlinRepository

const val TAG: String = "JavaCompatibilityApp"

class MainApplication : Application() {

    lateinit var java: JavaRepository
    lateinit var kotlin: KotlinRepository

    override fun onCreate() {
        super.onCreate()

        Realm.init(this.applicationContext)
        java = JavaRepository()
        kotlin = KotlinRepository()
    }
}
