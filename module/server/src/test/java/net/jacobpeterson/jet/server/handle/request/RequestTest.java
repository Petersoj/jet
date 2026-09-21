package net.jacobpeterson.jet.server.handle.request;

import net.jacobpeterson.jet.server.JetServer;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;

import static java.lang.String.format;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.Objects.requireNonNull;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class RequestTest {

    @Test
    public void getBodyString() throws Exception {
        try (final var jetServer = JetServer.builder()
                .router(handle -> handle.getResponse().responseText(handle.getRequest().getBodyString()))
                .build();
                final var httpClient = HttpClient.newHttpClient()) {
            final var body = "body";
            assertEquals(body, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                    .POST(BodyPublishers.ofString(body))
                    .build(), ofString()).body());
        }
    }

    @Test
    public void getBodyMultiPart() throws Exception {
        try (final var jetServer = JetServer.builder()
                .router(handle -> handle.getResponse().responseText(
                        requireNonNull(handle.getRequest().getBodyMultiPart("form")).getString()))
                .build();
                final var httpClient = HttpClient.newHttpClient()) {
            final var formData = "formData";
            assertEquals(formData, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                    .header(CONTENT_TYPE.toString(), "multipart/form-data; boundary=----Boundary")
                    .POST(BodyPublishers.ofString(format("""
                            ------Boundary
                            Content-Disposition: form-data; name="form"
                            Content-Type: text/plain
                            
                            %s
                            ------Boundary--
                            """, formData)))
                    .build(), ofString()).body());
        }
    }
}
