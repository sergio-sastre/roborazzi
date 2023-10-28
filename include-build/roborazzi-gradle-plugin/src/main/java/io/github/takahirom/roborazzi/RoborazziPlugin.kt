package io.github.takahirom.roborazzi

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.internal.cxx.logging.ThreadLoggingEnvironment
import com.github.takahirom.roborazzi.CaptureResult
import com.github.takahirom.roborazzi.CaptureResults
import com.github.takahirom.roborazzi.RoborazziReportConst
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin.VERIFICATION_GROUP
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import java.util.*
import javax.inject.Inject

private const val DEFAULT_OUTPUT_DIR = "outputs/roborazzi"
private const val DEFAULT_TEMP_DIR = "intermediates/roborazzi"

/**
 * Experimental API
 * This class can be changed without notice.
 */
open class RoborazziExtension @Inject constructor(objects: ObjectFactory) {
  val outputDir: DirectoryProperty = objects.directoryProperty()
}

@Suppress("unused")
// From Paparazzi: https://github.com/cashapp/paparazzi/blob/a76702744a7f380480f323ffda124e845f2733aa/paparazzi/paparazzi-gradle-plugin/src/main/java/app/cash/paparazzi/gradle/PaparazziPlugin.kt
class RoborazziPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create("roborazzi", RoborazziExtension::class.java)

    val verifyVariants = project.tasks.register("verifyRoborazzi")
    val compareVariants = project.tasks.register("compareRoborazzi")
    val recordVariants = project.tasks.register("recordRoborazzi")
    val verifyAndRecordVariants = project.tasks.register("verifyAndRecordRoborazzi")

    // For fixing unexpected skip test
    val outputDir =
      extension.outputDir.convention(project.layout.buildDirectory.dir(DEFAULT_OUTPUT_DIR))
    val testTaskOutputDir: DirectoryProperty = project.objects.directoryProperty()
    val intermediateDir =
      testTaskOutputDir.convention(project.layout.buildDirectory.dir(DEFAULT_TEMP_DIR))

    val restoreOutputDirRoborazziTaskProvider =
      project.tasks.register(
        "restoreOutputDirRoborazzi",
        RestoreOutputDirRoborazziTask::class.java
      ) { task ->

        task.inputDir.set(intermediateDir.map {
          if (!it.asFile.exists()) {
            it.asFile.mkdirs()
          }
          it
        })
        task.outputDir.set(outputDir)
        task.onlyIf {
          val outputDirFile = task.outputDir.asFile.get()
          val inputDirFile = task.inputDir.asFile.get()
          (outputDirFile.listFiles()?.isEmpty() ?: true)
            && (inputDirFile.listFiles()?.isNotEmpty() ?: false)
        }
      }

    fun isAnyTaskRun(
      isRecordRun: Property<Boolean>,
      isVerifyRun: Property<Boolean>,
      isVerifyAndRecordRun: Property<Boolean>,
      isCompareRun: Property<Boolean>
    ) = isRecordRun.get() || isVerifyRun.get() || isVerifyAndRecordRun.get() || isCompareRun.get()

    fun hasRoborazziTaskProperty(roborazziProperties: Map<String, Any?>): Boolean {
      return roborazziProperties["roborazzi.test.record"] == "true" || roborazziProperties["roborazzi.test.verify"] == "true" || roborazziProperties["roborazzi.test.compare"] == "true"
    }

    fun configureRoborazziTasks(variantSlug: String, testTaskName: String) {
      val testTaskOutputDirForEachVariant: DirectoryProperty = project.objects.directoryProperty()
      val intermediateDirForEachVariant =
        testTaskOutputDirForEachVariant.convention(
          project.layout.buildDirectory.dir(
            DEFAULT_TEMP_DIR
          )
        )

      //      val reportOutputDir = project.layout.buildDirectory.dir("reports/roborazzi")
      //      val snapshotOutputDir = project.layout.projectDirectory.dir("src/test/snapshots")

      val recordTaskProvider =
        project.tasks.register("recordRoborazzi$variantSlug", RoborazziTask::class.java) {
          it.group = VERIFICATION_GROUP
        }
      recordVariants.configure { it.dependsOn(recordTaskProvider) }

      val compareReportGenerateTaskProvider =
        project.tasks.register(
          "compareRoborazzi$variantSlug",
          RoborazziTask::class.java
        ) {
          it.group = VERIFICATION_GROUP
        }
      compareVariants.configure { it.dependsOn(compareReportGenerateTaskProvider) }

      val verifyTaskProvider =
        project.tasks.register("verifyRoborazzi$variantSlug", RoborazziTask::class.java) {
          it.group = VERIFICATION_GROUP
        }
      verifyVariants.configure { it.dependsOn(verifyTaskProvider) }

      val verifyAndRecordTaskProvider =
        project.tasks.register(
          "verifyAndRecordRoborazzi$variantSlug",
          RoborazziTask::class.java
        ) {
          it.group = VERIFICATION_GROUP
        }
      verifyAndRecordVariants.configure { it.dependsOn(verifyAndRecordTaskProvider) }

      val isRecordRun = project.objects.property(Boolean::class.java)
      val isVerifyRun = project.objects.property(Boolean::class.java)
      val isCompareRun = project.objects.property(Boolean::class.java)
      val isVerifyAndRecordRun = project.objects.property(Boolean::class.java)

      project.gradle.taskGraph.whenReady { graph ->
        isRecordRun.set(recordTaskProvider.map { graph.hasTask(it) })
        isVerifyRun.set(verifyTaskProvider.map { graph.hasTask(it) })
        isVerifyAndRecordRun.set(verifyAndRecordTaskProvider.map { graph.hasTask(it) })
        isCompareRun.set(compareReportGenerateTaskProvider.map { graph.hasTask(it) })
      }

      val testTaskProvider = project.tasks.withType(Test::class.java)
        .matching {
          it.name == testTaskName
        }
      testTaskProvider
        .configureEach { test ->
          val roborazziProperties =
            project.properties.filterKeys { it != "roborazzi" && it.startsWith("roborazzi") }
          val resultsDir = project.file(RoborazziReportConst.resultDirPath)
          val resultDirFileTree =
            project.fileTree(RoborazziReportConst.resultDirPath)
          val resultsSummaryFile =
            project.file(RoborazziReportConst.resultsSummaryFilePath)
          val reportFile =
            project.file(RoborazziReportConst.reportFilePath)
          if (restoreOutputDirRoborazziTaskProvider.isPresent) {
            test.inputs.files(restoreOutputDirRoborazziTaskProvider.map {
              if (!it.outputDir.get().asFile.exists()) {
                it.outputDir.get().asFile.mkdirs()
              }
              it.outputDir
            })
          } else {
            test.inputs.dir(outputDir.map {
              if (!it.asFile.exists()) {
                it.asFile.mkdirs()
              }
              it
            })
          }
          test.outputs.dir(intermediateDirForEachVariant)

          test.inputs.properties(
            mapOf(
              "isRecordRun" to isRecordRun,
              "isVerifyRun" to isVerifyRun,
              "isCompareRun" to isCompareRun,
              "isVerifyAndRecordRun" to isVerifyAndRecordRun,
              "roborazziProperties" to roborazziProperties,
            )
          )
          test.outputs.doNotCacheIf("Run Roborazzi tests if roborazzi output dir is empty") {
            (isAnyTaskRun(isRecordRun, isVerifyRun, isVerifyAndRecordRun, isCompareRun)
              || hasRoborazziTaskProperty(roborazziProperties))
              && outputDir.get().asFile.listFiles()?.isEmpty() ?: true
          }
          test.doFirst {
            val isTaskPresent =
              isAnyTaskRun(isRecordRun, isVerifyRun, isVerifyAndRecordRun, isCompareRun)
            if (!isTaskPresent) {
              test.systemProperties.putAll(roborazziProperties)
            } else {
              // Apply other roborazzi properties except for the ones that
              // start with "roborazzi.test"
              test.systemProperties.putAll(
                roborazziProperties.filter { (key, _) ->
                  !key.startsWith("roborazzi.test")
                }
              )
              test.systemProperties["roborazzi.test.record"] =
                isRecordRun.get() || isVerifyAndRecordRun.get()
              test.systemProperties["roborazzi.test.compare"] = isCompareRun.get()
              test.systemProperties["roborazzi.test.verify"] =
                isVerifyRun.get() || isVerifyAndRecordRun.get()
            }
            val isRoborazziRun =
              (isAnyTaskRun(isRecordRun, isVerifyRun, isVerifyAndRecordRun, isCompareRun)
                || hasRoborazziTaskProperty(roborazziProperties))
            if (isRoborazziRun) {
              resultsDir.deleteRecursively()
              resultsDir.mkdirs()
            }
          }
          // We don't use custom task action here because we want to run it even if we use `-P` parameter
          test.doLast {
            val isRoborazziRun =
              (isAnyTaskRun(isRecordRun, isVerifyRun, isVerifyAndRecordRun, isCompareRun)
                || hasRoborazziTaskProperty(roborazziProperties))
            if (!isRoborazziRun) {
              return@doLast
            }
            // Copy all files from outputDir to intermediateDir
            // so that we can use Gradle's output caching
            infoln("Copy files from ${outputDir.get()} to ${intermediateDir.get()}")
            // outputDir.get().asFileTree.forEach {
            //   println("Copy file ${it.absolutePath} to ${intermediateDir.get()}")
            // }
            outputDir.get().asFile.mkdirs()
            outputDir.get().asFile.copyRecursively(
              target = intermediateDir.get().asFile,
              overwrite = true
            )

            val results: List<CaptureResult> = resultDirFileTree.mapNotNull {
              if (it.name.endsWith(".json")) {
                CaptureResult.fromJsonFile(it.path)
              } else {
                null
              }
            }
            infoln("Save result to ${resultsSummaryFile.absolutePath} with results:${results.size}")

            val roborazziResult = CaptureResults.from(results)

            val jsonResult = roborazziResult.toJson()
            resultsSummaryFile.parentFile.mkdirs()
            resultsSummaryFile.writeText(jsonResult.toString())
            reportFile.parentFile.mkdirs()
            reportFile.writeText(
              RoborazziReportConst.reportHtml.replace(
                oldValue = "REPORT_TEMPLATE_BODY",
                newValue = roborazziResult.toHtml(reportFile.parentFile.absolutePath)
              )
            )
          }
        }

      recordTaskProvider.configure { it.dependsOn(testTaskProvider) }
      compareReportGenerateTaskProvider.configure { it.dependsOn(testTaskProvider) }
      verifyTaskProvider.configure { it.dependsOn(testTaskProvider) }
      verifyAndRecordTaskProvider.configure { it.dependsOn(testTaskProvider) }
    }

    fun AndroidComponentsExtension<*, *, *>.configureComponents() {
      onVariants { variant ->
        val unitTest = variant.unitTest ?: return@onVariants
        val variantSlug = variant.name.capitalizeUS()
        val testVariantSlug = unitTest.name.capitalizeUS()

        // e.g. testDebugUnitTest -> recordRoborazziDebug
        configureRoborazziTasks(variantSlug, "test$testVariantSlug")
      }
    }

    project.pluginManager.withPlugin("com.android.application") {
      project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
        .configureComponents()
    }
    project.pluginManager.withPlugin("com.android.library") {
      project.extensions.getByType(LibraryAndroidComponentsExtension::class.java)
        .configureComponents()
    }
    project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
      // e.g. test -> recordRoborazziJvm
      configureRoborazziTasks("Jvm", "test")
    }
    project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
      val kotlinMppExtension = checkNotNull(
        project.extensions.findByType(
          KotlinMultiplatformExtension::class.java
        )
      ) { "Kotlin multiplatform plugin not applied!" }
      kotlinMppExtension.targets.all { target ->
        if (target is KotlinJvmTarget) {
          target.testRuns.all { testRun ->
            // e.g. desktopTest -> recordRoborazziDesktop
            configureRoborazziTasks(target.name.capitalizeUS(), testRun.executionTask.name)
          }
        }
      }
    }
  }

  private fun String.capitalizeUS() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }

  abstract class RestoreOutputDirRoborazziTask @Inject constructor(objects: ObjectFactory) :
    DefaultTask() {
    @get:InputDirectory
    @Optional
    val inputDir: DirectoryProperty = objects.directoryProperty()

    @get:OutputDirectory
    val outputDir: DirectoryProperty = objects.directoryProperty()

    @TaskAction
    fun copy() {
      val outputDirFile = outputDir.get().asFile
      if (outputDirFile.exists() && outputDirFile.listFiles().isNotEmpty()) return
      inputDir.get().asFile.copyRecursively(outputDirFile)
    }
  }

  open class RoborazziTask : DefaultTask() {
    @Option(
      option = "tests",
      description = "Sets test class or method name to be included, '*' is supported."
    )
    open fun setTestNameIncludePatterns(testNamePattern: List<String>): RoborazziTask {
      project.tasks.withType(Test::class.java).configureEach {
        it.setTestNameIncludePatterns(testNamePattern)
      }
      return this
    }
  }
}

fun infoln(format: String) =
  ThreadLoggingEnvironment.reportFormattedInfoToCurrentLogger(format)
