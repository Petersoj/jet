import org.gradle.api.publish.maven.MavenPom
import java.io.File

fun applyCommonPomValues(pom: MavenPom) {
    pom.url.set("https://github.com/Petersoj/jet")
    pom.inceptionYear.set("2025")
    pom.licenses {
        license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
        }
    }
    pom.developers {
        developer {
            id.set("Petersoj")
            name.set("Jacob Peterson")
        }
    }
    pom.scm {
        connection.set("scm:git:https://github.com/Petersoj/jet.git")
        developerConnection.set("scm:git:ssh://github.com/Petersoj/jet.git")
        url.set("https://github.com/Petersoj/jet")
    }
}

fun getPublishStagingDirectory(rootDir: File): File = File(rootDir, "build/publish-staging")
