//:core:data-room-adapter build.gradle.kts

plugins {
    alias(libs.plugins.timeisgold.android.library)
    alias(libs.plugins.timeisgold.android.hilt)
    alias(libs.plugins.timeisgold.coroutine)
}

android {
    namespace = "software.seriouschoi.timeisgold.data"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}


dependencies {
    implementation(project(":core:domain"))

    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotlin.test)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.timber)
}