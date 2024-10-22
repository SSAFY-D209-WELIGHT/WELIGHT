import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin{
    plugins{
        register("androidApplicationConventionPlugin"){
            id = "rokhee.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryConventionPlugin"){
            id = "rokhee.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidComposeConventionPlugin"){
            id = "rokhee.android.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("androidHiltConventionPlugin"){
            id = "rokhee.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}