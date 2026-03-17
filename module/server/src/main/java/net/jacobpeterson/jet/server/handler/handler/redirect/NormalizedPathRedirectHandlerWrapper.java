package net.jacobpeterson.jet.server.handler.handler.redirect;

import lombok.Builder;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import org.jspecify.annotations.NullMarked;

/**
 * {@link NormalizedPathRedirectHandlerWrapper} is a {@link Handler} wrapper to redirect {@link Request#getUrl()} to
 * {@link Url#getNormalizedPath()}, if the request path is not already normalized.
 */
@NullMarked
@Getter @Builder(toBuilder = true)
public class NormalizedPathRedirectHandlerWrapper implements Handler {

    /**
     * The {@link Handler} to wrap.
     */
    private final Handler handler;

    /**
     * <code>true</code> to use {@link Response#redirectPermanently(Url)}, <code>false</code> to use
     * {@link Response#redirectTemporarily(Url)}.
     */
    private final boolean permanent;

    @Override
    public void handle(final Handle handle) {
        final var requestUrl = handle.getRequest().getUrl();
        final var encodedNormalizedPath = requestUrl.getEncodedNormalizedPath();
        if (!requestUrl.getEncodedPath().equals(encodedNormalizedPath)) {
            final var response = handle.getResponse();
            final var redirectUrl = requestUrl.toBuilder().path(encodedNormalizedPath).build();
            if (permanent) {
                response.redirectPermanently(redirectUrl);
            } else {
                response.redirectTemporarily(redirectUrl);
            }
        } else {
            handler.handle(handle);
        }
    }
}
