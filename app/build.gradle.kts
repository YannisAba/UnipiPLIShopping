plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    /*id("com.android.application")
    id("com.google.gms.google-services")*/
}

android {
    namespace = "com.jabat.personal.unipiplishopping"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jabat.personal.unipiplishopping"
        minSdk = 30
        targetSdk = 34
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.play.services.location)
    /*implementation(libs.firebase.auth)*/
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    /*implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.gms:google-services:4.4.2")*/
    /*implementation(platform("com.google.firebase:firebase-bom:33.7.0"))*/
}