plugins {
    alias(libs.plugins.module.android.library)
    alias(libs.plugins.module.hilt)
}

android {
    namespace = "com.rohkee.core.datastore"
}

dependencies {
    implementation(libs.datastore.preferences)
}
