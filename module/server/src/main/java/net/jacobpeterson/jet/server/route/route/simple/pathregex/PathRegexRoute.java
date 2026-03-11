package net.jacobpeterson.jet.server.route.route.simple.pathregex;

import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.url.Scheme;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.route.route.Route;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link PathRegexRoute} is a {@link Route} for matching a request path against the given {@link Pattern}.
 */
@NullMarked
@Immutable
@Getter @RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY) @ToString
public final class PathRegexRoute implements Route {

    /**
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder for {@link PathRegexRoute}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private @Nullable String method;
        private @Nullable Method methodEnum;
        private @Nullable String scheme;
        private @Nullable Scheme schemeEnum;
        private @Nullable String host;
        private @Nullable Pattern pathPattern;
        private boolean useDecodedRequestPath = true;
        private boolean useNormalizedRequestPath = true;

        /**
         * @see #getMethod()
         */
        public Builder method(final String method) {
            this.method = method;
            return this;
        }

        /**
         * @see #getMethodEnum()
         */
        public Builder methodEnum(final Method methodEnum) {
            this.methodEnum = methodEnum;
            return this;
        }

        /**
         * @see #getScheme()
         */
        public Builder scheme(final String scheme) {
            this.scheme = scheme;
            return this;
        }

        /**
         * @see #getSchemeEnum()
         */
        public Builder schemeEnum(final Scheme schemeEnum) {
            this.schemeEnum = schemeEnum;
            return this;
        }

        /**
         * @see #getHost()
         */
        public Builder host(final String host) {
            this.host = host;
            return this;
        }

        /**
         * @see #getPathPattern()
         */
        public Builder pathPattern(final Pattern pathPattern) {
            this.pathPattern = pathPattern;
            return this;
        }

        /**
         * Calls {@link #pathPattern(Pattern)} with {@link Pattern#compile(String)}
         */
        public Builder pathRegex(final String pathRegex) {
            return pathPattern(Pattern.compile(pathRegex));
        }

        /**
         * @see #isUseDecodedRequestPath()
         */
        public Builder useDecodedRequestPath(final boolean useDecodedRequestPath) {
            this.useDecodedRequestPath = useDecodedRequestPath;
            return this;
        }

        /**
         * @see #isUseNormalizedRequestPath()
         */
        public Builder useNormalizedRequestPath(final boolean useNormalizedRequestPath) {
            this.useNormalizedRequestPath = useNormalizedRequestPath;
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link PathRegexRoute} instance.
         *
         * @return the built {@link PathRegexRoute}
         */
        public PathRegexRoute build() {
            return new PathRegexRoute(method, methodEnum, scheme, schemeEnum, host,
                    requireNonNull(pathPattern, "`pattern` must be set"), useDecodedRequestPath,
                    useNormalizedRequestPath);
        }
    }

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
     * The path regular expression (regex) {@link Pattern}.
     */
    private final Pattern pathPattern;

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

    @Override
    public @Nullable PathRegexRouteMatch match(final Handle handle) {
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
        final var matcher = pathPattern.matcher(requestPath);
        return matcher.matches() ? new PathRegexRouteMatch(matcher) : null;
    }
}
