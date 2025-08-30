package com.github.takahirom.preview.tests

import com.github.takahirom.roborazzi.DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziComposeOptions
import com.github.takahirom.roborazzi.background
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.locale
import com.github.takahirom.roborazzi.size
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import sergio.sastre.composable.preview.scanner.common.CommonComposablePreviewScanner
import sergio.sastre.composable.preview.scanner.common.CommonPreviewInfo
import sergio.sastre.composable.preview.scanner.common.screenshotid.CommonPreviewScreenshotIdBuilder
import sergio.sastre.composable.preview.scanner.core.annotations.RequiresShowStandardStreams
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

@RunWith(ParameterizedRobolectricTestRunner::class)
class MultiplatformPreviewTests(
  private val preview: ComposablePreview<CommonPreviewInfo>,
) {
  companion object {
    @OptIn(RequiresShowStandardStreams::class)
    private val cachedBuildTimePreviews: List<ComposablePreview<CommonPreviewInfo>> by lazy {
      CommonComposablePreviewScanner()
        .enableScanningLogs()
        .scanPackageTrees("com.github.takahirom.preview.tests")
        .includePrivatePreviews()
        .getPreviews()
    }

    @JvmStatic
    @ParameterizedRobolectricTestRunner.Parameters
    fun values(): List<ComposablePreview<CommonPreviewInfo>> = cachedBuildTimePreviews
  }

  fun screenshotNameFor(preview: ComposablePreview<CommonPreviewInfo>): String =
    "$DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH/${
      CommonPreviewScreenshotIdBuilder(preview).build()
    }.png"

  @OptIn(ExperimentalRoborazziApi::class)
  @GraphicsMode(GraphicsMode.Mode.NATIVE)
  @Config(sdk = [30])
  @Test
  fun snapshot() {
    captureRoboImage(
      filePath = screenshotNameFor(preview),
      roborazziComposeOptions = RoborazziComposeOptions {
        val previewInfo = preview.previewInfo
        size(
          widthDp = previewInfo.widthDp,
          heightDp = previewInfo.heightDp
        )
        background(
          showBackground = previewInfo.showBackground,
          backgroundColor = previewInfo.backgroundColor
        )
        locale(previewInfo.locale)
      },
    ) {
      preview()
    }
  }
}