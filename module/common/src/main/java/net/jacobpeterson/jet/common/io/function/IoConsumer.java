package net.jacobpeterson.jet.common.io.function;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * {@link IoConsumer} is the same as {@link Consumer}, but allows {@link IOException}s.
 */
@FunctionalInterface
@NullMarked
public interface IoConsumer<T extends @Nullable Object> {

    /**
     * @see Consumer#accept(Object)
     */
    void accept(final T t) throws IOException;
}
