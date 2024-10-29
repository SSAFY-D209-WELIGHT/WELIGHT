plugins {
    alias(libs.plugins.module.android.library.compose)
}

android {
    namespace = "com.rohkee.core.ui"
}

dependencies {
    implementation(libs.androidx.paging.common.android)
}