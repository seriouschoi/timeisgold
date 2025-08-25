import org.gradle.api.Plugin
import org.gradle.api.Project
import software.seriouschoi.timeisgold.pluginAlias

/**
 * Created by jhchoi on 2025. 8. 25.
 * jhchoi
 */
@Suppress("unused")
class AndroidApplicationConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginAlias("android.application")
            pluginAlias("kotlin.android")
            pluginAlias("hilt.gradle")
            pluginAlias("ksp")
            pluginAlias("kotlin.compose")
        }
    }
}