import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import software.seriouschoi.timeisgold.setJvmTarget

/**
 * Created by jhchoi on 2025. 8. 25.
 * jhchoi@neofect.com
 */
class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        //jetbrains-kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "jetbrainsKotlinJvm" }
        with(target) {
//            apply(plugin = "java-library")
            //jetbrains-kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "jetbrainsKotlinJvm" }
            apply(plugin = "org.jetbrains.kotlin.jvm")

            extensions.configure<JavaPluginExtension> {
                toolchain {
                    languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(11))
                }
                // sourceCompatibility와 targetCompatibility도 toolchain과 일치시키는 것이 좋음
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            this.setJvmTarget(JvmTarget.JVM_11)
        }
    }
}