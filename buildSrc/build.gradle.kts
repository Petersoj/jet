plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(25)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Lombok for `module-common.gradle.kts`
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:9.1.0")

    // Gradle Versions Plugin for `module-common.gradle.kts`
    implementation("com.github.ben-manes:gradle-versions-plugin:0.53.0")
}
