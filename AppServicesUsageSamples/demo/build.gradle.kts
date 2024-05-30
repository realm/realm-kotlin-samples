plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // For some reason libsx.plugins.realm does not resolve directly so go through the provider
    // alias(libs.plugins.realm.kotlin)
    id(libs.plugins.realm.kotlin.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "io.realm.appservicesusagesamples"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.realm.appservicesusagesamples"

        // Field encryption key algorithms require minSdk 26
        // https://developer.android.com/reference/javax/crypto/SecretKeyFactory
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.serialization.json)

    val composeBom = platform(libs.androidx.compose.bom)

    implementation(composeBom)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)

    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.bonsai)
    // Compile only dependency of libs.bonsai, so need to include it explicitly
    implementation(compose.materialIconsExtended)

    implementation(libs.realm.library.sync)

    debugImplementation(composeBom)
    debugImplementation(libs.androidx.compose.ui.tooling.core)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit4)

    androidTestImplementation(kotlin("test"))
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
