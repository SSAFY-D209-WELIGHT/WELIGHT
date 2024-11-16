plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rohkee.core.websocket"
}

dependencies {
    implementation(libs.socket.io.client)
    implementation(libs.json)
    implementation(libs.play.services.location)
}
