import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import software.seriouschoi.timeisgold.const.TigConstJava
import software.seriouschoi.timeisgold.implementation
import software.seriouschoi.timeisgold.libs
import software.seriouschoi.timeisgold.pluginAlias
import software.seriouschoi.timeisgold.setJvmTarget
import software.seriouschoi.timeisgold.setJvmToolchain
import software.seriouschoi.timeisgold.testImplementation

@Suppress("unused")
class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            apply(plugin = "java-library")
            pluginAlias("jetbrains.kotlin.jvm")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = TigConstJava.JavaVersion
                targetCompatibility = TigConstJava.JavaVersion
            }

            setJvmToolchain(TigConstJava.JVM_TOOL_CHAIN)
            setJvmTarget(TigConstJava.JvmTarget)

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