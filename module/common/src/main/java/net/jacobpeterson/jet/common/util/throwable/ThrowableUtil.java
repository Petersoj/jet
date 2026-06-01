package net.jacobpeterson.jet.common.util.throwable;

import com.google.common.base.Throwables;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.base.Throwables.throwIfUnchecked;

/**
 * {@link ThrowableUtil} is a utility class for {@link Throwable}s.
 */
@NullMarked
public final class ThrowableUtil {

    /**
     * @return if <code>firstThrowable</code> is <code>null</code> then <code>secondThrowable</code> is returned,
     * otherwise calls {@link Throwable#addSuppressed(Throwable)} and returns <code>firstThrowable</code>
     */
    public static Throwable accumulateThrowable(final @Nullable Throwable firstThrowable,
            final Throwable secondThrowable) {
        if (firstThrowable == null) {
            return secondThrowable;
        } else {
            firstThrowable.addSuppressed(secondThrowable);
            return firstThrowable;
        }
    }

    /**
     * If <code>throwable</code> is non-<code>null</code>, calls {@link Throwables#throwIfUnchecked(Throwable)} and then
     * calls <code>throw new {@link RuntimeException}</code>.
     */
    public static void throwCheckedOrUnchecked(final @Nullable Throwable throwable) {
        if (throwable != null) {
            throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        }
    }

    private ThrowableUtil() {}
}
