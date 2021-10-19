pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven ("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "Realm Kotlin Multiplatform Demo"
include(":androidApp")
include(":jvmApp")
include(":shared")
