package net.jacobpeterson.jet.common.http.header.acceptencoding;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.jacobpeterson.jet.common.http.header.acceptencoding.AcceptEncoding.Entry;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.BROTLI;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.DEFLATE;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.GZIP;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.ZSTANDARD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class AcceptEncodingTest {

    public static final class EntryTest {

        @Test
        public void parse() {
            {
                final var entry = Entry.parse("zstd");
                assertEquals("zstd", entry.getValue());
                assertEquals(ZSTANDARD, entry.getValueCompressionType());
                assertNull(entry.getWeight());
                assertFalse(entry.isValueWildcard());
                assertFalse(entry.isValueIdentity());
            }
            {
                final var entry = Entry.parse("deflate; q=0.5");
                assertEquals("deflate", entry.getValue());
                assertEquals(DEFLATE, entry.getValueCompressionType());
                assertEquals(0.5, entry.getWeight());
                assertFalse(entry.isValueWildcard());
                assertFalse(entry.isValueIdentity());
            }
            assertThrows(IllegalArgumentException.class, () -> Entry.parse("a;q=a"));
        }

        @Test
        public void _toString() {
            assertEquals("zstd", Entry.builder()
                    .value(ZSTANDARD.toString())
                    .build().toString());
            assertEquals("deflate;q=0.5", Entry.builder()
                    .value(DEFLATE.toString())
                    .weight(0.5)
                    .build().toString());
        }
    }

    @Test
    public void parse() {
        assertEquals(ImmutableList.of(
                        Entry.builder().value(DEFLATE.toString()).build(),
                        Entry.builder()
                                .value(GZIP.toString())
                                .weight(1.0)
                                .build(),
                        Entry.builder()
                                .value(ZSTANDARD.toString())
                                .weight(0.5)
                                .build()),
                AcceptEncoding.parse("deflate, gzip; q=1.0 , zstd ; q = 0.5").getEntries());
        assertThrows(IllegalArgumentException.class, () -> AcceptEncoding.parse(""));
        assertThrows(IllegalArgumentException.class, () -> AcceptEncoding.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> AcceptEncoding.parse("gzip; q=abc"));
        assertThrows(IllegalArgumentException.class, () -> AcceptEncoding.parse("gzip; q="));
    }

    @Test
    public void getEntryTypes() {
        assertEquals(ImmutableSet.of(GZIP, DEFLATE, BROTLI, ZSTANDARD),
                AcceptEncoding.parse("gzip, deflate, br, zstd").getEntryTypes());
    }

    @Test
    public void toBuilder() {
        final var acceptEncoding = AcceptEncoding.builder()
                .add(Entry.builder()
                        .value(ZSTANDARD.toString())
                        .weight(0.5)
                        .build())
                .build();
        assertEquals(acceptEncoding, acceptEncoding.toBuilder().build());
    }

    @Test
    public void _toString() {
        assertEquals("*", AcceptEncoding.WILDCARD.toString());
        assertEquals("identity", AcceptEncoding.IDENTITY.toString());
        assertEquals("zstd;q=0.5, br;q=0.5", AcceptEncoding.builder()
                .addAll(Entry.builder()
                                .value(ZSTANDARD.toString())
                                .weight(0.5)
                                .build(),
                        Entry.builder()
                                .value(BROTLI.toString())
                                .weight(0.5)
                                .build())
                .build().toString());
    }
}
