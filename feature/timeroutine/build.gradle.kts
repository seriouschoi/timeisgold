plugins {
    alias(libs.plugins.timeisgold.android.library)
    alias(libs.plugins.timeisgold.android.compose)
    alias(libs.plugins.timeisgold.android.hilt)
}

android {
    namespace = "software.seriouschoi.timeisgold.feature.timeroutine.bar"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:navigator-api"))
    implementation(project(":core:common-ui"))

    implementation(libs.kotlinx.serialization.json)
}