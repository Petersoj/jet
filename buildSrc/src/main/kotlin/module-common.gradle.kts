import net.ltgt.gradle.errorprone.CheckSeverity.WARN
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.JavaVersion.VERSION_25
import org.gradle.api.tasks.testing.logging.TestLogEvent

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

val guavaVersion = "33.5.0-jre"

dependencies {
    api("org.jspecify:jspecify:1.0.0")
    api("com.google.guava:guava:$guavaVersion")

    implementation("org.slf4j:slf4j-api:2.0.17")

    errorprone("com.google.errorprone:error_prone_core:2.46.0")
    errorprone("com.uber.nullaway:nullaway:0.13.0")
    errorprone("net.jacobpeterson:final-coat:1.2.0")

    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.5.25")
}

tasks.withType(JavaCompile::class) {
    options.errorprone {
        allErrorsAsWarnings = true
        allSuggestionsAsWarnings = true
        disableWarningsInGeneratedCode = true

        disable("MissingSummary")
        disable("NullableOptional")
        check("Varifier", WARN)
        check("IdentifierName", WARN)
        check("MissingBraces", WARN)
        check("FieldCanBeFinal", WARN)
        check("MissingDefault", WARN)
        check("SwitchDefault", WARN)
        check("RedundantNullCheck", WARN)
        check("FieldMissingNullable", WARN)
        check("ParameterMissingNullable", WARN)
        check("ReturnMissingNullable", WARN)

        check("NullAway", WARN)
        option("NullAway:OnlyNullMarked", true)
        option("NullAway:JSpecifyMode", true)
        check("RequireExplicitNullMarking", WARN)

        check("FinalCoat", WARN)
    }
}

tasks.javadoc.configure {
    options {
        (this as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
        links = listOf(
                "https://docs.oracle.com/en/java/javase/${java.targetCompatibility.majorVersion}/docs/api/",
                "https://jspecify.dev/docs/api/",
                "https://guava.dev/releases/$guavaVersion/api/docs/",
                "https://errorprone.info/api/latest/")
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.tempdir.cleanup.mode.default", "ON_SUCCESS")
    systemProperty("java.io.tmpdir", temporaryDir.path)
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
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
                url = "https://github.com/$githubRepoPath"
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
                    connection = "scm:git:git://github.com/$githubRepoPath.git"
                    developerConnection = "scm:git:ssh://github.com:$githubRepoPath.git"
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
