package net.jacobpeterson.jet.server.route.route.simple.pathregex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.route.route.Route;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

/**
 * {@link PathRegexRoute} is a {@link Route} for matching a request path against the given {@link Pattern}.
 */
@NullMarked
@Getter @RequiredArgsConstructor(access = PRIVATE)
public class PathRegexRoute implements Route {

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

        private @Nullable Pattern pattern;
        private boolean useDecodedRequestPath = true;
        private boolean useNormalizedRequestPath = true;

        /**
         * @see #getPattern()
         */
        public Builder pattern(final Pattern pattern) {
            this.pattern = pattern;
            return this;
        }

        /**
         * Calls {@link #pattern(Pattern)} with {@link Pattern#compile(String)}
         */
        public Builder regex(final String regex) {
            return pattern(Pattern.compile(regex));
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
            return new PathRegexRoute(requireNonNull(pattern, "`pattern` must be set"),
                    useDecodedRequestPath, useNormalizedRequestPath);
        }
    }

    /**
     * The regular expression (regex) {@link Pattern}.
     */
    private final Pattern pattern;

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
        final var requestUrl = handle.getRequest().getUrl();
        final String requestPath;
        if (useNormalizedRequestPath) {
            requestPath = useDecodedRequestPath ? requestUrl.getNormalizedPath() :
                    requestUrl.getEncodedNormalizedPath();
        } else {
            requestPath = useDecodedRequestPath ? requestUrl.getPath() : requestUrl.getEncodedPath();
        }
        final var matcher = pattern.matcher(requestPath);
        return matcher.matches() ? new PathRegexRouteMatch(matcher) : null;
    }
}
