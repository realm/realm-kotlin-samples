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

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    group = "io.realm.sample"
    version = "0.8.2"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
