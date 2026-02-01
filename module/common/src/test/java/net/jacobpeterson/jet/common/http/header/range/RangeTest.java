package net.jacobpeterson.jet.common.http.header.range;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public class RangeTest {

    @Test
    public void parse() {
        {
            final var ranges = Range.parse("bytes=0-");
            assertEquals(1, ranges.size());
            final var range0 = ranges.getFirst();
            assertEquals("bytes", range0.getUnit());
            assertEquals(0, range0.getStart());
            assertNull(range0.getEnd());
        }
        {
            final var ranges = Range.parse("bits= -1");
            assertEquals(1, ranges.size());
            final var range0 = ranges.getFirst();
            assertEquals("bits", range0.getUnit());
            assertNull(range0.getStart());
            assertEquals(1, range0.getEnd());
        }
        {
            final var ranges = Range.parse(" bytes =  0 -1");
            assertEquals(1, ranges.size());
            final var range0 = ranges.getFirst();
            assertEquals("bytes", range0.getUnit());
            assertEquals(0, range0.getStart());
            assertEquals(1, range0.getEnd());
        }
        {
            final var ranges = Range.parse("bytes  =0 - 1 , -2");
            assertEquals(2, ranges.size());

            final var range0 = ranges.getFirst();
            assertEquals("bytes", range0.getUnit());
            assertEquals(0, range0.getStart());
            assertEquals(1, range0.getEnd());

            final var range1 = ranges.get(1);
            assertEquals("bytes", range1.getUnit());
            assertNull(range1.getStart());
            assertEquals(2, range1.getEnd());
        }
        {
            final var ranges = Range.parse("bytes  =1 - 4 , 8-9,,");
            assertEquals(2, ranges.size());

            final var range0 = ranges.getFirst();
            assertEquals("bytes", range0.getUnit());
            assertEquals(1, range0.getStart());
            assertEquals(4, range0.getEnd());

            final var range1 = ranges.get(1);
            assertEquals("bytes", range1.getUnit());
            assertEquals(8, range1.getStart());
            assertEquals(9, range1.getEnd());
        }
        assertThrows(IllegalArgumentException.class, () -> Range.parse(""));
        assertThrows(IllegalArgumentException.class, () -> Range.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("="));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("= - "));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("b= - "));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=0"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=a"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=-a"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=-1,-"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=0-1,0-,="));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=--1"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=-1-"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=-1--2"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=-1--2"));
        assertThrows(IllegalArgumentException.class, () -> Range.parse("bytes=2-1"));
    }

    @Test
    public void multipleToString() {
        assertEquals("bytes=0-", Range.multipleToString(List.of()));
        assertEquals("bytes=0-1", Range.multipleToString(Range.parse("bytes=0-1")));
        assertEquals("bytes=0-1, 2-3, -4", Range.multipleToString(Range.parse("bytes=0-1, 2-3, -4")));
    }

    public static final class BuilderTest {

        @Test
        public void unit() {
            assertEquals(Range.BYTES_UNIT, Range.builder().build().getUnit());
            assertEquals("bits", Range.builder()
                    .unit("bits")
                    .start(0L)
                    .build().getUnit());
        }

        @Test
        public void start() {
            assertEquals(0, Range.builder().build().getStart());
            assertEquals(1, Range.builder()
                    .start(1L)
                    .build().getStart());
            assertThrows(IllegalArgumentException.class, () -> Range.builder()
                    .start(-1L)
                    .build());
        }

        @Test
        public void end() {
            assertNull(Range.builder().build().getEnd());
            assertEquals(1, Range.builder()
                    .end(1L)
                    .build().getEnd());
            assertThrows(IllegalArgumentException.class, () -> Range.builder()
                    .end(-1L)
                    .build());
        }
    }

    @Test
    public void toStringNoUnit() {
        assertEquals("0-", Range.builder()
                .start(0L)
                .build().toStringNoUnit());
        assertEquals("-1", Range.builder()
                .end(1L)
                .build().toStringNoUnit());
        assertEquals("0-1", Range.builder()
                .start(0L)
                .end(1L)
                .build().toStringNoUnit());
    }

    @Test
    public void _toString() {
        assertEquals("bytes=0-", Range.builder().build().toString());
        assertEquals("bytes=1-", Range.builder()
                .start(1L)
                .build().toString());
        assertEquals("bytes=-1", Range.builder()
                .end(1L)
                .build().toString());
        assertEquals("bytes=0-1", Range.builder()
                .start(0L)
                .end(1L)
                .build().toString());
    }
}
