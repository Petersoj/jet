package net.jacobpeterson.jet.server.route.route.simple.pathparameters;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.url.Scheme;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.route.route.Route;
import net.jacobpeterson.jet.server.route.route.simple.pathregex.PathRegexRoute;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link PathParametersRoute} is a {@link Route} that transforms the given path-parameterized route into a wrapped
 * {@link PathRegexRoute}. A path-parameterized route is a path that defines path parameters using braces e.g.
 * <code>/blog/{postId}/{commentId}</code>.
 */
@NullMarked
@Getter @RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY) @ToString
public final class PathParametersRoute implements Route {

    private static final Pattern PATH_PARAMETER_PATTERN = Pattern.compile("\\{([^/]+)}");
    private static final Pattern NAMED_CAPTURE_GROUP_CHECK_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");

    /**
     * Transforms the given <code>parameterizedPath</code> into a regular expression (regex) for matching the path
     * parameters as capture groups.
     *
     * @param parameterizedPath the parameterized path e.g. <code>/blog/{postId}/{commentId}</code>
     *
     * @return the regex {@link String}
     */
    public static String toRegex(final String parameterizedPath) {
        return "^" + PATH_PARAMETER_PATTERN.matcher("\\Q" +
                        PATH_PARAMETER_PATTERN.matcher(parameterizedPath).replaceAll("\\\\E{$1}\\\\Q") + "\\E")
                .replaceAll(matchResult -> {
                    final var group1 = matchResult.group(1);
                    checkArgument(NAMED_CAPTURE_GROUP_CHECK_PATTERN.matcher(group1).matches(),
                            "Path parameter name must be alphanumerics and start with a letter: %s", group1);
                    return "(?<$1>[^/]+)";
                }) + "$";
    }

    /**
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder for {@link PathParametersRoute}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private final PathRegexRoute.Builder builder = PathRegexRoute.builder();

        /**
         * @see PathRegexRoute.Builder#method(String)
         */
        public Builder method(final String method) {
            builder.method(method);
            return this;
        }

        /**
         * @see PathRegexRoute.Builder#methodEnum(Method)
         */
        public Builder methodEnum(final Method methodEnum) {
            builder.methodEnum(methodEnum);
            return this;
        }

        /**
         * @see PathRegexRoute.Builder#scheme(String)
         */
        public Builder scheme(final String scheme) {
            builder.scheme(scheme);
            return this;
        }

        /**
         * @see PathRegexRoute.Builder#schemeEnum(Scheme)
         */
        public Builder schemeEnum(final Scheme schemeEnum) {
            builder.schemeEnum(schemeEnum);
            return this;
        }

        /**
         * @see PathRegexRoute.Builder#host(String)
         */
        public Builder host(final String host) {
            builder.host(host);
            return this;
        }

        /**
         * Calls {@link #parameterizedPath(String, int)} with <code>patternFlags</code> set to <code>9</code>.
         */
        public Builder parameterizedPath(final String parameterizedPath) {
            return parameterizedPath(parameterizedPath, 0);
        }

        /**
         * Calls {@link PathRegexRoute.Builder#pathPattern(Pattern)} with {@link Pattern#compile(String, int)} with
         * {@link #toRegex(String)}.
         */
        @SuppressWarnings("MagicConstant")
        public Builder parameterizedPath(final String parameterizedPath, final int patternFlags) {
            builder.pathPattern(Pattern.compile(toRegex(parameterizedPath), patternFlags));
            return this;
        }

        /**
         * @see PathRegexRoute.Builder#useDecodedRequestPath(boolean)
         */
        public Builder useDecodedRequestPath(final boolean useDecodedRequestPath) {
            builder.useDecodedRequestPath(useDecodedRequestPath);
            return this;
        }

        /**
         * @see PathRegexRoute.Builder#useNormalizedRequestPath(boolean)
         */
        public Builder useNormalizedRequestPath(final boolean useNormalizedRequestPath) {
            builder.useNormalizedRequestPath(useNormalizedRequestPath);
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link PathParametersRoute} instance.
         *
         * @return the built {@link PathParametersRoute}
         */
        public PathParametersRoute build() {
            return new PathParametersRoute(builder.build());
        }
    }

    private final PathRegexRoute pathRegexRoute;

    @Override
    public @Nullable PathParametersRouteMatch match(final Handle handle) {
        final var match = pathRegexRoute.match(handle);
        if (match == null) {
            return null;
        }
        final var parameters = ImmutableMap.<String, String>builder();
        final var matcher = match.getMatcher();
        for (final var groupName : matcher.namedGroups().keySet()) {
            final var group = matcher.group(groupName);
            if (group != null) {
                parameters.put(groupName, group);
            }
        }
        return new PathParametersRouteMatch(parameters.build());
    }
}
