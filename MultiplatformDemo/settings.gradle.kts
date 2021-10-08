pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Realm Kotlin Multiplatform Demo"
include(":androidApp")
include(":jvmApp")
include(":shared")