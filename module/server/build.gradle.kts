plugins {
    id("module-common")
}

val caffeineVersion = "3.2.4"

dependencies {
    api(project(":module:common"))

    api("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")

    val jettyVersion = "12.1.10"
    implementation("org.eclipse.jetty:jetty-alpn-java-server:${jettyVersion}")
    implementation("org.eclipse.jetty.http2:jetty-http2-server:${jettyVersion}")
    implementation("org.eclipse.jetty.websocket:jetty-websocket-jetty-server:${jettyVersion}")
}

tasks.withType(Javadoc::class) {
    options {
        (this as StandardJavadocDocletOptions).links(
                "https://javadoc.io/doc/com.github.ben-manes.caffeine/caffeine/$caffeineVersion")
    }
}

publishing {
    publications.getByName(JRELEASER_MAVEN_NAME, MavenPublication::class).pom.description =
            "A simple, lightweight, modern, turnkey, Java web server library."
}
