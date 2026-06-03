package net.jacobpeterson.jet.server.handle.exception;

import lombok.experimental.StandardException;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import org.eclipse.jetty.io.EofException;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Throwables.getCausalChain;

/**
 * {@link BodyStreamException} is an {@link IOException} representing {@link Request}/{@link Response} body streaming
 * client timeouts and early disconnects.
 */
@StandardException
@NullMarked
public class BodyStreamException extends IOException {

    /**
     * @return <code>true</code> if the given {@link Exception} should be wrapped in a {@link BodyStreamException} as it
     * is an internal {@link Exception} representing a client timeout or early disconnect, <code>false</code> otherwise
     */
    public static boolean shouldBeBodyStreamException(final Exception exception) {
        return getCausalChain(exception).stream().anyMatch(cause ->
                cause instanceof EofException || cause instanceof TimeoutException);
    }

    /**
     * @return a new {@link BodyStreamException} if {@link #shouldBeBodyStreamException(Exception)}, otherwise returns
     * an {@link IOException}
     */
    public static IOException asBodyStreamException(final Exception exception) {
        if (exception instanceof final BodyStreamException bodyStreamException) {
            return bodyStreamException;
        }
        if (shouldBeBodyStreamException(exception)) {
            return new BodyStreamException(exception);
        }
        if (exception instanceof final IOException ioException) {
            return ioException;
        }
        return new IOException(exception);
    }
}
