plugins {
    alias(libs.plugins.module.android.library.compose)
}

android {
    namespace = "com.rohkee.core.ui"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.compose.android)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation.core)
}
