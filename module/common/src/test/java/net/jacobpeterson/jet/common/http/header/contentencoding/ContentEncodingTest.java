package net.jacobpeterson.jet.common.http.header.contentencoding;

import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.BROTLI;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.DEFLATE;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.GZIP;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.ZSTANDARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class ContentEncodingTest {

    @Test
    public void parse() {
        {
            final var contentEncoding = ContentEncoding.parse("br");
            assertEquals(ImmutableList.of(BROTLI), contentEncoding.getTypes());
            assertEquals(BROTLI, contentEncoding.getType());
        }
        {
            final var contentEncoding = ContentEncoding.parse("deflate, gzip");
            assertEquals(ImmutableList.of(DEFLATE, GZIP), contentEncoding.getTypes());
            assertEquals(DEFLATE, contentEncoding.getType());
        }
        assertThrows(IllegalArgumentException.class, () -> ContentEncoding.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ContentEncoding.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> ContentEncoding.parse(" ,"));
        assertThrows(IllegalArgumentException.class, () -> ContentEncoding.parse("a"));
        assertThrows(IllegalArgumentException.class, () -> ContentEncoding.parse(" a,b"));
    }

    public static final class BuilderTest {

        @Test
        public void type() {
            assertEquals(ImmutableList.of(DEFLATE, GZIP), ContentEncoding.builder()
                    .type(DEFLATE)
                    .type(GZIP)
                    .build().getTypes());
            assertThrows(IllegalArgumentException.class, () -> ContentEncoding.builder().build());
        }
    }

    @Test
    public void getType() {
        assertEquals(BROTLI, ContentEncoding.builder()
                .type(BROTLI)
                .build().getType());
    }

    @Test
    public void _toString() {
        assertEquals("br", ContentEncoding.builder()
                .type(BROTLI)
                .build().toString());
        assertEquals("br, zstd", ContentEncoding.builder()
                .type(BROTLI)
                .type(ZSTANDARD)
                .build().toString());
    }
}
