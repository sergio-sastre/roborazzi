package previews

import androidx.compose.desktop.ui.tooling.preview.Preview as DesktopPreview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview as AndroidPreview
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import annotations.DesktopScreenshot

@Composable
@DesktopScreenshot
@DesktopPreview
@AndroidPreview
fun HelloWorldButton() {
  MaterialTheme {
    Button(
      modifier = Modifier.testTag("button"),
      onClick = {
        // Nothing
      }) {
      Text(
        style = MaterialTheme.typography.h2,
        text = "Hello, World!"
      )
    }
  }
}

@Composable
@DesktopScreenshot
@DesktopPreview
@AndroidPreview
fun HelloDesktopButton() {
  MaterialTheme {
    Button(
      modifier = Modifier.testTag("button"),
      onClick = {
        // Nothing
      }) {
      Text(
        style = MaterialTheme.typography.h2,
        text = "Hello, Desktop!"
      )
    }
  }
}

fun main() = application {
  Window(onCloseRequest = ::exitApplication) {
    HelloWorldButton()
  }
}
