import org.gradle.api.JavaVersion.VERSION_25

plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok")
    id("com.github.ben-manes.versions")
}

group = "net.jacobpeterson.jet"
version = "1.0.0"

java {
    sourceCompatibility = VERSION_25
    targetCompatibility = VERSION_25
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:33.5.0-jre")
}

tasks.javadoc.configure {
    options {
        (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        addStringOption("link",
                "https://docs.oracle.com/en/java/javase/${java.targetCompatibility.majorVersion}/docs/api/")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                url = "https://github.com/Petersoj/jet"
                inceptionYear = "2025"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "Petersoj"
                        name = "Jacob Peterson"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/Petersoj/jet.git"
                    developerConnection = "scm:git:ssh://github.com/Petersoj/jet.git"
                    url = "https://github.com/Petersoj/jet"
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(getJReleaserDeployDirectory(rootDir))
        }
    }
}
