plugins {
    alias(libs.plugins.module.android.library)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.core.audio"
}

dependencies {
    implementation(files("libs/TarsosDSP-Android-2.4.jar"))
}
