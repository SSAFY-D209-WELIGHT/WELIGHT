package com.rohkee.build_logic.convention.extension

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid() {
    configure<ApplicationExtension> {
        defaultConfig.minSdk = 26
        defaultConfig.targetSdk = 34
        compileSdk = 34

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
    configure<KotlinAndroidProjectExtension>{
        compilerOptions.jvmTarget = JvmTarget.JVM_17
    }
}