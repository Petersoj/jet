package net.jacobpeterson.jet.common.util.throwable;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@NullMarked
public final class ThrowableUtilTest {

    @Test
    public void accumulateThrowable() {
        {
            final var throwable = new RuntimeException();
            final var throwables = ThrowableUtil.accumulateThrowable(null, throwable);
            assertEquals(throwable, throwables);
            assertEquals(0, throwables.getSuppressed().length);
        }
        {
            final var firstThrowable = new RuntimeException();
            final var secondThrowable = new RuntimeException();
            final var throwables = ThrowableUtil.accumulateThrowable(firstThrowable, secondThrowable);
            assertEquals(firstThrowable, throwables);
            assertArrayEquals(new Throwable[]{secondThrowable}, throwables.getSuppressed());
        }
    }

    @Test
    public void throwCheckedOrUnchecked() {
        assertDoesNotThrow(() -> ThrowableUtil.throwCheckedOrUnchecked(null));
        assertThrowsExactly(RuntimeException.class, () -> ThrowableUtil.throwCheckedOrUnchecked(new Exception()));
        assertThrowsExactly(IllegalArgumentException.class,
                () -> ThrowableUtil.throwCheckedOrUnchecked(new IllegalArgumentException()));
    }
}
