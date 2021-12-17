buildscript {
    repositories {
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.android.tools.build:gradle:7.1.0-beta05")
        classpath ("io.realm.kotlin:gradle-plugin:0.8.0-SNAPSHOT")
    }
}

allprojects {
    repositories {
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        google()
        mavenCentral()
    }
}

group = "io.realm.sample.bookshelf"
version = "0.8.0"

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
