package net.jacobpeterson.jet.server.router;

import com.google.common.base.Throwables;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.exception.BodyStreamException;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import org.jspecify.annotations.NullMarked;

/**
 * {@link RouterThrowableHandler} is a {@link FunctionalInterface} to handle for {@link Throwable}s thrown by
 * {@link Router#route(Handle)}.
 */
@FunctionalInterface
@NullMarked
public interface RouterThrowableHandler {

    /**
     * Handles the {@link Throwable} thrown by {@link Router#route(Handle)}.
     *
     * @param handle            the {@link Handle}
     * @param statusCode        the status code (if <code>throwable</code> is {@link StatusException}, then this is
     *                          {@link StatusException#getStatusCode()}, if <code>throwable</code>
     *                          {@link Throwables#getCausalChain(Throwable)} contains {@link BodyStreamException},
     *                          then this is {@link Status#BAD_REQUEST_400}, otherwise, this is
     *                          {@link Status#INTERNAL_SERVER_ERROR_500})
     * @param statusDescription the {@link Status#getDescription()} of the <code>statusCode</code>
     * @param throwable         the {@link Throwable}
     */
    void handle(final Handle handle, final int statusCode, final String statusDescription, final Throwable throwable);
}
