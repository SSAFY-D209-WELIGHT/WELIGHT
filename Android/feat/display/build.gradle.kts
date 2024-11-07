plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.feat.display"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
}
