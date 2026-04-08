@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.agp.lib)
    alias(libs.plugins.compose.compiler)
}

val androidCompileSdkVersion: Int by rootProject.extra
val androidBuildToolsVersion: String by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidSourceCompatibility: JavaVersion by rootProject.extra
val androidTargetCompatibility: JavaVersion by rootProject.extra

android {
    namespace = "com.hansyeoh.shared"

    compileSdk = androidCompileSdkVersion
    buildToolsVersion = androidBuildToolsVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = androidSourceCompatibility
        targetCompatibility = androidTargetCompatibility
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
    }
}

dependencies {
    // Compose
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material.icons.extended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling.preview)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Lifecycle
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewmodel.compose)

    // Navigation3
    api(libs.androidx.navigation3.runtime)

    // Coroutines
    api(libs.kotlinx.coroutines.core)

    // Miuix
    api(libs.miuix)
    api(libs.miuix.icons)

    // Haze (glassmorphism)
    api(libs.backdrop)
    api(libs.capsule)
    api(libs.haze)

    // Material Kolor (dynamic theming)
    api(libs.material.kolor)

    // Activity Compose (needed for Window/Activity references in theme)
    api(libs.androidx.activity.compose)

}
