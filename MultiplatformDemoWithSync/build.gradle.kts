plugins {
    alias(libsx.plugins.kotlinMultiplatform) apply false
    alias(libsx.plugins.kotlinAndroid) apply false
    alias(libsx.plugins.kotlinJvm) apply false
    alias(libsx.plugins.androidApplication) apply false
    alias(libsx.plugins.androidLibrary) apply false
    alias(libsx.plugins.jetbrainsCompose) apply false
    alias(libsx.plugins.compose.compiler) apply false
}

// Explicitly adding the plugin to the classpath as it makes it easier to control the version
// centrally (don't need version in the 'plugins' block). Further, snapshots are not published with
// marker interface so would need to be added to the classpath manually anyway.
buildscript {
    dependencies {
        classpath(libsx.realm.plugin)
    }
}

allprojects {
    group = "io.realm.sample"
    version = "1.0.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
