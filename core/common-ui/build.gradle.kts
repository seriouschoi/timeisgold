plugins {
    alias(libs.plugins.timeisgold.android.library)
    alias(libs.plugins.timeisgold.android.compose)
}

android {
    namespace = "software.seriouschoi.timeisgold.core.common.ui"
}

dependencies {
    // TODO: jhchoi 2025. 9. 11. move to lib.verions.toml
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("com.google.accompanist:accompanist-placeholder-material:0.36.0")
}