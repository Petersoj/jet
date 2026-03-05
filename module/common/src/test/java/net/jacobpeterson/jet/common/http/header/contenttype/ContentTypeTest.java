package net.jacobpeterson.jet.common.http.header.contenttype;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.MediaType;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class ContentTypeTest {

    @Test
    public void parse() {
        assertEquals(ContentType.TEXT_CSV, ContentType.parse("text/csv"));
        assertThrows(IllegalArgumentException.class, () -> ContentType.parse(""));
        assertThrows(IllegalArgumentException.class, () -> ContentType.parse("a"));
        assertThrows(IllegalArgumentException.class, () -> ContentType.parse("!@/#$"));
        assertThrows(IllegalArgumentException.class, () -> ContentType.parse("*/a"));
    }

    @Test
    public void forFilename() {
        assertEquals(ContentType.create("image", "svg+xml"), ContentType.forFilename("abc.svg"));
        assertEquals(ContentType.create("image", "svg+xml"), ContentType.forFilename(".svg"));
        assertNull(ContentType.forFilename(""));
        assertNull(ContentType.forFilename("asdf"));
    }

    @Test
    public void forFileExtension() {
        assertEquals(ContentType.create("image", "svg+xml"), ContentType.forFileExtension("svg"));
        assertEquals(ContentType.create("image", "png"), ContentType.forFileExtension("png"));
        assertEquals(ContentType.create("image", "png"), ContentType.forFileExtension("PNG"));
        assertEquals(ContentType.create("video", "mp4"), ContentType.forFileExtension("mp4"));
        assertNull(ContentType.forFileExtension("asdfasdf"));
        assertNull(ContentType.forFileExtension("."));
    }

    @Test
    public void create() {
        final var contentType = ContentType.create("a", "b");
        assertEquals("a", contentType.getType());
        assertEquals("b", contentType.getSubtype());
        assertTrue(contentType.getParameters().isEmpty());
        assertNull(contentType.getCharset());
    }

    @Test
    public void createMapParameters() {
        final var contentType = ContentType.create("a", "b", Map.of("a", "b"));
        assertEquals("a", contentType.getType());
        assertEquals("b", contentType.getSubtype());
        assertEquals(ImmutableListMultimap.of("a", "b"), contentType.getParameters());
        assertNull(contentType.getCharset());
    }

    @Test
    public void createMultimapParameters() {
        final var contentType = ContentType.create("a", "b", ImmutableListMultimap.of("a", "b"));
        assertEquals("a", contentType.getType());
        assertEquals("b", contentType.getSubtype());
        assertEquals(ImmutableListMultimap.of("a", "b"), contentType.getParameters());
        assertNull(contentType.getCharset());
    }

    @Test
    public void createCharset() {
        final var contentType = ContentType.create("a", "b", UTF_8);
        assertEquals("a", contentType.getType());
        assertEquals("b", contentType.getSubtype());
        assertEquals(ImmutableListMultimap.of("charset", "utf-8"), contentType.getParameters());
        assertEquals(UTF_8, contentType.getCharset());
    }

    @Test
    public void wrap() {
        final var mediaType = MediaType.ANY_TYPE;
        final var contentType = ContentType.wrap(mediaType);
        assertEquals(mediaType.type(), contentType.getType());
        assertEquals(mediaType.subtype(), contentType.getSubtype());
        assertEquals(mediaType, contentType.unwrap());
    }

    @Test
    public void withoutParameters() {
        assertEquals(ContentType.APPLICATION_JSON, ContentType.APPLICATION_JSON_UTF_8.withoutParameters());
    }

    @Test
    public void withParameter() {
        assertEquals(ContentType.create("a", "b", Map.of("a", "b")),
                ContentType.create("a", "b", Map.of("c", "d")).withParameter("a", "b"));
    }

    @Test
    public void withParametersMap() {
        assertEquals(ContentType.create("a", "b", Map.of("a", "b")),
                ContentType.create("a", "b", Map.of("c", "d")).withParameters(Map.of("a", "b")));
    }

    @Test
    public void withParametersMultimap() {
        assertEquals(ContentType.create("a", "b", Map.of("a", "b")),
                ContentType.create("a", "b", Map.of("c", "d")).withParameters(ImmutableMultimap.of("a", "b")));
    }

    @Test
    public void addParameter() {
        assertEquals(ContentType.create("a", "b", Map.of("a", "b", "c", "d")),
                ContentType.create("a", "b", Map.of("c", "d")).addParameter("a", "b"));
    }

    @Test
    public void addParametersMap() {
        assertEquals(ContentType.create("a", "b", Map.of("a", "b", "c", "d")),
                ContentType.create("a", "b", Map.of("c", "d")).addParameters(Map.of("a", "b")));
    }

    @Test
    public void addParametersMultimap() {
        assertEquals(ContentType.create("a", "b", Map.of("a", "b", "c", "d")),
                ContentType.create("a", "b", Map.of("c", "d")).addParameters(ImmutableMultimap.of("a", "b")));
    }

    @Test
    public void getCharset() {
        assertEquals(UTF_8, ContentType.APPLICATION_JSON_UTF_8.getCharset());
    }

    @Test
    public void withCharset() {
        assertEquals(US_ASCII, ContentType.APPLICATION_JSON_UTF_8.withCharset(US_ASCII).getCharset());
    }

    @Test
    public void isContentType() {
        assertTrue(ContentType.APPLICATION_JSON_UTF_8.is(ContentType.APPLICATION_JSON_UTF_8));
        assertTrue(ContentType.APPLICATION_JSON_UTF_8.is(ContentType.APPLICATION_WILDCARD));
        assertFalse(ContentType.APPLICATION_JSON_UTF_8.is(ContentType.APPLICATION_JAVASCRIPT_UTF_8));
    }

    @Test
    public void isMediaType() {
        assertTrue(ContentType.APPLICATION_JSON_UTF_8.is(MediaType.JSON_UTF_8));
        assertTrue(ContentType.APPLICATION_JSON_UTF_8.is(MediaType.ANY_APPLICATION_TYPE));
        assertFalse(ContentType.APPLICATION_JSON_UTF_8.is(MediaType.JAVASCRIPT_UTF_8));
    }

    @Test
    public void isXssSafeHtmlTag() {
        assertTrue(ContentType.IMAGE_JPEG.isXssSafeHtmlTag());
        assertFalse(ContentType.IMAGE_SVG.isXssSafeHtmlTag());
    }

    @Test
    public void getFileExtensions() {
        assertTrue(ContentType.APPLICATION_JSON.getFileExtensions().contains("json"));
        assertTrue(ContentType.create("a", "b").getFileExtensions().isEmpty());
    }

    @Test
    public void _toString() {
        assertEquals("a/b; a=b", ContentType.create("a", "b", Map.of("a", "b")).toString());
        assertEquals("a/b; a=b; a=c",
                ContentType.create("a", "b", ImmutableListMultimap.of("a", "b", "a", "c")).toString());
    }
}
