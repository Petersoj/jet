package net.jacobpeterson.jet.common.http.version;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class VersionTest {

    @Test
    public void toStringNoPrefix() {
        assertEquals("1.1", Version.HTTP_1_1.toStringNoPrefix());
    }

    @Test
    public void toDecimal() {
        assertEquals(1.1, Version.HTTP_1_1.toDecimal());
    }

    @Test
    public void forString() {
        assertEquals(Version.HTTP_1_1, Version.forString("HTTP/1.1"));
    }

    @Test
    public void forStringNoPrefix() {
        assertEquals(Version.HTTP_1_1, Version.forStringNoPrefix("1.1"));
    }

    @Test
    public void forInteger() {
        assertEquals(Version.HTTP_1_1, Version.forInteger(11));
    }

    @Test
    public void forDecimal() {
        assertEquals(Version.HTTP_1_1, Version.forDecimal(1.1));
    }
}
