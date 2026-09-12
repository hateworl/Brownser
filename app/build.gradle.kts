plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.taiji.browser"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.taiji.browser"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // GeckoView - engine do navegador (Mozilla)
    implementation("org.mozilla.geckoview:geckoview:")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2026.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.core:core-ktx:1.13.1")

    // VPN local para bloqueio de anúncios em nível de DNS
    implementation("androidx.core:core-splashscreen:1.0.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
