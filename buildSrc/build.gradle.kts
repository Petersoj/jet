plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Define versions for `module-common.gradle.kts` plugins:
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:9.5.0")
    implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:5.1.1")
    implementation("io.github.ben-manes.versions:io.github.ben-manes.versions.gradle.plugin:0.61.0")
}
