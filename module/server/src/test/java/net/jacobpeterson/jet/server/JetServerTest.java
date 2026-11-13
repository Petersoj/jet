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
                        handle.getResponse().responseText(statusCode, statusDescription))
                .build();
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
        final var responseText = "Secure Text";
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
    public void sslPemPassphrase() throws Exception {
        final var trustAll = SSLContext.getInstance("TLS");
        trustAll.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }

            @Override
            public void checkClientTrusted(final X509Certificate[] certs, final String authType) {}

            @Override
            public void checkServerTrusted(final X509Certificate[] certs, final String authType) {}
        }}, new SecureRandom());
        final var sslCertificate = """
                -----BEGIN CERTIFICATE-----
                MIIDODCCAiCgAwIBAgIUC1y3R7XsGDkeHn7N4PuorHsxevIwDQYJKoZIhvcNAQEL
                BQAwFDESMBAGA1UEAwwJbG9jYWxob3N0MB4XDTI2MTExMzAzMjgwOFoXDTM2MTEx
                MDAzMjgwOFowFDESMBAGA1UEAwwJbG9jYWxob3N0MIIBIjANBgkqhkiG9w0BAQEF
                AAOCAQ8AMIIBCgKCAQEAmFEYofBhxW6WG78ZJHa1sgp4RdaJNzqIpfAV1qMn+qHT
                fItunmmAn0dzIX2NzQrgms0M2djBctK/3HSX9PKGI5+WZfzkMiXVmMZIEebsqQU0
                /QjIQn6qhztPMkzTK/qr8a4TKD2HmoXL/56YYPhwxtoMfrxrRhP2F5nQDFkK0ibH
                zWONPTl2ScyvSWAS5bSnOduS9Bkbw6Wa7/achNLRs1snMutOYopgC64EKDVd5ttD
                rCx/RvTbZ5ArlGAHuDziNz00G7ukbhJxfO/QOPEHlPJfhdnJd3KzssFShSAsO60K
                9LQd1t/uOLHjauwPneoVL7rRw53N4zmye6+u4qrDDQIDAQABo4GBMH8wHQYDVR0O
                BBYEFMg5AYZfHajr2dCAuluNbFNw/yksMB8GA1UdIwQYMBaAFMg5AYZfHajr2dCA
                uluNbFNw/yksMA8GA1UdEwEB/wQFMAMBAf8wLAYDVR0RBCUwI4IJbG9jYWxob3N0
                hwR/AAABhxAAAAAAAAAAAAAAAAAAAAABMA0GCSqGSIb3DQEBCwUAA4IBAQBmB55x
                jYbFonW/RBXxzwhMoInyHnZ2qzX7OJJJk7ePNcXQH4byOWJSVZvcBhnkpFjorUjT
                ucmv4udSoI5V8KbslIpK6X9ZPrUIBMnCrQ1x+arZDopbFnGRu45jWVgNXNc7QW7Z
                acFYMCpqGm9n+0ybAeksODsp2zQu9gvOHxjFxwgoH7z4CLIH+dM97/7D0VSOpT47
                Dql4PRQDN9UOwsCioVCzTTna0OSXC4kykd/GL24Ukos5VTKUrBQK6CzMp21mc54I
                CcIYiOOU7AyqPDgL2j2p8rT8NOGSaTmEYebrk85UD6PCIIjQ9y2gDQxBzHnjCXot
                c4LTUVTo8ZeKH4Ox
                -----END CERTIFICATE-----
                """;
        final var sslPrivateKey = """
                -----BEGIN ENCRYPTED PRIVATE KEY-----
                MIIFNTBfBgkqhkiG9w0BBQ0wUjAxBgkqhkiG9w0BBQwwJAQQJ9rhMdo513NSBBWu
                pg+xYgICCAAwDAYIKoZIhvcNAgkFADAdBglghkgBZQMEASoEEJq60t33q1w2xbT3
                +yqu8UEEggTQP1hoysNKkT+H+bXWwUQq6b9FGXdPqCqqot26UjuUoZF5T1vJMYMF
                9bU13f9TgZqoZre99C+dWFZJ/Ut1ftpBwEM7BAS00es25UINSeRRkVj0ZAl2VynR
                mhJUlfL+pePvkasZ9rccBkMcdF9TVhhWWTsnILtr/D7uJRPSuUTf9vMfhZ8/TOGg
                qbROQqERfkKL7FFDpe6WhPn1LQErgcKgbx369phRKoAJVbhAyM+64Ojss9ipGspa
                3NqGyZJw0n24EFhXOk7jYqRtHIuOZYK/HB7X9e0U0cLJIwZFI19JenD7NmnE+VV1
                WvhOwZnSkNpmypMgpdYidBjnWouSwwXcNhSCexgG+N5Yj+noa7hQbze28jIvKMA9
                UbGpUAoy2gWHhbUWnauedBQYS6rhQLqyZgbKMx6KK3AzKeQ556lXpTNTC4JKbY7r
                XYa8yJFpE+TKFhzFGEC1uo+F3JrrEzqzwMQHBQmLA9LJjN4+lz8dbd9czt4OQhAW
                JIaUpm3VE7SIVGZRHrVlVYST2hUbftG3fkuXhTBo9SJsUPSPBpoP01Mf0gx6Zoey
                UlbmUJvLAN0o0NjhEFze13jZkV9at9lJlJb+uzvhEkyamv1vk88J5uLeoEizUEyD
                Ys8oLM4ELgvPonLt0vxwdZ4kNBTwD3lM2PPZvYbbcK4gKaVaz3551ctD3OqydwvI
                /+g/pc4GltijAY+ZzXEbWkF6DoxJaqTxD6CZxqvmQ8nRpfJNsvC9lwGTsU2Piwnl
                r3wikUMGwxPjVYGdlDPj5jZeWA27qNF/cUSV/HHD/miDwPSWm5ucbPmIlYloczhy
                ovpw6IkTsuCoBwvSS2xvTvs7Nzrn5oKmHqzTH7V4QZaUT/cYrSu+EHHV7ABQ8EoU
                fp6ZhzTam6dfhNNtv+W0OgYWZKG7oqZh4RzshVTWJLI/3sp9Nt1fTmwSYjcWug1E
                VBemEPUmp7CvDgtBaer2sAGbabezngBNKU4LieL2NZNlf/hO1THExf50cFm1ZmCw
                ySsQfEf6Iz9y1acGMJQhsVxBamufEzoWX8UeL4ACjtBwe3ynM54ag57G85j7D7lO
                7EnD0dCYjTZo8N3um4ehGNC+hhAy7BuKhUgJxdMYcsOSFaFIbStTmdhwjlr8LflS
                zrrgr5rtv+WGjr8cv02ZpwGO4x6lVRbSnxxNdmQyxYk8P+6AY6rQCUQKnkyzWKsP
                s3tbioLyKzRaPFLKI/u7ychl3ye9/pM3DFt6LoxWo7kO1rEjiE/+6gHmrPNtdVAq
                kmklB5byBCwzyAJ6GH18PHWjn4Z8TuKBvEUVTJ9+hcgVclgyowOYzJmreBMB0v6s
                ies3rh7TznmJpjmmYLUztpx045oQEaTj5xaX/RedvtvabPGhIR8Dr+2ELDIrNRhb
                Ndp8eU9ZkOh/0Q0Hd6rBrxIvE3oxD8W4srdy7XwadJPXds1JZiyXSwbIWd6iMDBz
                qf0gGLDzgYLePPVdZqm29EGqmqbI8BfXT3dkayCPsv0AywcLBtHcSoHx3UTIKekN
                ujWu7kg4RWDZ4xlTtDJqUquqVZCrfO43+aycpYup1LwtDOS/wgRpJZil7Prvsztz
                Azt1W0rwfNSJ0+PTRbvXGen/rvqlrutz4p30usvM5aikUn09PxPt2ss=
                -----END ENCRYPTED PRIVATE KEY-----
                """;
        final var sslPassphrase = "passphrase";
        final var responseText = "Secure Text";
        try (final var jetServer = JetServer.builder()
                .sslPem(sslCertificate, sslPrivateKey, sslPassphrase)
                .router(handle -> handle.getResponse().responseText(responseText))
                .build();
                final var httpClient = HttpClient.newBuilder().sslContext(trustAll).build()) {
            assertEquals(responseText, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("https://localhost:" + jetServer.getHttpsPort()))
                    .GET()
                    .build(), ofString()).body());
        }
        try (final var jetServer = JetServer.builder()
                .http2(false)
                .sslPem(sslCertificate, sslPrivateKey, sslPassphrase)
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
