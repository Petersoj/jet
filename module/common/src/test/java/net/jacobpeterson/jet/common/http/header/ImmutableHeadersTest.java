package net.jacobpeterson.jet.common.http.header;

import com.google.common.collect.ImmutableListMultimap;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class ImmutableHeadersTest {

    @Test
    public void create() {
        final var headers = ImmutableHeaders.create(ImmutableListMultimap.of("abc", "123", "ABC", "123"));
        assertEquals(List.of("123", "123"), headers.get("abc"));
        assertEquals(List.of("123", "123"), headers.get("ABC"));
        assertEquals(List.of("123", "123"), headers.get("Abc"));
        assertEquals(List.of("123", "123"), headers.get("ABc"));
        assertEquals(List.of("123", "123"), headers.get("AbC"));
        assertThrows(UnsupportedOperationException.class, () -> headers.put("abc", "123"));
    }
}
