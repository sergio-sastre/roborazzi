import androidx.compose.ui.test.*
import com.github.takahirom.roborazzi.ROBORAZZI_DEBUG
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

class MainJvmTest {
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun test() {
    ROBORAZZI_DEBUG = true
    runDesktopComposeUiTest {
      setContent {
        HelloWorldButton()
      }
      val roborazziOptions = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0F)
      )
      onRoot().captureRoboImage(roborazziOptions = roborazziOptions)

      onNodeWithTag("button").performClick()

      onRoot().captureRoboImage(roborazziOptions = roborazziOptions)
    }
  }
}
