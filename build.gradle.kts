import org.jreleaser.model.Active.ALWAYS
import org.jreleaser.model.Active.NEVER

plugins {
    java
    `jacoco-report-aggregation`
    id("org.jreleaser") version "1.23.0"
}

group = JET_GROUP
version = JET_VERSION

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":module:common"))
    jacocoAggregation(project(":module:client"))
    jacocoAggregation(project(":module:server"))
    jacocoAggregation(project(":module:openapi-annotations:annotations"))
    jacocoAggregation(project(":module:openapi-annotations:plugin"))
}

tasks.withType(Test::class) {
    finalizedBy(tasks.testCodeCoverageReport)
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
