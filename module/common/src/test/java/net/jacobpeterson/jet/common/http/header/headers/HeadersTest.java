package net.jacobpeterson.jet.common.http.header.headers;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class HeadersTest {

    @Test
    public void create() {
        final var headers = Headers.create();
        headers.put("abc", "123");
        headers.put("ABC", "123");
        assertEquals(List.of("123", "123"), headers.get("abc"));
        assertEquals(List.of("123", "123"), headers.get("ABC"));
        assertEquals(List.of("123", "123"), headers.get("Abc"));
        assertEquals(List.of("123", "123"), headers.get("ABc"));
        assertEquals(List.of("123", "123"), headers.get("AbC"));
    }

    @Test
    public void getFirst() {
        final var headers = Headers.create();
        headers.put("abc", "123");
        headers.put("abc", "456");
        assertEquals("123", headers.getFirst("abc"));
        assertNull(headers.getFirst("def"));
    }

    @Test
    public void getCommaDelimited() {
        final var headers = Headers.create();
        assertNull(headers.getFirst("def"));
        headers.put("abc", "123");
        assertEquals("123", headers.getCommaDelimited("abc"));
        assertNull(headers.getFirst("def"));
    }

    @Test
    public void getSemicolonDelimited() {
        final var headers = Headers.create();
        assertNull(headers.getFirst("def"));
        headers.put("abc", "123");
        headers.put("abc", "456");
        assertEquals("123;456", headers.getSemicolonDelimited("abc"));
        assertNull(headers.getFirst("def"));
    }

    @Test
    public void set() {
        final var headers = Headers.create();
        headers.put("abc", "123");
        headers.put("abc", "456");
        headers.set("abc", "789");
        assertEquals(List.of("789"), headers.get("abc"));
    }

    @Test
    public void ensureEntry() {
        final var headers = Headers.create();
        headers.ensureEntry("abc", "def");
        assertEquals(List.of("def"), headers.get("abc"));
        headers.ensureEntry("ABC", "def");
        assertEquals(List.of("def"), headers.get("abc"));
    }

    @Test
    public void ensureEntryIgnoreCase() {
        final var headers = Headers.create();
        headers.ensureEntryIgnoreCase("abc", "def");
        assertEquals(List.of("def"), headers.get("abc"));
        headers.ensureEntryIgnoreCase("ABC", "DEF");
        assertEquals(List.of("def"), headers.get("abc"));
    }

    @Test
    public void ensureEntryContaining() {
        final var headers = Headers.create();
        headers.ensureEntryContaining("abc", "def");
        assertEquals(List.of("def"), headers.get("abc"));
        headers.ensureEntryContaining("ABC", "ef");
        assertEquals(List.of("def"), headers.get("abc"));
    }

    @Test
    public void ensureEntryContainingIgnoreCase() {
        final var headers = Headers.create();
        headers.ensureEntryContainingIgnoreCase("abc", "def");
        assertEquals(List.of("def"), headers.get("abc"));
        headers.ensureEntryContainingIgnoreCase("ABC", "EF");
        assertEquals(List.of("def"), headers.get("abc"));
    }
}
