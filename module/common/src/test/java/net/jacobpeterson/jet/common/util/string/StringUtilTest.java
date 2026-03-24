package net.jacobpeterson.jet.common.util.string;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class StringUtilTest {

    @Test
    public void isLikelyUtf8PeekLength() {
        assertTrue(StringUtil.isLikelyUtf8("abc".getBytes(UTF_8), 2));
        assertFalse(StringUtil.isLikelyUtf8(new byte[]{0, 1, 2, 3}, 2));
    }

    @Test
    public void isLikelyUtf8() {
        assertTrue(StringUtil.isLikelyUtf8("".getBytes(UTF_8)));
        assertTrue(StringUtil.isLikelyUtf8("abc".getBytes(UTF_8)));
        assertTrue(StringUtil.isLikelyUtf8("abc\r\n\t😂".getBytes(UTF_8)));
        assertFalse(StringUtil.isLikelyUtf8(new byte[]{0, 1, 2, 3}));
    }
}
