package net.jacobpeterson.jet.common.http.header.contentrange;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public class ContentRangeTest {

    @Test
    public void parse() {
        {
            final var contentRange = ContentRange.parse("bytes */*");
            assertEquals("bytes", contentRange.getUnit());
            assertNull(contentRange.getRangeStart());
            assertNull(contentRange.getRangeEnd());
            assertNull(contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.parse("bits  0-1 / *");
            assertEquals("bits", contentRange.getUnit());
            assertEquals(0, contentRange.getRangeStart());
            assertEquals(1, contentRange.getRangeEnd());
            assertNull(contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.parse("bytes */ 10");
            assertEquals("bytes", contentRange.getUnit());
            assertNull(contentRange.getRangeStart());
            assertNull(contentRange.getRangeEnd());
            assertEquals(10, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.parse("bytes 0-1/10");
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(0, contentRange.getRangeStart());
            assertEquals(1, contentRange.getRangeEnd());
            assertEquals(10, contentRange.getSize());
        }
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse(" /"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("a /"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("a -1/"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("a 1-2/"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("a 1-2/ "));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("a -/*"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("a -1/*"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("a 0/*"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("bytes -1--2/*"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("bytes 1-2/-1"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("bytes 2-1/2"));
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("bytes 1-3/2"));
    }

    public static final class BuilderTest {

        @Test
        public void unit() {
            assertEquals(ContentRange.BYTES_UNIT, ContentRange.builder().build().getUnit());
            assertEquals("bits", ContentRange.builder()
                    .unit("bits")
                    .build().getUnit());
        }

        @Test
        public void rangeStart() {
            assertNull(ContentRange.builder().build().getRangeStart());
            assertEquals(1, ContentRange.builder()
                    .rangeStart(1L)
                    .rangeEnd(2L)
                    .build().getRangeStart());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .rangeStart(1L)
                    .build());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .rangeStart(-1L)
                    .rangeEnd(1L)
                    .build());
        }

        @Test
        public void rangeEnd() {
            assertNull(ContentRange.builder().build().getRangeEnd());
            assertEquals(2, ContentRange.builder()
                    .rangeStart(1L)
                    .rangeEnd(2L)
                    .build().getRangeEnd());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .rangeEnd(1L)
                    .build());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .rangeStart(1L)
                    .rangeEnd(-1L)
                    .build());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .rangeStart(2L)
                    .rangeEnd(1L)
                    .build());
        }

        @Test
        public void size() {
            assertNull(ContentRange.builder().build().getSize());
            assertEquals(2, ContentRange.builder()
                    .size(2L)
                    .build().getSize());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .rangeStart(1L)
                    .rangeEnd(2L)
                    .size(1L)
                    .build());
        }
    }

    @Test
    public void _toString() {
        assertEquals("bytes */*", ContentRange.builder().build().toString());
        assertEquals("bits */*", ContentRange.builder()
                .unit("bits")
                .build().toString());
        assertEquals("bytes 0-1/*", ContentRange.builder()
                .rangeStart(0L)
                .rangeEnd(1L)
                .build().toString());
        assertEquals("bytes */1", ContentRange.builder()
                .size(1L)
                .build().toString());
        assertEquals("bytes 0-1/1", ContentRange.builder()
                .rangeStart(0L)
                .rangeEnd(1L)
                .size(1L)
                .build().toString());
    }
}
