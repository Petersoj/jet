package net.jacobpeterson.jet.server.route.router;

import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.HandleInternals;
import net.jacobpeterson.jet.server.handler.Handler;
import net.jacobpeterson.jet.server.route.route.Route;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Router} is a {@link FunctionalInterface} for routing a {@link Handle} to a {@link Handler} using
 * {@link Route#match(Handle)}.
 */
@NullMarked
@FunctionalInterface
public interface Router {

    /**
     * Routes the given {@link Handle} to a {@link Handler} using {@link Route#match(Handle)}.
     * <p>
     * Note: implementations must be thread-safe and call {@link HandleInternals#setRouteOfMatch(Route)} and
     * {@link HandleInternals#setRouteMatch(RouteMatch)}.
     *
     * @param handle the {@link Handle}
     */
    void route(final Handle handle);
}
