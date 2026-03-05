package net.jacobpeterson.jet.common.util.string;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class StringUtilTest {

    @Test
    public void isLikelyPlainTextPeekLength() {
        assertTrue(StringUtil.isLikelyPlainText(UTF_8, "abc".getBytes(UTF_8), 2));
        assertFalse(StringUtil.isLikelyPlainText(UTF_8, new byte[]{0, 1, 2, 3}, 2));
    }

    @Test
    public void isLikelyPlainText() {
        assertTrue(StringUtil.isLikelyPlainText(UTF_8, "".getBytes(UTF_8)));
        assertTrue(StringUtil.isLikelyPlainText(UTF_8, "abc".getBytes(UTF_8)));
        assertTrue(StringUtil.isLikelyPlainText(UTF_8, "abc\r\n\t😂".getBytes(UTF_8)));
        assertTrue(StringUtil.isLikelyPlainText(UTF_16, "abc".getBytes(UTF_16)));
        assertFalse(StringUtil.isLikelyPlainText(UTF_8, new byte[]{0, 1, 2, 3}));
    }
}
