package net.jacobpeterson.jet.common.http.header;

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
}
