package net.jacobpeterson.jet.server.handler.throwable.simple;

import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.server.Jet;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handler.throwable.ThrowableHandler;
import org.jspecify.annotations.NullMarked;

/**
 * {@link SimpleResponseBodyThrowableHandler} is a simple {@link ThrowableHandler} implementation for
 * {@link Jet#getResponseBodyThrowableHandler()}.
 */
@NullMarked
@Slf4j
public class SimpleResponseBodyThrowableHandler implements ThrowableHandler {

    @Override
    public void handle(final Handle handle, final Throwable throwable) {
        final var request = handle.getRequest();
        LOGGER.error("Failed to write response body for request: {} {} {}",
                request.getVersion(), request.getMethod(), request.getUrl(), throwable);
    }
}
