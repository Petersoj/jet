package net.jacobpeterson.jet.server.route.router.simple;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import net.jacobpeterson.jet.server.route.route.Route;
import net.jacobpeterson.jet.server.route.router.Router;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static java.util.Map.entry;
import static lombok.AccessLevel.PRIVATE;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;

/**
 * {@link ImmutableSimpleRouter} is an immutable {@link Router} that uses a priority {@link List} of {@link Route}s
 * mapped to {@link Handler}s. This {@link Router} implementation will only call one {@link Handler} per
 * {@link #route(Handle)} call.
 */
@NullMarked
@RequiredArgsConstructor(access = PRIVATE)
public class ImmutableSimpleRouter implements Router {

    /**
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder class for {@link ImmutableSimpleRouter}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private final List<Entry<Route, Handler>> handlersOfRoutes = new ArrayList<>();

        /**
         * Adds the given {@link Handler} for the given {@link Route} to the beginning of the internal route entry list.
         *
         * @param route   the {@link Route}
         * @param handler the {@link Handler}
         */
        public Builder addFirst(final Route route, final Handler handler) {
            handlersOfRoutes.addFirst(entry(route, handler));
            return this;
        }

        /**
         * Adds the given {@link Handler} for the given {@link Route} to the end of the internal route entry list.
         *
         * @param route   the {@link Route}
         * @param handler the {@link Handler}
         */
        public Builder addLast(final Route route, final Handler handler) {
            handlersOfRoutes.addLast(entry(route, handler));
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link ImmutableSimpleRouter} instance.
         *
         * @return the built {@link ImmutableSimpleRouter}
         */
        public ImmutableSimpleRouter build() {
            return new ImmutableSimpleRouter(ImmutableList.copyOf(handlersOfRoutes));
        }
    }

    private final ImmutableList<Entry<Route, Handler>> handlersOfRoutes;

    @Override
    public void route(final Handle handle) {
        for (final var handlerOfRoute : handlersOfRoutes) {
            final var route = handlerOfRoute.getKey();
            final var match = route.match(handle);
            if (match != null) {
                final var internals = handle.getInternals();
                internals.setRouteOfMatch(route);
                internals.setRouteMatch(match);
                handlerOfRoute.getValue().handle(handle);
                return;
            }
        }
        throw new StatusException(NOT_FOUND_404);
    }
}
