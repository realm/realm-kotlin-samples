buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.1.0-rc01")
    }
}
group = "io.realm.example"
version = "0.8.2"

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
