package net.jacobpeterson.jet.common.io;

import com.google.errorprone.annotations.Immutable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import static java.lang.Math.min;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static lombok.AccessLevel.PRIVATE;

/**
 * {@link IoUtil} is utility class for I/O.
 */
@NullMarked
public final class IoUtil {

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
    public static boolean isLikelyPlainText(final Charset charset,
            final byte[] bytes, final int offset, final int length) {
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
            if (c != '\n' && c != '\r' && c != '\t' && (c < 0x20 || c == 0x7F)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Peeks bytes from the given {@link InputStream}.
     * <p>
     * Note: the returned {@link Peeked#getInputStream()} must be used for future read operations and
     * the given {@link InputStream} must no longer be used directly.
     *
     * @param inputStream the {@link InputStream}
     * @param peekLength  the peek length
     *
     * @return the {@link Peeked}
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public static Peeked peekInputStream(final InputStream inputStream, final int peekLength) throws IOException {
        final var peekedBytesRead = inputStream.readNBytes(peekLength);
        return new Peeked(new SequenceInputStream(new ByteArrayInputStream(peekedBytesRead), inputStream),
                peekedBytesRead);
    }

    /**
     * {@link Peeked} is a class for the return type of {@link #peekInputStream(InputStream, int)}.
     */
    @Immutable
    @RequiredArgsConstructor(access = PRIVATE) @Getter
    public static final class Peeked {

        /**
         * The {@link InputStream} given to {@link #peekInputStream(InputStream, int)}, but with {@link #getBytes()}
         * prepended using {@link SequenceInputStream}.
         */
        private final InputStream inputStream;

        /** The peeked bytes. */
        private final byte[] bytes;
    }

    private IoUtil() {}
}
