package net.jacobpeterson.jet.server.http.status.exception;

import net.jacobpeterson.jet.server.Jet;
import net.jacobpeterson.jet.server.http.status.Status;
import org.jspecify.annotations.NullMarked;

/**
 * {@link JetStatusException} is an {@link StatusException} to be used and thrown internally by {@link Jet}, allowing
 * dependency consumers to distinguish an {@link StatusException} thrown by {@link Jet} versus an
 * {@link StatusException} thrown by a dependency consumer.
 */
@NullMarked
public class JetStatusException extends StatusException {

    /**
     * @see StatusException#StatusException(Status)
     */
    public JetStatusException(final Status status) {
        super(status);
    }

    /**
     * @see StatusException#StatusException(Status, String)
     */
    public JetStatusException(final Status status, final String message) {
        super(status, message);
    }

    /**
     * @see StatusException#StatusException(Status, String, Throwable)
     */
    public JetStatusException(final Status status, final String message, final Throwable cause) {
        super(status, message, cause);
    }

    /**
     * @see StatusException#StatusException(Status, Throwable)
     */
    public JetStatusException(final Status status, final Throwable cause) {
        super(status, cause);
    }

    /**
     * @see StatusException#StatusException(Status, String, Throwable, boolean, boolean)
     */
    public JetStatusException(final Status status, final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(status, message, cause, enableSuppression, writableStackTrace);
    }
}
