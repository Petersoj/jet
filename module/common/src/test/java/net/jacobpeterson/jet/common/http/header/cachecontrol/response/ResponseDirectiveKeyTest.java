package net.jacobpeterson.jet.common.http.header.cachecontrol.response;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class ResponseDirectiveKeyTest {

    @Test
    public void forString() {
        assertEquals(ResponseDirectiveKey.MAX_AGE, ResponseDirectiveKey.forString("max-age"));
        assertEquals(ResponseDirectiveKey.MAX_AGE, ResponseDirectiveKey.forString("MAX-AGE"));
        assertEquals(ResponseDirectiveKey.MAX_AGE, ResponseDirectiveKey.forString("Max-Age"));
        assertNull(ResponseDirectiveKey.forString("max age"));
    }
}
