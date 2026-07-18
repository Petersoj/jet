plugins {
    id("module-common")
}

description = "A simple, lightweight, modern, turnkey, Java web server library."

val caffeineVersion = "3.2.4"

dependencies {
    api(project(":module:common"))

    api("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")

    val jettyVersion = "12.1.11"
    implementation("org.eclipse.jetty:jetty-alpn-java-server:${jettyVersion}")
    implementation("org.eclipse.jetty.http2:jetty-http2-server:${jettyVersion}")
    implementation("org.eclipse.jetty.websocket:jetty-websocket-jetty-server:${jettyVersion}")
}

tasks.withType(Javadoc::class).configureEach {
    options {
        (this as StandardJavadocDocletOptions).links(
                "https://javadoc.io/doc/com.github.ben-manes.caffeine/caffeine/$caffeineVersion")
    }
}
