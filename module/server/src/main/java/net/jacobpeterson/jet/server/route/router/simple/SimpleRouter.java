package net.jacobpeterson.jet.server.route.router.simple;

import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import net.jacobpeterson.jet.server.route.route.Route;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import net.jacobpeterson.jet.server.route.router.Router;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static java.util.Collections.synchronizedList;
import static java.util.Map.entry;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;

/**
 * {@link SimpleRouter} is a {@link Router} that uses a priority {@link List} of {@link Route} to {@link Handler}
 * entries. This {@link Router} implementation will only call one {@link Handler} per {@link #route(Handle)} call.
 * <p>
 * This class is thread-safe.
 */
@NullMarked
public class SimpleRouter implements Router {

    private final List<Entry<Route, Handler>> handlersOfRoutes = synchronizedList(new ArrayList<>());

    /**
     * Adds the given {@link Handler} for the given {@link Route} to the beginning of the internal route entry list.
     *
     * @param route   the {@link Route}
     * @param handler the {@link Handler}
     */
    public void addFirst(final Route route, final Handler handler) {
        handlersOfRoutes.addFirst(entry(route, handler));
    }

    /**
     * Adds the given {@link Handler} for the given {@link Route} to the end of the internal route entry list.
     *
     * @param route   the {@link Route}
     * @param handler the {@link Handler}
     */
    public void addLast(final Route route, final Handler handler) {
        handlersOfRoutes.addLast(entry(route, handler));
    }

    /**
     * Removes the given {@link Route} from the internal route entry list.
     *
     * @param route the {@link Route}
     */
    public void remove(final Route route) {
        synchronized (handlersOfRoutes) {
            handlersOfRoutes.removeIf(entry -> entry.getKey().equals(route));
        }
    }

    @SuppressWarnings("NullAway") // TODO remove once NullAway false positives are fixed
    @Override
    public void route(final Handle handle) {
        Entry<Route, Handler> matchedHandlerOfRoute = null;
        RouteMatch routeMatch = null;
        synchronized (handlersOfRoutes) {
            for (final var handlerOfRoute : handlersOfRoutes) {
                final var match = handlerOfRoute.getKey().match(handle);
                if (match != null) {
                    matchedHandlerOfRoute = handlerOfRoute;
                    routeMatch = match;
                    break;
                }
            }
        }
        if (matchedHandlerOfRoute == null) {
            throw new StatusException(NOT_FOUND_404);
        }
        final var internals = handle.getInternals();
        internals.setRouteOfMatch(matchedHandlerOfRoute.getKey());
        internals.setRouteMatch(routeMatch);
        matchedHandlerOfRoute.getValue().handle(handle);
    }
}
