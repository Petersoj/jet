package net.jacobpeterson.jet.server.handler.handler;

import net.jacobpeterson.jet.server.handle.Handle;
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
     *
     * @param handle the {@link Handle}
     */
    void handle(final Handle handle);
}
