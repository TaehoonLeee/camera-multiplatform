plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.camera.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.example.camera.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}