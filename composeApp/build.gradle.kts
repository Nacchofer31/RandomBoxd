import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

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
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
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
            implementation("org.jetbrains.compose.runtime:runtime:1.10.0")
            implementation("org.jetbrains.compose.foundation:foundation:1.10.0")
            implementation("org.jetbrains.compose.material3:material3:1.9.0")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
            implementation("org.jetbrains.compose.ui:ui:1.10.0")
            implementation("org.jetbrains.compose.components:components-resources:1.10.0")
            implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
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
            implementation("org.jetbrains.compose.ui:ui-test:1.10.0")
            implementation(libs.junit)
            implementation(libs.androidx.junit)
            implementation(libs.androidx.ui.test.junit4)
            implementation(libs.androidx.espresso.core)
            implementation(libs.kotlin.test)
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
            implementation("io.coil-kt.coil3:coil-test:3.3.0")
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
        versionName = "1.1.1"
        versionCode = 12
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            val props = Properties()
            val keyPropsFile = rootProject.file("key.properties")
            if (keyPropsFile.exists()) props.load(keyPropsFile.inputStream())
            storeFile = props["storeFile"]?.let { file(it) }
            storePassword = props["storePassword"] as String?
            keyPassword = props["keyPassword"] as String?
            keyAlias = props["keyAlias"] as String?
        }
    }

    buildTypes {
        debug {
            enableAndroidTestCoverage = true
        }
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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

ksp {
    arg("room.generateKotlin", "true")
}

val sqliteTmpDir = layout.buildDirectory.dir("tmp/sqlite")
tasks.withType<com.google.devtools.ksp.gradle.KspAATask>().configureEach {
    doFirst {
        sqliteTmpDir.get().asFile.mkdirs()
        System.setProperty("org.sqlite.tmpdir", sqliteTmpDir.get().asFile.absolutePath)
    }
}

dependencies {
    implementation(libs.room.runtime.android)
    debugImplementation(libs.androidx.ui.test.junit4.android)
    debugImplementation(libs.androidx.ui.test.android)
    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.10.0")
    debugImplementation(libs.androidx.ui.test.manifest)
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
        // App navigation/entry (excluded by default)
        "com/nacchofer31/randomboxd/app/**",
        // Compose resources (generated)
        "randomboxd/composeapp/generated/resources/**",
        // Android platform-specific (require device context)
        "com/nacchofer31/randomboxd/AndroidPlatform*",
        "com/nacchofer31/randomboxd/MainActivity*",
        "com/nacchofer31/randomboxd/MainActivityKt*",
        "com/nacchofer31/randomboxd/Platform*",
        "com/nacchofer31/randomboxd/ComposableSingletons${'$'}MainActivityKt*",
        "com/nacchofer31/randomboxd/core/data/OnboardingPreferences*",
        "com/nacchofer31/randomboxd/core/data/RandomBoxdHttpClientExtKt*",
        "com/nacchofer31/randomboxd/core/presentation/RandomBoxdTheme*",
        // Room generated code
        "com/nacchofer31/randomboxd/core/data/UsernameDatabase_Impl*",
        "com/nacchofer31/randomboxd/core/data/UserNameDatabaseConstructor*",
        "com/nacchofer31/randomboxd/random_film/domain/model/UserNameDao_Impl*",
        // Inline functions — JaCoCo cannot track coverage of Kotlin inline function bodies
        "com/nacchofer31/randomboxd/core/domain/ResultData*",
        "com/nacchofer31/randomboxd/core/domain/ResultDataKt*",
        // Pure interfaces (no executable code)
        "com/nacchofer31/randomboxd/random_film/domain/repository/RandomFilmRepository*",
        // kotlinx.coroutines inlined lambda classes (phantom source entries)
        "com/nacchofer31/randomboxd/random_film/presentation/components/**\$\$inlined*",
        "com/nacchofer31/randomboxd/random_film/presentation/viewmodel/**\$special\$\$inlined*",
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
