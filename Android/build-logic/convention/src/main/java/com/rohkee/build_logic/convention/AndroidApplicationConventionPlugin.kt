import com.android.build.api.dsl.ApplicationExtension
import com.rohkee.build_logic.convention.extension.configureKotlinAndroid
import com.rohkee.build_logic.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")

            configure<ApplicationExtension>{
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
            }

            dependencies {
                add("implementation", libs.findBundle("android.core").get())

                // timber logger
                add("implementation", libs.findLibrary("timber").get())
            }
        }
    }
}
