package net.jacobpeterson.jet.server;

import net.jacobpeterson.jet.common.http.header.acceptencoding.AcceptEncoding;
import net.jacobpeterson.jet.common.http.header.acceptencoding.AcceptEncoding.Entry;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.nio.file.Files.writeString;
import static java.time.Duration.ofSeconds;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static net.jacobpeterson.jet.common.http.header.Header.ACCEPT_ENCODING;
import static net.jacobpeterson.jet.common.http.header.Header.ETAG;
import static net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding.BROTLI;
import static net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding.GZIP;
import static net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding.ZSTANDARD;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_OCTET_STREAM;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_PLAIN_UTF_8;
import static net.jacobpeterson.jet.common.http.status.Status.UNAUTHORIZED_401;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class JetServerTest {

    @Test
    public void handleThrowable() throws Exception {
        final var status = UNAUTHORIZED_401;
        try (final var jetServer = JetServer.builder()
                .router(_ -> { throw new StatusException(status); })
                .routerThrowableHandler((handle, statusCode, statusDescription, _) ->
                        handle.getResponse().responseText(statusCode, statusDescription)).build();
                final var httpClient = HttpClient.newHttpClient()) {
            assertEquals(status.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                    .GET()
                    .build(), ofByteArray()).statusCode());
        }
    }

    @Test
    public void handleDecompressionCompression() throws Exception {
        {
            final var responseContent = "Precompressed";
            final var responseContentType = TEXT_PLAIN_UTF_8;
            final var responseContentTypeCharset = requireNonNull(responseContentType.getCharset());
            final var responseContentEncoding = ZSTANDARD;
            final var responseBytes = responseContentEncoding.getType()
                    .compress(responseContent.getBytes(responseContentTypeCharset));
            final var computeETag = ETag.computeStrong(new ByteArrayInputStream(responseBytes));
            final var responseETag = computeETag.toBuilder().value(computeETag.getValue(),
                    responseContentEncoding.getType()).build();
            try (final var jetServer = JetServer.builder().router(handle -> {
                final var response = handle.getResponse();
                response.setContentEncoding(responseContentEncoding);
                response.setETag(responseETag);
                response.responseBytes(responseContentType, responseBytes);
            }).build(); final var httpClient = HttpClient.newHttpClient()) {
                final var acceptEncoding = GZIP;
                final var response = httpClient.send(HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                        .GET()
                        .header(ACCEPT_ENCODING.toString(), AcceptEncoding.builder()
                                .add(Entry.builder().value(acceptEncoding.toString()).build())
                                .build().toString())
                        .build(), ofByteArray());
                assertEquals(responseContent,
                        new String(acceptEncoding.getType().decompress(response.body()), responseContentTypeCharset));
                assertNotEquals(responseETag.toString(),
                        ETag.parse(response.headers().firstValue(ETAG.toString()).orElseThrow()).toString());
            }
        }
        {
            final var responseContent = new byte[1024 * 1024];
            ThreadLocalRandom.current().nextBytes(responseContent);
            final var responseContentEncoding = BROTLI;
            final var responseBytes = responseContentEncoding.getType().compress(responseContent);
            final var computeETag = ETag.computeWeak("name", responseContent.length, now().toEpochMilli());
            final var responseETag = computeETag.toBuilder().value(computeETag.getValue(),
                    responseContentEncoding.getType()).build();
            try (final var jetServer = JetServer.builder().router(handle -> {
                final var response = handle.getResponse();
                response.setContentEncoding(responseContentEncoding);
                response.setETag(responseETag);
                response.responseBytes(APPLICATION_OCTET_STREAM, responseBytes);
            }).build(); final var httpClient = HttpClient.newHttpClient()) {
                final var acceptEncoding = ZSTANDARD;
                final var response = httpClient.send(HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                        .GET()
                        .header(ACCEPT_ENCODING.toString(), AcceptEncoding.builder()
                                .add(Entry.builder().value(acceptEncoding.toString()).build())
                                .build().toString())
                        .build(), ofByteArray());
                assertArrayEquals(responseContent, acceptEncoding.getType().decompress(response.body()));
                assertNotEquals(responseETag.toString(),
                        ETag.parse(response.headers().firstValue(ETAG.toString()).orElseThrow()).toString());
            }
        }
    }

    @Test
    public void sslPem(final @TempDir Path tempDir) throws Exception {
        final var trustAll = SSLContext.getInstance("TLS");
        trustAll.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }

            @Override
            public void checkClientTrusted(final X509Certificate[] certs, final String authType) {}

            @Override
            public void checkServerTrusted(final X509Certificate[] certs, final String authType) {}
        }}, new SecureRandom());
        final var certificateFilename = "ssl.crt";
        final var keyFilename = "ssl.key";
        writeString(tempDir.resolve(certificateFilename), """
                -----BEGIN CERTIFICATE-----
                MIIBbDCCAR6gAwIBAgIUIh8cC9Hn11ARjvQ0QNqHdumlTxowBQYDK2VwMBQxEjAQ
                BgNVBAMMCWxvY2FsaG9zdDAeFw0yNjA5MTkwMzU4MTZaFw0zNjA5MTYwMzU4MTZa
                MBQxEjAQBgNVBAMMCWxvY2FsaG9zdDAqMAUGAytlcAMhACeft4uqHVj/+74PXJrT
                gy2XSQPEmTSyaHR2Y78gDZFEo4GBMH8wHQYDVR0OBBYEFCBsbQSplbyl9JGmJM2u
                ZLjdt+erMB8GA1UdIwQYMBaAFCBsbQSplbyl9JGmJM2uZLjdt+erMA8GA1UdEwEB
                /wQFMAMBAf8wLAYDVR0RBCUwI4IJbG9jYWxob3N0hwR/AAABhxAAAAAAAAAAAAAA
                AAAAAAABMAUGAytlcANBANefbv7Ne5RY9nQrFE6j8MOwhyrOwwrt+r2ojPPQBC55
                EgclVOWopJLd2P8B098tgME9HMdttPWIX+9ni6sT2A0=
                -----END CERTIFICATE-----
                """);
        writeString(tempDir.resolve(keyFilename), """
                -----BEGIN PRIVATE KEY-----
                MC4CAQAwBQYDK2VwBCIEIEZJk2YcPxuLF8claa7YXDI2Dslj4t7gRzSz3o5pYEAu
                -----END PRIVATE KEY-----
                """);
        final var responseText = "Encrypted Text";
        try (final var jetServer = JetServer.builder()
                .sslDirectory(tempDir,
                        path -> path.getFileName().toString().equals(certificateFilename),
                        path -> path.getFileName().toString().equals(keyFilename))
                .reloadSslPeriod(ofSeconds(1))
                .router(handle -> handle.getResponse().responseText(responseText))
                .build();
                final var httpClient = HttpClient.newBuilder().sslContext(trustAll).build()) {
            sleep(ofSeconds(2));
            assertEquals(responseText, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://localhost:" + jetServer.getHttpsPort()))
                    .GET()
                    .build(), ofString()).body());
        }
        try (final var jetServer = JetServer.builder()
                .http2(false)
                .sslDirectory(tempDir,
                        path -> path.getFileName().toString().equals(certificateFilename),
                        path -> path.getFileName().toString().equals(keyFilename))
                .router(handle -> handle.getResponse().responseText(responseText))
                .build();
                final var httpClient = HttpClient.newBuilder().sslContext(trustAll).build()) {
            assertEquals(responseText, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://localhost:" + jetServer.getHttpsPort()))
                    .GET()
                    .build(), ofString()).body());
        }
    }

    @Test
    public void addStopListener() {
        final var flag = new AtomicBoolean();
        try (final var jetServer = JetServer.builder().build()) {
            jetServer.addStopListener(() -> flag.setPlain(true));
        }
        assertTrue(flag.getPlain());
    }
}
