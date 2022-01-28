buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.1.0")
        classpath("io.realm.kotlin:gradle-plugin:0.9.0-SNAPSHOT")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }

    group = "io.realm.sample"
    version = "0.9.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
