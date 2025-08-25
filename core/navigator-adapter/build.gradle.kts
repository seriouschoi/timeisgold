plugins {
    alias(libs.plugins.timeisgold.android.library)
    alias(libs.plugins.timeisgold.android.hilt)
    alias(libs.plugins.timeisgold.android.compose)
}

android {
    namespace = "software.seriouschoi.navigator"
}

dependencies {
    implementation(project(":core:navigator-api"))
}