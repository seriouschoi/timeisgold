plugins {
    alias(libs.plugins.timeisgold.android.library)
}

android {
    // your module pacakgename
    namespace = "software.seriouschoi.timeisgold.core.android.test.util"
}

dependencies {
    implementation(project(":core:domain-data"))
    implementation(project(":core:common-util"))
    implementation(project(":core:common-ui"))

}