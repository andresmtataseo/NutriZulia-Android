plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.nutrizulia"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nutrizulia"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Fragment integration
    implementation(libs.androidx.fragment.ktx)
    // Activity integration
    implementation(libs.androidx.activity.ktx)
    // ViewModel integration
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData integration
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // Coroutines integration
    implementation(libs.kotlinx.coroutines.android)
    // Retrofit integration
    implementation(libs.retrofit)
    // Retrofit converter integration
    implementation(libs.converter.gson)
    // Room integration
    implementation(libs.androidx.room.runtime)
    // Room ktx integration
    implementation(libs.androidx.room.ktx)
    // Room compiler integration
    ksp(libs.androidx.room.compiler)
    // Hilt integration
    implementation(libs.hilt.android)
    // Hilt compiler integration
    ksp(libs.hilt.compiler)
    // Swipe
    implementation(libs.androidx.swiperefreshlayout)

}