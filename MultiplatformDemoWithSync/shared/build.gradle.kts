import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.realm.kotlin")
}

version = "1.0"

kotlin {
    // iosSimulatorArm64, iosArm64 and macosArm64 are disabled because they are not supported
    // by Ktor 1.*. This means that Apple Silicon machines must run the x86_64 variant with
    // Rosetta enabled.
    android()
    iosX64()
    // iosArm64()
    // iosSimulatorArm64()
    macosX64()
    // macosArm64()
    jvm {}

    cocoapods {
        summary = "Realm Kotlin Multiplatform Demo Shared Library"
        homepage = "https://github.com/realm/realm-kotlin"
        ios.deploymentTarget = "14.1"
        osx.deploymentTarget = "11.0"
        framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
                implementation("io.realm.kotlin:library-sync:${rootProject.extra["realmVersion"]}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        // val iosArm64Main by getting
        // val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            // iosArm64Main.dependsOn(this)
            // iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        // val iosArm64Test by getting
        // val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            // iosArm64Test.dependsOn(this)
            // iosSimulatorArm64Test.dependsOn(this)
        }
        val macosX64Main by getting
        // val macosArm64Main by getting
        val macosMain by creating {
            dependsOn(commonMain)
            macosX64Main.dependsOn(this)
            // macosArm64Main.dependsOn(this)
        }
    }
}

android {
    compileSdk= 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}
