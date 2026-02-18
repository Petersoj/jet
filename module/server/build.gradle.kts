plugins {
    id("module-common")
}

dependencies {
    api(project(":module:common"))

    val brotliDependencyGroup = "com.aayushatharva.brotli4j"
    setOf("brotli4j",
            "native-linux-x86_64", "native-linux-aarch64",
            "native-osx-x86_64", "native-osx-aarch64",
            "native-windows-x86_64", "native-windows-aarch64")
            .forEach { implementation("${brotliDependencyGroup}:${it}:1.20.0") }

    val zstdDependencyGroup = "com.github.luben"
    setOf("linux_amd64", "linux_aarch64",
            "darwin_x86_64", "darwin_aarch64",
            "win_amd64", "win_aarch64")
            .forEach { implementation("${zstdDependencyGroup}:zstd-jni:1.5.7-6:${it}") }

    val jettyVersion = "12.1.6"
    implementation("org.eclipse.jetty:jetty-alpn-java-server:${jettyVersion}")
    implementation("org.eclipse.jetty.http2:jetty-http2-server:${jettyVersion}")
    implementation("org.eclipse.jetty.websocket:jetty-websocket-jetty-server:${jettyVersion}")
    implementation("org.eclipse.jetty.compression:jetty-compression-server:${jettyVersion}")
    implementation("org.eclipse.jetty.compression:jetty-compression-gzip:${jettyVersion}")
    implementation("org.eclipse.jetty.compression:jetty-compression-brotli:${jettyVersion}") {
        exclude(brotliDependencyGroup)
    }
    implementation("org.eclipse.jetty.compression:jetty-compression-zstandard:${jettyVersion}") {
        exclude(zstdDependencyGroup)
    }
}

publishing {
    publications.getByName<MavenPublication>("maven").pom.description =
            "A simple, modern, turnkey, Java web server library."
}
