package net.jacobpeterson.jet.server.handler.directory;

import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.route.simple.pathstartswith.PathStartsWithRoute;
import net.jacobpeterson.jet.server.router.simple.ImmutableSimpleRouter;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Path;

import static java.lang.Thread.sleep;
import static java.net.http.HttpClient.Redirect.ALWAYS;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.writeString;
import static java.time.Duration.ofSeconds;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class FileDirectoryHandlerTest {

    @Test
    public void simpleMutable(final @TempDir Path tempDir) throws Exception {
        final var indexHtmlContent = "<html><body><h1>index.html</h1></body></html>";
        final var indexFilename = "index";
        final var indexHtmlFilename = indexFilename + ".html";
        final var indexHtmlPath = tempDir.resolve(indexHtmlFilename);
        writeString(indexHtmlPath, indexHtmlContent);
        final var path = "/path";
        try (final var simpleMutable = FileDirectoryHandler.simpleMutable(tempDir, path, true);
                final var jetServer = JetServer.builder()
                        .router(ImmutableSimpleRouter.builder()
                                .addLast(PathStartsWithRoute.builder().path(path + "/").build(), simpleMutable)
                                .build())
                        .build();
                final var httpClient = HttpClient.newBuilder().followRedirects(ALWAYS).build()) {
            assertTrue(simpleMutable.isWatchServiceEnabled());
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
            assertEquals(NOT_FOUND_404.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + "/non-existant"))
                    .GET()
                    .build(), ofString()).statusCode());
            assertEquals(NOT_FOUND_404.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path + "/non-existant"))
                    .GET()
                    .build(), ofString()).statusCode());
            final var newIndexHtmlContent = "<html><body><h1>new index.html</h1></body></html>";
            writeString(indexHtmlPath, newIndexHtmlContent);
            sleep(ofSeconds(2));
            assertEquals(newIndexHtmlContent, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path))
                    .GET()
                    .build(), ofString()).body());
            delete(indexHtmlPath);
            assertEquals(NOT_FOUND_404.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path))
                    .GET()
                    .build(), ofString()).statusCode());
            sleep(ofSeconds(2));
            assertEquals(NOT_FOUND_404.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort() + path))
                    .GET()
                    .build(), ofString()).statusCode());
        }
    }
}
