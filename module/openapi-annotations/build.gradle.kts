plugins {
    id("module-common")
}

dependencies {
    api(project(":module:server"))

    implementation("com.google.code.gson:gson:2.13.2")

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
