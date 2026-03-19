plugins {
    id("module-common")
}

dependencies {
    api(project(":module:common"))

    api("com.google.code.gson:gson:2.13.2")
}

publishing {
    publications.getByName(JRELEASER_MAVEN_NAME, MavenPublication::class).pom.description =
            "A code-first OpenAPI specification annotations library."
}
