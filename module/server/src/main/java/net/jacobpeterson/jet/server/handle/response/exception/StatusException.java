package net.jacobpeterson.jet.server.handle.response.exception;

import lombok.Getter;
import net.jacobpeterson.jet.common.http.status.Status;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@link StatusException} is a {@link RuntimeException} with an associated {@link Status} code.
 */
@NullMarked
public class StatusException extends RuntimeException {

    /**
     * The response {@link Status} code.
     */
    private final @Getter int statusCode;

    /**
     * Calls {@link #StatusException(int)} with {@link Status#getCode()}.
     */
    public StatusException(final Status status) {
        this(status.getCode());
    }

    /**
     * Calls {@link #StatusException(int, String)} with {@link Status#getCode()}.
     */
    public StatusException(final Status status, final String message) {
        this(status.getCode(), message);
    }

    /**
     * Calls {@link #StatusException(int, String, Throwable)} with {@link Status#getCode()}.
     */
    public StatusException(final Status status, final String message, final Throwable cause) {
        this(status.getCode(), message, cause);
    }

    /**
     * Calls {@link #StatusException(int, Throwable)} with {@link Status#getCode()}.
     */
    public StatusException(final Status status, final Throwable cause) {
        this(status.getCode(), cause);
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param statusCode the {@link #getStatusCode()}
     */
    public StatusException(final int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param statusCode the {@link #getStatusCode()}
     * @param message    the {@link #getMessage()}
     */
    public StatusException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param statusCode the {@link #getStatusCode()}
     * @param message    the {@link #getMessage()}
     * @param cause      the {@link #getCause()}
     */
    public StatusException(final int statusCode, final String message, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param statusCode the {@link #getStatusCode()}
     * @param cause      the {@link #getCause()}
     */
    public StatusException(final int statusCode, final Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }

    /**
     * @return {@link Status#forCode(int)} with {@link #getStatusCode()}
     */
    public @Nullable Status getStatus() {
        return Status.forCode(statusCode);
    }
}
