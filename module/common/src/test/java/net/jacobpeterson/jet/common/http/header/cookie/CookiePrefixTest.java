package net.jacobpeterson.jet.common.http.header.cookie;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class CookiePrefixTest {

    @Test
    public void forString() {
        assertEquals(CookiePrefix.HOST_HTTP, CookiePrefix.forString("__Host-Http-"));
        assertEquals(CookiePrefix.HOST_HTTP, CookiePrefix.forString("__HOST-HTTP-"));
        assertNull(CookiePrefix.forString("a"));
        assertNull(CookiePrefix.forString("__a"));
    }

    @Test
    public void fromCookieName() {
        assertEquals(CookiePrefix.HOST_HTTP, CookiePrefix.fromCookieName("__Host-Http-a"));
        assertNull(CookiePrefix.fromCookieName("a"));
        assertNull(CookiePrefix.fromCookieName("__a"));
    }
}
