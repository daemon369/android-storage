import me.daemon.gradle.Config
import me.daemon.gradle.PublishInfo
import me.daemon.gradle.PublishInfo.Pom
import me.daemon.gradle.PublishInfo.Pom.Scm

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("signing")
    id("me.daemon.gradle")
}

val artifactGroupId: String by project
val ossrhUsername: String by project.extra
val ossrhPassword: String by project.extra

val publishInfo = PublishInfo(
    artifactId = "android-storage",
    artifactVersion = "0.3.0",
    pom = Pom(
        name = "android-storage",
        description = "Android Storage",
        url = "https://github.com/daemon369/android-storage",
        scm = Scm(
            connection = "scm:git:git://github.com/daemon369/android-storage.git",
            developerConnection = "scm:git:ssh://github.com/daemon369/android-storage.git",
            url = "https://github.com/daemon369/android-storage/tree/main",
        )
    )
)

android {
    compileSdk = Config.compileSdkVersion

    defaultConfig {
        minSdk = Config.minSdkVersion
        targetSdk = Config.targetSdkVersion

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
        kotlinOptions.freeCompilerArgs =
            listOf("-module-name", "${artifactGroupId}.${publishInfo.artifactId}")
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
        multipleVariants {
            withSourcesJar()
            withJavadocJar()
            allVariants()
        }
    }
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.x.appcompat)

    implementation(libs.daemon.logger)
//    implementation("io.github.daemon369:android-logger:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.x.junit)
    androidTestImplementation(libs.x.espresso.core)
}

afterEvaluate {
    publishing {

        publications {
            repositories {
                maven {
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }

            create<MavenPublication>("release") {
                groupId = artifactGroupId
                artifactId = publishInfo.artifactId
                version = publishInfo.artifactVersion
                if (plugins.findPlugin("com.android.library") != null) {
                    from(components["release"])
                } else {
                    from(components["java"])
                }

                pom {
                    name.set(publishInfo.pom.name)
                    description.set(publishInfo.pom.description)
                    url.set(publishInfo.pom.url)
                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("daemon")
                            name.set("Daemon")
                            email.set("daemon336699@gmail.com")
                        }
                    }
                    scm {
                        connection.set(publishInfo.pom.scm.connection)
                        developerConnection.set(publishInfo.pom.scm.developerConnection)
                        url.set(publishInfo.pom.scm.url)
                    }
                }
            }
        }
    }

}

signing {
    sign(publishing.publications)
}