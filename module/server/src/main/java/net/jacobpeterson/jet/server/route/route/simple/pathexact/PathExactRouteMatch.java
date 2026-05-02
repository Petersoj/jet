package net.jacobpeterson.jet.server.route.route.simple.pathexact;

import lombok.Value;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import org.jspecify.annotations.NullMarked;

/**
 * {@link PathExactRouteMatch} is a {@link RouteMatch} for {@link PathExactRoute}.
 */
@NullMarked
@Value
public class PathExactRouteMatch implements RouteMatch {

    /**
     * The request path used to match against.
     */
    String requestPath;
}
