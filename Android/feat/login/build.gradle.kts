plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.feat.login"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Google Oauth 관련
    implementation(libs.play.services.auth)

    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
}
