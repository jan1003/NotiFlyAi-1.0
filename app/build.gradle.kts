plugins {
    id("com.android.application")
    id("com.google.gms.google-services")  // для Firebase
}

android {
    namespace = "com.example.notifly"  // Исправлено!
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notifly"  // Исправлено!
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX + UI
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Material Design
    implementation("com.google.android.material:material:1.9.0")

    // Firebase (используем BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // OkHttp для работы с OpenAI API
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // Activity API (для совместимости с новыми SDK)
    implementation("androidx.activity:activity-ktx:1.8.0")

    // Constraints для устранения конфликтов версий
    constraints {
        implementation("androidx.core:core-ktx") {
            version {
                strictly("1.12.0")
            }
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib") {
            version {
                strictly("1.8.22")
            }
        }
    }
}