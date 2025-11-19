import java.util.Properties

plugins {
    alias(libs.plugins.timeisgold.android.application)
    alias(libs.plugins.timeisgold.android.compose)
    alias(libs.plugins.timeisgold.android.hilt)
    id("io.github.irgaly.remove-unused-resources") version "2.3.0"
}

android {
    namespace = "software.seriouschoi.timeisgold"
    defaultConfig {
        applicationId = "software.seriouschoi.timeisgold"
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        getByName("debug") {
            val props = Properties()
            file("${rootDir}/keystore/keystore.properties").inputStream().use { props.load(it) }

            storeFile = file(props.getProperty("DEBUG_STORE_FILE") as String)
            storePassword = props.getProperty("DEBUG_STORE_PASSWORD") as String
            keyAlias = props.getProperty("DEBUG_KEY_ALIAS") as String
            keyPassword = props.getProperty("DEBUG_KEY_PASSWORD") as String
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    lint {
        checkGeneratedSources = true
        checkDependencies = true
    }
}

dependencies {
    implementation(project(":feature:timeroutine"))
    implementation(project(":core:domain"))
    implementation(project(":core:domain-data"))
    implementation(project(":core:data-room-adapter"))
    implementation(project(":core:navigator-api"))
    implementation(project(":core:navigator-adapter"))
    implementation(project(":core:common-ui"))
    implementation(project(":core:common-util"))
}