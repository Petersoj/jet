# Jet

[![Maven Central](https://img.shields.io/badge/Maven_Central-1.0.1-blue?logo=apachemaven)](https://central.sonatype.com/search?namespace=net.jacobpeterson.jet)
[![Gradle Plugin](https://img.shields.io/badge/Gradle_Plugin-1.0.1-blue?logo=gradle)](https://plugins.gradle.org/plugin/net.jacobpeterson.jet.openapiannotationsplugin)
[![Javadoc](https://img.shields.io/badge/javadoc-1.0.1-brightgreen)](https://javadoc.io/doc/net.jacobpeterson.jet)
[![Codecov](https://codecov.io/gh/Petersoj/jet/graph/badge.svg?token=Y8H056Y89E)](https://codecov.io/gh/Petersoj/jet)
[![Java Version](https://img.shields.io/badge/Java_Version-25-orange?logo=java)](https://openjdk.org/projects/jdk/25)
[![GitHub License](https://img.shields.io/github/license/Petersoj/jet)](https://github.com/Petersoj/jet/blob/main/LICENSE.txt)

A simple, modern, turnkey, Java web client and server library.

## Table of Contents

- [Modules](#modules)
    - [Server](#server)
        - [Installation](#installation)
        - [Guide](#guide)
    - [OpenAPI Annotations](#openapi-annotations)
        - [Installation](#installation-1)
        - [Guide](#guide-1)
    - [OpenAPI Annotations Plugin](#openapi-annotations-plugin)
        - [Installation](#installation-2)
        - [Guide](#guide-2)
    - [Client](#client)
        - [Installation](#installation-3)
        - [Guide](#guide-3)
    - [Common](#common)
        - [Installation](#installation-4)
        - [Guide](#guide-4)

## Modules

### Server

A simple, modern, turnkey, Java web server library.

#### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:server:1.0.1")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:server:1.0.1'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>server</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Guide

// TODO finish writing guide

### OpenAPI Annotations

A code-first OpenAPI specification annotations library.

#### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:openapi-annotations:1.0.1")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:openapi-annotations:1.0.1'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>openapi-annotations</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Guide

// TODO finish writing guide

### OpenAPI Annotations Plugin

A code-first OpenAPI specification annotations processor Gradle plugin. There is no Maven plugin available at this time.

#### Installation

For `build.gradle.kts`:

```kotlin
plugins {
    id("net.jacobpeterson.jet.openapiannotationsplugin") version "1.0.1"
}
```

For `build.gradle`:

```groovy
plugins {
    id 'net.jacobpeterson.jet.openapiannotationsplugin' version "1.0.1"
}
```

#### Guide

This Gradle plugin registers a task named `jetOpenApiAnnotations` and an extension also named `jetOpenApiAnnotations`
with the following configurations:

[`javaCompileTasks = <Set<JavaCompile>>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getJavaCompileTasks())

[`schemaGeneratorConfigBuilderProvider = <SchemaGeneratorConfigBuilderProvider>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorConfigBuilderProvider())

[`schemaGeneratorUseNullableModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseNullableModule())

[`schemaGeneratorUseSchemaNameModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseSchemaNameModule())

[`schemaGeneratorUseGsonModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseGsonModule())

[`schemaGeneratorUseJacksonModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseJacksonModule())

[`schemaGeneratorSimpleTypeMappings = <Map<String, String>>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorSimpleTypeMappings())

[`generateOperationId = <DISABLED, FROM_CLASS_METHOD_NAME, FROM_METHOD_AND_PATH, BOTH>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getGenerateOperationId())

[`moveClassSchemasToComponents = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getMoveClassSchemasToComponents())

[`schemaValidation = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaValidation())

[`outputDirectory = <directory>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getOutputDirectory())

[`outputDirectoryIncludeInJar = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getOutputDirectoryIncludeInJar())

Example:

```kotlin
jetOpenApiAnnotations {
    schemaGeneratorUseGsonModule = true
    schemaGeneratorSimpleTypeMappings.put("java.net.InetAddress", """{"type": "string"}""")
}
```

### Client

A simple, modern, turnkey, Java web client library.

#### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:client:1.0.1")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:client:1.0.1'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>client</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Guide

// TODO finish writing guide

### Common

The common module for various Jet modules.

#### Installation

This module is transitively depended on by the Server and Client modules, so you typically don't need to install this
module directly.

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:common:1.0.1")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:common:1.0.1'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>common</artifactId>
    <version>1.0.1</version>
</dependency>
```

#### Guide

// TODO finish writing guide
