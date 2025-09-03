import com.android.build.api.dsl.ApplicationExtension
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

/**
 * Created by jhchoi on 2025. 8. 25.
 * jhchoi
 */
@Suppress("unused")
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginAlias("android.application")

            pluginAlias("kotlin.android")
            pluginAlias("ksp")
            pluginAlias("kotlin.serialization")

            extensions.configure<ApplicationExtension> {
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


                buildTypes {
                    debug {
                        enableAndroidTestCoverage = true
                    }
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }

            setJvmTarget(TigConstJava.JvmTarget)
            setJvmToolchain(TigConstJava.JVM_TOOL_CHAIN)

            dependencies {
                implementation(target.libs, "androidx.core.ktx")
                implementation(target.libs, "androidx.appcompat")
                implementation(target.libs, "androidx.activity")
                implementation(target.libs, "material")

                testImplementation(target.libs, "junit")
                androidTestImplementation(target.libs, "androidx.junit")
                androidTestImplementation(target.libs, "androidx.espresso.core")
            }
        }
    }
}