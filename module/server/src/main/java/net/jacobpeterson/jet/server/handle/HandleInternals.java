package net.jacobpeterson.jet.server.handle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.server.Jet;
import net.jacobpeterson.jet.server.route.route.Route;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

/**
 * {@link HandleInternals} is a class to be internally instantiated by Jet and provided to {@link Handle} subclass
 * constructors.
 * <p>
 * Note: the fields in this class are for internal use only.
 */
@NullMarked
@RequiredArgsConstructor
public final class HandleInternals {

    /** FOR INTERNAL USE ONLY. */
    private final @Getter Jet jet;

    /** FOR INTERNAL USE ONLY. */
    private final @Getter Request request;

    /** FOR INTERNAL USE ONLY. */
    private final @Getter Response response;

    private @Nullable Route routeOfMatch;
    private @Nullable RouteMatch routeMatch;

    /** FOR INTERNAL USE ONLY. */
    public Route getRouteOfMatch() {
        return requireNonNull(routeOfMatch);
    }

    /** FOR INTERNAL USE ONLY. */
    public void setRouteOfMatch(final Route routeOfMatch) {
        checkState(this.routeOfMatch == null);
        this.routeOfMatch = routeOfMatch;
    }

    /** FOR INTERNAL USE ONLY. */
    public RouteMatch getRouteMatch() {
        return requireNonNull(routeMatch);
    }

    /** FOR INTERNAL USE ONLY. */
    public void setRouteMatch(final RouteMatch routeMatch) {
        checkState(this.routeMatch == null);
        this.routeMatch = routeMatch;
    }
}
