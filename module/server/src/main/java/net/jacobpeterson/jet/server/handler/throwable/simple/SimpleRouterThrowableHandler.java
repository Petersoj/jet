package net.jacobpeterson.jet.server.handler.throwable.simple;

import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handler.throwable.ThrowableHandler;
import org.jspecify.annotations.NullMarked;

import static net.jacobpeterson.jet.common.http.status.Status.INTERNAL_SERVER_ERROR_500;
import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.ERROR;

/**
 * {@link SimpleRouterThrowableHandler} is a simple {@link ThrowableHandler} implementation for
 * {@link JetServer#getRouterThrowableHandler()}.
 */
@NullMarked
@Slf4j
public class SimpleRouterThrowableHandler implements ThrowableHandler {

    @Override
    public void handle(final Handle handle, final Throwable throwable) {
        final var response = handle.getResponse();
        final var isStatusException = throwable instanceof StatusException;
        if (isStatusException) {
            response.setStatusCode(((StatusException) throwable).getStatusCode());
        } else {
            response.setStatus(INTERNAL_SERVER_ERROR_500);
        }
        // if-statement prevents superfluous `Object[]` creation from varargs.
        if (!isStatusException || LOGGER.isDebugEnabled()) {
            final var request = handle.getRequest();
            final var responseStatus = response.getStatus();
            LOGGER.atLevel(!isStatusException ? ERROR : DEBUG).log("Router threw with status {} for request: {} {} {}",
                    responseStatus != null ? responseStatus : response.getStatusCode(),
                    request.getVersion(), request.getMethod(), request.getUrl(), throwable);
        }
    }
}
