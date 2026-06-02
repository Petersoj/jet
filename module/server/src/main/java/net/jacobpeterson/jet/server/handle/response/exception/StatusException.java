package net.jacobpeterson.jet.server.handle.response.exception;

import lombok.Getter;
import lombok.ToString;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.server.handler.Handler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@link StatusException} is a {@link RuntimeException} with an associated {@link Status} code.
 * {@link StatusException}s are not intended to be caught and logged as errors. They should be used as silent errors in
 * a {@link Handler}. For example, if a client sends a request with an invalid header, the server should respond with
 * {@link Status#BAD_REQUEST_400} and not log any server errors.
 */
@NullMarked
@ToString
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
