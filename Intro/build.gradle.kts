buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.android.tools.build:gradle:7.1.0-beta05")
    }
}
group = "io.realm.example"
version = "0.4.1"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
