import androidx.compose.ui.test.*
import com.github.takahirom.roborazzi.DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH
import com.github.takahirom.roborazzi.ROBORAZZI_DEBUG
import com.github.takahirom.roborazzi.RoborazziOptions
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import sergio.sastre.composable.preview.scanner.core.annotations.RequiresShowStandardStreams
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationInfo
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationScanner

/**
 * Record:
 *    ./gradlew :sample-compose-desktop-multiplatform:recordRoborazziDesktop
 *
 * Verify:
 *    ./gradlew :sample-compose-desktop-multiplatform:verifyRoborazziDesktop
 */

class DesktopPreviewProvider : TestParameterValuesProvider() {
  @OptIn(RequiresShowStandardStreams::class)
  override fun provideValues(context: Context?): List<ComposablePreview<JvmAnnotationInfo>> =
    JvmAnnotationScanner("annotations.DesktopScreenshot")
      .enableScanningLogs()
      .scanPackageTrees("previews")
      .getPreviews()
}


fun screenshotNameFor(preview: ComposablePreview<JvmAnnotationInfo>): String =
  "$DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH/${preview.declaringClass}.${preview.methodName}.png"

@RunWith(TestParameterInjector::class)
class DesktopPreviewTest(
  @TestParameter(valuesProvider = DesktopPreviewProvider::class)
  val preview: ComposablePreview<JvmAnnotationInfo>
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