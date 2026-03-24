package net.jacobpeterson.jet.common.util.string;

import org.jspecify.annotations.NullMarked;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;

import static java.lang.Math.min;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link StringUtil} is utility class for {@link String}.
 */
@NullMarked
public final class StringUtil {

    /**
     * @return {@link #isLikelyUtf8(byte[], int, int)} with <code>offset</code> set to <code>0</code> and
     * <code>length</code> set to {@link Math#min(int, int)} of <code>peekLength</code> and <code>bytes.length</code>
     */
    public static boolean isLikelyUtf8(final byte[] bytes, final int peekLength) {
        return isLikelyUtf8(bytes, 0, min(bytes.length, peekLength));
    }

    /**
     * @return {@link #isLikelyUtf8(byte[], int, int)} with <code>offset</code> set to <code>0</code> and
     * <code>length</code> set to <code>bytes.length</code>
     */
    public static boolean isLikelyUtf8(final byte[] bytes) {
        return isLikelyUtf8(bytes, 0, bytes.length);
    }

    /**
     * Checks if the given <code>bytes</code> are likely encoded with {@link StandardCharsets#UTF_8}.
     *
     * @param bytes  the bytes
     * @param offset the <code>bytes</code> offset to check
     * @param length the <code>bytes</code> length to check
     *
     * @return <code>true</code> for {@link StandardCharsets#UTF_8} likely, <code>false</code> otherwise
     */
    public static boolean isLikelyUtf8(final byte[] bytes, final int offset, final int length) {
        final var decoder = UTF_8.newDecoder();
        decoder.onMalformedInput(REPORT);
        decoder.onUnmappableCharacter(REPORT);
        final CharBuffer decoded;
        try {
            decoded = decoder.decode(wrap(bytes, offset, length));
        } catch (final CharacterCodingException characterCodingException) {
            return false;
        }
        while (decoded.hasRemaining()) {
            final var c = decoded.get();
            if (c != '\r' && c != '\n' && c != '\t' && // CR, LF, TAB
                    (c <= 0x1F || (c >= 0x7F && c <= 0x9F))) { // https://www.compart.com/en/unicode/category/Cc
                return false;
            }
        }
        return true;
    }

    private StringUtil() {}
}
