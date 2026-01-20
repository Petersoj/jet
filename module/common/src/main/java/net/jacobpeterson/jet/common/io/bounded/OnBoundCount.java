package net.jacobpeterson.jet.common.io.bounded;

import org.jspecify.annotations.NullMarked;

import java.io.IOException;

/**
 * {@link OnBoundCount} is a {@link FunctionalInterface} for {@link BoundedInputStream} to call when
 * {@link BoundedInputStream#isBound()} occurs.
 */
@NullMarked
@FunctionalInterface
public interface OnBoundCount {

    /**
     * An {@link OnBoundCount} that throws a {@link BoundException}.
     */
    OnBoundCount THROW = boundedInputStream -> { throw new BoundException(boundedInputStream); };

    /**
     * An {@link OnBoundCount} that calls {@link BoundedInputStream#close()}.
     */
    OnBoundCount CLOSE = BoundedInputStream::close;

    /**
     * Called when {@link BoundedInputStream#isBound()} occurs.
     *
     * @param boundedInputStream the {@link BoundedInputStream}
     *
     * @throws IOException thrown for {@link IOException}s
     */
    void onBoundCount(final BoundedInputStream boundedInputStream) throws IOException;
}
