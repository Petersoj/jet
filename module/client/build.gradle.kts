plugins {
    id("module-common")
}

dependencies {
    api(project(":module:common"))
}

publishing {
    publications {
        getByName<MavenPublication>("maven") {
            pom {
                description = "A simple, modern, turnkey, Java web client library."
            }
        }
    }
}
