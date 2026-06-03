package net.jacobpeterson.jet.server.handler;

import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.exception.BodyStreamException;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Handler} is a {@link FunctionalInterface} for handling a web server request and response using a given
 * {@link Handle} instance.
 */
@NullMarked
@FunctionalInterface
public interface Handler {

    /**
     * Handles a web server request and response.
     * <p>
     * Note: this method implementation must be thread-safe.
     * <p>
     * Note: it is not recommended to use a catch-all for {@link Exception}s thrown in this method implementation.
     * {@link JetServer} expects this method implementation to throw {@link StatusException}s and
     * {@link BodyStreamException}s so that they can be handled appropriately. If this method implementation needs to
     * catch {@link StatusException}s or {@link BodyStreamException}s for some reason, it is recommended to re-throw
     * them as-is.
     *
     * @param handle the {@link Handle}
     */
    void handle(final Handle handle);
}
