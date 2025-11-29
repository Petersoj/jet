package net.jacobpeterson.jet.server.http.status.exception;

import net.jacobpeterson.jet.server.Jet;
import net.jacobpeterson.jet.server.http.status.Status;
import org.jspecify.annotations.NullMarked;

/**
 * {@link JetStatusException} is a {@link StatusException} meant to be thrown internally by {@link Jet}, allowing
 * dependency consumers to distinguish a {@link StatusException} thrown by {@link Jet} versus a {@link StatusException}
 * thrown by a dependency consumer.
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
}
