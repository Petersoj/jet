package net.jacobpeterson.jet.server.http.status;

import lombok.Value;
import net.jacobpeterson.jet.server.handler.Handler;
import org.jspecify.annotations.NullMarked;

/**
 * {@link HttpStatusException} is a {@link RuntimeException} for {@link Handler}s to respond with a given
 * {@link HttpStatus} and {@link String} message. By default, only {@link #getStatus()} and {@link #getMessage()} will
 * be provided in the HTTP response content.
 */
@NullMarked
@Value
public class HttpStatusException extends RuntimeException {

    HttpStatus status;

    /**
     * Instantiates a new {@link HttpStatusException}.
     *
     * @param status the {@link HttpStatus}
     */
    public HttpStatusException(final HttpStatus status) {
        this.status = status;
    }

    /**
     * Instantiates a new {@link HttpStatusException}.
     *
     * @param status  the status
     * @param message see {@link RuntimeException#RuntimeException(String)}
     */
    public HttpStatusException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    /**
     * Instantiates a new {@link HttpStatusException}.
     *
     * @param status  the {@link HttpStatus}
     * @param message see {@link RuntimeException#RuntimeException(String, Throwable)}
     * @param cause   see {@link RuntimeException#RuntimeException(String, Throwable)}
     */
    public HttpStatusException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Instantiates a new {@link HttpStatusException}.
     *
     * @param status the {@link HttpStatus}
     * @param cause  see {@link RuntimeException#RuntimeException(Throwable)}
     */
    public HttpStatusException(final HttpStatus status, final Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     * Instantiates a new {@link HttpStatusException}.
     *
     * @param status             the {@link HttpStatus}
     * @param message            see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     * @param cause              see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     * @param enableSuppression  see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     * @param writableStackTrace see {@link RuntimeException#RuntimeException(String, Throwable, boolean, boolean)}
     */
    public HttpStatusException(final HttpStatus status, final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }
}
