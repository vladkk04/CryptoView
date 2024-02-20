import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("android")
}

val properties = Properties()
properties.load(rootProject.file("local.properties").inputStream())
val binanceAPI = properties["API_KEY_BINANCE"]
val currencyAPI = properties["API_KEY_CURRENCY"]

android {
    namespace = "com.example.cryptoview"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.cryptoview"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY_BINANCE", "\"${binanceAPI}\"")
        buildConfigField("String", "API_KEY_CURRENCY", "\"${currencyAPI}\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    hilt {
        enableAggregatingTask = false
        enableExperimentalClasspathAggregation = true
    }

}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel)

    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.gson)

    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    ksp(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)


    testImplementation(testLibs.junit)
    androidTestImplementation(testLibs.androidx.test.ext.junit)
    androidTestImplementation(testLibs.espresso.core)
}