package net.jacobpeterson.jet.common.io.bounded;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * {@link BoundException} is an {@link IOException} for {@link OnBoundCount#THROW}.
 */
@NullMarked
public class BoundException extends IOException {

    /**
     * Instantiates a new {@link BoundException}.
     *
     * @param boundedInputStream the {@link BoundedInputStream}
     */
    public BoundException(final BoundedInputStream boundedInputStream) {
        final var boundCount = boundedInputStream.getBoundCount();
        super(boundCount == null ? "Unbound" : "Exceeded bound count of %d bytes".formatted(boundCount));
    }
}
