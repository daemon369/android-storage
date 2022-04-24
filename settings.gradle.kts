rootProject.name = "android-storage"
include(":demo")
include(":storage")


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases/") }
    }
    versionCatalogs {
        create("libs") {
            from("io.github.daemon369:android-version-catalog:1.0.3")
        }
    }
}