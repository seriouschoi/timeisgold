package software.seriouschoi.timeisgold.const


/**
 * Created by jhchoi on 2025. 8. 25.
 * jhchoi
 */
internal object TigConstJava {
    const val JVM_TOOL_CHAIN = 11
    val JvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    val JavaVersion = org.gradle.api.JavaVersion.VERSION_11
}

internal object TigConstAndroidSdk {
    const val COMPILE_SDK = 36
    const val MIN_SDK = 26
}