buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.android.tools.build:gradle:7.0.0-beta04")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.10")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

group = "io.realm.sample.bookshelf"
version = "0.4.0"

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}