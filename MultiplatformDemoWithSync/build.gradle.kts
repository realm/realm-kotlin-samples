plugins {
    kotlin("multiplatform") version "1.9.10" apply false
    id("com.android.library") version "7.3.1" apply false
}

// Explicitly adding the plugin to the classpath as it makes it easier to control the version
// centrally (don't need version in the 'plugins' block). Further, snapshots are not published with
// marker interface so would need to be added to the classpath manually anyway.
buildscript {
    dependencies {
        classpath("io.realm.kotlin:gradle-plugin:1.15.0")
    }
}
rootProject.extra["realmVersion"] = "1.15.0"

allprojects {
    group = "io.realm.sample"
    version = "1.0.0"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
