buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // Google Services (для Firebase)
        classpath("com.google.gms:google-services:4.3.15")

        // Последняя стабильная версия AGP
        classpath("com.android.tools.build:gradle:8.4.2")
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin-stdlib-jdk")) {
            useVersion("1.8.22")
        }
    }
}
