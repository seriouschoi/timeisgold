import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import software.seriouschoi.timeisgold.androidTestImplementation
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
            pluginAlias("hilt.gradle")
            pluginAlias("ksp")
            pluginAlias("kotlin.serialization")

            extensions.configure<LibraryExtension> {
                compileSdk = 36

                defaultConfig {
                    minSdk = 26
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                compileOptions {
                    sourceCompatibility = TigConstJava.JavaVersion
                    targetCompatibility = TigConstJava.JavaVersion
                }


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

            setJvmToolchain(TigConstJava.JVM_TOOL_CHAIN)
            setJvmTarget(TigConstJava.JvmTarget)

            dependencies {
                implementation(target.libs, "androidx.core.ktx")
                implementation(target.libs, "androidx.appcompat")
                implementation(target.libs, "material")

                testImplementation(target.libs, "junit")
                androidTestImplementation(target.libs, "androidx.junit")
                androidTestImplementation(target.libs, "androidx.espresso.core")
            }
        }
    }


}