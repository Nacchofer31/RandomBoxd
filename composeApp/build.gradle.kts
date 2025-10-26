import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.mockmp)
    jacoco
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
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

            // room
            implementation(libs.room.runtime.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
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

            // ksoup
            implementation(libs.ksoup)
            implementation(libs.ksoupKotlinx)
            implementation(libs.ksoupNetwork)

            // room
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
        }
        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.junit)
            implementation(libs.androidx.junit)
            implementation(libs.androidx.ui.test.junit4)
            implementation(libs.androidx.espresso.core)
            implementation(libs.kotlin.test)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.ktor.client.mock)
            implementation(libs.assertk)
            implementation(libs.turbine)
            implementation(libs.coroutines.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.assertk)
            implementation(kotlin("test"))
            implementation(libs.androidx.test.runner)
        }
    }
}

android {
    namespace = "com.nacchofer31.randomboxd"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.nacchofer31.randomboxd"
        versionName = "0.9.0"
        versionCode = 7
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

mockmp {
    onTest {
        withHelper(junit4)
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.room.runtime.android)
    debugImplementation(libs.androidx.ui.test.junit4.android)
    debugImplementation(libs.androidx.ui.test.android)
    debugImplementation(compose.uiTooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    ksp(libs.room.compiler)
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
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

val fileFilter =
    listOf<String>(
        "com/nacchofer31/randomboxd/app/**",
        "randomboxd/composeapp/generated/resources/**",
    )

tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn(tasks.withType(Test::class), "connectedDebugAndroidTest")

    group = "Reporting"
    description = "Generate Jacoco coverage reports."

    val coverageSourceDirs =
        arrayOf(
            "src/commonMain",
            "src/androidMain",
        )

    val classFiles =
        layout.buildDirectory
            .dir("tmp/kotlin-classes/debug")
            .get()
            .asFileTree
            .matching {
                include("**/*.class")
                exclude(fileFilter)
            }

    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("jacoco/testDebugUnitTest.exec")
            include("outputs/code_coverage/**/*.ec")
        },
    )

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.register("jacocoTestCoverageVerification", JacocoCoverageVerification::class) {
    dependsOn(tasks.withType(Test::class), "connectedDebugAndroidTest")

    group = "Reporting"
    description = "Verifies code coverage metrics based on specific rules."

    val coverageSourceDirs =
        arrayOf(
            "src/commonMain",
            "src/androidMain",
        )

    val classFiles =
        layout.buildDirectory
            .dir("tmp/kotlin-classes/debug")
            .get()
            .asFileTree
            .matching {
                include("**/*.class")
                exclude(fileFilter)
            }

    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("jacoco/testDebugUnitTest.exec")
            include("outputs/code_coverage/**/*.ec")
        },
    )

    violationRules {
        isFailOnViolation = true
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = BigDecimal("0.70")
            }
        }
    }
}

tasks.register("fullCoverageReport") {
    group = "Reporting"
    description = "Runs all tests (unit + android) and generates full coverage report."
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest", "jacocoTestReport")
}
