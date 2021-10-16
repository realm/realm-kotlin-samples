plugins {
    id("com.android.application")
    kotlin("android")
}

val compose_version = "1.0.4"

dependencies {
    implementation(project(":shared"))

    implementation("androidx.compose.compiler:compiler:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.ui:ui-tooling:$compose_version")
    implementation("androidx.activity:activity-compose:1.4.0-beta01")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "io.realm.kotlin.demo"
        minSdk = 21
        targetSdk = 31
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
        freeCompilerArgs += "-Xallow-jvm-ir-dependencies"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose_version
    }
}