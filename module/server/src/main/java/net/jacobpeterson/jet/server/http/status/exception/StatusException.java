package net.jacobpeterson.jet.server.http.status.exception;

import lombok.Value;
import lombok.experimental.NonFinal;
import net.jacobpeterson.jet.server.handler.Handler;
import net.jacobpeterson.jet.server.http.status.Status;
import org.jspecify.annotations.NullMarked;

/**
 * {@link StatusException} is a {@link RuntimeException} for {@link Handler}s to respond with a given {@link Status} and
 * {@link String} message.
 */
@NullMarked
@Value @NonFinal
public class StatusException extends RuntimeException {

    Status status;

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status the {@link Status}
     */
    public StatusException(final Status status) {
        this.status = status;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status  the {@link Status}
     * @param message see {@link RuntimeException#RuntimeException(String)}
     */
    public StatusException(final Status status, final String message) {
        super(message);
        this.status = status;
    }
}
