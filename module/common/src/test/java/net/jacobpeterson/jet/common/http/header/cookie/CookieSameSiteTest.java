package net.jacobpeterson.jet.common.http.header.cookie;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class CookieSameSiteTest {

    @Test
    public void forString() {
        assertEquals(CookieSameSite.STRICT, CookieSameSite.forString("Strict"));
        assertEquals(CookieSameSite.STRICT, CookieSameSite.forString("STRICT"));
        assertNull(CookieSameSite.forString("a"));
    }
}
