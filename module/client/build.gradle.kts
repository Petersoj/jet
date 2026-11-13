plugins {
    id("module-common")
}

description = "A simple, lightweight, modern, turnkey, Java web client library."

dependencies {
    api(project(":module:common"))

    val jettyVersion = "12.1.15"
    implementation("org.eclipse.jetty:jetty-client:${jettyVersion}")
    implementation("org.eclipse.jetty.http2:jetty-http2-client:${jettyVersion}")
}
