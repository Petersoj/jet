package net.jacobpeterson.jet.server.handle.response.sse;

import net.jacobpeterson.jet.server.JetServer;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jacobpeterson.jet.server.handle.response.sse.Sse.COMMENT_PREFIX;
import static net.jacobpeterson.jet.server.handle.response.sse.Sse.DATA_PREFIX;
import static net.jacobpeterson.jet.server.handle.response.sse.Sse.KEEPALIVE_COMMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class SseTest {

    @Test
    public void testClientClose() throws Exception {
        try (final var jetServer = JetServer.builder()
                .router(handle -> handle.getResponse().sse(sse -> {
                    sleepUninterruptibly(500, MILLISECONDS);
                    var index = 0;
                    while (!sse.isCloseCalled()) {
                        sse.send(String.valueOf(index++));
                        sleepUninterruptibly(1, SECONDS);
                    }
                }, ofSeconds(1))).build();
                final var httpClient = HttpClient.newHttpClient()) {
            final var body = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                    .GET()
                    .build(), ofInputStream()).body();
            try (final var reader = new BufferedReader(new InputStreamReader(body, UTF_8))) {
                for (var index = 0; index < 3; index++) {
                    assertEquals(DATA_PREFIX + index, reader.readLine());
                    assertTrue(reader.readLine().isEmpty());
                    assertEquals(COMMENT_PREFIX + KEEPALIVE_COMMENT, reader.readLine());
                    assertTrue(reader.readLine().isEmpty());
                }
            }
        }
    }

    @Test
    public void testServerClose() throws Exception {
        try (final var jetServer = JetServer.builder()
                .router(handle -> handle.getResponse().sse(sse -> {
                    for (var index = 0; index < 3; index++) {
                        sleepUninterruptibly(1, SECONDS);
                        sse.send(String.valueOf(index));
                    }
                }, null)).build();
                final var httpClient = HttpClient.newHttpClient()) {
            final var body = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                    .GET()
                    .build(), ofInputStream()).body();
            try (final var reader = new BufferedReader(new InputStreamReader(body, UTF_8))) {
                var count = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    assertEquals(DATA_PREFIX + count++, line);
                }
            }
        }
    }
}
