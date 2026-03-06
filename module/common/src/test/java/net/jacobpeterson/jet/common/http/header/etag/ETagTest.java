package net.jacobpeterson.jet.common.http.header.etag;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class ETagTest {

    @Test
    public void computeStrong(final @TempDir File tempDir) throws IOException {
        final var testFile = new File(tempDir, "test.txt");
        writeString(testFile.toPath(), "abc");
        final var etag = ETag.computeStrong(testFile);
        assertFalse(etag.isWeak());
        assertFalse(etag.getValue().isEmpty());
    }

    @Test
    public void computeWeak(final @TempDir File tempDir) {
        final var etag = ETag.computeWeak(tempDir);
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
