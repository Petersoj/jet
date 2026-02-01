package net.jacobpeterson.jet.common.http.header;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class HeaderTest {

    @Test
    public void forString() {
        assertEquals(Header.CONTENT_TYPE, Header.forString("Content-Type"));
        assertEquals(Header.CONTENT_TYPE, Header.forString("CONTENT-TYPE"));
        assertEquals(Header.CONTENT_TYPE, Header.forString("content-type"));
        assertNull(Header.forString("content type"));
    }
}
