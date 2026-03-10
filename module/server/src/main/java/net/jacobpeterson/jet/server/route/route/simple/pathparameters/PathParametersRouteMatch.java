package net.jacobpeterson.jet.server.route.route.simple.pathparameters;

import lombok.Value;
import lombok.experimental.NonFinal;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import net.jacobpeterson.jet.server.route.route.simple.pathregex.PathRegexRouteMatch;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Matcher;

import static java.util.Objects.requireNonNull;

/**
 * {@link PathParametersRouteMatch} is a {@link RouteMatch} for {@link PathParametersRoute}.
 */
@NullMarked
@Value @NonFinal
public class PathParametersRouteMatch extends PathRegexRouteMatch {

    /**
     * Instantiates a new {@link PathParametersRouteMatch}.
     *
     * @param matcher the {@link Matcher}
     */
    public PathParametersRouteMatch(final Matcher matcher) {
        super(matcher);
    }

    /**
     * @return {@link #getMatcher()} {@link Matcher#group(String)}
     */
    public String get(final String key) {
        return requireNonNull(getMatcher().group(key));
    }
}
