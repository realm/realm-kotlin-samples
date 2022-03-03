import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("io.realm.kotlin")
}

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
        frameworkName = "shared"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
                implementation("io.realm.kotlin:library-base:${rootProject.extra["realmVersion"]}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting
        val androidAndroidTestRelease by getting
        val androidTest by getting {
            dependsOn(androidAndroidTestRelease)
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
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
    compileSdk= 30
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }
}
