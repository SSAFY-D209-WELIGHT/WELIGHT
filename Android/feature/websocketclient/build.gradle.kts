plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rohkee.feature.websocketclient"
}

dependencies {
    implementation("io.socket:socket.io-client:2.1.0")
    implementation("org.json:json:20210307")
}