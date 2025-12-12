package com.github.takahirom.preview.tests

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter as AndroidPreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider as AndroidPreviewParameterProvider
import androidx.compose.ui.tooling.preview.Preview as AndroidPreview

@AndroidPreview
@AndroidPreview(
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

private class AndroidStringProvider: AndroidPreviewParameterProvider<String> {
  override val values: Sequence<String> =
    sequenceOf("Takahiro", "Sergio")
}
@AndroidPreview
@Composable
fun PreviewParameter(
  @AndroidPreviewParameter(AndroidStringProvider::class) name: String
) {
  Text(
    color = Color.Yellow,
    text = "Multiplatform Preview with PreviewParameter is working, $name!")
}
