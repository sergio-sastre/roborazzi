plugins {
  id("com.android.application")
//  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("io.github.takahirom.roborazzi")
}

roborazzi {
  generateComposePreviewRobolectricTests {
    enable = true
    packages = listOf("com.github.takahirom.preview.tests")
  }
}

repositories {
  mavenCentral()
  google()
}

android {
  namespace = "com.github.takahirom.preview.tests"
  compileSdk = 34

  defaultConfig {
    minSdk = 24

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all {
        it.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
        // For large preview
        it.maxHeapSize = "4096m"
        it.jvmArgs("-noverify")
      }
    }
  }
}

dependencies {
  implementation(project(":roborazzi-annotations"))
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.runtime)

  // replaced by dependency substitution
  testImplementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:0.1.0")
  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
  testImplementation(libs.composable.preview.scanner)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)
}