package net.jacobpeterson.jet.common.io.function;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.function.Function;

/**
 * {@link IoFunction} is the same as {@link Function}, but allows {@link IOException}s.
 */
@FunctionalInterface
@NullMarked
public interface IoFunction<T extends @Nullable Object, R extends @Nullable Object> {

    /**
     * @see Function#apply(Object)
     */
    R apply(final T t) throws IOException;
}
