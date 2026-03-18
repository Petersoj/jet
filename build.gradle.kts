import org.jreleaser.model.Active.ALWAYS
import org.jreleaser.model.Active.NEVER

plugins {
    base
    id("org.jreleaser") version "1.23.0"
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
                    stagingRepository(getJReleaserDeployDirectory(layout.buildDirectory).get())
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
