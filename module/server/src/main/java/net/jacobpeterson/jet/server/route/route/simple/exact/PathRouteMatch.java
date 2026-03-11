package net.jacobpeterson.jet.server.route.route.simple.exact;

import lombok.Value;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import org.jspecify.annotations.NullMarked;

/**
 * {@link PathRouteMatch} is a {@link RouteMatch} for {@link PathRoute}.
 */
@NullMarked
@Value
public class PathRouteMatch implements RouteMatch {

    /**
     * The request path used to match against.
     */
    String requestPath;
}
