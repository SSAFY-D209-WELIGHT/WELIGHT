import java.util.Properties

plugins {
    alias(libs.plugins.module.android.library)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
}

val properties = Properties()
properties.load(rootProject.file("local.properties").inputStream())


android {
    namespace = "com.rohkee.core.network"
    defaultConfig {
        buildConfigField("String", "BASE_URL", properties["BASE_URL"] as String)
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.bundles.network)
    implementation(libs.bundles.serialization)
    implementation(project(":core:datastore"))
}