import java.io.FileInputStream
import java.util.Properties

rootProject.extra["ossrhUsername"] = ""
rootProject.extra["ossrhPassword"] = ""
rootProject.extra["sonatypeStagingProfileId"] = ""
rootProject.extra["signing.keyId"] = ""
rootProject.extra["signing.password"] = ""
rootProject.extra["signing.secretKeyRingFile"] = ""

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    Properties().let { p ->
        FileInputStream(secretPropsFile).use { fis ->
            p.load(fis)
            p.stringPropertyNames().forEach { name ->
                val value = p.getProperty(name) as String
                println("[$name]->[$value]")
                rootProject.extra[name] = value
            }
        }
    }
}