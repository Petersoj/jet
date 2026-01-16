import org.jreleaser.model.Active.ALWAYS
import org.jreleaser.model.Active.NEVER

plugins {
    base
    id("org.jreleaser") version "1.22.0"
}

jreleaser {
    signing {
        pgp {
            active = ALWAYS
            armored = true
        }
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(getJReleaserDeployDirectory(rootDir).path)
                }
            }
        }
    }
    release {
        github {
            uploadAssets = NEVER
        }
    }
}
