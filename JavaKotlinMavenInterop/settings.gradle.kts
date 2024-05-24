pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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

rootProject.name = "JavaKotlinMavenInterop"
include("kotlin-lib")
include("java-app-gradle")
