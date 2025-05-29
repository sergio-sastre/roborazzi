package com.github.takahirom.preview.tests

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class StringProvider: PreviewParameterProvider<String> {
  override val values: Sequence<String> = sequenceOf("Takahiro", "Sergio")
}

@Preview
@Composable
fun PreviewNormal() {
  Text(
    color = MaterialTheme.colorScheme.error,
    text = "Multiplatform Preview is working!"
  )
}


@Preview
@Composable
fun PreviewParameter(
  @PreviewParameter(StringProvider::class) name: String
) {
  Text(
    color = MaterialTheme.colorScheme.error,
    text ="Multiplatform Preview with PreviewParameter is working, $name!")
}
