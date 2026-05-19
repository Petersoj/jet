package net.jacobpeterson.jet.common.util.string;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class StringUtilTest {

    @Test
    public void containsIgnoreCase() {
        assertTrue(StringUtil.containsIgnoreCase("ABC", ""));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "a"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "b"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "c"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "A"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "B"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "C"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "Ab"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "bC"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "abc"));
        assertTrue(StringUtil.containsIgnoreCase("ABC", "aBc"));
        assertFalse(StringUtil.containsIgnoreCase("ABC", "D"));
        assertFalse(StringUtil.containsIgnoreCase("ABC", "cd"));
        assertFalse(StringUtil.containsIgnoreCase("ABC", "abd"));
        assertFalse(StringUtil.containsIgnoreCase("ABC", "abcd"));
    }

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
