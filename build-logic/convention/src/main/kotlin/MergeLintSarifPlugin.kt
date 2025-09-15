import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Created by jhchoi on 2025. 9. 3.
 * jhchoi
 */
@Suppress("Unused")
class MergeLintPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 루트 프로젝트에만 task 등록
        if (project != project.rootProject) return

        project.tasks.register("mergeLint") {
            mergeHtmlsToSingleDir()
        }

    }

    private fun Task.mergeHtmlsToSingleDir() {
        group = "verification"
        description = "Collect all lint reports from subprojects into one directory"

        val outputDir = project.layout.buildDirectory.dir("reports/merged-lint")
        outputs.dir(outputDir)

        doLast {
            println("mergeHtmlsToSingleDir")
            val outDir = outputDir.get().asFile
            outDir.takeIf { it.exists() }?.deleteRecursively()
            outDir.mkdirs()
            println("outDir.exists(): ${outDir.exists()}")

            project.rootProject.allprojects.forEach { sub ->
                val sourceFile =
                    sub.layout.buildDirectory.file("reports/lint-results-debug.html")
                        .get().asFile
                println("${sourceFile.path}: exists=${sourceFile.exists()}")
                if (sourceFile.exists()) {
                    val target = outDir.resolve("${sub.name}-lint-results-debug.html")
                    println("copy from ${sourceFile.path} to ${target.path}")
                    Files.copy(
                        sourceFile.toPath(),
                        target.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                    println("Collected Html from ${sub.path}")
                }
            }
        }
    }
}