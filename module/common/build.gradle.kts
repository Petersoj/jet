plugins {
    id("module-common")
    id("module-maven-publication-for-jreleaser")
}

dependencies {
    // `jetty-client` depends on `jetty-server`, so including this dependency in the `common` module is fine.
    implementation("org.eclipse.jetty:jetty-server:12.1.7")

    setOf("brotli4j",
            "native-linux-x86_64", "native-linux-aarch64",
            "native-osx-x86_64", "native-osx-aarch64",
            "native-windows-x86_64", "native-windows-aarch64")
            .forEach { implementation("com.aayushatharva.brotli4j:${it}:1.20.0") }

    setOf("linux_amd64", "linux_aarch64",
            "darwin_x86_64", "darwin_aarch64",
            "win_amd64", "win_aarch64")
            .forEach { implementation("com.github.luben:zstd-jni:1.5.7-7:${it}") }
}

publishing {
    publications.getByName(MAVEN_PUBLICATION_NAME, MavenPublication::class).pom.description =
            "The common module for Jet libraries."
}
