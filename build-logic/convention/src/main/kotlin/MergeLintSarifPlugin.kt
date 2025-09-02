import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Created by jhchoi on 2025. 9. 3.
 * jhchoi
 */
@Suppress("Unused")
class MergeLintSarifPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 루트 프로젝트에만 task 등록
        if (project != project.rootProject) return

        project.tasks.register("mergeLintSarif") {
            mergeHtmlsToSingleDir()
        }
    }

    private fun Task.mergeToSingleDir() {
        group = "verification"
        description = "Collect all SARIF reports from subprojects into one directory"

        val outputDir = project.layout.buildDirectory.dir("reports/merged-lint")
        outputs.dir(outputDir)

        doLast {
            val outDir = outputDir.get().asFile
            outDir.mkdirs()

            project.rootProject.allprojects.forEach { sub ->
                val sarifFile =
                    sub.layout.buildDirectory.file("reports/lint-results-debug.sarif")
                        .get().asFile
                if (sarifFile.exists()) {
                    val target = outDir.resolve("${sub.name}-lint-results-debug.sarif")
                    Files.copy(
                        sarifFile.toPath(),
                        target.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                    println("Collected SARIF from ${sub.path}")
                }
            }
        }
    }

    private fun Task.mergeHtmlsToSingleDir() {
        group = "verification"
        description = "Collect all SARIF reports from subprojects into one directory"

        val outputDir = project.layout.buildDirectory.dir("reports/merged-lint")
        outputs.dir(outputDir)

        doLast {
            val outDir = outputDir.get().asFile
            outDir.mkdirs()

            project.rootProject.allprojects.forEach { sub ->
                val sarifFile =
                    sub.layout.buildDirectory.file("reports/lint-results-debug.html")
                        .get().asFile
                if (sarifFile.exists()) {
                    val target = outDir.resolve("${sub.name}-lint-results-debug.html")
                    Files.copy(
                        sarifFile.toPath(),
                        target.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                    println("Collected SARIF from ${sub.path}")
                }
            }
        }
    }

    private fun Task.mergeToSingleSarif2() {
        group = "verification"
        description = "Merge all Android Lint SARIF reports into a single SARIF file with one run"

        val outputDir = project.layout.buildDirectory.dir("reports/merged-lint")
        val outputFile = outputDir.get().file("lint-merged.sarif").asFile

        outputs.file(outputFile)

        doLast {
            outputFile.parentFile.mkdirs()

            val gson = GsonBuilder().setPrettyPrinting().create()
            var baseRun: JsonObject? = null

            // 모든 서브 프로젝트 순회
            project.rootProject.allprojects.forEach { sub ->
                val sarifFile = File(sub.buildDir, "reports/lint-results-debug.sarif")
                if (sarifFile.exists()) {
                    println("Merging SARIF from ${sub.path}")
                    val root = gson.fromJson(sarifFile.readText(), JsonObject::class.java)
                    val run = root["runs"].asJsonArray[0].asJsonObject

                    if (baseRun == null) {
                        baseRun = run.deepCopy()
                        baseRun!!.getAsJsonArray("results").removeAll { true }
                    }

                    val results = run.getAsJsonArray("results")
                    results.forEach { result ->
                        baseRun!!.getAsJsonArray("results").add(result.deepCopy())
                    }
                }
            }

            if (baseRun != null) {
                val mergedSarif = JsonObject().apply {
                    addProperty("version", "2.1.0")
                    add("runs", JsonArray().apply { add(baseRun) })
                }
                outputFile.writeText(gson.toJson(mergedSarif))
                println("✅ Merged SARIF written to: ${outputFile.absolutePath}")
            } else {
                println("⚠️ No SARIF files found to merge")
            }
        }
    }
    private fun Task.mergeToSingleSarif() {
        group = "verification"
        description = "Merge all SARIF reports into a single file"

        val outputFile = project.layout.buildDirectory.file("reports/merged-lint/lint-merged.sarif")
        outputs.file(outputFile)

        doLast {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val mergedRuns = JsonArray()

            project.rootProject.allprojects.forEach { sub ->
                val sarifFile =
                    sub.layout.buildDirectory.file("reports/lint-results-debug.sarif").get().asFile
                if (sarifFile.exists()) {
                    val root = JsonParser.parseReader(sarifFile.reader()).asJsonObject
                    val runs = root.getAsJsonArray("runs")
                    if (runs != null) {
                        mergedRuns.addAll(runs)
                        println("Merged SARIF from ${sub.path}")
                    }
                }
            }

            val mergedRoot = JsonObject().apply {
                //sarif version.
                addProperty("version", "2.1.0")
                add("runs", mergedRuns)
            }

            val outFile = outputFile.get().asFile
            outFile.parentFile.mkdirs()
            outFile.writeText(gson.toJson(mergedRoot))
            println("Merged SARIF written to ${outFile.absolutePath}")
        }
    }
}