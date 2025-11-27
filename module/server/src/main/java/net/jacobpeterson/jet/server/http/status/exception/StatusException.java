package net.jacobpeterson.jet.server.http.status.exception;

import lombok.Value;
import lombok.experimental.NonFinal;
import net.jacobpeterson.jet.server.handler.Handler;
import net.jacobpeterson.jet.server.http.status.Status;
import org.jspecify.annotations.NullMarked;

/**
 * {@link StatusException} is a {@link RuntimeException} for {@link Handler}s to respond with a given
 * {@link Status} and {@link String} message. By default, only {@link #getStatus()} and {@link #getMessage()} will
 * be provided in the HTTP response content.
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
     * @param status  the status
     * @param message see {@link RuntimeException#RuntimeException(String)}
     */
    public StatusException(final Status status, final String message) {
        super(message);
        this.status = status;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status  the {@link Status}
     * @param message see {@link RuntimeException#RuntimeException(String, Throwable)}
     * @param cause   see {@link RuntimeException#RuntimeException(String, Throwable)}
     */
    public StatusException(final Status status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status the {@link Status}
     * @param cause  see {@link RuntimeException#RuntimeException(Throwable)}
     */
    public StatusException(final Status status, final Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status             the {@link Status}
     * @param message            see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     * @param cause              see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     * @param enableSuppression  see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     * @param writableStackTrace see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     */
    public StatusException(final Status status, final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }
}
