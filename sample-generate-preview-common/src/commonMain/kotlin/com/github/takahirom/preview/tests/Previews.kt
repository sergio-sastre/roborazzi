package com.github.takahirom.preview.tests

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@Preview
@Preview(
  widthDp = 400,
  heightDp = 400,
  showBackground = true,
  backgroundColor = 0xFF0000FF
)
@Composable
fun PreviewNormal() {
  Text(
    color = Color.Yellow,
    text = "Multiplatform Preview is working!"
  )
}

class StringProvider: PreviewParameterProvider<String> {
  override val values: Sequence<String> =
    sequenceOf("Takahiro", "Sergio")
}
@Preview
@Composable
fun PreviewParameter(
  @PreviewParameter(StringProvider::class) name: String
) {
  Text(
    color = Color.Yellow,
    text = "Multiplatform Preview with PreviewParameter is working, $name!")
}
