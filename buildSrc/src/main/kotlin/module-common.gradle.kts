import net.ltgt.gradle.errorprone.CheckSeverity.WARN
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.JavaVersion.VERSION_25

plugins {
    `java-library`
    jacoco
    `maven-publish`
    id("io.freefair.lombok")
    id("net.ltgt.errorprone")
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
    errorprone("com.google.errorprone:error_prone_core:2.44.0")
    errorprone("com.uber.nullaway:nullaway:0.12.12")
    errorprone("net.jacobpeterson:final-coat:1.1.0")

    implementation("org.jspecify:jspecify:1.0.0")

    api("com.google.guava:guava:33.5.0-jre")

    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.5.21")
}

tasks.withType(JavaCompile::class) {
    options.errorprone {
        allErrorsAsWarnings = true
        allSuggestionsAsWarnings = true
        disableWarningsInGeneratedCode = true

        check("IdentifierName", WARN)
        check("MissingBraces", WARN)
        check("FieldCanBeFinal", WARN)
        check("MissingDefault", WARN)
        check("SwitchDefault", WARN)
        check("ReturnMissingNullable", WARN)
        check("Varifier", WARN)
        disable("MissingSummary")

        check("NullAway", WARN)
        option("NullAway:OnlyNullMarked", true)
        option("NullAway:JSpecifyMode", true)
        // TODO check("RequireExplicitNullMarking", WARN)

        check("FinalCoat", WARN)
    }
}

tasks.javadoc.configure {
    options {
        (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        addStringOption("link",
                "https://docs.oracle.com/en/java/javase/${java.targetCompatibility.majorVersion}/docs/api/")
        addStringOption("link", "https://guava.dev/releases/snapshot-jre/api/docs/")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name = artifactId
                val githubRepoPath = "Petersoj/jet"
                url = "https://github.com/${githubRepoPath}"
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
                    connection = "scm:git:git://github.com/${githubRepoPath}.git"
                    developerConnection = "scm:git:ssh://github.com:${githubRepoPath}.git"
                    url = pom.url
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
