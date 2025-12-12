package com.github.takahirom.preview.tests

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview as CommonPreview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter as CommonPreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider as CommonPreviewParameterProvider

@CommonPreview
@CommonPreview(
  widthDp = 400,
  heightDp = 400,
  showBackground = true,
  backgroundColor = 0xFF0000FF
)
@Composable
fun CommonPreviewNormal() {
  Text(
    color = Color.Yellow,
    text = "Multiplatform Preview is working!"
  )
}

private class CommonStringProvider: CommonPreviewParameterProvider<String> {
  override val values: Sequence<String> =
    sequenceOf("Takahiro", "Sergio")
}
@CommonPreview
@Composable
fun CommonPreviewParameter(
  @CommonPreviewParameter(CommonStringProvider::class) name: String
) {
  Text(
    color = Color.Yellow,
    text = "Multiplatform Preview with PreviewParameter is working, $name!")
}
