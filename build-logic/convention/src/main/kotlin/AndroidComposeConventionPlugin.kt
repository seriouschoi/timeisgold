import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import software.seriouschoi.timeisgold.androidTestImplementation
import software.seriouschoi.timeisgold.debugImplementation
import software.seriouschoi.timeisgold.implementation
import software.seriouschoi.timeisgold.ksp
import software.seriouschoi.timeisgold.libs
import software.seriouschoi.timeisgold.pluginAlias

/**
 * Created by jhchoi on 2025. 8. 25.
 * jhchoi
 */
@Suppress("unused")
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginAlias("kotlin.compose")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                val bom = libs.findLibrary("compose.bom").get()
                implementation(target.libs, bom)
                androidTestImplementation(target.libs, bom)

                implementation(target.libs, "compose.material")
                implementation(target.libs, "compose.ui")
                implementation(target.libs, "compose.ui.tooling.preview")
                debugImplementation(target.libs, "compose.ui.tooling")
                implementation(target.libs, "navigation.compose")

                androidTestImplementation(target.libs, "compose.ui.test.junit4")
                debugImplementation(target.libs, "compose.ui.test.manifest")

                implementation(target.libs, "hilt.android")
                ksp(target.libs, "hilt.compiler")
                implementation(target.libs, "androidx.hilt.navigation.compose")
            }
        }
    }
}