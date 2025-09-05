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
    implementation(project(":core:domain-data"))
    implementation(project(":core:navigator-api"))
    implementation(project(":core:common-ui"))
    testImplementation(project(":core:test-util"))
    androidTestImplementation(project(":core:test-util"))
    androidTestImplementation(project(":core:android-test-util"))
}