package net.jacobpeterson.jet.server.route.route.simple.pathstartswith;

import com.google.errorprone.annotations.Immutable;
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

import static com.google.common.base.Preconditions.checkArgument;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link PathStartsWithRoute} is a {@link Route} for matching a request path against the given route path using
 * {@link String#startsWith(String)}.
 */
@NullMarked
@Immutable
@Getter @EqualsAndHashCode(cacheStrategy = LAZY) @ToString
public final class PathStartsWithRoute implements Route {

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
     * <p>
     * Defaults to <code>true</code>.
     */
    private final boolean requirePathEndsWithSlash;

    /**
     * Whether to use the decoded path from {@link Request#getUrl()}.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final boolean useDecodedRequestPath;

    /**
     * Whether to use the normalized path from {@link Request#getUrl()}.
     * <p>
     * Defaults to <code>true</code>.
     */
    private final boolean useNormalizedRequestPath;

    /**
     * Instantiates a new Path starts with route.
     *
     * @param method                   the {@link #getMethod()}
     * @param methodEnum               the {@link #getMethodEnum()}
     * @param scheme                   the {@link #getScheme()}
     * @param schemeEnum               the {@link #getSchemeEnum()}
     * @param host                     the {@link #getHost()}
     * @param path                     the {@link #getPath()}
     * @param requirePathEndsWithSlash the {@link #isRequirePathEndsWithSlash()}
     * @param useDecodedRequestPath    the {@link #isUseDecodedRequestPath()}
     * @param useNormalizedRequestPath the {@link #isUseNormalizedRequestPath()}
     */
    @lombok.Builder(toBuilder = true)
    private PathStartsWithRoute(final @Nullable String method, final @Nullable Method methodEnum,
            final @Nullable String scheme, final @Nullable Scheme schemeEnum, final @Nullable String host,
            final String path, final @Nullable Boolean requirePathEndsWithSlash,
            final @Nullable Boolean useDecodedRequestPath, final @Nullable Boolean useNormalizedRequestPath) {
        this.method = method;
        this.methodEnum = methodEnum;
        this.scheme = scheme;
        this.schemeEnum = schemeEnum;
        this.host = host;
        this.path = path;
        pathEndsWithSlash = path.endsWith("/");
        pathWithoutEndingSlash = pathEndsWithSlash ? path.substring(0, path.length() - 1) : path;
        this.requirePathEndsWithSlash = requirePathEndsWithSlash == null || requirePathEndsWithSlash;
        checkArgument(pathEndsWithSlash || !this.requirePathEndsWithSlash,
                "`requirePathEndsWithSlash` is `true`, but the given `path` doesn't end with `/`");
        this.useDecodedRequestPath = useDecodedRequestPath == null || useDecodedRequestPath;
        this.useNormalizedRequestPath = useNormalizedRequestPath == null || useNormalizedRequestPath;
    }

    @Override
    public @Nullable PathStartsWithRouteMatch match(final Handle handle) {
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
        return requestPath.startsWith(path) || (pathEndsWithSlash && pathWithoutEndingSlash.equals(requestPath)) ?
                new PathStartsWithRouteMatch(requestPath) : null;
    }
}
