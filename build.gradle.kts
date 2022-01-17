buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.plugin)
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

apply(from="$rootDir/gradle/publish-root.gradle.kts")