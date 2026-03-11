package net.jacobpeterson.jet.server.route.route.simple.pathparameters;

import com.google.common.collect.ImmutableMap;
import lombok.Value;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import org.jspecify.annotations.NullMarked;

import static java.util.Objects.requireNonNull;

/**
 * {@link PathParametersRouteMatch} is a {@link RouteMatch} for {@link PathParametersRoute}.
 */
@NullMarked
@Value
public class PathParametersRouteMatch implements RouteMatch {

    ImmutableMap<String, String> parameters;

    /**
     * @return {@link #getParameters()} {@link ImmutableMap#get(Object)}
     */
    public String get(final String key) {
        return requireNonNull(parameters.get(key));
    }
}
