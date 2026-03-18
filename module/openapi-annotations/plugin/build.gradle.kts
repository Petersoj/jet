import org.gradle.plugin.compatibility.compatibility
import org.jreleaser.model.api.signing.Signing.GPG_PASSPHRASE
import org.jreleaser.model.api.signing.Signing.GPG_SECRET_KEY
import org.jreleaser.util.Env.JRELEASER_ENV_PREFIX
import java.lang.System.getenv

plugins {
    id("module-common")
    `java-gradle-plugin`
    signing
    id("com.gradle.plugin-publish") version "2.1.0"
    id("org.gradle.plugin-compatibility") version "1.0.0"
    id("io.github.gmazzo.gradle.testkit.jacoco") version "1.0.5"
}

group = "$group.openapi-annotations"

dependencies {
    api(project(":module:openapi-annotations:annotations"))

    val jsonschemaGeneratorVersion = "5.0.0"
    api("com.github.victools:jsonschema-generator:$jsonschemaGeneratorVersion")
    api("com.github.victools:jsonschema-module-jackson:$jsonschemaGeneratorVersion")

    api("com.networknt:json-schema-validator:3.0.1")
}

// Gradle Test Kit already provides an SLF4j binding.
configurations.testRuntimeOnly {
    exclude("ch.qos.logback", "logback-classic")
}

val projectDescription = "A code-first OpenAPI specification annotations processor Gradle plugin."

gradlePlugin {
    website = "https://$GITHUB_PROJECT_DOMAIN_PATH"
    vcsUrl = "https://$GITHUB_PROJECT_DOMAIN_PATH.git"
    plugins.create("openApiAnnotationsPlugin") {
        id = "net.jacobpeterson.jet.openapiannotations.plugin"
        implementationClass = "net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsPlugin"
        displayName = "Jet OpenAPI Annotations Plugin"
        description = projectDescription
        tags = listOf("jet", "openapi", "annotations")
        compatibility {
            features.configurationCache = true
        }
    }
}

publishing {
    publications.getByName<MavenPublication>(MAVEN_PUBLICATION_NAME).pom.description = projectDescription
}

signing {
    useInMemoryPgpKeys(getenv(JRELEASER_ENV_PREFIX + GPG_SECRET_KEY),
            getenv(JRELEASER_ENV_PREFIX + GPG_PASSPHRASE))
    sign(publishing.publications)
}

tasks.withType(AbstractPublishToMaven::class) {
    dependsOn(tasks.withType(Sign::class))
}
