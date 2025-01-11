buildscript {
    repositories {
        // Эти репозитории тоже должны быть указаны
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // Android Gradle Plugin
        classpath("com.android.tools.build:gradle:8.0.2")
        // Google Services (для Firebase)
        classpath("com.google.gms:google-services:4.3.15")

    }
}