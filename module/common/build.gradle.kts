plugins {
    id("module-common")
}

publishing {
    publications {
        getByName<MavenPublication>("maven") {
            pom {
                description = "The common module for Jet libraries."
            }
        }
    }
}
