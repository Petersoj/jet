plugins {
    id("module-common")
    id("module-maven-publication-for-jreleaser")
}

dependencies {
    api(project(":module:common"))
}

publishing {
    publications.getByName(MAVEN_PUBLICATION_NAME, MavenPublication::class).pom.description =
            "A simple, modern, turnkey, Java web client library."
}
