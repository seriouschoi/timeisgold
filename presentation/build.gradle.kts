// presentation/build.gradle.kts
plugins {
    alias(libs.plugins.timeisgold.android.library)
}

android {
    namespace = "software.seriouschoi.timeisgold.presentation"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:navigator-api"))
}