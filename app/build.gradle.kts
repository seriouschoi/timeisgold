import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.timeisgold.android.application)
    alias(libs.plugins.timeisgold.android.compose)
    alias(libs.plugins.timeisgold.android.hilt)
}

android {
    namespace = "software.seriouschoi.timeisgold"
    defaultConfig {
        applicationId = "software.seriouschoi.timeisgold"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":feature:timeroutine"))
    implementation(project(":core:domain"))
    implementation(project(":core:data-room-adapter"))
    implementation(project(":core:navigator-api"))
    implementation(project(":core:navigator-adapter"))
}