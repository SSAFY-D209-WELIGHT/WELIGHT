import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
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
        register("androidApplicationComposeConventionPlugin"){
            id = "rokhee.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibraryComposeConventionPlugin"){
            id = "rokhee.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidHiltConventionPlugin"){
            id = "rokhee.android.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}