// presentation/build.gradle.kts
plugins {
    alias(libs.plugins.timeisgold.android.library)
    alias(libs.plugins.timeisgold.android.compose)
    alias(libs.plugins.timeisgold.android.hilt)
}

android {
    namespace = "software.seriouschoi.timeisgold.presentation"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:domain-data"))
    implementation(project(":core:navigator-api"))
}