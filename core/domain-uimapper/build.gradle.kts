plugins {
    alias(libs.plugins.timeisgold.android.library)
}

android {
    // your module pacakgename
    namespace = "software.seriouschoi.timeisgold.core.domain.mapper"
}

dependencies {
    implementation(project(":core:domain-data"))
    implementation(project(":core:common-ui"))
}