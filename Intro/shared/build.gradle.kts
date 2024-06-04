/*
 * Copyright 2020 JetBrains s.r.o.
 * Copyright 2020 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the License for the specific language gov erning permissions and
 * limitations under the License.
 */

plugins {
    alias(libsx.plugins.kotlinMultiplatform)
    alias(libsx.plugins.androidLibrary)
    // For some reason libsx.plugins.realm does not resolve directly so go through the provider
    id(libsx.plugins.realm.get().pluginId)
}

kotlin {
    android()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libsx.kotlinx.coroutines.core)
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
                implementation("com.google.android.material:material:1.6.1")
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libsx.kotlin.test.junit)
                implementation(libsx.junit)
                implementation("androidx.test:runner:1.4.0")
                implementation("androidx.test.ext:junit-ktx:1.1.3")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}
android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // FIXME connectedAndroidTest does not trigger any test.
    // https://youtrack.jetbrains.com/issue/KT-35016
    // https://github.com/realm/realm-kotlin/issues/73
    sourceSets.getByName("androidTest").java.srcDir(file("src/androidTest/kotlin"))
}
