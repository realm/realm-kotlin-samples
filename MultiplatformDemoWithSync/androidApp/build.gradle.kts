plugins {
    id("com.android.application")
    kotlin("android")
    id ("org.jetbrains.kotlin.plugin.compose")
}

// https://maven.google.com/web/index.html?q=ui#androidx.compose.ui:ui
val compose_ui_version = "1.6.7"

dependencies {
    implementation(project(":shared"))

    implementation("androidx.compose.material:material:$compose_ui_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    implementation("androidx.activity:activity-compose:1.9.0")
}

android {
    compileSdk = 34
    defaultConfig {
        namespace = "io.realm.kotlin.demo"
        applicationId = "io.realm.kotlin.demo"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}
