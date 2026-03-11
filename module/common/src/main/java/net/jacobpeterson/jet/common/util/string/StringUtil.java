package net.jacobpeterson.jet.common.util.string;

import org.jspecify.annotations.NullMarked;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import static java.lang.Math.min;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;

/**
 * {@link StringUtil} is utility class for {@link String}.
 */
@NullMarked
public final class StringUtil {

    /**
     * @return {@link #isLikelyPlainText(Charset, byte[], int, int)} with <code>offset</code> set to <code>0</code> and
     * <code>length</code> set to {@link Math#min(int, int)} of <code>peekLength</code> and <code>bytes.length</code>
     */
    public static boolean isLikelyPlainText(final Charset charset, final byte[] bytes, final int peekLength) {
        return isLikelyPlainText(charset, bytes, 0, min(bytes.length, peekLength));
    }

    /**
     * @return {@link #isLikelyPlainText(Charset, byte[], int, int)} with <code>offset</code> set to <code>0</code> and
     * <code>length</code> set to <code>bytes.length</code>
     */
    public static boolean isLikelyPlainText(final Charset charset, final byte[] bytes) {
        return isLikelyPlainText(charset, bytes, 0, bytes.length);
    }

    /**
     * Checks if the given <code>bytes</code> is likely plain text in the given {@link Charset}.
     *
     * @param charset the {@link Charset} of the plain text
     * @param bytes   the bytes
     * @param offset  the <code>bytes</code> offset to check
     * @param length  the <code>bytes</code> length to check
     *
     * @return <code>true</code> for plain text likely, <code>false</code> otherwise
     */
    public static boolean isLikelyPlainText(final Charset charset, final byte[] bytes,
            final int offset, final int length) {
        final var decoder = charset.newDecoder();
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
            if (c != '\r' && c != '\n' && c != '\t' && (c < 0x20 || c == 0x7F)) {
                return false;
            }
        }
        return true;
    }

    private StringUtil() {}
}
