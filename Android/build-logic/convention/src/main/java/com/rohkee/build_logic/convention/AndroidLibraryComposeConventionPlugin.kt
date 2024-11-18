import com.android.build.api.dsl.LibraryExtension
import com.rohkee.build_logic.convention.extension.configureAndroidCompose
import com.rohkee.build_logic.convention.extension.configureKotlinAndroid
import com.rohkee.build_logic.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")

            configure<LibraryExtension>{
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
                configureAndroidCompose(this)
            }

            dependencies {
                add("implementation", libs.findBundle("android.core").get())
            }
        }
    }
}