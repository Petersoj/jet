<br><br>
<div align="center">
    <a href="https://github.com/Petersoj/jet">
    <img src="https://raw.githubusercontent.com/Petersoj/jet/refs/heads/main/docs/logo.svg" alt="Logo" width="70%"></a>
    <br><br><br><br>
    <a href="https://github.com/Petersoj/jet">
    <img src="https://img.shields.io/badge/GitHub_Repository-black?logo=github" alt="GitHub Repository"></a>
    <a href="https://petersoj.github.io/jet">
    <img src="https://img.shields.io/badge/GitHub_Pages-whitesmoke?logo=github&logoColor=black" alt="GitHub Pages"></a>
    <a href="https://github.com/Petersoj/jet/discussions">
    <img src="https://img.shields.io/badge/GitHub_Discussions-royalblue?logo=github" alt="GitHub Discussions"></a>
    <br>
    <a href="https://central.sonatype.com/search?namespace=net.jacobpeterson.jet">
    <img src="https://img.shields.io/badge/Maven_Central-3.3.0-blue?logo=apachemaven" alt="Maven Central"></a>
    <a href="https://plugins.gradle.org/plugin/net.jacobpeterson.jet.openapiannotationsplugin">
    <img src="https://img.shields.io/badge/Gradle_Plugin-3.3.0-blue?logo=gradle" alt="Gradle Plugin"></a>
    <a href="https://javadoc.io/doc/net.jacobpeterson.jet">
    <img src="https://img.shields.io/badge/javadoc-3.3.0-brightgreen" alt="javadoc"></a>
    <br>
    <a href="https://codecov.io/gh/Petersoj/jet">
    <img src="https://codecov.io/gh/Petersoj/jet/graph/badge.svg?token=Y8H056Y89E" alt="Codecov"></a>
    <a href="https://openjdk.org/projects/jdk/25">
    <img src="https://img.shields.io/badge/Java_Version-25-orange?logo=java" alt="Java Version"></a>
    <a href="https://github.com/Petersoj/jet/blob/main/LICENSE.txt">
    <img src="https://img.shields.io/badge/license-MIT-green" alt="License"></a>
</div>
<br>

# Table of Contents

- [Introduction](#introduction)
- [Modules](#modules)
    - [Common](#common)
        - [Installation](#installation)
        - [Guide](#guide)
            - [Header Models](#header-models)
            - [Header Data Structures](#header-data-structures)
            - [Enums](#enums)
            - [URL Building](#url-building)
            - [I/O Utilities](#io-utilities)
            - [Other Utilities](#other-utilities)
    - [Server](#server)
        - [Installation](#installation-1)
        - [Guide](#guide-1)
            - [`JetServer.Builder` Configuration](#jetserverbuilder-configuration)
            - [`Handle`](#handle)
                - [Exceptions](#exceptions)
            - [Useful `Handler` Implementations](#useful-handler-implementations)
                - [Directory](#directory)
                - [Redirect](#redirect)
            - [`Router` Implementations](#router-implementations)
            - [`Route` Implementations](#router-implementations)
            - [`Session` & `SessionStore` Implementations](#session--sessionstore-implementations)
            - [Let's Encrypt SSL Certificates](#lets-encrypt-ssl-certificates)
    - [OpenAPI Annotations](#openapi-annotations)
        - [Installation](#installation-2)
        - [Guide](#guide-2)
    - [OpenAPI Annotations Plugin](#openapi-annotations-plugin)
        - [Installation](#installation-3)
        - [Guide](#guide-3)
            - [Example Configuration (`build.gradle.kts`)](#example-configuration-buildgradlekts)
            - [Why not an annotation processor?](#why-not-an-annotation-processor)
    - [Client](#client)
- [Personal Note About AI](#personal-note-about-ai)
- [Alternatives](#alternatives)

# Introduction

**Jet is a simple, lightweight, modern, turnkey, Java web client and server library.**

Jet is a wrapper around the excellent [Jetty](https://jetty.org) web client and server library. Jetty provides
the battle-tested low-level protocol handling, while Jet focuses on providing a modern and consistent interface with
superb documentation and an amazing developer experience.

Jet offers four modules: [Common](#common), [Server](#server), [OpenAPI Annotations](#openapi-annotations),
[OpenAPI Annotations Plugin](#openapi-annotations-plugin), and [Client](#client).

A few more awesome things about Jet:

- Exhaustive Javadoc documentation (all public classes, fields, and methods have Javadocs)
- Amazing developer experience
- High quality modern Java code (no AI slop)
- No runtime annotation magic
- A library, not a framework (structure your codebase however you prefer)
- Lightweight with minimal dependencies
- Wide code coverage via unit testing
- Releases are built directly from this source repository using GitHub Actions CI
- Kotlin friendly
- MIT licensed

Give this repository a star ⭐ and consider [sponsoring](https://github.com/sponsors/Petersoj) ❤️

# Modules

## Common

**The common module for various Jet modules.**

This module contains many useful model classes and utilities for all your web server and client needs. For example,
instead of crafting a response cookie header value using manual string concatenation, you can use
`Cookie.builder().name("name").value("value").httpOnly().secure().build().toResponseString()`. For another example,
instead of crafting a URL string using manual string concatenation, you can use
`Url.builder().scheme(HTTPS).host("example.com").addQueryParameter("key", "value").build().toString()`. There are many
header models supported, such as `Content-Security-Policy`, `ETag`, `ContentEncoding`. There are many enums supported,
such as `Method` (e.g. GET, POST), `Status` (e.g. 404 Not Found), `Version` (e.g. HTTP/2), and more. All of these
classes exist in an effort to improve developer experience, increase type-safety and single-source-of-truth (e.g. no
scattered variables or duplicate string constants), and decrease bugs. See the [guide](#guide) for all models classes
and utilities this module provides. 

### Installation

This module is transitively depended on by the [Server](#server) and [Client](#client) modules, so you typically don't
need to install this module directly.

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:common:3.3.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:common:3.3.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>common</artifactId>
    <version>3.3.0</version>
</dependency>
```

### Guide

Most classes in this module are immutable and follow the builder pattern for creation.

#### Header Models

- [`StrictTransportSecurity`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/stricttransportsecurity/StrictTransportSecurity.html)
- [`Range`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/range/Range.html)
- [`IfRange`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/ifrange/IfRange.html)
- [`ETag`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/etag/ETag.html)
- [`Cookie`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/cookie/Cookie.html)
- [`ContentType`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/contenttype/ContentType.html)
- [`ContentSecurityPolicy`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/contentsecuritypolicy/ContentSecurityPolicy.html)
- [`ContentRange`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/contentrange/ContentRange.html)
- [`ContentEncoding`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/contentencoding/ContentEncoding.html)
- [`ContentDisposition`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/contentdisposition/ContentDisposition.html)
- [`RequestCacheControl`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/cachecontrol/request/RequestCacheControl.html)
- [`ResponseCacheControl`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/cachecontrol/response/ResponseCacheControl.html)
- [`BasicAuthentication`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/authorization/BasicAuthentication.html)
- [`AcceptEncoding`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/acceptencoding/AcceptEncoding.html)
- [`Accept`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/accept/Accept.html)

#### Header Data Structures

Case-insensitive key-value pairs:

- [`Headers`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/headers/Headers.html)
- [`ImmutableHeaders`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/headers/ImmutableHeaders.html)

#### Enums

- [`Header`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/Header.html)
- [`Method`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/method/Method.html)
- [`Status`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/status/Status.html)
- [`Version`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/version/Version.html)

#### URL Building

- [`Url`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/url/Url.html)

#### I/O Utilities

- [`BoundedInputStream`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/io/bounded/BoundedInputStream.html)
- [`ReplacingInputStream`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/io/replacing/ReplacingInputStream.html)

#### Other Utilities

- [`NullableUtil`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/util/nullable/NullableUtil.html)

## Server

**A simple, lightweight, modern, turnkey, Java web server library.**

Features:

- HTTP/1, HTTP/1.1, HTTP/2
- HTTPS encryption with SSL/TSL certificate hot-swap reloading
- Custom routing and handlers
- Sessions
- Resource serving (classpath files, filesystem files, `InputStream`)
- Server-Sent Events (SSE)
- WebSockets (_coming soon_)
- Multipart request body
- Response body compression (Zstandard, Brotli, Gzip, Deflate)
- Virtual threads (no more ugly async/reactive programming)

### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:server:3.3.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:server:3.3.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>server</artifactId>
    <version>3.3.0</version>
</dependency>
```

### Guide

Here's a very simple example to get you going:

```java
static void main() {
    JetServer.builder()
            .sslLetsEncrypt() // Enable Let's Encrypt SSL/TLS
            .sessionStore() // Enable in-memory sessions
            .router(ImmutableSimpleRouter.builder()
                    // Add a custom handler
                    .addLast(PathExactRoute.builder().path("/custom-path").build(), handle ->
                            handle.getResponse().responseHtml("<h1>Custom handler!</h1>"))
                    // Serve files from `~/webroot/`
                    .addLast(PathStartsWithRoute.builder().path("/").build(), FileDirectoryHandler
                            .simpleMutable(Path.of(System.getProperty("user.home"), "webroot"), null, true))
                    .build())
            .build(); // Automatically starts the server and adds a JVM shutdown hook to stop the server gracefully
}
```

[`JetServer`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.html)
represents the web server instance. Use the builder to configure and start the web server.

#### [`JetServer.Builder`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html) Configuration

- [`handleFactory(HandleFactory)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#handleFactory(net.jacobpeterson.jet.server.handle.HandleFactory))
- [`sessionStore()`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#sessionStore())
- [`sessionStore(SessionStore)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#sessionStore(net.jacobpeterson.jet.server.session.SessionStore))
- [`router(Router)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#router(net.jacobpeterson.jet.server.router.Router))
- [`preventMimeSniffing(boolean)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#preventMimeSniffing(boolean))
- [`preventAmbiguousResponseCacheControl(boolean)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#preventAmbiguousResponseCacheControl(boolean))
- [`host(String)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#host(java.lang.String))
- [`httpPort(int)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#httpPort(int))
- [`httpsPort(int)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#httpsPort(int))
- [`http2(boolean)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#http2(boolean))
- [`sslLetsEncrypt()`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#sslLetsEncrypt())
- [`sslDirectory(Path, Predicate, Predicate)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#sslDirectory(java.nio.file.Path,java.util.function.Predicate,java.util.function.Predicate))
- [`sslPem(Path, Path)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#sslPem(java.nio.file.Path,java.nio.file.Path))
- [`sslPem(String, String)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#sslPem(java.lang.String,java.lang.String))
- [`sslPems(Supplier)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#sslPems(java.util.function.Supplier))
- [`reloadSslPeriod(Duration)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#reloadSslPeriod(java.time.Duration))
- [`gracefulStopTimeout(Duration)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#gracefulStopTimeout(java.time.Duration))
- [`connectionIdleTimeout(Duration)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#connectionIdleTimeout(java.time.Duration))
- [`connectionIdleTimeoutWhenStopping(Duration)`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/JetServer.Builder.html#connectionIdleTimeoutWhenStopping(java.time.Duration))

#### [`Handle`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/Handle.html)

[`Handle`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/Handle.html)
is a class that represents a web server request and response. It has getters for the
[`Request`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/request/Request.html)
and
[`Response`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/response/Response.html)
objects. Features: multipart request bodies, dynamic transparent response compression config,
[`Resource`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/response/resource/Resource.html) 
serving,
[`SSE`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/response/sse/Sse.html),
and more. For HTML templating, [Jte](https://jte.gg/) is recommended as it provides the best features with the best 
type-safety out of all major Java templating engine libraries.

The
[`Handler`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/Handler.html)
functional interface is provided a
[`Handle`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/Handle.html)
instance for the current HTTP request/response lifecycle. Using a functional interface makes it very easy to wrap/nest
`Handler` implementations, which is the mechanism for implementing middleware.

##### Exceptions

[`StatusException`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/response/exception/StatusException.html)
is thrown by some methods for `400 Bad Request` (for example if a request header is malformed) and is used as a silent
`Exception` for breaking the flow of execution in an implementation handler and returning a response status code.

[`BodyStreamException`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/exception/BodyStreamException.html)
is thrown by some methods for request/response body streaming client timeouts and early disconnects.

The `Handler` should not consume these exceptions. `JetServer` expects these exceptions to be thrown for special
processing (e.g. to set the response status upon a `StatusException` or to not log an error for `BodyStreamException`).

#### Useful [`Handler`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/Handler.html) Implementations

##### Directory

Handlers for serving files from the classpath or filesystem with support for relativizing request paths, serving a
default file (like `/index.html`) for request paths representing a directory, serving a default file for requests paths
representing a file without an extension (like `/index`), redirecting to default files or default extensions, applying a
[`ResponseCacheControl`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/header/cachecontrol/response/ResponseCacheControl.html),
and caching 
[`Resource`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handle/response/resource/Resource.html)
instances from with automatic cache invalidation using
[`WatchService`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/file/WatchService.html):

- [`ClasspathDirectoryHandler`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/directory/ClasspathDirectoryHandler.html)
- [`FileDirectoryHandler`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/directory/FileDirectoryHandler.html)

##### Redirect

Simple handlers for common redirection cases:

- [`HostRedirectHandler`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/redirect/HostRedirectHandler.html)
- [`NormalizedPathRedirectHandlerWrapper`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/redirect/NormalizedPathRedirectHandlerWrapper.html)
- [`SecureRedirectHandler`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/redirect/SecureRedirectHandler.html)

#### [`Router`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/router/Router.html) Implementations

These `Router` implementations represent a priority list for registered `Route` instances. Only one `Route` will be
matched for a request/response lifecycle.

- [`ImmutableSimpleRouter`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/router/simple/ImmutableSimpleRouter.html)
- [`SimpleRouter`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/router/simple/SimpleRouter.html)

#### [`Route`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/route/Route.html) Implementations

These `Route` implementations are added to the `Router` and tested for a match when a request is received.

- [`PathExactRoute`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/route/simple/pathexact/PathExactRoute.html)
- [`PathParametersRoute`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/route/simple/pathparameters/PathParametersRoute.html)
- [`PathRegexRoute`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/route/simple/pathregex/PathRegexRoute.html)
- [`PathStartsWithRoute`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/route/simple/pathstartswith/PathStartsWithRoute.html)

#### [`Session`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/session/Session.html) & [`SessionStore`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/session/SessionStore.html) Implementations

HTTP sessions store data server-side on behalf of a client. The client stores a cookie to be sent to the server to
reference the stored server-side data. These simple implementations store data in memory and have sensible defaults to
get you started with HTTP sessions:

- [`SimpleSession`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/session/simple/SimpleSession.html)
- [`SimpleSessionStore`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/session/simple/SimpleSessionStore.html)

A recommended, but more advanced, implementation is to use Redis/Valkey via Redisson with
[RLO](https://redisson.pro/docs/data-and-services/services/#live-object-service) for better type-safety and persisting
session data after a server restart.

#### Let's Encrypt SSL Certificates

`JetServer` has native support for [Let's Encrypt](https://letsencrypt.org/) SSL certificates. Follow these steps to add
HTTPS support to your web server for free. This guide assumes you're using an Ubuntu Linux VM hosted on an AWS EC2
instance, but can be adapted to other Linux distributions.

1. Install Java Amazon Corretto 25 for an unprivileged user named `jet`:
   - Via SDKMAN!:
     ```shell
     sudo useradd -ms /bin/bash jet
     sudo apt install zip
     # https://sdkman.io
     curl -s "https://get.sdkman.io" | sudo -u jet bash
     sudo -iu jet bash -ic "sdk install java 25.0.3-amzn"
     ```
   - Via Apt repositories:
     ```shell
     sudo useradd -ms /bin/bash jet
     # https://docs.aws.amazon.com/corretto/latest/corretto-25-ug/generic-linux-install.html
     wget -O - https://apt.corretto.aws/corretto.key | \ 
     sudo gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg
     echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" | \ 
     sudo tee /etc/apt/sources.list.d/corretto.list
     sudo apt-get update
     sudo apt-get install -y java-25-amazon-corretto-jdk
     ```

2. Install `certbot` and add a renewal hook that allows users in the `letsencrypt` group to read certificate files:
   ```shell
   sudo snap install certbot --classic
   sudo groupadd letsencrypt
   sudo usermod -aG letsencrypt jet
   sudo mkdir -p /etc/letsencrypt/renewal-hooks/deploy/
   sudo tee /etc/letsencrypt/renewal-hooks/deploy/change-permissions.sh <<EOF
   #!/bin/bash
   chgrp -R letsencrypt /etc/letsencrypt/{live,archive}
   chmod -R g+rX /etc/letsencrypt/{live,archive}
   EOF
   sudo chmod +x /etc/letsencrypt/renewal-hooks/deploy/change-permissions.sh
   ```

3. Update your DNS A (IPv4) and AAAA (IPv6) records to point to your server. Ensure your AWS EC2 Security Group allows
   ports `80` and `443` from any IPv4 or any IPv6 address. Configure `nftables` (successor to the `iptables`) to
   redirect privileged ports `80` and `443` to the unprivileged default web server ports `8080` and `8443`:
   ```shell
   sudo tee -a /etc/nftables.conf <<EOF
   table inet nat {
       chain prerouting {
           type nat hook prerouting priority dstnat; policy accept;
           tcp dport { 80, 443 } redirect to tcp dport map { 80 : 8080, 443 : 8443 }
       }
       chain output {
           type nat hook output priority dstnat; policy accept;
           fib daddr type local tcp dport { 80, 443 } redirect to tcp dport map { 80 : 8080, 443 : 8443 }
       }
   }
   EOF
   sudo systemctl enable --now nftables
   ```

4. In your Maven or Gradle project with the [Server](#server) module installed, use
   [`FileDirectoryHandler`](https://javadoc.io/doc/net.jacobpeterson.jet/server/latest/net/jacobpeterson/jet/server/handler/directory/FileDirectoryHandler.html)
   to serve a webroot for the `certbot` certificate renewal process and run your Maven or Gradle project as the `jet`
   user on your server:
   ```shell
   sudo -iu jet bash -ic "mkdir webroot"
   ```
   ```java
   JetServer.builder()
        .router(ImmutableSimpleRouter.builder()
                .addLast(PathStartsWithRoute.builder().path("/.well-known/acme-challenge/").build(),
                        FileDirectoryHandler.simpleMutable(Path.of("webroot"), null, true))
                .build())
        .build();
   ```

5. Now request your SSL certificate: `sudo certbot certonly -d <your_domain> --webroot /home/jet/webroot/` (specify
   `/home/jet/webroot/` as the webroot)

6. Now configure `JetServer.Builder` with `.sslLetsEncrypt()`:
   ```java
   JetServer.builder()
           .sslLetsEncrypt() // Automatically enables hot-swap SSL reloading
           .router(ImmutableSimpleRouter.builder()
                   // Keep the `FileDirectoryHandler` for future `certbot` auto-renewals.
                   .addLast(PathStartsWithRoute.builder().path("/.well-known/acme-challenge/").build(),
                           FileDirectoryHandler.simpleMutable(Path.of("webroot"), null, true))
                   .addLast(PathExactRoute.builder()
                           .schemeEnum(HTTPS)
                           .path("/")
                           .build(), handle -> handle.getResponse().responseText("HTTPS yay!"))
                   .build())
           .build();
   ```

Because of Jet Server's native support for SSL/TLS, it is generally not recommended to put Jet Server behind a reverse
proxy (such as Nginx) to handle SSL/TLS termination. Unless you have other web server requirements, like using PHP FPM
via Nginx to support PHP applications, you should use Jet Server as the main web server on your Linux VM.

## OpenAPI Annotations

**A code-first OpenAPI specification annotations library.**

This module provides Java annotations for all OpenAPI objects defined within the
[OpenAPI Description](https://spec.openapis.org/oas/v3.2.0.html#openapi-description-structure). This allows you to
define OpenAPI specifications directly within Java code using Java annotations so that your API specification lives
alongside the API implementation handlers. This module is built in a way that enforces type-safety and encourages
single-source-of-truth within API specifications. For example, instead of declaring the path
`"/api/account/profile-picture"` separately in your OpenAPI spec, in the Java web server handler code, and in the client
API request code (e.g. a JavaScript `fetch()` call), you can declare the path once as a string constant in Java,
reference that same constant within an OpenAPI annotation, generate an OpenAPI spec from those annotations, and then
generate a client library from that OpenAPI spec. This way, everything is type-safe and dervied from a
single-source-of-truth, which reduces bugs caused by typos, forgetting to update request paths and JSON schemas, etc.

One of the best features this module offers is
[`OpenApiSchema.fromClass()`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiSchema.html#fromClass())
which enables you to include JSON schemas generated directly from Java classes in your OpenAPI specification, which is
the pinnacle of type-safety and single-source-of-truth! The enables you to create a strong client-server API contract in
which the Java server defines the contract that all generated OpenAPI clients adhere to. The excellent
[victools/jsonschema-generator](https://github.com/victools/jsonschema-generator) library is used to generate the JSON
schemas in the [OpenAPI Annotations Plugin](#openapi-annotations-plugin), so generics, inheritance, nullability, etc.
are all supported! Note that you can enforce `@Nullable` annotations on the server in implementation handlers using
[`NullableUtil.requireNonNullFieldsSet()`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/util/nullable/NullableUtil.html#requireNonNullFieldsSet(java.lang.Object)).

### Installation

For `build.gradle.kts`:

```kotlin
dependencies {
    implementation("net.jacobpeterson.jet:openapi-annotations:3.3.0")
}
```

For `build.gradle`:

```groovy
dependencies {
    implementation 'net.jacobpeterson.jet:openapi-annotations:3.3.0'
}
```

For `pom.xml`:

```xml
<dependency>
    <groupId>net.jacobpeterson.jet</groupId>
    <artifactId>openapi-annotations</artifactId>
    <version>3.3.0</version>
</dependency>
```

### Guide

All OpenAPI objects defined within the OpenAPI description have been implemented, enabling you to define an OpenAPI
specification using Java annotations with no limitations:

- [`OpenApi`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApi.html)
- [`OpenApiCallback`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiCallback.html)
- [`OpenApiComponents`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiComponents.html)
- [`OpenApiContact`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiContact.html)
- [`OpenApiDiscriminator`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiDiscriminator.html)
- [`OpenApiEncoding`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiEncoding.html)
- [`OpenApiExample`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiExample.html)
- [`OpenApiExternalDoc`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiExternalDoc.html)
- [`OpenApiHeader`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiHeader.html)
- [`OpenApiInfo`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiInfo.html)
- [`OpenApiLicense`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiLicense.html)
- [`OpenApiLink`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiLink.html)
- [`OpenApiMediaType`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiMediaType.html)
- [`OpenApiOAuthFlow`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiOAuthFlow.html)
- [`OpenApiOAuthFlows`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiOAuthFlows.html)
- [`OpenApiOperation`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiOperation.html)
- [`OpenApiParameter`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiParameter.html)
- [`OpenApiPathItem`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiPathItem.html)
- [`OpenApiPaths`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiPaths.html)
- [`OpenApiReference`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiReference.html)
- [`OpenApiRequestBody`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiRequestBody.html)
- [`OpenApiResponse`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiResponse.html)
- [`OpenApiResponses`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiResponses.html)
- [`OpenApiSchema`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiSchema.html)
- [`OpenApiSecurityRequirement`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiSecurityRequirement.html)
- [`OpenApiSecurityScheme`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiSecurityScheme.html)
- [`OpenApiServer`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiServer.html)
- [`OpenApiServerVariable`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiServerVariable.html)
- [`OpenApiTag`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiTag.html)
- [`OpenApiXml`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiXml.html)

There are few quirks when working with Java annotations. First, annotation methods/values cannot be set to `null`, so
some OpenAPI annotation methods have been marked with the special annotation
[`AnnotationArrayIsNullableValue`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/meta/AnnotationArrayIsNullableValue.html)
to denote that the array represents a nullable value, meaning `null` is defined as an empty array and non-`null` is
defined as an array with a single element. Second, to support maps/dictionaries, some OpenAPI annotation methods have
been marked with the special annotation
[`AnnotationArrayIsMap`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/meta/AnnotationArrayIsMap.html)
to denote that the array represents a map with annotation entries that uses the special annotation
[`AnnotationArrayIsMapKey`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/meta/AnnotationArrayIsMapKey.html)
to denote the map key.

Note that some OpenAPI annotations encourage the use of the models and enums from [Common](#common) module. For example,
[`OpenApiPathItem.MethodEntry`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiPathItem.MethodEntry.html)
has both
[`key`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiPathItem.MethodEntry.html#key())
which can be set to a `String` constant, and
[`keyEnum`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiPathItem.MethodEntry.html#keyEnum())
which can be set to a
[`Method`](https://javadoc.io/doc/net.jacobpeterson.jet/common/latest/net/jacobpeterson/jet/common/http/method/Method.html)
enum. However, an unfortunate limitation of Java annotations with methods of enum types is that they cannot be set to
constant references and must be set to the enum constant directly. This makes implementing the single-source-of-truth
practice a bit harder, since, for example, you cannot declare the constant
`public static final Method METHOD = Method.GET` and use that constant in a type-safe way within the annotation
declaration and for the web server implementation handler route registration. A workaround is to use `String` references
instead, which Java allows enum methods to reference. The [Common](#common) module provides a `ToString` inner class for
all enums so they can be used within Java annotations as constant references. For example,
`public static final Method METHOD = Method.GET` becomes `public static final String METHOD = Method.ToString.GET`. Now
this string constant can be directly referenced both by the OpenAPI annotation and the  web server implementation
handler route registration, so we keep our type-safety and single-source-of-truth practice!

Here is an example of the recommended approach for using OpenAPI annotations and implementation handlers with Jet using
type-safety and single-source-of-truth practices. Note that this example uses static inner classes for `Web` and 
`AccountHandlers`, but ideally these would be separate class files, to better manage separation of concerns and to not
include so much code in one file.

```java
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import net.jacobpeterson.jet.openapiannotations.OpenApi;
import net.jacobpeterson.jet.openapiannotations.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.OpenApiMediaType;
import net.jacobpeterson.jet.openapiannotations.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.OpenApiParameter;
import net.jacobpeterson.jet.openapiannotations.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.OpenApiPaths;
import net.jacobpeterson.jet.openapiannotations.OpenApiResponse;
import net.jacobpeterson.jet.openapiannotations.OpenApiResponses;
import net.jacobpeterson.jet.openapiannotations.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.schemaname.SchemaName;
import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.route.simple.pathexact.PathExactRoute;
import net.jacobpeterson.jet.server.router.simple.ImmutableSimpleRouter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_JSON_STRING;
import static net.jacobpeterson.jet.common.http.method.Method.ToString.GET;
import static net.jacobpeterson.jet.common.http.status.Status.OK_200;
import static net.jacobpeterson.jet.openapiannotations.OpenApiParameter.ParameterLocation.QUERY;

@NullMarked
public class Server {

    @OpenApi(
            annotationGroupName = Web.PATH,
            info = @OpenApiInfo(
                    title = "Web",
                    version = "1.0.0"
            ),
            servers = @OpenApiServer(url = "http://localhost/" + Web.PATH)
    )
    public static final class Web {

        public static final String PATH = "web";

        private final @Getter AccountHandlers accountHandlers;

        public Web(final ImmutableSimpleRouter.Builder router) {
            accountHandlers = new AccountHandlers(router);
        }

        @SchemaName("Account")
        public static final class AccountHandlers {

            public AccountHandlers(final ImmutableSimpleRouter.Builder router) {
                router.addLast(PathExactRoute.builder()
                        .method(GetInfo.METHOD)
                        .path("/" + Web.PATH + GetInfo.PATH)
                        .build(), this::getInfo);
            }

            public static final String TAG_NAME = "account";

            public static final class GetInfo {

                public static final String METHOD = GET;
                public static final String PATH = "/" + TAG_NAME + "/info";
                public static final String QUERY_KEY_ID = "id";

                @Value @Builder
                public static class Success {

                    String name;
                    String email;
                    @Nullable String profilePictureUrl;
                }

                public enum FailReason {

                    NOT_LOGGED_IN,
                    INVALID_ID
                }

                @Value @Builder
                public static class Response {

                    @Nullable Success success;
                    @Nullable FailReason failReason;
                }
            }

            @OpenApi(annotationGroupName = PATH, paths = @OpenApiPaths(@OpenApiPathItem.MapEntry(
                    key = GetInfo.PATH, value = @OpenApiPathItem(methods = @OpenApiPathItem.MethodEntry(
                    key = GetInfo.METHOD, value = @OpenApiOperation(tags = TAG_NAME,
                    parameters = @OpenApiParameter(
                            name = GetInfo.QUERY_KEY_ID,
                            in = QUERY,
                            required = true,
                            schema = @OpenApiParameter.Schema(schema = @OpenApiSchema(fromClass = String.class))
                    ),
                    responses = @OpenApiResponses({@OpenApiResponse.MapEntry(
                            keyEnum = OK_200,
                            value = @OpenApiResponse(content = @OpenApiMediaType.MapEntry(
                                    key = APPLICATION_JSON_STRING,
                                    value = @OpenApiMediaType(schema = @OpenApiSchema(fromClass =
                                            GetInfo.Response.class))
                            ))
                    )})
            ))))))
            public void getInfo(final Handle handle) {
                if (<check_logged_in_logic>) {
                    handle.getResponse().responseJson(toJson(GetInfo.Response.builder()
                            .failReason(GetInfo.FailReason.NOT_LOGGED_IN)
                            .build()));
                    return;
                }
                final var id = handle.getRequest().getUrl().getQueryValue(GetInfo.QUERY_KEY_ID);
                if (<check_id_logic>) {
                    handle.getResponse().responseJson(toJson(GetInfo.Response.builder()
                            .failReason(GetInfo.FailReason.INVALID_ID)
                            .build()));
                    return;
                }
                <get_account_info_logic>
                handle.getResponse().responseJson(toJson(GetInfo.Response.builder()
                        .success(new GetInfo.Success(name, email, profilePictureUrl))
                        .build()));
            }
        }
    }

    static void main() {
        final var router = ImmutableSimpleRouter.builder();
        new Web(router);
        JetServer.builder().router(router.build()).build();
    }
}
```

## OpenAPI Annotations Plugin

**A code-first OpenAPI specification annotations processor Gradle plugin.**

This Gradle plugin generates OpenAPI specifications from the [OpenAPI annotations](#openapi-annotations) declared in
your Java source code. Then you can use the
[OpenAPI Generator Gradle plugin](https://plugins.gradle.org/plugin/org.openapi.generator) to generate OpenAPI client
libraries all within your Gradle build script. This creates an end-to-end solution for building APIs in a type-safe and
single-source-of-truth manner, all with great developer experience. For example, you have a model class called `GetInfo`
inside a class called `AccountHandlers`. The `GetInfo` class has the field `@Nullable String firstName`, but you want to
change it to `String firstName` denoting that it is no longer nullable. If you're using a generated OpenAPI TypeScript
client library, your TypeScript codebase will throw a compilation error for all the cases your frontend TypeScript code
uses `firstName` as a nullable value, allowing you to catch type-safety bugs for APIs at compile-time instead of at
runtime! This is how full-stack web development should have been all along!

### Installation

For `build.gradle.kts`:

```kotlin
plugins {
    id("net.jacobpeterson.jet.openapiannotationsplugin") version "3.3.0"
}
```

For `build.gradle`:

```groovy
plugins {
    id 'net.jacobpeterson.jet.openapiannotationsplugin' version "3.3.0"
}
```

There is no Maven plugin available at this time.

### Guide

This Gradle plugin registers a task named `jetOpenApiAnnotations` and an extension also named `jetOpenApiAnnotations`
with the following configurations:

- [`annotatedClassFiles = <files>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getAnnotatedClassFiles())
- [`classpaths = <files>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getClasspaths())
- [`schemaGeneratorConfigBuilderProvider = <SchemaGeneratorConfigBuilderProvider>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorConfigBuilderProvider())
- [`schemaGeneratorUseNullableModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseNullableModule())
- [`schemaGeneratorUseSchemaNameModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseSchemaNameModule())
- [`schemaGeneratorUseGsonModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseGsonModule())
- [`schemaGeneratorUseJacksonModule = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorUseJacksonModule())
- [`schemaGeneratorSimpleTypeMappings = <Map<String, String>>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaGeneratorSimpleTypeMappings())
- [`generateOperationId = <DISABLED, FROM_CLASS_METHOD_NAME, FROM_METHOD_AND_PATH, BOTH>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getGenerateOperationId())
- [`moveClassSchemasToComponents = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getMoveClassSchemasToComponents())
- [`schemaValidation = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getSchemaValidation())
- [`outputDirectory = <directory>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getOutputDirectory())
- [`outputDirectoryIncludeInJar = <true or false>`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations-plugin/latest/net/jacobpeterson/jet/openapiannotationsplugin/JetOpenApiAnnotationsExtension.html#getOutputDirectoryIncludeInJar())

#### Example Configuration (`build.gradle.kts`)

```kotlin
jetOpenApiAnnotations {
    schemaGeneratorUseGsonModule = true // Set to this `true` if you're using Gson
    schemaGeneratorUseJacksonModule = false // Set to this `true` if you're using Jackson
    // If you have a custom (de)serializer for `InetAddress` with Gson or Jackson, you can specify that every time
    // `InetAddress` is used in a model class for an OpenAPI schema, it should be treated as a string.
    schemaGeneratorSimpleTypeMappings.put("java.net.InetAddress", """{"type": "string"}""")
}
```

#### Why not an annotation processor?

The main reason is that annotation processors cannot load classes if the class is part of the source code currently
being compiled, so 
[`OpenApiSchema.fromClass()`](https://javadoc.io/doc/net.jacobpeterson.jet/openapi-annotations/latest/net/jacobpeterson/jet/openapiannotations/OpenApiSchema.html#fromClass())
would not be able to utilize the excellent
[victools/jsonschema-generator](https://github.com/victools/jsonschema-generator) library and the class file would
have to be manually inspected using `TypeMirror`. Additionally, annotation processors have various limitations, such as
no support for [`@CompileClasspath`](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/CompileClasspath.html)
for [Gradle incremental builds](https://docs.gradle.org/current/userguide/incremental_build.html).

## Client

**A simple, lightweight, modern, turnkey, Java web client library.**

The Client module is a WIP and will be released soon! See issue [#5](https://github.com/Petersoj/jet/issues/5).

# Personal Note About AI

100% of this codebase was written by a human. 100% of this guide was written by a human. No AI slop is allowed here. I
care deeply about good engineering and excellent code quality. AI does not. So, do I use AI? Of course! But it will stay
inside my web browser as a chatbot where I'll ask it questions about specific problems, like a glorified StackOverflow.
I remain in the driver's seat. It's not in my IDE where an AI autocomplete is constantly suggesting average buggy code, 
and certainly not where AI agents are mangling the codebase. Yes, AI autocompleted code can be reviewed, but I find
myself accepting lower quality code instead of critically thinking through each line and typing it myself, which almost
always yields higher quality code, but takes longer. LLMs fundamentally output the mean. I'm not saying that my code is
always perfect and better than AI, but I am saying that too many engineers are being sold a lie that if they aren't
constantly using AI, they will be left behind. Until there is a fundamental change in how LLMs work, this will remain my
opinion on the state of AI coding. `</rant>`

Contributions are very welcome, but keep these remarks in mind when submitting pull requests.

# Alternatives

Please note that the below list focuses on the weaknesses of the alternative projects compared to Jet's strengths. Jet
obviously has shortcomings compared to these other projects. Personal note: the below list is highly opinionated as a
result of my work as a full-stack software engineer, almost always using a Java backend.

- Jetty - excellent web server and client library (which is why Jet uses it), but has a complicated setup and lacks
  exhaustive Javadocs and header models

- Javalin - awesome library, but requires Kotlin dependency and lacks header models and exhaustive KDocs

- Spring - enterprise-grade, but bloated, uses lots of runtime annotation magic, and enforces an opinionated codebase 
  structure

- Vert.x Web - reactive programming is no longer necessary with the advent of Virtual Threads

- Apache Tomcat - strict legacy Jakarta Servlet specification adherence

- Spark Java - project abandoned
