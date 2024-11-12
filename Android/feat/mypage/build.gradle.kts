plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rohkee.feat.mypage"
}

dependencies {

    debugImplementation(libs.androidx.compose.ui.tooling)
}
