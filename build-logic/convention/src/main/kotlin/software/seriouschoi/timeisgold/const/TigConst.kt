package software.seriouschoi.timeisgold.const


/**
 * Created by jhchoi on 2025. 8. 25.
 * jhchoi
 */
internal object TigConstJava {
    const val JVM_TOOL_CHAIN = 17
    val JvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    val JavaVersion = org.gradle.api.JavaVersion.VERSION_17
}

internal object TigConstAndroidSdk {
    const val COMPILE_SDK = 36
    const val MIN_SDK = 26
}