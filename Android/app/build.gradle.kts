plugins {
    alias(libs.plugins.module.android.application.compose)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.welight"

    defaultConfig {
        applicationId = "com.rohkee.welight"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    //serialization
    implementation(libs.kotlinx.serialization.core)
    //compose-navigation
    implementation(libs.androidx.navigation.compose)

    implementation(project(":core:ui"))
    implementation(project(":feat:storage"))
    implementation(project(":feat:detail"))
    implementation(project(":feat:display"))
}