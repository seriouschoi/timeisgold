//:core:data-room-adapter build.gradle.kts
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.timeisgold.android.library)
    alias(libs.plugins.timeisgold.android.hilt)

}

android {
    namespace = "software.seriouschoi.timeisgold.data"

}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":core:domain"))
    implementation(libs.coroutine)

    testImplementation(libs.junit)

    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.coroutine.test)
    androidTestImplementation(libs.coroutine.test)

    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotlin.test)


    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.timber)
}