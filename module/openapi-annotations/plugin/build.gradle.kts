plugins {
    id("module-common")
    `java-gradle-plugin`
    id("io.github.gmazzo.gradle.testkit.jacoco") version "1.0.5"
    id("com.gradle.plugin-publish") version "2.0.0"
}

dependencies {
    api(project(":module:openapi-annotations:annotations"))

    val jsonschemaGeneratorVersion = "5.0.0"
    api("com.github.victools:jsonschema-generator:$jsonschemaGeneratorVersion")
    api("com.github.victools:jsonschema-module-jackson:$jsonschemaGeneratorVersion")

    api("com.networknt:json-schema-validator:3.0.0")
}

publishing {
    publications.getByName<MavenPublication>("maven").pom.description =
            "A code-first OpenAPI specification annotations processor Gradle plugin."
}
