/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
}

kotlin {

    androidLibrary {
        namespace = "eu.antoinepurnelle.jobapplication.data"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    val xcfName = "dataKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.datetime)
                // Koin core for DI
                implementation(project.dependencies.platform(libs.koin.bom))
                api(libs.koin.core)
                // Ktor core and serialization
                implementation(libs.bundles.ktor)
                // Other modules
                implementation(project(":domain"))
            }
        }

        androidMain {
            dependencies {
                // Koin for Android
                implementation(libs.koin.android)
                // Ktor OkHttp client
                implementation(libs.ktor.client.okhttp)
            }
        }

        iosMain {
            dependencies {
                // Ktor Darwin client for iOS
                implementation(libs.ktor.client.darwin)
            }
        }

        jvmMain {
            dependencies {
                // Ktor client for desktop JVM
                implementation(libs.ktor.client.okhttp)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotest.assertions)
                implementation(libs.mockk)
                implementation(libs.junit)
                implementation(libs.junitparams)
                implementation(libs.datafaker)
                implementation(libs.coroutines.test)
                implementation(libs.ktor.client.mock)
            }
        }
    }

}
