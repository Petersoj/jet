import org.gradle.plugin.compatibility.compatibility
import org.jreleaser.model.api.signing.Signing.GPG_PASSPHRASE
import org.jreleaser.model.api.signing.Signing.GPG_SECRET_KEY
import org.jreleaser.util.Env.JRELEASER_ENV_PREFIX
import java.lang.System.getenv

plugins {
    id("module-common")
    id("io.github.gmazzo.gradle.testkit.jacoco") version "1.0.5"
    signing
    id("com.gradle.plugin-publish") version "2.1.0"
    id("org.gradle.plugin-compatibility") version "1.0.0"
}

dependencies {
    api(project(":module:openapi-annotations"))

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
    website = GITHUB_PROJECT_URL
    vcsUrl = website.map { "$it.git" }
    plugins.create("JetOpenApiAnnotationsPlugin") {
        val pluginPackage = "$JET_GROUP.openapiannotationsplugin"
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
    useInMemoryPgpKeys(getenv(JRELEASER_ENV_PREFIX + GPG_SECRET_KEY),
            getenv(JRELEASER_ENV_PREFIX + GPG_PASSPHRASE))
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
