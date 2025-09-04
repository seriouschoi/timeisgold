import org.jetbrains.kotlin.gradle.dsl.JvmTarget

//:core:domain build.gradle.kts

plugins {
    alias(libs.plugins.timeisgold.kotlin.library)
}

dependencies {
    implementation(project(":core:domain-data"))
    implementation(project(":core:localtime-util"))
}

