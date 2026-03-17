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
