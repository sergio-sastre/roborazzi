package androidx.compose.desktop.ui.tooling.preview

import androidx.compose.ui.test.*
import com.github.takahirom.roborazzi.DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH
import com.github.takahirom.roborazzi.ROBORAZZI_DEBUG
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
import sergio.sastre.composable.preview.scanner.core.annotations.RequiresShowStandardStreams
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

/**
 * Record: ./gradlew :sample-generate-preview-desktop:recordRoborazziDesktop --tests "androidx.compose.desktop.ui.tooling.preview.AndroidPreviewTest"
 * Verify: ./gradlew :sample-generate-preview-desktop:verifyRoborazziDesktop --tests "androidx.compose.desktop.ui.tooling.preview.AndroidPreviewTest"
 */
private class AndroidPreviewProvider : TestParameterValuesProvider() {
  @OptIn(RequiresShowStandardStreams::class)
  override fun provideValues(context: Context?): List<ComposablePreview<AndroidPreviewInfo>> =
    AndroidComposablePreviewScanner()
      .enableScanningLogs()
      .scanPackageTrees("previews")
      .getPreviews()
}


private fun screenshotNameFor(preview: ComposablePreview<AndroidPreviewInfo>): String =
  "$DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH/${preview.declaringClass}.${preview.methodName}_Android.png"

@RunWith(TestParameterInjector::class)
class AndroidPreviewTest(
  @TestParameter(valuesProvider = AndroidPreviewProvider::class)
  val preview: ComposablePreview<AndroidPreviewInfo>
) {
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun test() {
    ROBORAZZI_DEBUG = true
    runDesktopComposeUiTest {
      setContent { preview() }
      onRoot().captureRoboImage(
        filePath = screenshotNameFor(preview),
      )
    }
  }
}