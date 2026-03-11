package net.jacobpeterson.jet.server.handle;

import org.jspecify.annotations.NullMarked;

/**
 * {@link HandleFactory} is a {@link FunctionalInterface} for creating new {@link Handle} instances.
 */
@NullMarked
@FunctionalInterface
public interface HandleFactory {

    /**
     * Creates a new {@link Handle} instance.
     * <p>
     * Note: this method implementation must be thread-safe.
     *
     * @param internals the {@link HandleInternals}
     *
     * @return the new {@link Handle} instance
     */
    Handle create(final HandleInternals internals);
}
