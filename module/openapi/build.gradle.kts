plugins {
    id("module-common")
}

dependencies {
    // Server
    api(project(":module:server"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                applyCommonPomValues(this)
                description = "A code-first OpenAPI schema annotation processor."
            }
        }
    }
    repositories {
        maven {
            url = uri(getPublishStagingDirectory(rootDir))
        }
    }
}
