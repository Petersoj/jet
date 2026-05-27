package net.jacobpeterson.jet.server.handler.redirect;

import lombok.Builder;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response.RedirectType;
import net.jacobpeterson.jet.server.handler.Handler;
import org.jspecify.annotations.NullMarked;

/**
 * {@link NormalizedPathRedirectHandlerWrapper} is a {@link Handler} wrapper to redirect {@link Request#getUrl()} to
 * {@link Url#getNormalizedPath()}, if the request path is not already normalized. This prevents multiple requests paths
 * from serving the same content e.g. helps establish the canonical request path of a resource.
 */
@NullMarked
@Getter @Builder(toBuilder = true)
public class NormalizedPathRedirectHandlerWrapper implements Handler {

    /**
     * The {@link Handler} to wrap.
     */
    private final Handler handler;

    /**
     * The {@link RedirectType}.
     */
    private final RedirectType type;

    @Override
    public void handle(final Handle handle) {
        final var requestUrl = handle.getRequest().getUrl();
        final var encodedNormalizedPath = requestUrl.getEncodedNormalizedPath();
        if (!requestUrl.getEncodedPath().equals(encodedNormalizedPath)) {
            handle.getResponse().redirect(type, requestUrl.toBuilder().path(encodedNormalizedPath).build());
        } else {
            handler.handle(handle);
        }
    }
}
