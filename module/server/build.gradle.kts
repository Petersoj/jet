plugins {
    id("module-common")
}

dependencies {
    api(project(":module:common"))

    api("com.github.ben-manes.caffeine:caffeine:3.2.3")

    val jettyVersion = "12.1.8"
    implementation("org.eclipse.jetty:jetty-alpn-java-server:${jettyVersion}")
    implementation("org.eclipse.jetty.http2:jetty-http2-server:${jettyVersion}")
    implementation("org.eclipse.jetty.websocket:jetty-websocket-jetty-server:${jettyVersion}")

    implementation("dev.scheibelhofer:crypto-tools:0.0.8")
}

publishing {
    publications.getByName(JRELEASER_MAVEN_NAME, MavenPublication::class).pom.description =
            "A simple, modern, turnkey, Java web server library."
}
