plugins {
    id("module-common")
}

dependencies {
    implementation("org.eclipse.jetty:jetty-http:12.1.5")
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
