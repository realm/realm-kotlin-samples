import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("kotlin-android-extensions")
    kotlin("plugin.serialization") version "1.5.31"
    id("io.realm.kotlin") /*version "0.9.0"*/
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
        frameworkName = "shared"
    }

    val ktorVersion = "1.6.1"
    val serializationVersion = "1.2.1"
    val coroutinesVersion = "1.5.2-native-mt"
    val realmVersion = "0.9.0-SNAPSHOT"

    sourceSets {
        val commonMain  by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.realm.kotlin:library-base:$realmVersion")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:$ktorVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
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

    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk =31
    }
}
