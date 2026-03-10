package net.jacobpeterson.jet.server.route.route.simple.pathparameters;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.route.route.Route;
import net.jacobpeterson.jet.server.route.route.simple.pathregex.PathRegexRoute;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

/**
 * {@link PathParametersRoute} is a {@link Route} that transforms the given path-parameterized route into a wrapped
 * {@link PathRegexRoute}. A path-parameterized route is a path that defines path parameters using braces e.g.
 * <code>/blog/{postId}/{commentId}</code>.
 */
@NullMarked
@Getter @RequiredArgsConstructor(access = PRIVATE)
public class PathParametersRoute implements Route {

    private static final Pattern PATH_PARAMETERS_TO_REGEX = Pattern.compile("\\{([^/]+)}");

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
         * The parameterized path e.g. <code>/blog/{postId}/{commentId}</code>.
         */
        public Builder path(final String path) {
            builder.regex(PATH_PARAMETERS_TO_REGEX.matcher(path).replaceAll("(?<$1>[^/]+)"));
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
        return match == null ? null : new PathParametersRouteMatch(match.getMatcher());
    }
}
