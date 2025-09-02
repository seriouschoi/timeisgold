import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import software.seriouschoi.timeisgold.androidTestImplementation
import software.seriouschoi.timeisgold.const.TigConstAndroidSdk
import software.seriouschoi.timeisgold.const.TigConstJava
import software.seriouschoi.timeisgold.implementation
import software.seriouschoi.timeisgold.libs
import software.seriouschoi.timeisgold.pluginAlias
import software.seriouschoi.timeisgold.setJvmTarget
import software.seriouschoi.timeisgold.setJvmToolchain
import software.seriouschoi.timeisgold.testImplementation

@Suppress("unused")
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginAlias("android.library")

            pluginAlias("kotlin.android")
            pluginAlias("ksp")
            pluginAlias("kotlin.serialization")

            extensions.configure<LibraryExtension> {
                compileSdk = TigConstAndroidSdk.COMPILE_SDK

                defaultConfig {
                    minSdk = TigConstAndroidSdk.MIN_SDK
                    targetSdk = TigConstAndroidSdk.COMPILE_SDK
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                compileOptions {
                    sourceCompatibility = TigConstJava.JavaVersion
                    targetCompatibility = TigConstJava.JavaVersion
                }

                lint.sarifReport = true
                lint.sarifOutput = file("build/reports/lint-results.sarif")
            }

            setJvmToolchain(TigConstJava.JVM_TOOL_CHAIN)
            setJvmTarget(TigConstJava.JvmTarget)

            dependencies {
                implementation(target.libs, "androidx.core.ktx")
                implementation(target.libs, "androidx.appcompat")
                implementation(target.libs, "material")

                testImplementation(target.libs, "junit")
                androidTestImplementation(target.libs, "androidx.junit")
                androidTestImplementation(target.libs, "androidx.espresso.core")

                testImplementation(target.libs, "kotlin.test")
                androidTestImplementation(target.libs, "kotlin.test")

                //for log.
                implementation(target.libs, "timber")

                implementation(target.libs, "gson")

            }
        }
    }


}