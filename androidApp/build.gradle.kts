import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.spotless)
}

android {
    namespace = "com.nacchofer31.randomboxd"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.nacchofer31.randomboxd"
        versionName = "1.6.0"
        versionCode = 22
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
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
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.koin.android)
    implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
    debugImplementation("org.jetbrains.compose.ui:ui-tooling:1.10.0")
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
