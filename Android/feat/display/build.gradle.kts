plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rohkee.feat.display"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:ui"))

    //serialization
    implementation(libs.kotlinx.serialization.core)
}