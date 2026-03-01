package net.jacobpeterson.jet.common.http.header.contentdisposition;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDispositionType.ATTACHMENT;
import static net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDispositionType.FORM_DATA;
import static net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDispositionType.INLINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class ContentDispositionTest {

    @Test
    public void parse() {
        {
            final var contentDisposition = ContentDisposition.parse("inline");
            assertEquals(INLINE, contentDisposition.getType());
            assertNull(contentDisposition.getName());
            assertNull(contentDisposition.getUtf8EncodedFilename());
            assertNull(contentDisposition.getFilename());
        }
        {
            final var contentDisposition = ContentDisposition.parse("attachment");
            assertEquals(ATTACHMENT, contentDisposition.getType());
            assertNull(contentDisposition.getName());
            assertNull(contentDisposition.getUtf8EncodedFilename());
            assertNull(contentDisposition.getFilename());
        }
        {
            final var contentDisposition = ContentDisposition.parse("ATTACHMENT; FILENAME=\"file name.jpg\"");
            assertEquals(ATTACHMENT, contentDisposition.getType());
            assertNull(contentDisposition.getName());
            assertEquals("file%20name.jpg", contentDisposition.getUtf8EncodedFilename());
            assertEquals("file name.jpg", contentDisposition.getFilename());
        }
        {
            final var contentDisposition = ContentDisposition.parse("attachment; filename*=utf-8''file%20name.jpg");
            assertEquals(ATTACHMENT, contentDisposition.getType());
            assertNull(contentDisposition.getName());
            assertEquals("file%20name.jpg", contentDisposition.getUtf8EncodedFilename());
            assertEquals("file name.jpg", contentDisposition.getFilename());
        }
        {
            final var contentDisposition = ContentDisposition.parse("form-data; name=\"fieldName\"");
            assertEquals(FORM_DATA, contentDisposition.getType());
            assertEquals("fieldName", contentDisposition.getName());
            assertNull(contentDisposition.getUtf8EncodedFilename());
            assertNull(contentDisposition.getFilename());
        }
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse(" ; ;="));
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse(" a;"));
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse(" filename="));
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse("inline; filename"));
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse("inline; filename*="));
        assertThrows(IllegalArgumentException.class, () -> ContentDisposition.parse("inline; filename*=ASCII''a"));
    }

    public static final class BuilderTest {

        @Test
        public void type() {
            assertEquals(INLINE, ContentDisposition.builder()
                    .type(INLINE)
                    .build().getType());
            assertThrows(IllegalArgumentException.class, () -> ContentDisposition.builder().build());
        }

        @Test
        public void name() {
            assertEquals("name", ContentDisposition.builder()
                    .type(FORM_DATA)
                    .name("name")
                    .build().getName());
        }

        @Test
        public void utf8EncodedFilename() {
            assertEquals("file%20name.jpg", ContentDisposition.builder()
                    .type(ATTACHMENT)
                    .utf8EncodedFilename("file%20name.jpg")
                    .build().getUtf8EncodedFilename());
        }

        @Test
        public void filename() {
            assertEquals("file name.jpg", ContentDisposition.builder()
                    .type(ATTACHMENT)
                    .filename("file name.jpg")
                    .build().getFilename());
        }
    }

    @Test
    public void getFilename() {
        assertEquals("file name.jpg", ContentDisposition.builder()
                .type(ATTACHMENT)
                .utf8EncodedFilename("file%20name.jpg")
                .build().getFilename());
    }

    @Test
    public void _toString() {
        assertEquals("inline", ContentDisposition.builder()
                .type(INLINE)
                .build().toString());
        assertEquals("form-data; name=\"name\"", ContentDisposition.builder()
                .type(FORM_DATA)
                .name("name")
                .build().toString());
        assertEquals("attachment; filename=\"file name.jpg\"; filename*=UTF-8''file%20name.jpg",
                ContentDisposition.builder()
                        .type(ATTACHMENT)
                        .filename("file name.jpg")
                        .build().toString());
    }
}
