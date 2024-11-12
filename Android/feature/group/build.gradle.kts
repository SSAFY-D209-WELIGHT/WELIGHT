plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rohkee.feature.group"

}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
}