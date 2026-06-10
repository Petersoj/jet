<br><br>
<div align="center">
    <img src="https://raw.githubusercontent.com/Petersoj/jet/refs/heads/main/docs/logo.svg" alt="Logo" width="70%">
    <br><br><br><br>
    <a href="https://github.com/Petersoj/jet">
    <img src="https://img.shields.io/badge/GitHub_Repository-black?logo=github" alt="GitHub Repository"></a>
    <a href="https://petersoj.github.io/jet">
    <img src="https://img.shields.io/badge/GitHub_Pages-whitesmoke?logo=github&logoColor=black" alt="GitHub Pages"></a>
    <a href="https://github.com/Petersoj/jet/discussions">
    <img src="https://img.shields.io/badge/GitHub_Discussions-royalblue?logo=github" alt="GitHub Discussions"></a>
    <br>
    <a href="https://central.sonatype.com/search?namespace=net.jacobpeterson.jet">
    <img src="https://img.shields.io/badge/Maven_Central-3.2.0-blue?logo=apachemaven" alt="Maven Central"></a>
    <a href="https://plugins.gradle.org/plugin/net.jacobpeterson.jet.openapiannotationsplugin">
    <img src="https://img.shields.io/badge/Gradle_Plugin-3.2.0-blue?logo=gradle" alt="Gradle Plugin"></a>
    <a href="https://javadoc.io/doc/net.jacobpeterson.jet">
    <img src="https://img.shields.io/badge/javadoc-3.2.0-brightgreen" alt="javadoc"></a>
    <br>
    <a href="https://codecov.io/gh/Petersoj/jet">
    <img src="https://codecov.io/gh/Petersoj/jet/graph/badge.svg?token=Y8H056Y89E" alt="Codecov"></a>
    <a href="https://openjdk.org/projects/jdk/25">
    <img src="https://img.shields.io/badge/Java_Version-25-orange?logo=java" alt="Java Version"></a>
    <a href="https://github.com/Petersoj/jet/blob/main/LICENSE.txt">
    <img src="https://img.shields.io/badge/license-MIT-green" alt="License"></a>
</div>

Jet is a simple, lightweight, modern, turnkey, Java web client and server library.

## Table of Contents

- [Modules](#modules)
    - [Common](#common)
        - [Installation](#installation)
        - [Guide](#guide)
    - [Server](#server)
        - [Installation](#installation-1)
        - [Guide](#guide-1)
    - [OpenAPI Annotations](#openapi-annotations)
        - [Installation](#installation-2)
        - [Guide](#guide-2)
    - [OpenAPI Annotations Plugin](#openapi-annotations-plugin)
        - [Installation](#installation-3)
        - [Guide](#guide-3)
    - [Client](#client)
        - [Installation](#installation-4)
        - [Guide](#guide-4)

## Modules

### Common

The common module for various Jet modules.

#### Installation

This module is transitively depended on by the [Server](#server) and [Client](#client) modules, so you typically don't
need to install this module directly.

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:common:3.2.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:common:3.2.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>common</artifactId>
    <version>3.2.0</version>
</dependency>
```

#### Guide

// TODO finish writing guide

### Server

A simple, lightweight, modern, turnkey, Java web server library.

#### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:server:3.2.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:server:3.2.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>server</artifactId>
    <version>3.2.0</version>
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
    implementation("net.jacobpeterson.jet:openapi-annotations:3.2.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:openapi-annotations:3.2.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>openapi-annotations</artifactId>
    <version>3.2.0</version>
</dependency>
```

#### Guide

// TODO finish writing guide

### OpenAPI Annotations Plugin

A code-first OpenAPI specification annotations processor Gradle plugin.

#### Installation

For `build.gradle.kts`:

```kotlin
plugins {
    id("net.jacobpeterson.jet.openapiannotationsplugin") version "3.2.0"
}
```

For `build.gradle`:

```groovy
plugins {
    id 'net.jacobpeterson.jet.openapiannotationsplugin' version "3.2.0"
}
```

There is no Maven plugin available at this time.

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

A simple, lightweight, modern, turnkey, Java web client library.

#### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:client:3.2.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:client:3.2.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>client</artifactId>
    <version>3.2.0</version>
</dependency>
```

#### Guide

// TODO finish writing guide
