package net.jacobpeterson.jet.common.http.method;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class MethodTest {

    @Test
    public void isSafe() {
        assertTrue(Method.GET.isSafe());
        assertFalse(Method.POST.isSafe());
    }

    @Test
    public void isIdempotent() {
        assertTrue(Method.GET.isIdempotent());
        assertFalse(Method.POST.isIdempotent());
    }

    @Test
    public void isCacheable() {
        assertTrue(Method.GET.isCacheable());
        assertFalse(Method.POST.isCacheable());
    }

    @Test
    public void forName() {
        assertEquals(Method.GET, Method.forName("GET"));
        assertEquals(Method.GET, Method.forName("get"));
        assertNull(Method.forName("a"));
    }
}
