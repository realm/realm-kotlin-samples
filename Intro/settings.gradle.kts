// If you want to run against the local source repository just include the source projects by
// reincluding the below
// includeBuild("<Realm-Kotlin-Repo>/packages")


pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:4.0.1")
            }
        }
    }
}
rootProject.name = "KmmSample"

include(":androidApp")
include(":shared")
