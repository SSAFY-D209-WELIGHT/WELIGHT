import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rohkee.demo"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig에 GOOGLE_OAUTH_CLIENT_ID 정의
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"$googleClientId\"")
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\Users\\KDGKING\\.android\\debug.keystore") // 실제 키스토어 경로로 변경하세요
            storePassword = "android" // 키스토어 비밀번호
            keyAlias = "androiddebugkey" // 키 별칭
            keyPassword = "android" // 키 비밀번호
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug") // 디버그 빌드에 signingConfig 추가
        }
        release {
            signingConfig = signingConfigs.getByName("debug") // 필요 시 다른 signingConfig 사용 가능
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":feat:login"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
