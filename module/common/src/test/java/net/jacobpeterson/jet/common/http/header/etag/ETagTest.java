package net.jacobpeterson.jet.common.http.header.etag;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class ETagTest {

    @Test
    public void computeStrong() {
        final var etag = ETag.computeStrong(new ByteArrayInputStream("abc".getBytes(UTF_8)));
        assertFalse(etag.isWeak());
        assertFalse(etag.getValue().isEmpty());
    }

    @Test
    public void computeWeak() {
        final var etag = ETag.computeWeak("abc", 123, now().toEpochMilli());
        assertTrue(etag.isWeak());
        assertFalse(etag.getValue().isEmpty());
    }

    @Test
    public void parse() {
        {
            final var etag = ETag.parse("\"abc\"");
            assertFalse(etag.isWeak());
            assertEquals("abc", etag.getValue());
        }
        {
            final var etag = ETag.parse(" W/\"abc\" ");
            assertTrue(etag.isWeak());
            assertEquals("abc", etag.getValue());
        }
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" W/"));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" W/\""));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse(" \""));
        assertThrows(IllegalArgumentException.class, () -> ETag.parse("\"W/"));
    }

    @Test
    public void getValueQuoted() {
        assertEquals("\"abc\"", ETag.builder()
                .value("abc")
                .build().getValueQuoted());
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
