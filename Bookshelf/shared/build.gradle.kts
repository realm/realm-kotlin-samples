import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libsx.plugins.kotlinMultiplatform)
    alias(libsx.plugins.androidLibrary)
    alias(libsx.plugins.kotlinSerialization)
    // For snapshots which are already on the classpath we have to apply the plugin by id instead of
    // alias as it fails to validate the version
    // alias(libsx.plugins.realm)
    id(libsx.plugins.realm.get().pluginId)
    // For some reason this does not resolve even though [id: 'org.jetbrains.kotlin.native.cocoapods', version: '2.0.0'] is available in Gradle plugin portal
    // alias(libsx.plugins.cocoapods)
    kotlin("native.cocoapods")
}

kotlin {
    android()

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }
    iosTarget("ios") {}

    cocoapods {
        summary = "Realm Kotlin Bookshelf shared Library"
        homepage = "https://github.com/realm/realm-kotlin"
        ios.deploymentTarget = "14.1"
        osx.deploymentTarget = "11.0"
        framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain  by getting {
            dependencies {
                implementation(libsx.kotlinx.coroutines.core)
                implementation(libsx.ktor.client.core)
                implementation(libsx.ktor.client.content.negotiation)
                implementation(libsx.ktor.serialization.json)
                implementation(libsx.ktor.client.serialization)
                implementation(libsx.realm.base)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libsx.kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libsx.ktor.client.android)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libsx.kotlin.test.junit)
                implementation(libsx.junit)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libsx.ktor.client.ios)
            }
        }
        val iosTest by getting
    }
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}
