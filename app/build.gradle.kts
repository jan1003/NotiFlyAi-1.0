plugins {
    id("com.android.application")
    // Код на Java, Kotlin-плагин не нужен
    id("com.google.gms.google-services")  // для Firebase
}

android {
    namespace = "com.example.notifly"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.notifly"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX + UI
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    // Material
    implementation("com.google.android.material:material:1.9.0")

    // Firebase (BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-messaging")

    // OkHttp для вызова OpenAI
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}