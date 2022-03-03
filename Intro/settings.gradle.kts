// If you want to run against the local source repository just include the source projects by
// reincluding the below
// includeBuild("<Realm-Kotlin-Repo>/packages")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "KmmSample"

include(":androidApp")
include(":shared")
