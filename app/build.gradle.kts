import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.travelwise"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.travelwise"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Expose API keys via local.properties (not committed)
        val props = Properties()
        val localProps = rootProject.file("local.properties")
        if (localProps.exists()) {
            localProps.inputStream().use { props.load(it) }
        }
        val geminiKey = props.getProperty("GEMINI_API_KEY")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
        // ...
        val geminiModel = props.getProperty("GEMINI_MODEL") ?: "gemini-1.5-flash-latest"
        buildConfigField("String", "GEMINI_MODEL", "\"$geminiModel\"")
        // ...
        val googleCseApiKey = props.getProperty("GOOGLE_CSE_API_KEY")
        buildConfigField("String", "GOOGLE_CSE_API_KEY", "\"$googleCseApiKey\"")
        val googleCseId = props.getProperty("GOOGLE_CSE_ID")
        buildConfigField("String", "GOOGLE_CSE_ID", "\"$googleCseId\"")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // AndroidX Core and UI components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Lifecycle and Navigation
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")

    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Networking and Serialization
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.amazon.ion:ion-java:1.11.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    // Firebase (BoM-managed)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
