plugins {
    id("module-common")
}

group = "$group.openapi-annotations"

dependencies {
    api(project(":module:common"))

    api("com.google.code.gson:gson:2.13.2")
}

publishing {
    publications.getByName<MavenPublication>(MAVEN_PUBLICATION_NAME).pom.description =
            "A code-first OpenAPI specification annotations library."
}
