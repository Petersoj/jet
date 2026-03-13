package net.jacobpeterson.jet.server.handler.handler.redirect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import org.jspecify.annotations.NullMarked;

import static com.google.common.base.Preconditions.checkState;

/**
 * {@link HostRedirectHandler} is a {@link Handler} to redirect a {@link Request#getUrl()} to {@link Url#getHost()} set
 * to the given {@link #getHost()}. If {@link Request#getUrl()} {@link Url#getHost()} is equal to {@link #getHost()}, an
 * exception is thrown to expose the infinite redirect.
 */
@NullMarked
@Getter @RequiredArgsConstructor
public class HostRedirectHandler implements Handler {

    /**
     * The {@link Url#getHost()} to redirect to.
     */
    private final String host;

    /**
     * <code>true</code> to use {@link Response#redirectPermanently(Url)}, <code>false</code> to use
     * {@link Response#redirectTemporarily(Url)}.
     */
    private final boolean permanent;

    @Override
    public void handle(final Handle handle) {
        final var requestUrl = handle.getRequest().getUrl();
        final var requestHost = requestUrl.getHost();
        checkState(!requestHost.equals(host),
                "`HostRedirectHandler` cannot be used with a `Route` that does not match host: %s", requestHost);
        final var redirectUrl = requestUrl.toBuilder().host(host).build();
        final var response = handle.getResponse();
        if (permanent) {
            response.redirectPermanently(redirectUrl);
        } else {
            response.redirectTemporarily(redirectUrl);
        }
    }
}
