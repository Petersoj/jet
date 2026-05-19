package net.jacobpeterson.jet.common.http.header.etag;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.now;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.ZSTANDARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class ETagTest {

    @Test
    public void computeStrong() {
        final var eTag = ETag.computeStrong(new ByteArrayInputStream("abc".getBytes(UTF_8)));
        assertFalse(eTag.isWeak());
        assertFalse(eTag.getValue().isEmpty());
    }

    @Test
    public void computeWeak() {
        final var eTag = ETag.computeWeak("abc", 123, now().toEpochMilli());
        assertTrue(eTag.isWeak());
        assertFalse(eTag.getValue().isEmpty());
    }

    @Test
    public void parse() {
        {
            final var eTag = ETag.parse("\"abc\"");
            assertFalse(eTag.isWeak());
            assertEquals("abc", eTag.getValue());
        }
        {
            final var eTag = ETag.parse(" W/\"abc\" ");
            assertTrue(eTag.isWeak());
            assertEquals("abc", eTag.getValue());
        }
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" W/"));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" W/\""));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" \""));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse("\"W/"));
    }

    @Test
    public void getValueWithoutCompressionType() {
        {
            final var eTag = ETag.builder()
                    .value("abc")
                    .build();
            assertEquals("abc", eTag.getValue());
            assertEquals("abc", eTag.getValueWithoutCompressionType());
        }
        {
            final var eTag = ETag.builder()
                    .value("abc", ZSTANDARD)
                    .build();
            assertEquals("abc-compression-type-zstd", eTag.getValue());
            assertEquals("abc", eTag.getValueWithoutCompressionType());
        }
    }

    @Test
    public void equalsWithoutCompressionType() {
        assertTrue(ETag.parse("\"abc-compression-type-zstd\"")
                .equalsWithoutCompressionType(ETag.parse("\"abc\"")));
        assertTrue(ETag.parse("W/\"abc\"")
                .equalsWithoutCompressionType(ETag.parse("W/\"abc-compression-type-zstd\"")));
        assertTrue(ETag.parse("\"abc-compression-type-gzip\"")
                .equalsWithoutCompressionType(ETag.parse("\"abc-compression-type-zstd\"")));
        assertFalse(ETag.parse("W/\"abc\"")
                .equalsWithoutCompressionType(ETag.parse("\"abc-compression-type-zstd\"")));
        assertFalse(ETag.parse("W/\"abc\"")
                .equalsWithoutCompressionType(ETag.parse("W/\"def-compression-type-zstd\"")));
        assertFalse(ETag.parse("W/\"abc\"").equalsWithoutCompressionType(new Object()));
    }

    @Test
    public void _toString() {
        assertEquals("\"abc\"", ETag.builder()
                .value("abc")
                .build().toString());
        assertEquals("W/\"abc\"", ETag.builder()
                .weak()
                .value("abc")
                .build().toString());
    }
}
