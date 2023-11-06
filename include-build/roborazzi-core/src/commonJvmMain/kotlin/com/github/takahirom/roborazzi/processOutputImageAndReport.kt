package com.github.takahirom.roborazzi

import com.dropbox.differ.ImageComparator
import java.io.File

fun interface EmptyCanvasFactory {
  operator fun invoke(
    width: Int,
    height: Int,
    filled: Boolean,
    bufferedImageType: Int
  ): RoboCanvas
}

fun interface CanvasFactoryFromFile {
  operator fun invoke(
    file: File,
    bufferedImageType: Int
  ): RoboCanvas
}

fun interface ComparisonCanvasFactory {
  operator fun invoke(
    goldenRoboCanvas: RoboCanvas,
    newRoboImage: RoboCanvas,
    resizeScale: Double,
    bufferedImageType: Int
  ): RoboCanvas
}

@InternalRoborazziApi
fun processOutputImageAndReport(
  newRoboCanvas: RoboCanvas,
  goldenFile: File,
  roborazziOptions: RoborazziOptions,
  emptyCanvasFactory: EmptyCanvasFactory,
  canvasFactoryFromFile: CanvasFactoryFromFile,
  comparisonCanvasFactory: ComparisonCanvasFactory,
) {
  debugLog {
    "processOutputImageAndReport(): " +
      "goldenFile:${goldenFile.absolutePath}"
  }
  val forbiddenFileSuffixes = listOf("_compare", "_actual")
  forbiddenFileSuffixes.forEach {
    if (goldenFile.nameWithoutExtension.endsWith(it)) {
      throw IllegalArgumentException("The file name should not end with $it because it is reserved for Roborazzi")
    }
  }
  val recordOptions = roborazziOptions.recordOptions
  val resizeScale = recordOptions.resizeScale
  if (roborazziCompareEnabled() || roborazziVerifyEnabled()) {
    val width = (newRoboCanvas.croppedWidth * resizeScale).toInt()
    val height = (newRoboCanvas.croppedHeight * resizeScale).toInt()
    val goldenRoboCanvas = if (goldenFile.exists()) {
      canvasFactoryFromFile(goldenFile, recordOptions.pixelBitConfig.toBufferedImageType())
    } else {
      emptyCanvasFactory(
        width = width,
        height = height,
        filled = true,
        bufferedImageType = recordOptions.pixelBitConfig.toBufferedImageType()
      )
    }
    val changed = if (height == goldenRoboCanvas.height && width == goldenRoboCanvas.width) {
      val comparisonResult: ImageComparator.ComparisonResult =
        newRoboCanvas.differ(
          other = goldenRoboCanvas,
          resizeScale = resizeScale,
          imageComparator = roborazziOptions.compareOptions.imageComparator
        )
      val changed = !roborazziOptions.compareOptions.resultValidator(comparisonResult)
      log("${goldenFile.name} The differ result :$comparisonResult changed:$changed")
      changed
    } else {
      log("${goldenFile.name} The image size is changed. actual = (${goldenRoboCanvas.width}, ${goldenRoboCanvas.height}), golden = (${newRoboCanvas.croppedWidth}, ${newRoboCanvas.croppedHeight})")
      true
    }

    val result: CaptureResult = if (changed) {
      val comparisonFile = File(
        roborazziOptions.compareOptions.outputDirectoryPath,
        goldenFile.nameWithoutExtension + "_compare." + goldenFile.extension
      )
      val comparisonCanvas = comparisonCanvasFactory(
        goldenRoboCanvas = goldenRoboCanvas,
        newRoboImage = newRoboCanvas,
        resizeScale = resizeScale,
        bufferedImageType = recordOptions.pixelBitConfig.toBufferedImageType()
      )
      comparisonCanvas
        .save(
          file = comparisonFile,
          resizeScale = resizeScale
        )
      debugLog {
        "processOutputImageAndReport(): compareCanvas is saved " +
          "compareFile:${comparisonFile.absolutePath}"
      }
      comparisonCanvas.release()

      val actualFile = if (roborazziRecordingEnabled()) {
        // If record option is enabled, we should save the actual file as the golden file.
        goldenFile
      } else {
        File(
          roborazziOptions.compareOptions.outputDirectoryPath,
          goldenFile.nameWithoutExtension + "_actual." + goldenFile.extension
        )
      }
      newRoboCanvas
        .save(
          file = actualFile,
          resizeScale = resizeScale
        )
      debugLog {
        "processOutputImageAndReport(): actualCanvas is saved " +
          "actualFile:${actualFile.absolutePath}"
      }
      if (goldenFile.exists()) {
        CaptureResult.Changed(
          compareFile = comparisonFile,
          actualFile = actualFile,
          goldenFile = goldenFile,
          timestampNs = System.nanoTime(),
        )
      } else {
        CaptureResult.Added(
          compareFile = comparisonFile,
          actualFile = actualFile,
          goldenFile = goldenFile,
          timestampNs = System.nanoTime(),
        )
      }
    } else {
      CaptureResult.Unchanged(
        goldenFile = goldenFile,
        timestampNs = System.nanoTime(),
      )
    }
    debugLog {
      "processOutputImageAndReport: \n" +
        "  goldenFile: $goldenFile\n" +
        "  changed: $changed\n" +
        "  result: $result\n"
    }
    roborazziOptions.reportOptions.captureResultReporter.report(result)
  } else {
    // roborazzi.record is checked before
    newRoboCanvas.save(goldenFile, resizeScale)
    debugLog {
      "processOutputImageAndReport: \n" +
        " record goldenFile: $goldenFile\n"
    }
    roborazziOptions.reportOptions.captureResultReporter.report(
      CaptureResult.Recorded(
        goldenFile = goldenFile,
        timestampNs = System.nanoTime()
      )
    )
  }
}

private fun log(message: String) {
  println("Roborazzi: $message")
}
