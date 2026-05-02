package net.jacobpeterson.jet.server.handler.throwable.simple;

import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handler.throwable.ThrowableHandler;
import org.jspecify.annotations.NullMarked;
import org.slf4j.event.Level;

import static net.jacobpeterson.jet.common.http.status.Status.INTERNAL_SERVER_ERROR_500;
import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.ERROR;

/**
 * {@link SimpleThrowableHandler} is a simple {@link ThrowableHandler} implementation that sets
 * {@link Response#setStatusCode(int)} to {@link StatusException#getStatusCode()} or to
 * {@link Status#INTERNAL_SERVER_ERROR_500}, clears {@link Response#getHeaders()}, clears the existing response body
 * and calls {@link Response#setBodyString(String)} to {@link Status#toString()}, and logs the {@link Throwable}
 * with {@link Level#ERROR} if not a {@link StatusException}.
 */
@NullMarked
@Slf4j
public class SimpleThrowableHandler implements ThrowableHandler {

    @Override
    public void handle(final Handle handle, final Throwable throwable) {
        final var response = handle.getResponse();
        response.getHeaders().clear();
        final int statusCode;
        final String statusString;
        final var isStatusException = throwable instanceof StatusException;
        if (isStatusException) {
            statusCode = ((StatusException) throwable).getStatusCode();
            final var status = Status.forCode(statusCode);
            statusString = status == null ? statusCode + " Error" : status.toString();
        } else {
            final var status = INTERNAL_SERVER_ERROR_500;
            statusCode = status.getCode();
            statusString = status.toString();
        }
        response.responseText(statusCode, statusString);
        // if-statement prevents superfluous `Object[]` creation from varargs.
        if (isStatusException || LOGGER.isDebugEnabled()) {
            final var request = handle.getRequest();
            LOGGER.atLevel(isStatusException ? DEBUG : ERROR)
                    .log("Handler threw with status {} for request: {} {} {}", statusString,
                            request.getVersion(), request.getMethod(), request.getUrl(), throwable);
        }
    }
}
