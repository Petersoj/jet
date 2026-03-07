package net.jacobpeterson.jet.server.handle;

import org.jspecify.annotations.NullMarked;

/**
 * {@link HandleFactory} is a {@link FunctionalInterface} factory for creating {@link Handle} instances.
 *
 * @param <T> the {@link Handle} type
 */
@NullMarked
@FunctionalInterface
public interface HandleFactory<T extends Handle> {

    /**
     * Creates a {@link Handle} instance.
     *
     * @param internals the {@link HandleInternals}
     *
     * @return the {@link Handle} instance
     */
    T create(final HandleInternals internals);
}
