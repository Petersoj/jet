package net.jacobpeterson.jet.server.route.route.simple.exact;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.route.route.Route;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@link PathRoute} is a {@link Route} for matching a request path against the given route path.
 */
@NullMarked
@Getter @Builder(toBuilder = true)
public class PathRoute implements Route {

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
        final var requestUrl = handle.getRequest().getUrl();
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
