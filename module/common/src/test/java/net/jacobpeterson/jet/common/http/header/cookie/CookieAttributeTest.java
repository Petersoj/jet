package net.jacobpeterson.jet.common.http.header.cookie;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class CookieAttributeTest {

    @Test
    public void forString() {
        assertEquals(CookieAttribute.SECURE, CookieAttribute.forString("secure"));
        assertEquals(CookieAttribute.SECURE, CookieAttribute.forString("SECURE"));
        assertNull(CookieAttribute.forString("a"));
    }
}
