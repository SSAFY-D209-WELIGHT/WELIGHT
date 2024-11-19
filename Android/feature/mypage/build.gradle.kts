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
    implementation(project(":core:ui"))
    implementation(libs.androidx.paging.common.android)
    debugImplementation(libs.androidx.compose.ui.tooling)


    implementation(libs.androidx.paging.compose)
    // coil
    implementation(libs.coil.compose)
}
