import org.jetbrains.kotlin.gradle.dsl.JvmTarget

//:core:domain build.gradle.kts

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
//    alias(libs.plugins.timeisgold.kotlin.library)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    implementation(libs.javax.inject)
}
