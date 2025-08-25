import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import software.seriouschoi.timeisgold.androidTestImplementation
import software.seriouschoi.timeisgold.debugImplementation
import software.seriouschoi.timeisgold.implementation
import software.seriouschoi.timeisgold.libs
import software.seriouschoi.timeisgold.setJvmTarget
import software.seriouschoi.timeisgold.setJvmToolchain
import software.seriouschoi.timeisgold.testImplementation

@Suppress("unused")
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            //android-library = { id = "com.android.library", version.ref = "agp" }
            apply(plugin = "com.android.library")
            //kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
            apply(plugin = "org.jetbrains.kotlin.android")


            //hilt-gradle = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
            apply(plugin = "com.google.dagger.hilt.android")
            //ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
            apply(plugin = "com.google.devtools.ksp")
            //kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                compileSdk = 36

                defaultConfig {
                    minSdk = 26
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
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

            setJvmToolchain(11)
            setJvmTarget(JvmTarget.JVM_11)

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