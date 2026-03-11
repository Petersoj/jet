package net.jacobpeterson.jet.server.route.route.simple.pathstartswith;

import lombok.Value;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import org.jspecify.annotations.NullMarked;

/**
 * {@link PathStartsWithRouteMatch} is a {@link RouteMatch} for {@link PathStartsWithRoute}.
 */
@NullMarked
@Value
public class PathStartsWithRouteMatch implements RouteMatch {

    /**
     * The request path used to match against.
     */
    String requestPath;
}
