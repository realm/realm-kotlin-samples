pluginManagement {
    repositories {
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
}
rootProject.name = "JVM_Console"

