pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Only required for realm-kotlin snapshots
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
rootProject.name = "App Services Usage Samples"
include(":demo")
