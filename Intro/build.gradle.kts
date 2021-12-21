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
version = "0.8.0"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
