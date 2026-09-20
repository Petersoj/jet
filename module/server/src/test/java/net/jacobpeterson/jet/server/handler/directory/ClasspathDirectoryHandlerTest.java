package net.jacobpeterson.jet.server.handler.directory;

import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.route.simple.pathstartswith.PathStartsWithRoute;
import net.jacobpeterson.jet.server.router.simple.ImmutableSimpleRouter;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static java.net.http.HttpClient.Redirect.ALWAYS;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class ClasspathDirectoryHandlerTest {

    @Test
    public void simpleMutable() throws Exception {
        final var indexHtmlContent = "<h1>index.html</h1>\n";
        final var indexFilename = "index";
        final var indexHtmlFilename = indexFilename + ".html";
        final var aHtmlContent = "<h1>a.html</h1>\n";
        final var aFilename = "a";
        final var aHtmlFilename = aFilename + ".html";
        final var path = "/path";
        try (final var jetServer = JetServer.builder()
                .router(ImmutableSimpleRouter.builder()
                        .addLast(PathStartsWithRoute.builder().path(path + "/").build(),
                                ClasspathDirectoryHandler.simpleMutable(getClass(), "simplemutable", path, true))
                        .build())
                .build();
                final var httpClient = HttpClient.newBuilder().followRedirects(ALWAYS).build()) {
            assertEquals(indexHtmlContent, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path))
                    .GET()
                    .build(), ofString()).body());
            assertEquals(indexHtmlContent, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path + "/" + indexFilename))
                    .GET()
                    .build(), ofString()).body());
            assertEquals(indexHtmlContent, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path + "/" + indexHtmlFilename))
                    .GET()
                    .build(), ofString()).body());
            assertEquals(aHtmlContent, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path + "/" + aFilename))
                    .GET()
                    .build(), ofString()).body());
            assertEquals(aHtmlContent, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path + "/" + aHtmlFilename))
                    .GET()
                    .build(), ofString()).body());
            assertEquals(NOT_FOUND_404.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + "/non-existant"))
                    .GET()
                    .build(), ofString()).statusCode());
            assertEquals(NOT_FOUND_404.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path + "/non-existant"))
                    .GET()
                    .build(), ofString()).statusCode());
        }
    }
}
