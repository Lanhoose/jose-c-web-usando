import java.util.Properties

plugins {
    kotlin("plugin.serialization") version "2.2.10"
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

val firebaseProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

fun firebaseProp(name: String) = firebaseProperties.getProperty(name, "")
    .ifBlank { if (name == "firebase.storageBucket") "arquivo-paranormal.firebasestorage.app" else "" }

android {
    namespace = "com.arquivoparanormal.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.arquivoparanormal.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "FIREBASE_API_KEY",
            "\"${firebaseProp("firebase.apiKey")}\""
        )
        buildConfigField(
            "String",
            "FIREBASE_APP_ID",
            "\"${firebaseProp("firebase.appId")}\""
        )
        buildConfigField(
            "String",
            "FIREBASE_PROJECT_ID",
            "\"${firebaseProp("firebase.projectId")}\""
        )
        buildConfigField(
            "String",
            "FIREBASE_MESSAGING_SENDER_ID",
            "\"${firebaseProp("firebase.messagingSenderId")}\""
        )
        buildConfigField(
            "String",
            "FIREBASE_STORAGE_BUCKET",
            "\"${firebaseProp("firebase.storageBucket")}\""
        )

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    // Firebase Storage não é mais usado: a foto de perfil fica local no
    // aparelho e só uma miniatura (Base64) é sincronizada pelo Firestore.

    implementation("androidx.compose.material:material-icons-extended")
    // Carrega a foto de perfil local (file://) na tela de Configurações.
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.appcompat)

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}