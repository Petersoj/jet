plugins {
    `java`
    id("net.jacobpeterson.jet.openapiannotations.plugin")
    id("jacoco-testkit-coverage")
}

group = "net.jacobpeterson.jet.openapiannotations.plugin.testproject"
version = "1.0.0"

dependencies {
    implementation(files(System.getenv("plugin-classpath").split('\n')))
}
