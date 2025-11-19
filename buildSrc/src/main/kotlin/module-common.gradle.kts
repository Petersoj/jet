plugins {
    `java-library`
    id("io.freefair.lombok")
    `maven-publish`
    id("com.github.ben-manes.versions")
}

group = "net.jacobpeterson.jet"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // Guava
    implementation("com.google.guava:guava:33.5.0-jre")
}

tasks.javadoc.configure {
    options {
        (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        addStringOption("link",
                "https://docs.oracle.com/en/java/javase/${java.targetCompatibility.majorVersion}/docs/api/")
    }
}
