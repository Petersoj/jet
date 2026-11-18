package net.jacobpeterson.jet.server.handle.exception;

import com.google.common.base.Throwables;
import lombok.experimental.StandardException;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.base.Throwables.getCausalChain;

/**
 * {@link BodyStreamException} is an {@link IOException} representing {@link Request}/{@link Response} body streaming
 * failures due to client timeouts, early disconnects, etc.
 */
@StandardException
@NullMarked
public class BodyStreamException extends IOException {

    /**
     * @return {@link Throwables#getCausalChain(Throwable)} {@link Stream#anyMatch(Predicate)} <code>instanceof</code>
     * {@link IOException} or {@link TimeoutException}
     */
    public static boolean matches(final Exception exception) {
        return getCausalChain(exception).stream().anyMatch(cause ->
                cause instanceof IOException || cause instanceof TimeoutException);
    }

    /**
     * @return a new {@link BodyStreamException} if {@link #matches(Exception)}, otherwise returns an
     * {@link IOException}, wrapping the given {@link Exception} as needed
     */
    public static IOException catching(final Exception exception) {
        return BodyStreamException.matches(exception) ? new BodyStreamException(exception) : new IOException(exception);
    }
}
