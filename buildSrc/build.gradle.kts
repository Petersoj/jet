plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Define versions for `module-common.gradle.kts` plugins:
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:9.2.0")
    implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:5.0.0")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.53.0")
}
