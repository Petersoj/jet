package net.jacobpeterson.jet.common.http.header.contentencoding;

import com.aayushatharva.brotli4j.encoder.PreparedDictionaryGenerator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

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
    public void compressAndDecompress() {
        assertThrows(UnsupportedOperationException.class, () -> CompressionType.COMPRESS.decompress(new byte[]{}));
        assertThrows(UnsupportedOperationException.class, () -> CompressionType.COMPRESS.compress(new byte[]{}));

        for (final var compressionType : Set.of(GZIP, DEFLATE, BROTLI, ZSTANDARD)) {
            compressAndDecompress(compressionType, null);
        }

        final var brotliDictionary = PreparedDictionaryGenerator.generate(wrap(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}))
                .getData();
        final var brotliDictionaryBytes = new byte[brotliDictionary.remaining()];
        brotliDictionary.get(brotliDictionaryBytes);
        compressAndDecompress(DICTIONARY_COMPRESSED_BROTLI, brotliDictionaryBytes);

        compressAndDecompress(DICTIONARY_COMPRESSED_ZSTANDARD, new byte[]{0, 1, 2, 3, 4});
    }

    private void compressAndDecompress(final CompressionType compressionType, final byte @Nullable [] dictionary) {
        for (final var randomBytesLength : List.of(0, 1, 2, 8, 1024, 8 * 1024, 1024 * 1024)) {
            final var randomBytes = new byte[randomBytesLength];
            ThreadLocalRandom.current().nextBytes(randomBytes);
            assertArrayEquals(randomBytes, compressionType.decompress(compressionType.compress(randomBytes,
                    dictionary), dictionary));
        }
    }

    @Test
    public void forString() {
        assertEquals(BROTLI, CompressionType.forString("br"));
        assertEquals(BROTLI, CompressionType.forString("BR"));
        assertNull(CompressionType.forString("a"));
    }
}
