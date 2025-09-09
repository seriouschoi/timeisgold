import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.timeisgold.kotlin.library)
}

dependencies {
    implementation(project(":core:common-util"))
}