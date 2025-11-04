import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
        // Expose Gemini API key via local.properties (not committed)
        val props = Properties()
        val localProps = rootProject.file("local.properties")
        if (localProps.exists()) {
            localProps.inputStream().use { props.load(it) }
        }
        val geminiKey = props.getProperty("GEMINI_API_KEY")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")

        // Allow overriding the model from local.properties; default to flash-latest
        val geminiModel = props.getProperty("GEMINI_MODEL") ?: "gemini-1.5-flash-latest"
        buildConfigField("String", "GEMINI_MODEL", "\"$geminiModel\"")
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
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.amazon.ion:ion-java:1.11.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // CardView and RecyclerView
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Image Loading (Glide)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ViewPager2 for swipeable screens
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    // Networking for Gemini REST
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Amazon Ion is required by a transitive dependency at runtime
    implementation("com.amazon.ion:ion-java:1.11.1")
    testImplementation(libs.junit)
    implementation("com.google.code.gson:gson:2.10.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}