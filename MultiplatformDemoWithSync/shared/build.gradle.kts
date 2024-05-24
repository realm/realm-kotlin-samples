import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libsx.plugins.kotlinMultiplatform)
    alias(libsx.plugins.androidLibrary)
    id(libsx.plugins.realm.get().pluginId)
    // For some reason this does not resolve even though [id: 'org.jetbrains.kotlin.native.cocoapods', version: '2.0.0'] is available in Gradle plugin portal
    // alias(libsx.plugins.cocoapods)
    kotlin("native.cocoapods")
}

version = "1.0"

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }
    iosTarget("ios") {}
    val macosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::macosArm64
        else -> ::macosX64
    }
    macosTarget("macos") {}
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
                implementation(libsx.kotlinx.coroutines.core)
                implementation(libsx.realm.sync)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libsx.kotlin.test)
            }
        }
        val androidMain by getting
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libsx.kotlin.test.junit)
                implementation(libsx.junit)
            }
        }

        val iosMain by getting
        val iosTest by getting
        val macosMain by getting
        val macosTest by getting
        val jvmMain by getting
    }
}

android {
    compileSdk= 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        namespace = "io.realm.kotlin.demo.shared"
        minSdk = 21
        targetSdk = 33
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = libsx.versions.jvmTarget.get().toString()
}
