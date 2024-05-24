pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
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

rootProject.name = "Bookshelf"
include(":androidApp")
include(":shared")
