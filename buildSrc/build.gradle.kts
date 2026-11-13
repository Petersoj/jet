plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Define versions for `module-common.gradle.kts` plugins:
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:9.8.3")
    implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:5.2.0")
    implementation("io.github.ben-manes.versions:io.github.ben-manes.versions.gradle.plugin:0.67.0")
}
