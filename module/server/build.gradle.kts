plugins {
    id("module-common")
    id("module-maven-publication-for-jreleaser")
}

dependencies {
    api(project(":module:common"))

    api("com.github.ben-manes.caffeine:caffeine:3.2.3")

    val jettyVersion = "12.1.7"
    implementation("org.eclipse.jetty:jetty-alpn-java-server:${jettyVersion}")
    implementation("org.eclipse.jetty.http2:jetty-http2-server:${jettyVersion}")
    implementation("org.eclipse.jetty.websocket:jetty-websocket-jetty-server:${jettyVersion}")
    implementation("org.eclipse.jetty.compression:jetty-compression-server:${jettyVersion}")
    implementation("org.eclipse.jetty.compression:jetty-compression-gzip:${jettyVersion}")
    implementation("org.eclipse.jetty.compression:jetty-compression-brotli:${jettyVersion}") {
        exclude("com.aayushatharva.brotli4j")
    }
    implementation("org.eclipse.jetty.compression:jetty-compression-zstandard:${jettyVersion}") {
        exclude("com.github.luben")
    }
}

publishing {
    publications.getByName(MAVEN_PUBLICATION_NAME, MavenPublication::class).pom.description =
            "A simple, modern, turnkey, Java web server library."
}
