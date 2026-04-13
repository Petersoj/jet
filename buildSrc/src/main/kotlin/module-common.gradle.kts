import net.ltgt.gradle.errorprone.CheckSeverity.WARN
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.JavaVersion.VERSION_25
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    id("io.freefair.lombok")
    id("net.ltgt.errorprone")
    jacoco
    id("com.github.ben-manes.versions")
    `maven-publish`
}

group = JET_GROUP
version = JET_VERSION

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

    errorprone("com.google.errorprone:error_prone_core:2.48.0")
    errorprone("com.uber.nullaway:nullaway:0.13.1")
    errorprone("net.jacobpeterson:final-coat:1.2.1")

    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.5.32")
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

tasks.withType(Javadoc::class) {
    options {
        (this as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
        links = listOf(
                "https://docs.oracle.com/en/java/javase/${java.targetCompatibility.majorVersion}/docs/api/",
                "https://jspecify.dev/docs/api/",
                "https://guava.dev/releases/$guavaVersion/api/docs/",
                "https://errorprone.info/api/latest/")
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
    systemProperty("junit.jupiter.tempdir.cleanup.mode.default", "ON_SUCCESS")
    systemProperty("java.io.tmpdir", temporaryDir.path)
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showStandardStreams = true
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType(JacocoReport::class) {
    dependsOn(tasks.test)
    reports.xml.required = true
}

publishing {
    publications.create(JRELEASER_MAVEN_NAME, MavenPublication::class) {
        from(components["java"])
        pom {
            name = artifactId
            url = GITHUB_PROJECT_URL
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
                connection = pom.url.map { "scm:git:$it.git" }
                developerConnection = connection
                url = pom.url
            }
        }
    }
    repositories.maven {
        name = JRELEASER_MAVEN_NAME
        url = uri(rootProject.layout.buildDirectory.dir(JRELEASER_MAVEN_REPOSITORY_DIRECTORY))
    }
}
