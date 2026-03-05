package net.jacobpeterson.jet.server.exception;

import lombok.Getter;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.server.handler.Handler;
import org.jspecify.annotations.NullMarked;

/**
 * {@link StatusException} is a {@link RuntimeException} for {@link Handler}s to respond with a given {@link Status}.
 */
@NullMarked
public class StatusException extends RuntimeException {

    /** The response {@link Status}. */
    private final @Getter Status status;

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status the status
     */
    public StatusException(final Status status) {
        this.status = status;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status  the {@link #getStatus()}
     * @param message the {@link #getMessage()}
     */
    public StatusException(final Status status, final String message) {
        super(message);
        this.status = status;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status  the {@link #getStatus()}
     * @param message the {@link #getMessage()}
     * @param cause   the {@link #getCause()}
     */
    public StatusException(final Status status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status the {@link #getStatus()}
     * @param cause  the {@link #getCause()}
     */
    public StatusException(final Status status, final Throwable cause) {
        super(cause);
        this.status = status;
    }
}
