@file:OptIn(ExperimentalRoborazziApi::class)

import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.jetbrains.kotlin.multiplatform")
  id("com.android.application")
//  id("com.android.library")
  id("io.github.takahirom.roborazzi")
  id("org.jetbrains.kotlin.plugin.compose")
  id("org.jetbrains.compose")
}

// ./gradlew :sample-generate-preview-tests-multiplatform:recordRoborazziDebug
roborazzi {
  generateComposePreviewRobolectricTests {
    enable = false
    packages = listOf("com.github.takahirom.preview.tests")
    testerQualifiedClassName = "com.github.takahirom.preview.tests.MultiplatformPreviewTester"
  }
}

repositories {
  mavenCentral()
  google()
}

android {
  namespace = "com.github.takahirom.preview.tests"
  compileSdk = 35

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
      }
    }
  }
}

kotlin {
  androidTarget()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.androidx.compose.ui.tooling)
        implementation(compose.components.uiToolingPreview)
      }
    }
    val androidMain by getting {
      dependencies {
        implementation(compose.material3)
        implementation(compose.ui)
        implementation(compose.uiTooling)
        implementation(compose.runtime)
      }
    }

    val androidUnitTest by getting {
      dependencies {
        // replaced by dependency substitution
        implementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:1.46.1")
        implementation(project(":roborazzi-compose"))
        implementation(project(":roborazzi-annotations"))
        implementation(libs.junit)
        implementation(libs.robolectric)
        implementation(libs.composable.preview.scanner.common)
        implementation(libs.composable.preview.scanner)
        implementation(libs.androidx.compose.ui.test.junit4)
        implementation("com.google.testparameterinjector:test-parameter-injector:1.18")
      }
    }
    val androidDebug by creating {
      dependencies {
        implementation(libs.androidx.compose.ui.test.manifest)
      }
    }

    val androidInstrumentedTest by getting {
      dependencies {
        implementation(libs.androidx.test.ext.junit)
        implementation(libs.androidx.test.espresso.core)
      }
    }
  }
}

tasks.withType<Test>().configureEach {
  useJUnit()
  testLogging.showStandardStreams = true
}

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions {
    allWarningsAsErrors.set(false)
  }
}
