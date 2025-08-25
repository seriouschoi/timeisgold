import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.android.lint)
}

group = "software.seriouschoi.timeisgold.buildlogic"

// Configure the build-logic plugins to target JDK 11
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.android.tools.common)
    implementation(libs.compose.gradlePlugin)


    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.truth)
    implementation(libs.kotlin.serialization.plugin.artifact)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = libs.plugins.timeisgold.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("kotlinLibrary") {
            id = libs.plugins.timeisgold.kotlin.library.get().pluginId
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("androidCompose") {
            //timeisgold-android-compose
            id = libs.plugins.timeisgold.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            //timeisgold-android-compose
            id = libs.plugins.timeisgold.android.hilt.get().pluginId
            implementationClass = "AndroidHiltConventionPlugin"
        }
    }
}
