plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rohkee.feature.group"
}

dependencies {
    implementation(libs.play.services.location)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose.android)
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:websocket"))
}
