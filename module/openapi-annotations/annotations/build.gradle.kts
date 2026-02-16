plugins {
    id("module-common")
}

dependencies {
    api(project(":module:common"))

    api("com.google.code.gson:gson:2.13.2")
}

publishing {
    publications {
        getByName<MavenPublication>("maven") {
            pom {
                description = "A code-first OpenAPI specification annotations library."
            }
        }
    }
}
