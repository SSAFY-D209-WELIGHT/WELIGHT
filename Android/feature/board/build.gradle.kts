plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.feat.board"
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose.android)
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    //추가
    implementation("io.coil-kt:coil-compose:2.4.0")
}
