import java.util.Properties

plugins {
    alias(libs.plugins.module.android.application.compose)
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
    namespace = "com.rohkee.welight"

    defaultConfig {
        applicationId = "com.rohkee.welight"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 따옴표 이스케이프 처리 추가
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"${googleClientId.replace("\"", "\\\"")}\"")

        // 웹 클라이언트 ID도 추가
        manifestPlaceholders["GOOGLE_OAUTH_CLIENT_ID"] = googleClientId
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // serialization
    implementation(libs.kotlinx.serialization.core)
    // compose-navigation
    implementation(libs.androidx.navigation.compose)

    // Google Oauth 관련
    implementation(libs.play.services.auth)

    implementation(libs.datastore.preferences)
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))
    implementation(project(":feat:login"))
    implementation(project(":feature:storage"))
    implementation(project(":feature:detail"))
    implementation(project(":feature:editor"))
    implementation(project(":feature:group"))
}
