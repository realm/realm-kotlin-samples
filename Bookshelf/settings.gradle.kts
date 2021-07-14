// If you want to run against the local source repository just include the source projects using
// includeBuild("../../packages")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Bookshelf"
include(":androidApp")
include(":shared")