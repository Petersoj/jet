plugins {
    id("module-common")
}

dependencies {
    api(project(":module:common"))
}

publishing {
    publications.getByName(JRELEASER_MAVEN_NAME, MavenPublication::class).pom.description =
            "A code-first OpenAPI specification annotations library."
}
