plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "net.htlgkr.skywatcher"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.htlgkr.skywatcher"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.google.gson)
    implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.volley)
    implementation("com.google.android.material:material:1.12.0")

//    implementation(libs.sceneform.ux)
//
//    implementation(libs.sceneform.core)
//    implementation(libs.sceneform.ux)


    implementation("com.google.android.gms:play-services-location:21.3.0")





    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}