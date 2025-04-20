
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.spotless)
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            // splash
            implementation(libs.core.splashscreen)

            // koin
            implementation(libs.koin.android)

            // ktor
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(kotlin("reflect"))

            // koin
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // ktor
            implementation(libs.bundles.ktor)

            // coil
            implementation(libs.bundles.coil)

            // navigation
            implementation(libs.jetbrains.compose.navigation)

            implementation(libs.kotlinx.serialization.json)
        }
        nativeMain.dependencies {
            // ktor
            implementation(libs.ktor.client.darwin)
        }
        tasks.register("jacocoTestReport", JacocoReport::class) {
            dependsOn(tasks.withType(Test::class))
            val coverageSourceDirs =
                arrayOf(
                    "src/commonMain",
                    "src/androidMain",
                    "src/iosMain",
                )

            val buildDirectory = layout.buildDirectory

            val classFiles =
                buildDirectory.dir("classes/kotlin/jvm").get().asFile
                    .walkBottomUp()
                    .toSet()

            classDirectories.setFrom(classFiles)
            sourceDirectories.setFrom(files(coverageSourceDirs))

            buildDirectory.files("jacoco/jvmTest.exec").let {
                executionData.setFrom(it)
            }

            reports {
                xml.required = true
                csv.required = true
                html.required = true
            }
        }
    }
}

android {
    namespace = "com.nacchofer31.randomboxd"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nacchofer31.randomboxd"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

spotless {
    kotlin {
        target("src/**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        trimTrailingWhitespace()
        endWithNewline()
        ktlint()
            .setEditorConfigPath("${project.rootDir}/spotless/.editorconfig")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
