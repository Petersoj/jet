plugins {
    id("module-common")
    id("module-maven-publication-for-jreleaser")
}

group = "$group.openapi-annotations"

dependencies {
    api(project(":module:common"))

    api("com.google.code.gson:gson:2.13.2")
}

publishing {
    publications.getByName(MAVEN_PUBLICATION_NAME, MavenPublication::class).pom.description =
            "A code-first OpenAPI specification annotations library."
}
