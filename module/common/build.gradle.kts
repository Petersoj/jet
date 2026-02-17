plugins {
    id("module-common")
}

dependencies {
    // `jetty-client` depends on `jetty-server`, so including this dependency in the `common` module is fine.
    implementation("org.eclipse.jetty:jetty-server:12.1.5")
}

publishing {
    publications.getByName<MavenPublication>("maven").pom.description = "The common module for Jet libraries."
}
