package net.jacobpeterson.jet.server.handle.response.sse;

import net.jacobpeterson.jet.server.JetServer;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class SseTest {

    private static final String DATA_PREFIX = "data: ";
    private static final String COMMENT_PREFIX = ": ";

    @Test
    public void test() throws Exception {
        final var sseData = "data";
        final var sseComment = "comment";
        try (final var jetServer = JetServer.builder()
                .router(handle -> handle.getResponse().sse(sse -> {
                    sse.send(sseData);
                    sse.comment(sseComment);
                    sse.close();
                })).build();
                final var httpClient = HttpClient.newHttpClient()) {
            final var body = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                    .GET()
                    .build(), ofInputStream()).body();
            var data = false;
            var comment = false;
            try (final var reader = new BufferedReader(new InputStreamReader(body, UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(DATA_PREFIX)) {
                        data = line.substring(DATA_PREFIX.length()).equals(sseData);
                    } else if (!line.isBlank()) {
                        comment = (line.startsWith(COMMENT_PREFIX) ? line.substring(COMMENT_PREFIX.length()) : line)
                                .equals(sseComment);
                    }
                }
            }
            assertTrue(data);
            assertTrue(comment);
        }
    }
}
