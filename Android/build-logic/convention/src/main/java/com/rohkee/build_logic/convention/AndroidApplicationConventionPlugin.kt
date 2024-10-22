import com.rohkee.build_logic.convention.extension.configureKotlinAndroid
import com.rohkee.build_logic.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

internal class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")

            configureKotlinAndroid()

            dependencies {
                add("implementation", libs.findBundle("android.core").get())

                // timber logger
                add("implementation", libs.findLibrary("timber").get())
            }
        }
    }
}
