import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import software.seriouschoi.timeisgold.implementation
import software.seriouschoi.timeisgold.ksp
import software.seriouschoi.timeisgold.libs

/**
 * Created by jhchoi on 2025. 8. 25.
 * jhchoi
 */
@Suppress("unused")
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.dagger.hilt.android")

            dependencies {
                implementation(target.libs, "hilt.android")
                ksp(target.libs, "hilt.compiler")
            }
        }
    }
}