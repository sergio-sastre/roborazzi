package com.github.takahirom.preview.tests

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.*
import com.github.takahirom.roborazzi.ComposePreviewTester.TestParameter.JUnit4TestParameter
import org.junit.rules.RuleChain
import org.junit.rules.TestWatcher
import sergio.sastre.composable.preview.scanner.common.CommonComposablePreviewScanner
import sergio.sastre.composable.preview.scanner.common.CommonPreviewInfo
import sergio.sastre.composable.preview.scanner.common.screenshotid.CommonPreviewScreenshotIdBuilder
import sergio.sastre.composable.preview.scanner.core.annotations.RequiresShowStandardStreams

/**
 * Execute ./gradlew :sample-generate-preview-tests-multiplatform:recordRoborazziDebug
 */
@OptIn(ExperimentalRoborazziApi::class)
class MultiplatformPreviewTester : ComposePreviewTester<JUnit4TestParameter<CommonPreviewInfo>> {
  override fun options(): ComposePreviewTester.Options = super.options().copy(
    testLifecycleOptions = ComposePreviewTester.Options.JUnit4TestLifecycleOptions(
      composeRuleFactory = {
        @Suppress("UNCHECKED_CAST")
        createAndroidComposeRule<RoborazziActivity>() as AndroidComposeTestRule<ActivityScenarioRule<out androidx.activity.ComponentActivity>, *>
      },
      testRuleFactory = { composeTestRule ->
        RuleChain.outerRule(
          object : TestWatcher() {
            override fun starting(description: org.junit.runner.Description?) {
              super.starting(description)
              registerRoborazziActivityToRobolectricIfNeeded()
            }
          })
          .around(composeTestRule)
      }
    )
  )

  @OptIn(RequiresShowStandardStreams::class)
  override fun testParameters(): List<JUnit4TestParameter<CommonPreviewInfo>> {
    val options = options()
    return CommonComposablePreviewScanner()
      .enableScanningLogs()
      .scanPackageTrees(*options.scanOptions.packages.toTypedArray())
      .getPreviews()
      .map {
        JUnit4TestParameter(
          (options.testLifecycleOptions as ComposePreviewTester.Options.JUnit4TestLifecycleOptions).composeRuleFactory,
          it
        )
      }
  }

  override fun test(testParameter: JUnit4TestParameter<CommonPreviewInfo>) {
    val preview = testParameter.preview
    val previewInfo = preview.previewInfo
    val screenshotName = CommonPreviewScreenshotIdBuilder(preview).build()
    val filePath = "$DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH/$screenshotName.png"
    captureRoboImage(
      filePath = filePath,
      roborazziComposeOptions = RoborazziComposeOptions {
        size(previewInfo.widthDp, previewInfo.heightDp)
        locale(previewInfo.locale)
        background(
          showBackground = previewInfo.showBackground,
          backgroundColor = previewInfo.backgroundColor
        )
      }
    ) {
      preview()
    }
  }
}