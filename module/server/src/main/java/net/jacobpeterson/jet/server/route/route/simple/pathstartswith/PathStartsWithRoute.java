package net.jacobpeterson.jet.server.route.route.simple.pathstartswith;

import lombok.Getter;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.route.route.Route;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link PathStartsWithRoute} is a {@link Route} for matching a request path against the given route path using
 * {@link String#startsWith(String)}.
 */
@NullMarked
@Getter
public class PathStartsWithRoute implements Route {

    /**
     * The path to starts-with match a request path against.
     * <p>
     * If this path ends with a slash (<code>/</code>), then a request path is exact-matched against this path without
     * the ending slash, in addition to starts-with matching. For example, if {@link #getPath()} is
     * <code>/admin/</code>, then a request path of <code>/admin</code>, <code>/admin/</code>, or
     * <code>/admin/...</code> would all match.
     */
    private final String path;

    /**
     * <code>true</code> if {@link #getPath()} ends with <code>/</code>, <code>false</code> otherwise.
     */
    private final boolean pathEndsWithSlash;

    /**
     * {@link #getPath()} without an ending <code>/</code>.
     */
    private final String pathWithoutEndingSlash;

    /**
     * Whether to require {@link #getPath()} ends with a <code>/</code>.
     */
    private final boolean requirePathEndsWithSlash;

    /**
     * Whether to use the decoded path from {@link Request#getUrl()}.
     */
    private final boolean useDecodedRequestPath;

    /**
     * Whether to use the normalized path from {@link Request#getUrl()}.
     */
    private final boolean useNormalizedRequestPath;

    /**
     * Whether to use case-insensitive matching against {@link #getPath()}.
     */
    private final boolean ignoreCase;

    /**
     * @param path                     the {@link #getPath()}
     * @param requirePathEndsWithSlash the {@link #isRequirePathEndsWithSlash()}. Defaults to <code>true</code>
     * @param useDecodedRequestPath    the {@link #isUseDecodedRequestPath()}. Defaults to <code>true</code>
     * @param useNormalizedRequestPath the {@link #isUseNormalizedRequestPath()}. Defaults to <code>true</code>
     * @param ignoreCase               the {@link #isIgnoreCase()}. Defaults to <code>true</code>
     */
    @lombok.Builder(toBuilder = true)
    private PathStartsWithRoute(final String path, final @Nullable Boolean requirePathEndsWithSlash,
            final @Nullable Boolean useDecodedRequestPath, final @Nullable Boolean useNormalizedRequestPath,
            final @Nullable Boolean ignoreCase) {
        this.path = path;
        pathEndsWithSlash = path.endsWith("/");
        pathWithoutEndingSlash = pathEndsWithSlash ? path.substring(0, path.length() - 1) : path;
        this.requirePathEndsWithSlash = requirePathEndsWithSlash == null || requirePathEndsWithSlash;
        checkArgument(pathEndsWithSlash || !this.requirePathEndsWithSlash,
                "`requirePathEndsWithSlash` is `true`, but the given `path` doesn't end with `/`");
        this.useDecodedRequestPath = useDecodedRequestPath == null || useDecodedRequestPath;
        this.useNormalizedRequestPath = useNormalizedRequestPath == null || useNormalizedRequestPath;
        this.ignoreCase = ignoreCase == null || ignoreCase;
    }

    @Override
    public @Nullable PathStartsWithRouteMatch match(final Handle handle) {
        final var requestUrl = handle.getRequest().getUrl();
        final String requestPath;
        if (useNormalizedRequestPath) {
            requestPath = useDecodedRequestPath ? requestUrl.getNormalizedPath() :
                    requestUrl.getEncodedNormalizedPath();
        } else {
            requestPath = useDecodedRequestPath ? requestUrl.getPath() : requestUrl.getEncodedPath();
        }
        if (requestPath.regionMatches(ignoreCase, 0, path, 0, path.length()) || (pathEndsWithSlash && (ignoreCase ?
                pathWithoutEndingSlash.equalsIgnoreCase(requestPath) : pathWithoutEndingSlash.equals(requestPath)))) {
            return new PathStartsWithRouteMatch(requestPath);
        }
        return null;
    }
}
