package net.jacobpeterson.jet.server.handler.redirect;

import lombok.Builder;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response.RedirectType;
import net.jacobpeterson.jet.server.handler.Handler;
import org.jspecify.annotations.NullMarked;

import static com.google.common.base.Preconditions.checkState;

/**
 * {@link HostRedirectHandler} is a {@link Handler} to redirect {@link Request#getUrl()} to {@link Url#getHost()} set to
 * {@link #getHost()}. If {@link Request#getUrl()} {@link Url#getHost()} is equal to {@link #getHost()}, an exception is
 * thrown to expose the infinite redirect.
 */
@NullMarked
@Getter @Builder(toBuilder = true)
public class HostRedirectHandler implements Handler {

    /**
     * The {@link Url#getHost()} to redirect to.
     */
    private final String host;

    /**
     * The {@link RedirectType}.
     */
    private final RedirectType type;

    @Override
    public void handle(final Handle handle) {
        final var requestUrl = handle.getRequest().getUrl();
        final var requestHost = requestUrl.getHost();
        checkState(!requestHost.equals(host),
                "`HostRedirectHandler` cannot be used with a `Route` that does not match host: %s", requestHost);
        handle.getResponse().redirect(type, requestUrl.toBuilder().host(host).build());
    }
}
