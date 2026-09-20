package net.jacobpeterson.jet.server.session.simple;

import com.github.benmanes.caffeine.cache.Caffeine;
import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.route.simple.pathexact.PathExactRoute;
import net.jacobpeterson.jet.server.router.simple.ImmutableSimpleRouter;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static java.lang.String.format;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static net.jacobpeterson.jet.common.http.status.Status.BAD_REQUEST_400;
import static net.jacobpeterson.jet.common.http.status.Status.OK_200;
import static net.jacobpeterson.jet.server.session.simple.SimpleSessionStore.DEFAULT_COOKIE_NAME;
import static net.jacobpeterson.jet.server.session.simple.SimpleSessionStore.DEFAULT_ID_TOKEN_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class SimpleSessionTest {

    @Test
    public void test() throws Exception {
        final var sessionKey = "key";
        final var queryKeyValue = "value";
        final var getPath = "/get";
        final var setPath = "/set";
        final var deletePath = "/delete";
        try (final var jetServer = JetServer.builder()
                .sessionStore(new SimpleSessionStore(DEFAULT_ID_TOKEN_LENGTH, DEFAULT_COOKIE_NAME, null,
                        Caffeine.newBuilder().softValues().build()))
                .router(ImmutableSimpleRouter.builder()
                        .addLast(PathExactRoute.builder().path(getPath).build(), handle -> {
                            final var value = (String) handle.getSession().get(sessionKey);
                            final var response = handle.getResponse();
                            if (value != null) {
                                response.responseText(value);
                            } else {
                                response.setStatus(BAD_REQUEST_400);
                            }
                        }).addLast(PathExactRoute.builder().path(setPath).build(), handle ->
                                handle.getSession().set(sessionKey,
                                        handle.getRequest().getUrl().getQueryValue(queryKeyValue)))
                        .addLast(PathExactRoute.builder().path(deletePath).build(), handle ->
                                handle.getSession().remove(sessionKey))
                        .build())
                .build();
                final var httpClient = HttpClient.newBuilder().cookieHandler(new CookieManager()).build()) {
            final var host = "http://localhost:" + jetServer.getHttpPort();
            assertEquals(BAD_REQUEST_400.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(host + getPath))
                    .GET()
                    .build(), ofString()).statusCode());
            final var value = "value";
            assertEquals(OK_200.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(host + format("%s?%s=%s", setPath, queryKeyValue, value)))
                    .GET()
                    .build(), ofString()).statusCode());
            assertEquals(value, httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(host + getPath))
                    .GET()
                    .build(), ofString()).body());
            assertEquals(OK_200.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(host + deletePath))
                    .GET()
                    .build(), ofString()).statusCode());
            assertEquals(BAD_REQUEST_400.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(host + getPath))
                    .GET()
                    .build(), ofString()).statusCode());
        }
    }
}
