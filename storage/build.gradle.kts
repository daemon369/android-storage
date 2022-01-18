plugins {
    id("com.android.library")
    id("kotlin-android")
}

ext {
    set("artifactId", "android-storage")
    set("artifactVersion", "0.0.1")

    set(
        "pom",
        mapOf(
            "name" to get("artifactId"),
            "description" to "Android Storage",
            "url" to "https://github.com/daemon369/android-storage",
            "scm" to mapOf(
                "connection" to "scm:git:git://github.com/daemon369/android-storage.git",
                "developerConnection" to "scm:git:ssh://github.com/daemon369/android-storage.git",
                "url" to "https://github.com/daemon369/android-storage/tree/main",
            )
        )
    )
}

val artifactId: String by extra

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 19
        targetSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        buildConfig = false
    }
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.x.appcompat)

    implementation(libs.daemon.logger)

    testImplementation(libs.junit)
    androidTestImplementation(libs.x.junit)
    androidTestImplementation(libs.x.espresso.core)
}

apply(from = "$rootDir/gradle/maven-publish.gradle")
