plugins {
    `maven-publish`
}

publishing {
    publications.create(MAVEN_PUBLICATION_NAME, MavenPublication::class) {
        from(components["java"])
        pom {
            name = artifactId
            url = "https://$GITHUB_PROJECT_DOMAIN_PATH"
            inceptionYear = "2025"
            licenses {
                license {
                    name = "MIT License"
                    url = "https://opensource.org/licenses/MIT"
                }
            }
            developers {
                developer {
                    id = "Petersoj"
                    name = "Jacob Peterson"
                }
            }
            scm {
                connection = "scm:git:https://$GITHUB_PROJECT_DOMAIN_PATH.git"
                developerConnection = connection
                url = pom.url
            }
        }
    }
    repositories.maven(uri(getJReleaserDeployDirectory(rootProject.layout.buildDirectory)))
}
