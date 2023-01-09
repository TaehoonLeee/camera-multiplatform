plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            isStatic = true
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
            }
        }
        getByName("androidMain").dependencies {
            implementation(compose.foundation)
        }
        create("iosMain") {
            dependsOn(commonMain)
            getByName("iosX64Main").dependsOn(this)
            getByName("iosArm64Main").dependsOn(this)
            getByName("iosSimulatorArm64Main").dependsOn(this)
        }
    }
}

android {
    namespace = "com.example.camera"
    compileSdk = 32
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}