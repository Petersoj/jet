package net.jacobpeterson.jet.common.http.header.contentencoding;

import com.aayushatharva.brotli4j.encoder.PreparedDictionaryGenerator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.ByteBuffer.wrap;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.BROTLI;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.DEFLATE;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.DICTIONARY_COMPRESSED_BROTLI;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.DICTIONARY_COMPRESSED_ZSTANDARD;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.GZIP;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.ZSTANDARD;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class CompressionTypeTest {

    @Test
    public void compressAndDecompress() throws IOException {
        assertThrows(UnsupportedOperationException.class, () ->
                CompressionType.COMPRESS.decompress(new ByteArrayInputStream(new byte[]{}), null));
        assertThrows(UnsupportedOperationException.class, () ->
                CompressionType.COMPRESS.compress(new ByteArrayOutputStream(), (byte[]) null));
        for (final var compressionType : Set.of(GZIP, DEFLATE, BROTLI, ZSTANDARD)) {
            outputMatchesInput(compressionType, null);
        }

        final var brotliDictionary = PreparedDictionaryGenerator.generate(wrap(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}))
                .getData();
        final var brotliDictionaryBytes = new byte[brotliDictionary.remaining()];
        brotliDictionary.get(brotliDictionaryBytes);
        outputMatchesInput(DICTIONARY_COMPRESSED_BROTLI, brotliDictionaryBytes);

        outputMatchesInput(DICTIONARY_COMPRESSED_ZSTANDARD, new byte[]{0, 1, 2, 3, 4});
    }

    private void outputMatchesInput(final CompressionType compressionType, final byte @Nullable [] dictionary)
            throws IOException {
        for (final var randomBytesLength : List.of(0, 1, 2, 8, 1024, 8 * 1024, 1024 * 1024)) {
            final var randomBytes = new byte[randomBytesLength];
            ThreadLocalRandom.current().nextBytes(randomBytes);
            final var compressed = new ByteArrayOutputStream();
            try (final var compress = compressionType.compress(compressed, dictionary)) {
                compress.write(randomBytes);
            }
            try (final var decompress = compressionType
                    .decompress(new ByteArrayInputStream(compressed.toByteArray()), dictionary)) {
                assertArrayEquals(randomBytes, decompress.readAllBytes());
            }
        }
    }

    @Test
    public void forString() {
        assertEquals(BROTLI, CompressionType.forString("br"));
        assertEquals(BROTLI, CompressionType.forString("BR"));
        assertNull(CompressionType.forString("a"));
    }
}
