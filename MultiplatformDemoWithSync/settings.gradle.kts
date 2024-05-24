pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // Only required for realm-kotlin snapshots
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libsx") {
            from(files("../versions/current.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
        // Only required for realm-kotlin snapshots
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
rootProject.name = "realm-kotlin-multiplatform-sync-demo"
include(":androidApp")
include(":jvmApp")
include(":shared")
