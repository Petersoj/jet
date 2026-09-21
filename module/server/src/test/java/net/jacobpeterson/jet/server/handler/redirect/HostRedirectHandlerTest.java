package net.jacobpeterson.jet.server.handler.redirect;

import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.route.simple.pathstartswith.PathStartsWithRoute;
import net.jacobpeterson.jet.server.router.simple.ImmutableSimpleRouter;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import static net.jacobpeterson.jet.common.http.status.Status.PERMANENT_REDIRECT_308;
import static net.jacobpeterson.jet.server.handle.response.Response.RedirectType.PERMANENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class HostRedirectHandlerTest {

    @Test
    public void test() throws Exception {
        try (final var jetServer = JetServer.builder()
                .router(ImmutableSimpleRouter.builder()
                        .addLast(PathStartsWithRoute.builder()
                                .host("localhost")
                                .path("/")
                                .build(), HostRedirectHandler.builder()
                                .host("www.localhost")
                                .type(PERMANENT)
                                .build())
                        .build())
                .build();
                final var httpClient = HttpClient.newHttpClient()) {
            assertEquals(PERMANENT_REDIRECT_308.getCode(), httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + jetServer.getHttpPort()))
                    .GET()
                    .build(), ofByteArray()).statusCode());
        }
    }
}
