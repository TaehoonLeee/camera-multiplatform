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
        all {
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
        }
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
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    sourceSets["main"].res.srcDir("src/commonMain/resources")
}