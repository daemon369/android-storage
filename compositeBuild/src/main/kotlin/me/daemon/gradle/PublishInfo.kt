package me.daemon.gradle

data class PublishInfo(
    val artifactId: String,
    val artifactVersion: String,
    val pom: Pom,
) {
    data class Pom(
        val name: String,
        val description: String,
        val url: String,
        val scm: Scm,
    ) {
        data class Scm(
            val connection: String,
            val developerConnection: String,
            val url: String,
        )
    }
}