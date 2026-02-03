package net.jacobpeterson.jet.common.http.header.cachecontrol;

import net.jacobpeterson.jet.common.http.header.cachecontrol.util.CacheControlUtil;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class CacheControlUtilTest {

    @Test
    public void parse() {
        assertEquals(Map.of(), CacheControlUtil.parse(""));
        assertEquals(Map.of(), CacheControlUtil.parse(" "));
        assertEquals(Map.of(), CacheControlUtil.parse(" , , , "));
        assertEquals(Map.of(), CacheControlUtil.parse(" = , =, =,=="));
        assertEquals(Map.of("a", "b", "c", "", "d", ""), CacheControlUtil.parse("a=b,c,D"));
    }

    @Test
    public void parseValueLong() {
        assertEquals(Optional.empty(), CacheControlUtil.parseValueLong(Map.of(), "a"));
        assertEquals(Optional.of(1L), CacheControlUtil.parseValueLong(Map.of("a", "1"), "a"));
        assertThrows(IllegalArgumentException.class, () -> CacheControlUtil.parseValueLong(Map.of("a", "a"), "a"));
    }

    @Test
    public void _toString() {
        assertEquals("", CacheControlUtil.toString(Map.of()));
        assertEquals("", CacheControlUtil.toString(Map.of("", "")));
        assertEquals("a", CacheControlUtil.toString(Map.of("a", "")));
        assertEquals("c=d", CacheControlUtil.toString(Map.of("c", "d")));
    }
}
