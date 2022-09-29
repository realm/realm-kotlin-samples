plugins {
    id("com.android.application")
    kotlin("android")
}

// Android Compose Compiler Version: https://maven.google.com/web/index.html?q=androidx.compose.compiler#androidx.compose.compiler:compiler
val compose_compiler_version = "1.3.1"
// Android Compose UI Components: https://maven.google.com/web/index.html?q=androidx.compose.ui#androidx.compose.ui:ui
val compose_ui_version = "1.2.1"

dependencies {
    implementation(project(":shared"))

    implementation("androidx.compose.compiler:compiler:$compose_compiler_version")
    implementation("androidx.compose.material:material:$compose_ui_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt") {
        version {
            strictly("1.6.0-native-mt")
        }
    }
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    implementation("androidx.activity:activity-compose:1.6.0")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "io.realm.kotlin.demo"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    // Required by Compose
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose_compiler_version
    }
}
