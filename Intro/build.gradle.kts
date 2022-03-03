plugins {
    kotlin("multiplatform") version "1.6.10" apply false
    id("com.android.library") version "7.1.0" apply false
}

buildscript {
    dependencies {
        classpath("io.realm.kotlin:gradle-plugin:0.10.0-SNAPSHOT")
    }
}
rootProject.extra["realmVersion"] = "0.10.0-SNAPSHOT"

group = "io.realm.example"
version = "0.10.0"
