package net.jacobpeterson.jet.server.route.route.simple.exact;

import com.google.errorprone.annotations.Immutable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.url.Scheme;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.route.route.Route;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link PathRoute} is a {@link Route} for matching a request path against the given route path.
 */
@NullMarked
@Immutable
@Getter @Builder(toBuilder = true) @EqualsAndHashCode(cacheStrategy = LAZY) @ToString
public final class PathRoute implements Route {

    /**
     * The method to match the request method against, or <code>null</code> for any.
     */
    private final @Nullable String method;

    /**
     * The {@link Method} to match the request {@link Method} against, or <code>null</code> for any.
     */
    private final @Nullable Method methodEnum;

    /**
     * The scheme to match the request scheme against, or <code>null</code> for any.
     */
    private final @Nullable String scheme;

    /**
     * The {@link Scheme} to match the request {@link Scheme} against, or <code>null</code> for any.
     */
    private final @Nullable Scheme schemeEnum;

    /**
     * The host to match the request host against, or <code>null</code> for any.
     */
    private final @Nullable String host;

    /**
     * The path to match the request path against.
     */
    private final String path;

    /**
     * Whether to use the decoded path from {@link Request#getUrl()}.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Default boolean useDecodedRequestPath = true;

    /**
     * Whether to use the normalized path from {@link Request#getUrl()}.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Default boolean useNormalizedRequestPath = true;

    /**
     * Whether to use case-insensitive matching against {@link #getPath()}.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final @Default boolean ignoreCase = true;

    @Override
    public @Nullable PathRouteMatch match(final Handle handle) {
        final var request = handle.getRequest();
        if (method != null && !method.equalsIgnoreCase(request.getMethod())) {
            return null;
        }
        if (methodEnum != null && methodEnum != request.getMethodEnum()) {
            return null;
        }
        final var requestUrl = request.getUrl();
        if (scheme != null && !scheme.equalsIgnoreCase(requestUrl.getScheme())) {
            return null;
        }
        if (schemeEnum != null && schemeEnum != requestUrl.getSchemeEnum()) {
            return null;
        }
        if (host != null && host.equalsIgnoreCase(requestUrl.getHost())) {
            return null;
        }
        final String requestPath;
        if (useNormalizedRequestPath) {
            requestPath = useDecodedRequestPath ? requestUrl.getNormalizedPath() :
                    requestUrl.getEncodedNormalizedPath();
        } else {
            requestPath = useDecodedRequestPath ? requestUrl.getPath() : requestUrl.getEncodedPath();
        }
        if (ignoreCase ? path.equalsIgnoreCase(requestPath) : path.equals(requestPath)) {
            return new PathRouteMatch(requestPath);
        }
        return null;
    }
}
