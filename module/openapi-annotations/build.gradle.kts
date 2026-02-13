plugins {
    id("module-common")
}

dependencies {
    api(project(":module:server"))

    api("com.google.code.gson:gson:2.13.2")

    implementation("com.networknt:json-schema-validator:3.0.0")

    testImplementation("io.toolisticon.cute:cute:1.9.0")
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
