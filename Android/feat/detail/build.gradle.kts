plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.feat.detail"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:network"))
}