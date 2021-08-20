buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("com.android.tools.build:gradle:7.0.1")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.21")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

group = "io.realm.sample.bookshelf"
version = "0.5.0"

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
