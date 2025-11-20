plugins {
    id("module-common")
}

dependencies {
    api(project(":module:server"))
}

publishing {
    publications {
        getByName<MavenPublication>("maven") {
            pom {
                description = "A code-first OpenAPI schema annotation processor."
            }
        }
    }
}
