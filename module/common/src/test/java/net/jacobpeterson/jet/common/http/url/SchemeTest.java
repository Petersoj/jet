package net.jacobpeterson.jet.common.http.url;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class SchemeTest {

    @Test
    public void forString() {
        assertEquals(Scheme.HTTPS, Scheme.forString("https"));
        assertEquals(Scheme.HTTPS, Scheme.forString("HTTPS"));
        assertNull(Scheme.forString("a"));
    }

    @Test
    public void forDefaultPort() {
        assertEquals(Scheme.HTTPS, Scheme.forDefaultPort(443));
        assertNull(Scheme.forDefaultPort(-1));
    }
}
