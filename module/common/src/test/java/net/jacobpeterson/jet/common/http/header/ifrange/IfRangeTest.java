package net.jacobpeterson.jet.common.http.header.ifrange;

import net.jacobpeterson.jet.common.http.header.etag.ETag;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class IfRangeTest {

    @Test
    public void parse() {
        {
            final var ifRange = IfRange.parse(" W/\"abc\" ");
            assertNull(ifRange.getDateTime());
            assertNotNull(ifRange.getETag());
        }
        {
            final var ifRange = IfRange.parse(" Thu, 1 Jan 2026 00:00:00 GMT ");
            assertNotNull(ifRange.getDateTime());
            assertNull(ifRange.getETag());
        }
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse(""));
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse(" W/"));
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse(" W/\""));
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse(" \""));
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse("\"W/"));
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse("Thu"));
        assertThrows(IllegalArgumentException.class, () -> IfRange.parse("Thu, 1 Jan 2026 00:00:00"));
    }

    @Test
    public void builder() {
        assertThrows(IllegalArgumentException.class, () -> IfRange.builder().build());
        assertThrows(IllegalArgumentException.class, () -> IfRange.builder()
                .dateTime(ZonedDateTime.now(UTC))
                .eTag(ETag.builder().value("abc").build())
                .build());
    }

    @Test
    public void _toString() {
        assertEquals("Thu, 1 Jan 2026 00:00:00 GMT", IfRange.builder()
                .dateTime(ZonedDateTime.parse("Thu, 1 Jan 2026 00:00:00 GMT", RFC_1123_DATE_TIME))
                .build().toString());
        assertEquals("\"abc\"", IfRange.builder()
                .eTag(ETag.builder().value("abc").build())
                .build().toString());
    }
}
