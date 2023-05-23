plugins {
    id("com.android.application")
    kotlin("android")
}

// https://maven.google.com/web/index.html?q=compiler#androidx.compose.compiler:compiler
val compose_compiler_version = "1.4.6"
// https://maven.google.com/web/index.html?q=ui#androidx.compose.ui:ui
val compose_ui_version = "1.4.3"

dependencies {
    implementation(project(":shared"))

    implementation("androidx.compose.compiler:compiler:$compose_compiler_version")
    implementation("androidx.compose.material:material:$compose_ui_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
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
