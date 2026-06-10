import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}


val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}
val elevenLabsApiKey = providers.gradleProperty("ELEVENLABS_API_KEY")
    .orElse(providers.environmentVariable("ELEVENLABS_API_KEY"))
    .orElse(localProperties.getProperty("ELEVENLABS_API_KEY", ""))
val backendBaseUrl = providers.gradleProperty("VOICE_BACKEND_BASE_URL")
    .orElse(providers.environmentVariable("VOICE_BACKEND_BASE_URL"))
    .orElse(localProperties.getProperty("VOICE_BACKEND_BASE_URL", ""))
val debugVoiceGenerationMode = providers.gradleProperty("VOICE_GENERATION_MODE")
    .orElse(localProperties.getProperty("VOICE_GENERATION_MODE", ""))

fun String.asBuildConfigString(): String {
    return "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""
}


android {
    namespace = "com.pranksterlab"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pranksterlab"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            buildConfigField("String", "ELEVENLABS_API_KEY", elevenLabsApiKey.get().asBuildConfigString())
            val defaultMode = if (elevenLabsApiKey.get().isBlank()) "LOCAL_ONLY" else "DEBUG_ELEVENLABS_DIRECT"
            buildConfigField("String", "VOICE_GENERATION_MODE", (debugVoiceGenerationMode.get().ifBlank { defaultMode }).asBuildConfigString())
            buildConfigField("String", "VOICE_BACKEND_BASE_URL", backendBaseUrl.get().asBuildConfigString())
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "ELEVENLABS_API_KEY", "\"\"")
            buildConfigField("String", "VOICE_GENERATION_MODE", "\"PRODUCTION_BACKEND\"")
            buildConfigField("String", "VOICE_BACKEND_BASE_URL", backendBaseUrl.get().asBuildConfigString())
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    androidResources {
        noCompress += listOf("mp3", "ogg", "oga", "wav", "m4a", "aac", "flac", "opus", "amr", "mp4")
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.2")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.json:json:20231013")
}
