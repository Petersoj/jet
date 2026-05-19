package net.jacobpeterson.jet.common.http.header.headers;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
