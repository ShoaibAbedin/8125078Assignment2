plugins {
    alias(libs.plugins.android.application)
    // KSP runs Hilt's annotation processor (the modern replacement for kapt)
    alias(libs.plugins.ksp)
    // Hilt dependency injection (Lecture 7)
    alias(libs.plugins.hilt)
    // Safe Args generates type-safe Directions/Args classes for navigation (Lecture 4)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.s8125078.assignment2"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.s8125078.assignment2"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests {
            // Android framework calls (e.g. inside RecyclerView.Adapter) return default
            // values in local JVM unit tests instead of throwing "Method not mocked".
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // AndroidX and Material Design 3 (Lectures 2-3)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Fragments, ViewModel, lifecycle-aware coroutines and navigation (Lectures 4-5)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // List display (Lecture 6)
    implementation(libs.androidx.recyclerview)

    // Dependency injection (Lecture 7)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Networking and coroutines (Lecture 5)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)

    // Local unit tests (Lecture 8)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests (template defaults)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
