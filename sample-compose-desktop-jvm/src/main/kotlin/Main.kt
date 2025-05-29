
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun HelloWorldButton() {
  var text by remember { mutableStateOf("Hello, World!") }

  MaterialTheme {
    Button(
      modifier = Modifier.testTag("button"),
      onClick = {
        text = "Hello, Desktop! test"
      }) {
      Text(
        style = MaterialTheme.typography.h2,
        text = text
      )
    }
  }
}

fun main() = application {
  Window(onCloseRequest = ::exitApplication) {
    HelloWorldButton()
  }
}
