package net.jacobpeterson.jet.common.http.header.cookie;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class SameSiteTest {

    @Test
    public void forString() {
        assertEquals(SameSite.STRICT, SameSite.forString("Strict"));
        assertEquals(SameSite.STRICT, SameSite.forString("STRICT"));
        assertNull(SameSite.forString("a"));
    }
}
