import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  id("io.github.takahirom.roborazzi")
  id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.github.takahirom.roborazzi.compose.desktop.kmp.sample"
version = "1.0-SNAPSHOT"

kotlin {
  jvm("desktop") {
  }
  sourceSets {
    val desktopMain by getting {
      dependencies {
        // Note, if you develop a library, you should use compose.desktop.common.
        // compose.desktop.currentOs should be used in launcher-sourceSet
        // (in a separate module for demo project and in testMain).
        // With compose.desktop.common you will also lose @Preview functionality
        implementation(compose.desktop.currentOs)
        implementation(libs.androidx.compose.ui.tooling)
      }
    }

    val desktopTest by getting {
      dependencies {
        implementation(project(":roborazzi-compose-desktop"))
        implementation(kotlin("test"))

        implementation("junit:junit:4.13.2")
        implementation("com.google.testparameterinjector:test-parameter-injector:1.18")
        implementation(libs.composable.preview.scanner.jvm)
        implementation(libs.composable.preview.scanner)
      }
    }
  }
}
compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "app-compose-desktop"
      packageVersion = "1.0.0"
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