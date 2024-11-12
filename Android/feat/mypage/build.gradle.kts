plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rohkee.feat.mypage"
}

dependencies {
    implementation(project(":core:network"))
    debugImplementation(libs.androidx.compose.ui.tooling)

    // coil
    implementation(libs.coil.compose.v200)
}
