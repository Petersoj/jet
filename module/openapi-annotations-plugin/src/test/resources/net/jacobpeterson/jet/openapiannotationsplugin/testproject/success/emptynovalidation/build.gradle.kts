plugins {
    `java`
    id("net.jacobpeterson.jet.openapiannotationsplugin")
    id("jacoco-testkit-coverage")
}

group = "net.jacobpeterson.jet.openapiannotationsplugin.testproject"
version = "1.0.0"

dependencies {
    implementation(files(System.getenv("plugin-classpath").split('\n')))
}

jetOpenApiAnnotations {
    schemaValidation = false
}
