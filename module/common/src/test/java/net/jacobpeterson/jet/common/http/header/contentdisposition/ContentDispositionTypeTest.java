package net.jacobpeterson.jet.common.http.header.contentdisposition;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class ContentDispositionTypeTest {

    @Test
    public void forString() {
        assertEquals(ContentDispositionType.INLINE, ContentDispositionType.forString("inline"));
        assertEquals(ContentDispositionType.INLINE, ContentDispositionType.forString("INLINE"));
        assertNull(ContentDispositionType.forString("a"));
    }
}
