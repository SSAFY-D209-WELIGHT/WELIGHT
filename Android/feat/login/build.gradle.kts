plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.feat.login"
}

dependencies {
    implementation(libs.play.services.auth)
}
