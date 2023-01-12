plugins {
    kotlin("multiplatform")
    id("com.android.library")
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
        create("iosMain") {
            dependsOn(commonMain.get())
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