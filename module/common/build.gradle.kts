plugins {
    id("module-common")
}

description = "The common module for various Jet modules."

dependencies {
    implementation("org.eclipse.jetty:jetty-http:12.1.13")

    setOf("brotli4j",
            "native-linux-x86_64", "native-linux-aarch64",
            "native-osx-x86_64", "native-osx-aarch64",
            "native-windows-x86_64", "native-windows-aarch64")
            .forEach { implementation("com.aayushatharva.brotli4j:${it}:1.23.0") }

    setOf("linux_amd64", "linux_aarch64",
            "darwin_x86_64", "darwin_aarch64",
            "win_amd64", "win_aarch64")
            .forEach { implementation("com.github.luben:zstd-jni:1.5.7-16:${it}") }
}
