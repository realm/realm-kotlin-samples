buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.1.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    group = "io.realm.sample.bookshelf"
    version = "0.9.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
