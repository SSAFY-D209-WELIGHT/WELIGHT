import java.util.Properties

plugins {
    alias(libs.plugins.module.android.application.compose)
    alias(libs.plugins.module.hilt)
    alias(libs.plugins.kotlin.serialization)
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
    namespace = "com.rohkee.demo"

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "com.rohkee.demo"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 따옴표 이스케이프 처리 추가
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"${googleClientId.replace("\"", "\\\"")}\"")

        // 웹 클라이언트 ID도 추가
        manifestPlaceholders["GOOGLE_OAUTH_CLIENT_ID"] = googleClientId
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(libs.play.services.auth)
    implementation(project(":feat:login"))
    implementation(project(":feat:mypage"))
    implementation(project(":core:network"))

//    implementation(libs.androidx.lifecycle.viewmodel.compose.v262)
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    implementation(libs.androidx.compose.material3)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
