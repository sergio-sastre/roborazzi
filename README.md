# Roborazzi

**Make JVM Android Integration Test Visible**

## Roborazzi now supports [Robolectric Native Graphics (RNG)](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.10) and enables screenshot testing.📣

## Why Choose Roborazzi?

### Why is screenshot testing important?

Screenshot testing is key to validate your app's appearance and functionality. It efficiently
detects visual issues and tests the app as users would use it, making it easier to spot problems.
It's quicker than writing many assert statements, ensuring your app looks right and behaves
correctly.

### What are JVM tests and why test with JVM instead of on Android?

JVM tests, also known as local tests, are placed in the test/ directory and are run on a developer's
PC or CI environment. On the other hand, device tests, also known as Instrumentation tests, are
written in the androidTest/ directory and are run on real devices or emulators. Device testing can
result in frequent failures due to the device environment, leading to false negatives. These
failures are often hard to reproduce, making them tough to resolve.

### Paparazzi and Roborazzi: A Comparison

Paparazzi is a great tool for visualizing displays within the JVM. However, it's incompatible with
Robolectric, which also mocks the Android framework.

Roborazzi fills this gap. It integrates with Robolectric, allowing tests to run with Hilt and
interact with components. Essentially, Roborazzi enhances Paparazzi's capabilities, providing a more
efficient and reliable testing process by capturing screenshots with Robolectric.

**Leveraging Roborazzi in Test Architecture: An Example**

<img src="https://github.com/takahirom/roborazzi/assets/1386930/937a96a4-f637-4029-87e1-c1bb94abc8ae" width="320" />


**Integrating Roborazzi into the Architecture: An Example from DroidKaigi 2023 App**

In the DroidKaigi 2023 app, Roborazzi was introduced from the early stages of development as part of the architectural design. This integration allowed the team to verify changes throughout the development process. The specific architectural decisions and how they were implemented can be found [README](https://github.com/DroidKaigi/conference-app-2023#screenshot-testing-with-robolectric-native-graphics-rng-and-roborazzi).

## Try it out

Available on Maven Central.

### Add Robolectric

This library is dependent on Robolectric. Please see below to add Robolectric.

https://robolectric.org/getting-started/

To take screenshots, please use Robolectric 4.10 alpha 1 or later and please
add `@GraphicsMode(GraphicsMode.Mode.NATIVE)` to your test class.

```kotlin
@GraphicsMode(GraphicsMode.Mode.NATIVE)
```

### Apply Roborazzi Gradle Plugin

Roborazzi is available on maven central.

This plugin simply creates Gradle tasks record, verify, compare and passes the configuration to the
test.

<table>
<tr><td>plugins</td><td>buildscript</td></tr>
<tr><td>

Define plugin in root build.gradle

```groovy
plugins {
  ...
  id "io.github.takahirom.roborazzi" version "[version]" apply false
}
```

Apply plugin in module build.gradle

```groovy
plugins {
  ...
  id 'io.github.takahirom.roborazzi'
}
```

</td><td>

root build.gradle

```groovy
buildscript {
  dependencies {
    ...
    classpath "io.github.takahirom.roborazzi:roborazzi-gradle-plugin:[version]"
  }
}
```

module build.gradle

```groovy
apply plugin: "io.github.takahirom.roborazzi"
```

</td></tr>

</table>


<table>
<tr>
<td> Use Roborazzi task </td> <td> Use default unit test task </td> <td> Description </td>
</tr>
<tr>
<td>

`./gradlew recordRoborazziDebug`


</td><td> 

`./gradlew testDebugUnitTest` after adding `roborazzi.test.record=true` to your gradle.properties file.

or

`./gradlew testDebugUnitTest -Proborazzi.test.record=true`


</td><td> 

Record a screenshot  
Default output directory is `build/outputs/roborazzi`  
You can check a report under `build/reports/roborazzi/index.html`

</td>
</tr>
<tr>
<td>

`./gradlew compareRoborazziDebug`

</td><td> 


`./gradlew testDebugUnitTest` after adding `roborazzi.test.compare=true` to your gradle.properties file.

or

`./gradlew testDebugUnitTest -Proborazzi.test.compare=true`

</td><td>

Review changes made to an image. This action will
compare the current image with the saved one, generating a comparison image labeled
as `[original]_compare.png`. It also produces a JSON file containing the diff information, which can
be found under `build/test-results/roborazzi`.

</td>
</tr>
<tr>
<td>

`./gradlew verifyRoborazziDebug`

</td><td> 


`./gradlew testDebugUnitTest` after adding `roborazzi.test.verify=true` to your gradle.properties file.

or

`./gradlew testDebugUnitTest -Proborazzi.test.verify=true`

</td><td>

Validate changes made to an image. If there is any difference between the current image and the
saved one, the test will fail.

</td>
</tr>
<tr>
<td>

`./gradlew verifyAndRecordRoborazziDebug`

</td><td> 


`./gradlew testDebugUnitTest` after adding  `roborazzi.test.verify=true` and `roborazzi.test.record=true` to your gradle.properties file.

or

`./gradlew testDebugUnitTest -Proborazzi.test.verify=true -Proborazzi.test.record=true`

</td><td>

This task will first verify the images and, if differences are detected, it will record a new
baseline.

</td>
</tr>

</table>

The comparison image, saved as `[original]_compare.png`, is shown below:

![image](https://github.com/takahirom/roborazzi/assets/1386930/579199d5-8e17-4f51-b990-de603ca36251)

This
uses [JetNew from Compose Samples](https://github.com/android/compose-samples/tree/main/JetNews).
You can check the pull request introducing Roborazzi to the
compose-samples [here](https://github.com/takahirom/compose-samples/pull/1/files).

### Add dependencies

| Description     | Dependencies                                                                         |
|-----------------|--------------------------------------------------------------------------------------|
| Core functions  | `testImplementation("io.github.takahirom.roborazzi:roborazzi:[version]")`            |
| Jetpack Compose | `testImplementation("io.github.takahirom.roborazzi:roborazzi-compose:[version]")`    |
| JUnit rules     | `testImplementation("io.github.takahirom.roborazzi:roborazzi-junit-rule:[version]")` |

## How to use

### Take a screenshot manually

You can take a screenshot by calling captureRoboImage().

app/src/test/java/../ManualTest.kt

```kotlin
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

// All you need to do is use the captureRoboImage function in the test!
import com.github.takahirom.roborazzi.captureRoboImage


// Tips: You can use Robolectric while using AndroidJUnit4
@RunWith(AndroidJUnit4::class)
// Enable Robolectric Native Graphics (RNG) 
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ManualTest {
  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun captureRoboImageSample() {
    // Tips: You can use Robolectric with Espresso API
    // launch
    ActivityScenario.launch(MainActivity::class.java)

    // Capture screen
    onView(ViewMatchers.isRoot())
      // If you don't specify a screenshot file name, Roborazzi will automatically use the method name as the file name for you.
      // The format of the file name will be as follows:
      // build/outputs/roborazzi/com_..._ManualTest_captureRoboImageSample.png
      .captureRoboImage()

    // Capture Jetpack Compose Node
    composeTestRule.onNodeWithTag("MyComposeButton")
      .onParent()
      .captureRoboImage("build/compose.png")
  }
}
```

Roborazzi supports the following APIs.

<table>
<tr><td>Capture</td><td>Code</td></tr>
<tr><td>
✅ Jetpack Compose's onNode()
</td><td>

```kotlin
composeTestRule.onNodeWithTag("MyComposeButton")
  .captureRoboImage()
```

</td></tr>
<tr><td>
✅ Espresso's onView()
</td><td>

```kotlin
onView(ViewMatchers.isRoot())
  .captureRoboImage()
```

```kotlin
onView(withId(R.id.button_first))
  .captureRoboImage()
```

</td></tr>
<tr><td>
✅ View
</td><td>

```kotlin
val view: View = composeTestRule.activity.findViewById<View>(R.id.button_second)
view.captureRoboImage()
```

</td></tr>

<tr><td>
✅ Jetpack Compose lambda

</td><td>

```kotlin
captureRoboImage() {
  Text("Hello Compose!")
}
```

</td></tr>

<tr><td>
✅ Bitmap

</td><td>

```kotlin
val bitmap: Bitmap = createBitmap(100, 100, Bitmap.Config.ARGB_8888)
  .apply {
    applyCanvas {
      drawColor(android.graphics.Color.YELLOW)
    }
  }
bitmap.captureRoboImage()
```

</td></tr>

</table>

### Device configuration

You can configure the device by using the `@Config` annotation and `RobolectricDeviceQualifiers`.

<table>
<tr><td>Configuration</td><td>Code</td></tr>
<tr><td>
✅ Predefined device configuration
</td><td>

You can change the device configuration by adding `@Config` to the class or method.

```kotlin
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class RoborazziTest {
```

```kotlin
@Test
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
fun test() {
```

</td></tr>
<tr><td>
✅ Night mode
</td><td>

```kotlin
@Config(qualifiers = "+night")
```

</td></tr>
<tr><td>
✅ Locale
</td><td>

```kotlin
@Config(qualifiers = "+ja")
```

</td></tr>
<tr><td>
✅ Screen size
</td><td>

```kotlin
@Config(qualifiers = RobolectricDeviceQualifiers.MediumTablet)
```

</td></tr>

</table>

### Integrate to your GitHub Actions

It is easy to integrate Roborazzi to your GitHub Actions.

#### Add a job to store screenshots

```yaml
name: store screenshots

on:
  push

env:
  GRADLE_OPTS: "-Dorg.gradle.jvmargs=-Xmx6g -Dorg.gradle.daemon=false -Dkotlin.incremental=false"

jobs:
  test:
    runs-on: macos-latest

    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3.9.0
        with:
          distribution: 'zulu'
          java-version: 19

      - name: Gradle cache
        uses: gradle/gradle-build-action@v2

      - name: test
        run: |
          # Create screenshots
          ./gradlew app:recordRoborazziDebug --stacktrace

      # Upload screenshots to GitHub Actions Artifacts
      - uses: actions/upload-artifact@v3
        with:
          name: screenshots
          path: app/build/outputs/roborazzi
          retention-days: 30
```

#### Add a job to verify screenshots

```yaml
name: verify test

on:
  push

env:
  GRADLE_OPTS: "-Dorg.gradle.jvmargs=-Xmx6g -Dorg.gradle.daemon=false -Dkotlin.incremental=false"

jobs:
  test:
    runs-on: macos-latest

    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3.9.0
        with:
          distribution: 'zulu'
          java-version: 19

      - name: Gradle cache
        uses: gradle/gradle-build-action@v2

      # Download screenshots from main branch
      - uses: dawidd6/action-download-artifact@v2
        with:
          name: screenshots
          path: app/build/outputs/roborazzi
          workflow: test.yaml
          branch: main

      - name: verify test
        id: verify-test
        run: |
          # If there is a difference between the screenshots, the test will fail.
          ./gradlew app:verifyRoborazziDebug --stacktrace

      - uses: actions/upload-artifact@v3
        if: ${{ always() }}
        with:
          name: screenshot-diff
          path: app/build/outputs/roborazzi
          retention-days: 30

      - uses: actions/upload-artifact@v3
        if: ${{ always() }}
        with:
          name: screenshot-diff-reports
          path: app/build/reports
          retention-days: 30

      - uses: actions/upload-artifact@v3
        if: ${{ always() }}
        with:
          name: screenshot-diff-test-results
          path: app/build/test-results
          retention-days: 30

```

#### Advanced workflow Sample: Compare Snapshot Results on Pull Requests

For those who are looking for a more advanced example, we have prepared a sample repository that
demonstrates how to use Roborazzi to compare snapshot results on GitHub pull requests. This sample
showcases the integration of Roborazzi with GitHub Actions workflows, making it easy to visualize
and review the differences between snapshots directly in the pull request comments.

Check out
the [roborazzi-compare-on-github-comment-sample](https://github.com/takahirom/roborazzi-compare-on-github-comment-sample)
repository to see this powerful feature in action and learn how to implement it in your own
projects.

Example of the comment

<img src="https://user-images.githubusercontent.com/1386930/236480693-80483cde-53fe-4c04-ba1f-2352e14b5f15.png" width="600" />

## RoborazziRule (Optional)

RoborazziRule is a JUnit rule for Roborazzi.
RoborazziRule is **optional**. You can use `captureRoboImage()` without this rule.

RoborazziRule has two features.

1. Provide context such as `RoborazziOptions` and `outputDirectoryPath` etc for `captureRoboImage()`.
2. Capture screenshots for each test when specifying RoborazziRule.options.captureType.

For example, The following code generates an output file
named `**custom_outputDirectoryPath**/**custom_outputFileProvider**-com.github.takahirom.roborazzi.sample.RuleTestWithPath.captureRoboImage.png` :

```kotlin
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RuleTestWithPath {
  @get:Rule
  val roborazziRule = RoborazziRule(
    options = Options(
      outputDirectoryPath = "$DEFAULT_ROBORAZZI_OUTPUT_DIR_PATH/custom_outputDirectoryPath",
      outputFileProvider = { description, outputDirectory, fileExtension ->
        File(
          outputDirectory,
          "custom_outputFileProvider-${description.testClass.name}.${description.methodName}.$fileExtension"
        )
      }
    ),
  )

  @Test
  fun captureRoboImage() {
    launch(MainActivity::class.java)
    // The file will be saved using the rule's outputDirectoryPath and outputFileProvider
    onView(isRoot()).captureRoboImage()
  }
}
```

### Generate gif image

```kotlin
@Test
fun captureRoboGifSample() {
  onView(ViewMatchers.isRoot())
    .captureRoboGif("build/test.gif") {
      // launch
      ActivityScenario.launch(MainActivity::class.java)
      // move to next page
      onView(withId(R.id.button_first))
        .perform(click())
      // back
      pressBack()
      // move to next page
      onView(withId(R.id.button_first))
        .perform(click())
    }
}
```

<img width="350" src="https://user-images.githubusercontent.com/1386930/226362212-35d34c9e-6df1-4671-8949-10fad7ad98c9.gif" />

### Automatically generate gif with test rule

> **Note**  
> You **don't need to use RoborazziRule** if you're using captureRoboImage().

With the JUnit test rule, you do not need to name the gif image,
and if you prefer, you can output the gif image **only if the test fails**.

This test will output this file.

`build/outputs/roborazzi/com.github.takahirom.roborazzi.sample.RuleTestWithOnlyFail_captureRoboGifSampleFail.gif`

```kotlin
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RuleTestWithOnlyFail {
  @get:Rule
  val roborazziRule = RoborazziRule(
    captureRoot = onView(isRoot()),
    options = Options(
      onlyFail = true,
      captureType = RoborazziRule.CaptureType.Gif,
    )
  )

  @Test
  fun captureRoboLastImageSampleFail() {
    // launch
    ActivityScenario.launch(MainActivity::class.java)
    // move to next page
    onView(withId(R.id.button_first))
      .perform(click())
    // should fail because the button does not exist
    // Due to failure, the gif image will be saved in the outputs folder.
    onView(withId(R.id.button_first))
      .perform(click())
  }
}
```

### Automatically generate Jetpack Compose gif with test rule

Test target

```kotlin
@Composable
fun SampleComposableFunction() {
  var count by remember { mutableStateOf(0) }
  Column(
    Modifier
      .size(300.dp)
  ) {
    Box(
      Modifier
        .testTag("MyComposeButton")
        .size(50.dp)
        .clickable {
          count++
        }
    )
    (0..count).forEach {
      Box(
        Modifier
          .size(30.dp)
      )
    }
  }
}
```

Test (Just add RoborazziRule)

```kotlin
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ComposeTest {
  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @get:Rule
  val roborazziRule = RoborazziRule(
    composeRule = composeTestRule,
    captureRoot = composeTestRule.onRoot(),
    options = RoborazziRule.Options(
      RoborazziRule.CaptureType.Gif()
    )
  )

  @Test
  fun composable() {
    composeTestRule.setContent {
      SampleComposableFunction()
    }
    (0 until 3).forEach { _ ->
      composeTestRule
        .onNodeWithTag("MyComposeButton")
        .performClick()
    }
  }
}
```

![com github takahirom roborazzi sample ComposeTest_composable](https://user-images.githubusercontent.com/1386930/226366224-b9950b60-26a2-489e-bc03-08bfb86c533a.gif)

### RoborazziRule options

You can use some RoborazziRule options

```kotlin
/**
 * This rule is a JUnit rule for roborazzi.
 * This rule is optional. You can use [captureRoboImage] without this rule.
 *
 * This rule have two features.
 * 1. Provide context such as `RoborazziOptions` and `outputDirectoryPath` etc for [captureRoboImage].
 * 2. Capture screenshots for each test when specifying RoborazziRule.options.captureType.
 */
class RoborazziRule private constructor(
  private val captureRoot: CaptureRoot,
  private val options: Options = Options()
) : TestWatcher() {
  /**
   * If you add this annotation to the test, the test will be ignored by
   * roborazzi's CaptureType.LastImage, CaptureType.AllImage and CaptureType.Gif.
   */
  annotation class Ignore

  data class Options(
    val captureType: CaptureType = CaptureType.None,
    /**
     * output directory path
     */
    val outputDirectoryPath: String = provideRoborazziContext().outputDirectory,

    val outputFileProvider: FileProvider = provideRoborazziContext().fileProvider
      ?: defaultFileProvider,
    val roborazziOptions: RoborazziOptions = provideRoborazziContext().options,
  )

  sealed interface CaptureType {
    /**
     * Do not generate images. Just provide the image path to [captureRoboImage].
     */
    object None : CaptureType

    /**
     * Generate last images for each test
     */
    data class LastImage(
      /**
       * capture only when the test fail
       */
      val onlyFail: Boolean = false,
    ) : CaptureType

    /**
     * Generate images for Each layout change like TestClass_method_0.png for each test
     */
    data class AllImage(
      /**
       * capture only when the test fail
       */
      val onlyFail: Boolean = false,
    ) : CaptureType

    /**
     * Generate gif images for each test
     */
    data class Gif(
      /**
       * capture only when the test fail
       */
      val onlyFail: Boolean = false,
    ) : CaptureType
  }
```

### Roborazzi options

```kotlin
data class RoborazziOptions(
  val captureType: CaptureType = if (isNativeGraphicsEnabled()) CaptureType.Screenshot() else CaptureType.Dump(),
  val compareOptions: CompareOptions = CompareOptions(),
  val recordOptions: RecordOptions = RecordOptions(),
) {
  sealed interface CaptureType {
    class Screenshot : CaptureType

    data class Dump(
      val takeScreenShot: Boolean = isNativeGraphicsEnabled(),
      val basicSize: Int = 600,
      val depthSlideSize: Int = 30,
      val query: ((RoboComponent) -> Boolean)? = null,
      val explanation: ((RoboComponent) -> String?) = DefaultExplanation,
    ) : CaptureType {
      companion object {
        val DefaultExplanation: ((RoboComponent) -> String) = {
          it.text
        }
        val AccessibilityExplanation: ((RoboComponent) -> String) = {
          it.accessibilityText
        }
      }
    }
  }

  data class CompareOptions(
    val roborazziCompareReporter: RoborazziCompareReporter = RoborazziCompareReporter(),
    val resultValidator: (result: ImageComparator.ComparisonResult) -> Boolean,
  ) {
    constructor(
      roborazziCompareReporter: RoborazziCompareReporter = RoborazziCompareReporter(),
      /**
       * This value determines the threshold of pixel change at which the diff image is output or not.
       * The value should be between 0 and 1
       */
      changeThreshold: Float = 0.01F,
    ) : this(roborazziCompareReporter, ThresholdValidator(changeThreshold))
  }

  interface RoborazziCompareReporter {
    fun report(compareReportCaptureResult: CompareReportCaptureResult)

    companion object {
      operator fun invoke(): RoborazziCompareReporter {
        ...
      }
    }

    class JsonOutputRoborazziCompareReporter : RoborazziCompareReporter {
      ...

      override fun report(compareReportCaptureResult: CompareReportCaptureResult) {
        ...
      }
    }

    class VerifyRoborazziCompareReporter : RoborazziCompareReporter {
      override fun report(compareReportCaptureResult: CompareReportCaptureResult) {
        ...
      }
    }
  }

  data class RecordOptions(
    val resizeScale: Double = roborazziDefaultResizeScale(),
    val applyDeviceCrop: Boolean = false,
    val pixelBitConfig: PixelBitConfig = PixelBitConfig.Argb8888,
  )

  enum class PixelBitConfig {
    Argb8888,
    Rgb565;

    fun toBitmapConfig(): Bitmap.Config {
      ...
    }

    fun toBufferedImageType(): Int {
      ...
    }
  }
}
```

### Dump mode

If you are having trouble debugging your test, try Dump mode as follows.

![image](https://user-images.githubusercontent.com/1386930/226364158-a07a0fb0-d8e7-46b7-a495-8dd217faaadb.png)

### Experimental feature: Compose Desktop support

Roborazzi supports Compose Desktop. You can use Roborazzi with Compose Desktop as follows:

Gradle settings

```kotlin
plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  id("io.github.takahirom.roborazzi")
}

kotlin {
  // You can use your source set name
  jvm("desktop")
  sourceSets {
    ...
    val desktopTest by getting {
      dependencies {
        implementation(project("io.github.takahirom.roborazzi:roborazzi-compose-desktop:[1.6.0-alpha-2 or higher]"))
        implementation(kotlin("test"))
      }
    }
  ...

// Roborazzi Desktop support uses Context Receivers
tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs += "-Xcontext-receivers"
  }
}
```

Test target Composable function

```kotlin
@Composable
fun App() {
  var text by remember { mutableStateOf("Hello, World!") }

  MaterialTheme {
    Button(
      modifier = Modifier.testTag("button"),
      onClick = {
        text = "Hello, Desktop!"
      }) {
      Text(
        style = MaterialTheme.typography.h2,
        text = text
      )
    }
  }
}
```

Test with Roborazzi

```kotlin
class MainKmpTest {
  @OptIn(ExperimentalTestApi::class)
  @Test
  fun test() = runDesktopComposeUiTest {
    setContent {
      App()
    }
    val roborazziOptions = RoborazziOptions(
      recordOptions = RoborazziOptions.RecordOptions(
        resizeScale = 0.5
      ),
      compareOptions = RoborazziOptions.CompareOptions(
        changeThreshold = 0F
      )
    )
    onRoot().captureRoboImage(roborazziOptions = roborazziOptions)

    onNodeWithTag("button").performClick()

    onRoot().captureRoboImage(roborazziOptions = roborazziOptions)
  }
}
```

Then, you can run the Gradle tasks for Desktop Support, just like you do for Android Support.

```
./gradlew recordRoborazzi[SourceSet]
```

```
./gradlew recordRoborazziDesktop
./gradlew compareRoborazziDesktop
./gradlew verifyRoborazziDesktop
...
```

If you use the Kotlin JVM plugin, the task will be `recordRoborazzi**Jvm**`.

The sample image

![MainJvmTest test](https://github.com/takahirom/roborazzi/assets/1386930/41287c29-26ae-4539-b387-de570ae3f2b3)
![MainJvmTest test_2](https://github.com/takahirom/roborazzi/assets/1386930/2edc828c-6fd8-4a9a-8f3d-b0e7baa85f0d)

## Roborazzi gradle.properties Options

You can configure the following options in your `gradle.properties` file:

### roborazzi.test

This option enables you to configure the behavior of Roborazzi. By default, all settings are set to false. 
For additional configuration options, please refer to the 'Apply Roborazzi Gradle Plugin' section.

```properties
roborazzi.test.record=true
# roborazzi.test.compare=true
# roborazzi.test.verify=true
```

### roborazzi.record

#### roborazzi.record.resizeScale

This option lets you set the resize scale for the image being recorded. The default value is 1.0.

```properties
roborazzi.record.resizeScale=0.5
```

#### roborazzi.record.filePathStrategy

This setting allows you to specify the file path strategy for the recorded image. The default strategy is `relativePathFromCurrentDirectory`. If you choose `relativePathFromRoborazziContextOutputDirectory`, the file will be saved in the output directory specified by `RoborazziRule.Options.outputDirectoryPath`.

```properties
roborazzi.record.filePathStrategy=relativePathFromRoborazziContextOutputDirectory
```

#### roborazzi.record.namingStrategy

This option enables you to define the naming strategy for the recorded image. The default strategy is `testPackageAndClassAndMethod`.

- If you choose `testPackageAndClassAndMethod`, the file name will be `com.example.MyTest.testMethod.png`.
- If you choose `escapedTestPackageAndClassAndMethod`, the file name will be `com_example_MyTest.testMethod.png`.
- If you choose `testClassAndMethod`, the file name will be `MyTest.testMethod.png`.

```properties
roborazzi.record.namingStrategy=testClassAndMethod
```

# FAQ

### Q: How can I run only screenshot tests using Roborazzi?

**A:** To run only screenshot tests, you can configure your project with the following:

```groovy
android {
    testOptions {
        unitTests {
            all {
                // -Pscreenshot to filter screenshot tests
                it.useJUnit {
                    if (project.hasProperty("screenshot")) {
                        includeCategories("io.github.takahirom.roborazzi.testing.category.ScreenshotTests")
                    }
                }
            }
        }
    }
}
```

Include the `-Pscreenshot` property, and only the screenshot tests will be run.

Note: This feature is not provided in the Roborazzi library itself, to keep it simple and utilize JUnit's built-in features for test filtering.

You can also annotate your tests like this:

```kotlin
/**
 * You can filter ScreenshotTests using -Pscreenshot parameter
 */
interface ScreenshotTests

@Test
@Category(ScreenshotTests::class)
fun checkLaunchShot() {
  onRoot().captureRoboImage(roborazziOptions = roborazziOptions)
}
```

This allows you to create a category of screenshot tests and filter them using the `-Pscreenshot` property, thus making it easier to run only those specific tests.

### Q: How can I debug screenshot tests in Android Studio?
### Q: How can I execute screenshot tests using Android Studio's Run button?

A: To execute screenshot tests using Android Studio's Run button, configure your project as follows:

`gradle.properties`

```groovy
roborazzi.test.record=true
```

After that, you can execute screenshot tests using either Android Studio's Run or Debug button as you normally would.

### Q: My screenshot tests are not capturing images. What could be the issue?

**A:** If your screenshot tests are not capturing images, there may be several patterns that are causing this issue. Please follow these troubleshooting steps:

- **Enable Debugging**: Set `ROBORAZZI_DEBUG = true` to see logs.
- **Check Plugin**: Ensure that the plugin is properly applied.
- **Run Task**: Verify that the `recordRoborazziDebug` task is running.
- **Call Method**: Confirm that `captureRoboImage()` is being called.

By following these steps, you should be able to identify and resolve the issue causing the screenshot tests to not capture images.

### Q: I'm seeing an optimization warning related to Java lambdas in Gradle. What can I do?

**A:** This warning may occur with Gradle 7.5. Upgrade to Gradle 7.6.2 to resolve this issue. Change the distribution URL in `gradle-wrapper.properties`:

```diff
-distributionUrl=https\://services.gradle.org/distributions/gradle-7.5-bin.zip
+distributionUrl=https\://services.gradle.org/distributions/gradle-7.6.2-bin.zip
```

### Q: Can I run Roborazzi with Bazel?

**A:** As of now, there is no direct support for running Roborazzi with Bazel. However, it is possible to do so. Please refer to the following comment for more details:
[Roborazzi Bazel Support Comment](https://github.com/takahirom/roborazzi/issues/63#issuecomment-1531990825)

### Q: My tests are being skipped or, conversely, are being run when they should be skipped. How can I handle caching to address this?

**A:** The behavior you are experiencing may be related to caching issues. Although it's
experimental, you can set the `outputDir` parameter in your `build.gradle` file to handle caching
and improve the stability of your tests. This parameter allows you to specify the output directory
for your screenshots, which can help in managing the cache. Here is how you can set it up:
If you use the default output directory(module/build/outputs/roborazzi), specifying the `outputDir`
parameter is not necessary. For more reference, you can check
out [the issue](https://github.com/takahirom/roborazzi/issues/193#issuecomment-1782073746).

```gradle
roborazzi {
    outputDir = "src/your/screenshot/folder"
}
```

### LICENSE

```
Copyright 2023 takahirom
Copyright 2019 Square, Inc.
Copyright The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
