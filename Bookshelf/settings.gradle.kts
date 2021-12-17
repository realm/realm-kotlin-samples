pluginManagement {
    repositories {
        maven(url = "file://Users/cm/Realm/realm-kotlin/packages/build/m2-buildrepo")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Bookshelf"
include(":androidApp")
include(":shared")