package net.jacobpeterson.jet.common.io.util;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class IoUtilTest {

    @Test
    public void isLikelyPlainTextPeekLength() {
        assertTrue(IoUtil.isLikelyPlainText(UTF_8, "abc".getBytes(UTF_8), 2));
        assertFalse(IoUtil.isLikelyPlainText(UTF_8, new byte[]{0, 1, 2, 3}, 2));
    }

    @Test
    public void isLikelyPlainText() {
        assertTrue(IoUtil.isLikelyPlainText(UTF_8, "".getBytes(UTF_8)));
        assertTrue(IoUtil.isLikelyPlainText(UTF_8, "abc".getBytes(UTF_8)));
        assertTrue(IoUtil.isLikelyPlainText(UTF_8, "abc\r\n\t😂".getBytes(UTF_8)));
        assertTrue(IoUtil.isLikelyPlainText(UTF_16, "abc".getBytes(UTF_16)));
        assertFalse(IoUtil.isLikelyPlainText(UTF_8, new byte[]{0, 1, 2, 3}));
    }

    @Test
    public void peekInputStream() throws IOException {
        {
            final var peeked = IoUtil.peekInputStream(new ByteArrayInputStream(new byte[]{0, 1, 2, 3}), 2);
            assertArrayEquals(new byte[]{0, 1, 2, 3}, peeked.getInputStream().readAllBytes());
            assertArrayEquals(new byte[]{0, 1}, peeked.getBytes());
        }
        {
            final var peeked = IoUtil.peekInputStream(new ByteArrayInputStream(new byte[]{0, 1, 2, 3}), 4);
            assertArrayEquals(new byte[]{0, 1, 2, 3}, peeked.getInputStream().readAllBytes());
            assertArrayEquals(new byte[]{0, 1, 2, 3}, peeked.getBytes());
        }
        {
            final var peeked = IoUtil.peekInputStream(new ByteArrayInputStream(new byte[]{0, 1, 2, 3}), 5);
            assertArrayEquals(new byte[]{0, 1, 2, 3}, peeked.getInputStream().readAllBytes());
            assertArrayEquals(new byte[]{0, 1, 2, 3}, peeked.getBytes());
        }
    }
}
