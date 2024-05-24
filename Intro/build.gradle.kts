plugins {
    alias(libsx.plugins.kotlinMultiplatform) apply false
    alias(libsx.plugins.kotlinAndroid) apply false
    alias(libsx.plugins.androidApplication) apply false
    alias(libsx.plugins.androidLibrary) apply false
}

// Explicitly adding the plugin to the classpath as it makes it easier to control the version
// centrally (don't need version in the 'plugins' block). Further, snapshots are not published with
// marker interface so would need to be added to the classpath manually anyway.
buildscript {
    dependencies {
        classpath(libsx.realm.plugin)
    }
}

group = "io.realm.example"
version = "1.0.0"
