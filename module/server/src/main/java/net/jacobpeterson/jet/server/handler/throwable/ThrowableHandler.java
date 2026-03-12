package net.jacobpeterson.jet.server.handler.throwable;

import net.jacobpeterson.jet.server.handle.Handle;
import org.jspecify.annotations.NullMarked;

/**
 * {@link ThrowableHandler} is a {@link FunctionalInterface} for handling {@link Throwable}s thrown in the context of a
 * {@link Handle}.
 */
@NullMarked
@FunctionalInterface
public interface ThrowableHandler {

    /**
     * Handles the {@link Throwable} thrown in the context of a {@link Handle}.
     *
     * @param handle    the {@link Handle}
     * @param throwable the {@link Throwable}
     */
    void handle(final Handle handle, final Throwable throwable);
}
