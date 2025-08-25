import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import software.seriouschoi.timeisgold.implementation
import software.seriouschoi.timeisgold.libs
import software.seriouschoi.timeisgold.setJvmTarget
import software.seriouschoi.timeisgold.setJvmToolchain
import software.seriouschoi.timeisgold.testImplementation

@Suppress("unused")
class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            apply(plugin = "java-library")

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

            setJvmToolchain(11)
            setJvmTarget(JvmTarget.JVM_11)

            dependencies {
                testImplementation(target.libs, "junit")
                testImplementation(target.libs, "coroutine.test")
                testImplementation(target.libs, "mockito.core")
                testImplementation(target.libs, "mockito.kotlin")

                implementation(target.libs, "javax.inject")
            }
        }
    }
}