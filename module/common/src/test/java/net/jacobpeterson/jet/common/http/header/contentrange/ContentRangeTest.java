package net.jacobpeterson.jet.common.http.header.contentrange;

import net.jacobpeterson.jet.common.http.header.range.Range;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class ContentRangeTest {

    @Test
    public void forRange() {
        {
            final var contentRange = ContentRange.forRange(Range.builder().build(), 0);
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(0, contentRange.getStart());
            assertEquals(0, contentRange.getEnd());
            assertEquals(0, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.forRange(Range.builder()
                    .start(0L)
                    .build(), 0);
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(0, contentRange.getStart());
            assertEquals(0, contentRange.getEnd());
            assertEquals(0, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.forRange(Range.builder().build(), 10);
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(0, contentRange.getStart());
            assertEquals(9, contentRange.getEnd());
            assertEquals(10, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.forRange(Range.builder()
                    .end(2L)
                    .build(), 10);
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(8, contentRange.getStart());
            assertEquals(9, contentRange.getEnd());
            assertEquals(10, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.forRange(Range.builder()
                    .start(1L)
                    .end(2L)
                    .build(), 10);
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(1, contentRange.getStart());
            assertEquals(2, contentRange.getEnd());
            assertEquals(10, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.forRange(Range.builder()
                    .start(5L)
                    .build(), 10);
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(5, contentRange.getStart());
            assertEquals(9, contentRange.getEnd());
            assertEquals(10, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.forRange(null, 10);
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(0, contentRange.getStart());
            assertEquals(9, contentRange.getEnd());
            assertEquals(10, contentRange.getSize());
        }
    }

    @Test
    public void parse() {
        {
            final var contentRange = ContentRange.parse("bytes */*");
            assertEquals("bytes", contentRange.getUnit());
            assertNull(contentRange.getStart());
            assertNull(contentRange.getEnd());
            assertNull(contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.parse("bits  0-1 / *");
            assertEquals("bits", contentRange.getUnit());
            assertEquals(0, contentRange.getStart());
            assertEquals(1, contentRange.getEnd());
            assertNull(contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.parse("bytes */ 10");
            assertEquals("bytes", contentRange.getUnit());
            assertNull(contentRange.getStart());
            assertNull(contentRange.getEnd());
            assertEquals(10, contentRange.getSize());
        }
        {
            final var contentRange = ContentRange.parse("bytes 0-1/10");
            assertEquals("bytes", contentRange.getUnit());
            assertEquals(0, contentRange.getStart());
            assertEquals(1, contentRange.getEnd());
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
        assertThrows(IllegalArgumentException.class, () -> ContentRange.parse("bytes 1-2/2"));
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
        public void start() {
            assertNull(ContentRange.builder().build().getStart());
            assertEquals(1, ContentRange.builder()
                    .start(1L)
                    .end(2L)
                    .build().getStart());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .start(1L)
                    .build());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .start(-1L)
                    .end(1L)
                    .build());
        }

        @Test
        public void end() {
            assertNull(ContentRange.builder().build().getEnd());
            assertEquals(2, ContentRange.builder()
                    .start(1L)
                    .end(2L)
                    .build().getEnd());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .end(1L)
                    .build());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .start(1L)
                    .end(-1L)
                    .build());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .start(2L)
                    .end(1L)
                    .build());
        }

        @Test
        public void size() {
            assertNull(ContentRange.builder().build().getSize());
            assertEquals(2, ContentRange.builder()
                    .size(2L)
                    .build().getSize());
            assertThrows(IllegalArgumentException.class, () -> ContentRange.builder()
                    .start(1L)
                    .end(2L)
                    .size(1L)
                    .build());
        }
    }

    @Test
    public void isRedundant() {
        assertFalse(ContentRange.builder().build().isRedundant());
        assertFalse(ContentRange.builder()
                .start(0L)
                .end(1L)
                .build().isRedundant());
        assertTrue(ContentRange.builder()
                .start(0L)
                .end(1L)
                .size(2L)
                .build().isRedundant());
        assertTrue(ContentRange.builder()
                .start(0L)
                .end(0L)
                .size(0L)
                .build().isRedundant());
        assertTrue(ContentRange.builder()
                .start(0L)
                .end(0L)
                .size(1L)
                .build().isRedundant());
    }

    @Test
    public void newFileInputStream(final @TempDir File tempDir) throws Exception {
        final var emptyFile = new File(tempDir, "empty");
        writeString(emptyFile.toPath(), "");
        try (final var inputStream = ContentRange.builder().build().newFileInputStream(emptyFile)) {
            assertArrayEquals(new byte[]{}, inputStream.readAllBytes());
        }
        try (final var inputStream = ContentRange.builder()
                .start(0L)
                .end(0L)
                .build().newFileInputStream(emptyFile)) {
            assertArrayEquals(new byte[]{}, inputStream.readAllBytes());
        }

        final var abcFile = new File(tempDir, "abc");
        writeString(abcFile.toPath(), "abc");
        try (final var inputStream = ContentRange.builder()
                .start(0L)
                .end(0L)
                .build().newFileInputStream(abcFile)) {
            assertArrayEquals("a".getBytes(UTF_8), inputStream.readAllBytes());
        }
        try (final var inputStream = ContentRange.builder()
                .start(1L)
                .end(1L)
                .build().newFileInputStream(abcFile)) {
            assertArrayEquals("b".getBytes(UTF_8), inputStream.readAllBytes());
        }
        try (final var inputStream = ContentRange.builder()
                .start(1L)
                .end(2L)
                .build().newFileInputStream(abcFile)) {
            assertArrayEquals("bc".getBytes(UTF_8), inputStream.readAllBytes());
        }
        try (final var inputStream = ContentRange.builder()
                .start(0L)
                .end(0L)
                .build().newFileInputStream(abcFile)) {
            assertArrayEquals("a".getBytes(UTF_8), inputStream.readAllBytes());
        }
        try (final var inputStream = ContentRange.builder()
                .start(0L)
                .end(2L)
                .build().newFileInputStream(abcFile)) {
            assertArrayEquals("abc".getBytes(UTF_8), inputStream.readAllBytes());
        }
        try (final var inputStream = ContentRange.builder()
                .start(0L)
                .end(2L)
                .size(3L)
                .build().newFileInputStream(abcFile)) {
            assertArrayEquals("abc".getBytes(UTF_8), inputStream.readAllBytes());
        }
    }

    @Test
    public void _toString() {
        assertEquals("bytes */*", ContentRange.builder().build().toString());
        assertEquals("bits */*", ContentRange.builder()
                .unit("bits")
                .build().toString());
        assertEquals("bytes 0-1/*", ContentRange.builder()
                .start(0L)
                .end(1L)
                .build().toString());
        assertEquals("bytes */1", ContentRange.builder()
                .size(1L)
                .build().toString());
        assertEquals("bytes 0-0/0", ContentRange.builder()
                .start(0L)
                .end(0L)
                .size(0L)
                .build().toString());
        assertEquals("bytes 0-1/2", ContentRange.builder()
                .start(0L)
                .end(1L)
                .size(2L)
                .build().toString());
    }
}
