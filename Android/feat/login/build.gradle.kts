import java.util.Properties

plugins {
    alias(libs.plugins.module.android.library.compose)
    alias(libs.plugins.module.hilt)
}

// local.properties에서 GOOGLE_OAUTH_CLIENT_ID를 가져오기
val googleClientId: String =
    project.rootProject.file("local.properties").let { file ->
        if (file.exists()) {
            val properties = Properties().apply { load(file.inputStream()) }
            properties.getProperty("GOOGLE_OAUTH_CLIENT_ID") ?: ""
        } else {
            ""
        }
    }

android {
    namespace = "com.rohkee.feat.login"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 따옴표 이스케이프 처리 추가
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"${googleClientId.replace("\"", "\\\"")}\"")

        // 웹 클라이언트 ID도 추가
        manifestPlaceholders["GOOGLE_OAUTH_CLIENT_ID"] = googleClientId
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Google Oauth 관련
    implementation(libs.play.services.auth)

    implementation(project(":core:ui"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
}
