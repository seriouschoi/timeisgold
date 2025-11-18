import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.timeisgold.kotlin.library)
}

dependencies {
    implementation(project(":core:domain-data"))
    implementation(project(":core:common-util"))

    implementation(libs.junit)
    implementation(libs.mockito.core)
    implementation(libs.mockito.kotlin)
    implementation(libs.kotlin.test)
    implementation(libs.coroutine.test)
}

