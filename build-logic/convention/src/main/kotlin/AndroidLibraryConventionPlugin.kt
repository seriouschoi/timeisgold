import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class AndroidLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            //android-library = { id = "com.android.library", version.ref = "agp" }
            apply(plugin = "com.android.library")
            //kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
            apply(plugin = "org.jetbrains.kotlin.android")
            //kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            //hilt-gradle = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
            apply(plugin = "com.google.dagger.hilt.android")
            //ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
            apply(plugin = "com.google.devtools.ksp")
            //kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
//            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
        }
    }
}