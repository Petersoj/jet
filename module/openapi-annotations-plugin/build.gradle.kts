import org.gradle.plugin.compatibility.compatibility
import org.jreleaser.model.api.signing.Signing.GPG_PASSPHRASE
import org.jreleaser.model.api.signing.Signing.GPG_SECRET_KEY
import org.jreleaser.util.Env.JRELEASER_ENV_PREFIX
import java.lang.System.getenv

plugins {
    id("module-common")
    id("io.github.gmazzo.gradle.testkit.jacoco") version "1.1.0"
    signing
    id("com.gradle.plugin-publish") version "2.1.1"
    id("org.gradle.plugin-compatibility") version "1.0.0"
}

val jsonSchemaGeneratorVersion = "5.0.0"

dependencies {
    api(project(":module:openapi-annotations"))

    implementation("com.google.code.gson:gson:2.14.0")

    api("com.github.victools:jsonschema-generator:$jsonSchemaGeneratorVersion")
    api("com.github.victools:jsonschema-module-jackson:$jsonSchemaGeneratorVersion")

    implementation("com.networknt:json-schema-validator:3.0.3")
}

// Gradle Test Kit already provides an SLF4j binding.
configurations.testRuntimeOnly {
    exclude("ch.qos.logback", "logback-classic")
}

tasks.withType(Javadoc::class) {
    options {
        (this as StandardJavadocDocletOptions).links(
                "https://javadoc.io/doc/com.github.victools/jsonschema-generator/$jsonSchemaGeneratorVersion",
                "https://javadoc.io/doc/com.github.victools/jsonschema-module-jackson/$jsonSchemaGeneratorVersion")
    }
}

val projectDescription = "A code-first OpenAPI specification annotations processor Gradle plugin."

gradlePlugin {
    website = PROJECT_GITHUB_URL
    vcsUrl = website.map { "$it.git" }
    plugins.create("JetOpenApiAnnotationsPlugin") {
        val pluginPackage = "$PROJECT_GROUP.openapiannotationsplugin"
        id = pluginPackage
        implementationClass = "$pluginPackage.JetOpenApiAnnotationsPlugin"
        displayName = "Jet OpenAPI Annotations Plugin"
        description = projectDescription
        tags = listOf("jet", "openapi", "annotations")
        compatibility {
            features.configurationCache = true
        }
    }
}

signing {
    val secretKey = getenv(JRELEASER_ENV_PREFIX + GPG_SECRET_KEY)
    val passphrase = getenv(JRELEASER_ENV_PREFIX + GPG_PASSPHRASE)
    if (secretKey != null && passphrase != null) {
        useInMemoryPgpKeys(secretKey, passphrase)
    }
}
tasks.withType(Sign::class).configureEach {
    val predicate = provider {
        gradle.taskGraph.allTasks.none {
            it is PublishToMavenLocal
        }
    }
    onlyIf {
        predicate.get()
    }
}

publishing {
    publications.getByName(JRELEASER_MAVEN_NAME, MavenPublication::class).pom.description = projectDescription
}

// The `com.gradle.plugin-publish` plugin creates `MavenRepository` publications that should not be published to
// `JRELEASER_MAVEN_NAME`, so use conditional publishing:
// https://docs.gradle.org/current/userguide/publishing_customization.html#sec:publishing_maven:conditional_publishing
tasks.withType(PublishToMavenRepository::class).configureEach {
    val predicate = provider {
        publication == publishing.publications[JRELEASER_MAVEN_NAME] &&
                repository == publishing.repositories[JRELEASER_MAVEN_NAME]
    }
    onlyIf {
        predicate.get()
    }
}

subprojects {
    tasks.configureEach {
        tasks.publishPlugins.configure {
            mustRunAfter(this@configureEach)
        }
    }
}
