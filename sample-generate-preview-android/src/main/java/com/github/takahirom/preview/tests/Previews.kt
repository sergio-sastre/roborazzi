package com.github.takahirom.preview.tests

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.annotations.ManualClockOptions
import com.github.takahirom.roborazzi.annotations.RoboComposePreviewOptions
import kotlinx.coroutines.delay

@Preview
@Composable
fun PreviewNormal() {
  MaterialTheme {
    Surface(
    ) {
      ElevatedCard(
        Modifier
          .padding(8.dp)
          .width(180.dp),
        elevation = CardDefaults.elevatedCardElevation(
          defaultElevation = 12.dp
        )
      ) {
        Text(
          modifier = Modifier.padding(8.dp),
          text = "Generate Preview Test Sample"
        )
      }
    }
  }
}

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDarkMode() {
  val isSystemInDarkTheme = isSystemInDarkTheme()
  MaterialTheme(
    colorScheme = if (isSystemInDarkTheme) {
      darkColorScheme()
    } else {
      lightColorScheme()
    }
  ) {
    Card(
      Modifier
        .width(180.dp)
    ) {
      Text(
        modifier = Modifier.padding(8.dp),
        text = "Generate Preview Test Sample"
      )
    }
  }
}

@RoboComposePreviewOptions(
  manualClockOptions = [
    ManualClockOptions(
      advanceTimeMillis = 0L,
    ),
    ManualClockOptions(
      advanceTimeMillis = 900L,
    ),
    ManualClockOptions(
      advanceTimeMillis = 1000L,
    ),
  ]
)
@Preview
@Composable
fun PreviewDelayed() {
  var isBlue by remember { mutableStateOf(false) }
  var counter by remember { mutableStateOf(0) }

  LaunchedEffect(Unit) {
    while (true) {
      delay(100)
      counter++
      // after 1 second (1000 ms) -> blue
      if (counter == 10) {
        isBlue = true
      }
    }
  }

  Column(
    modifier = Modifier
      .size(300.dp)
      .background(if (isBlue) Color.Blue else Color.Gray)
  ) {
    Text(text = "Counter: ${counter}00 ms", color = Color.White)
    CircularProgressIndicator()
  }
}

@Preview
@Composable
fun PreviewDialog() {
  MaterialTheme {
    AlertDialog(
      onDismissRequest = {},
      confirmButton = @Composable { Text("Confirm") },
      text = @Composable { Text("Generate Preview Test Sample!") }
    )
  }
}