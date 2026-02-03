package net.jacobpeterson.jet.common.http.header.cachecontrol.request;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class RequestDirectiveKeyTest {

    @Test
    public void forString() {
        assertEquals(RequestDirectiveKey.MAX_AGE, RequestDirectiveKey.forString("max-age"));
        assertEquals(RequestDirectiveKey.MAX_AGE, RequestDirectiveKey.forString("MAX-AGE"));
        assertEquals(RequestDirectiveKey.MAX_AGE, RequestDirectiveKey.forString("Max-Age"));
        assertNull(RequestDirectiveKey.forString("max age"));
    }
}
