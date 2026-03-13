package net.jacobpeterson.jet.server.handler.handler.redirect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.url.Scheme;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import org.jspecify.annotations.NullMarked;

import static com.google.common.base.Preconditions.checkState;
import static net.jacobpeterson.jet.common.http.url.Scheme.HTTP;
import static net.jacobpeterson.jet.common.http.url.Scheme.HTTPS;

/**
 * {@link SecureRedirectHandler} is a {@link Handler} to redirect {@link Request#getUrl()} to {@link Scheme#HTTPS}. If
 * {@link Request#getUrl()} {@link Url#getSchemeEnum()} is not equal to {@link Scheme#HTTP}, an exception is thrown to
 * expose the infinite redirect.
 */
@NullMarked
@Getter @RequiredArgsConstructor
public class SecureRedirectHandler implements Handler {

    /**
     * <code>true</code> to use {@link Response#redirectPermanently(Url)}, <code>false</code> to use
     * {@link Response#redirectTemporarily(Url)}.
     */
    private final boolean permanent;

    @Override
    public void handle(final Handle handle) {
        final var requestUrl = handle.getRequest().getUrl();
        checkState(requestUrl.getSchemeEnum() == HTTP,
                "`SecureRedirectHandler` cannot be used with a `Route` that does not match `Scheme.HTTP`");
        final var redirectUrl = requestUrl.toBuilder().scheme(HTTPS).build();
        final var response = handle.getResponse();
        if (permanent) {
            response.redirectPermanently(redirectUrl);
        } else {
            response.redirectTemporarily(redirectUrl);
        }
    }
}
