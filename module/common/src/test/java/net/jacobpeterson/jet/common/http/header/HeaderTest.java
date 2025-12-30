package net.jacobpeterson.jet.common.http.header;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class HeaderTest {

    @Test
    public void forName() {
        assertEquals(Header.CONTENT_TYPE, Header.forName("Content-Type"));
        assertEquals(Header.CONTENT_TYPE, Header.forName("CONTENT-TYPE"));
        assertEquals(Header.CONTENT_TYPE, Header.forName("content-type"));
        assertNull(Header.forName("content type"));
    }
}
