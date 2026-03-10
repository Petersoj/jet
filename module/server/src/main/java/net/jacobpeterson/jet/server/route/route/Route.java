package net.jacobpeterson.jet.server.route.route;

import net.jacobpeterson.jet.server.handle.Handle;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@link Route} is a {@link FunctionalInterface} for matching against the route of {@link Handle} instance.
 */
@NullMarked
@FunctionalInterface
public interface Route {

    /**
     * Matches the route of the given {@link Handle} to this {@link Route} implementation.
     * <p>
     * Note: this method implementation must be thread-safe.
     *
     * @param handle the {@link Handle}
     *
     * @return a {@link RouteMatch} instance if the {@link Handle} route matches this {@link Route}, <code>null</code>
     * otherwise
     */
    @Nullable RouteMatch match(final Handle handle);
}
