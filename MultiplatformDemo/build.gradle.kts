buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven ("https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("io.realm.kotlin:gradle-plugin:0.7.0-SNAPSHOT")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven ("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
