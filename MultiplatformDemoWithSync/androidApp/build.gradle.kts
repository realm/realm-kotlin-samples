plugins {
    alias(libsx.plugins.androidApplication)
    alias(libsx.plugins.kotlinAndroid)
    alias(libsx.plugins.compose.compiler)
}

dependencies {
    implementation(project(":shared"))
    implementation(libsx.androidx.compose)
    implementation(libsx.androidx.compose.ui)
    implementation(libsx.androidx.compose.material)
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
    kotlinOptions.jvmTarget = libsx.versions.jvmTarget.get().toString()
}
