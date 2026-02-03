package net.jacobpeterson.jet.common.http.header.cachecontrol.response;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class ResponseCacheControlTest {

    @Test
    public void parse() {
        assertEquals(Map.of(), ResponseCacheControl.parse("").getDirectives());
        assertEquals(Map.of(), ResponseCacheControl.parse(" ").getDirectives());
        assertEquals(Map.of(), ResponseCacheControl.parse(" , , ").getDirectives());
        assertEquals(Map.of(), ResponseCacheControl.parse("=, ==, ,,=").getDirectives());
        assertEquals(Map.of("a", ""), ResponseCacheControl.parse(",a").getDirectives());
        assertEquals(Map.of("a", "B"), ResponseCacheControl.parse("A=B,").getDirectives());
        assertEquals(Map.of("a", "B", "c", ""), ResponseCacheControl.parse("A=B,c").getDirectives());
    }

    @Test
    public void builder() {
        assertEquals(ResponseCacheControl.builder().build(), ResponseCacheControl.builder().build());
        assertEquals(ResponseCacheControl.parse(""), ResponseCacheControl.builder().build());
    }

    @Test
    public void builderMap() {
        assertEquals(Map.of(),
                ResponseCacheControl.builder(Map.of()).build().getDirectives());
        assertEquals(Map.of("", ""),
                ResponseCacheControl.builder(Map.of("", "")).build().getDirectives());
        assertEquals(Map.of("a", ""),
                ResponseCacheControl.builder(Map.of("a", "")).build().getDirectives());
        assertEquals(Map.of("a", "a"),
                ResponseCacheControl.builder(Map.of("a", "a")).build().getDirectives());
        assertEquals(Map.of("a", "A", "b", "b"),
                ResponseCacheControl.builder(Map.of("A", "A", "B", "b")).build().getDirectives());

        assertEquals("", ResponseCacheControl.builder(Map.of("", "")).build().toString());
        assertEquals("a", ResponseCacheControl.builder(Map.of("A", "")).build().toString());
    }

    public static final class BuilderTest {

        @Test
        public void putDirectiveValueString() {
            assertEquals(ResponseCacheControl.parse("a=b, c=d"), ResponseCacheControl.builder()
                    .putDirectiveValue("a", "b")
                    .putDirectiveValue("c", "d")
                    .build());
        }

        @Test
        public void putDirectiveValueResponseDirectiveKey() {
            assertEquals(ResponseCacheControl.parse("max-age=1, s-maxage=1"), ResponseCacheControl.builder()
                    .putDirectiveValue(ResponseDirectiveKey.MAX_AGE, "1")
                    .putDirectiveValue(ResponseDirectiveKey.S_MAXAGE, "1")
                    .build());
        }

        @Test
        public void putDirectiveValuelessString() {
            assertEquals(ResponseCacheControl.parse("a, b"), ResponseCacheControl.builder()
                    .putDirectiveValueless("a")
                    .putDirectiveValueless("b")
                    .build());
        }

        @Test
        public void putDirectiveValuelessResponseDirectiveKey() {
            assertEquals(ResponseCacheControl.parse("immutable, public"), ResponseCacheControl.builder()
                    .putDirectiveValueless(ResponseDirectiveKey.IMMUTABLE)
                    .putDirectiveValueless(ResponseDirectiveKey.PUBLIC)
                    .build());
        }

        @Test
        public void putDirectiveValueLongString() {
            assertEquals(ResponseCacheControl.parse("a=1, b=1"), ResponseCacheControl.builder()
                    .putDirectiveValueLong("a", 1)
                    .putDirectiveValueLong("b", 1)
                    .build());
        }

        @Test
        public void putDirectiveValueLongResponseDirectiveKey() {
            assertEquals(ResponseCacheControl.parse("max-age=1, s-maxage=1"), ResponseCacheControl.builder()
                    .putDirectiveValueLong(ResponseDirectiveKey.MAX_AGE, 1)
                    .putDirectiveValueLong(ResponseDirectiveKey.S_MAXAGE, 1)
                    .build());
        }

        @Test
        public void maxAge() {
            assertEquals(ResponseCacheControl.parse("max-age=1"), ResponseCacheControl.builder()
                    .maxAge(1)
                    .build());
        }

        @Test
        public void sMaxage() {
            assertEquals(ResponseCacheControl.parse("s-maxage=1"), ResponseCacheControl.builder()
                    .sMaxage(1)
                    .build());
        }

        @Test
        public void noCache() {
            assertEquals(ResponseCacheControl.parse("no-cache"), ResponseCacheControl.builder()
                    .noCache()
                    .build());
        }

        @Test
        public void mustRevalidate() {
            assertEquals(ResponseCacheControl.parse("must-revalidate"), ResponseCacheControl.builder()
                    .mustRevalidate()
                    .build());
        }

        @Test
        public void proxyRevalidate() {
            assertEquals(ResponseCacheControl.parse("proxy-revalidate"), ResponseCacheControl.builder()
                    .proxyRevalidate()
                    .build());
        }

        @Test
        public void noStore() {
            assertEquals(ResponseCacheControl.parse("no-store"), ResponseCacheControl.builder()
                    .noStore()
                    .build());
        }

        @Test
        public void _private() {
            assertEquals(ResponseCacheControl.parse("private"), ResponseCacheControl.builder()
                    ._private()
                    .build());
        }

        @Test
        public void _public() {
            assertEquals(ResponseCacheControl.parse("public"), ResponseCacheControl.builder()
                    ._public()
                    .build());
        }

        @Test
        public void mustUnderstand() {
            assertEquals(ResponseCacheControl.parse("must-understand"), ResponseCacheControl.builder()
                    .mustUnderstand()
                    .build());
        }

        @Test
        public void noTransform() {
            assertEquals(ResponseCacheControl.parse("no-transform"), ResponseCacheControl.builder()
                    .noTransform()
                    .build());
        }

        @Test
        public void immutable() {
            assertEquals(ResponseCacheControl.parse("immutable"), ResponseCacheControl.builder()
                    .immutable()
                    .build());
        }

        @Test
        public void staleWhileRevalidate() {
            assertEquals(ResponseCacheControl.parse("stale-while-revalidate=1"), ResponseCacheControl.builder()
                    .staleWhileRevalidate(1)
                    .build());
        }

        @Test
        public void staleIfError() {
            assertEquals(ResponseCacheControl.parse("stale-if-error=1"), ResponseCacheControl.builder()
                    .staleIfError(1)
                    .build());
        }
    }

    @Test
    public void containsKeyResponseDirectiveKey() {
        assertTrue(ResponseCacheControl.parse("public").containsKey(ResponseDirectiveKey.PUBLIC));
    }

    @Test
    public void containsKeyString() {
        assertTrue(ResponseCacheControl.parse("a").containsKey("a"));
    }

    @Test
    public void parseValueLongResponseDirectiveKey() {
        assertEquals(1, ResponseCacheControl.parse("max-age=1")
                .parseValueLong(ResponseDirectiveKey.MAX_AGE).orElseThrow());
        assertThrows(IllegalArgumentException.class, () ->
                ResponseCacheControl.parse("max-age=a").parseValueLong(ResponseDirectiveKey.MAX_AGE));
    }

    @Test
    public void parseValueLongString() {
        assertEquals(1, ResponseCacheControl.parse("a=1")
                .parseValueLong("a").orElseThrow());
        assertThrows(IllegalArgumentException.class, () ->
                ResponseCacheControl.parse("a=a").parseValueLong("a"));
    }

    @Test
    public void getMaxAge() {
        assertEquals(1, ResponseCacheControl.parse("max-age=1").getMaxAge());
        assertEquals(1, ResponseCacheControl.builder()
                .maxAge(1)
                .build().getMaxAge());
    }

    @Test
    public void getSMaxage() {
        assertEquals(1, ResponseCacheControl.parse("s-maxage=1").getSMaxage());
        assertEquals(1, ResponseCacheControl.builder()
                .sMaxage(1)
                .build().getSMaxage());
    }

    @Test
    public void isNoCache() {
        assertTrue(ResponseCacheControl.parse("no-cache").isNoCache());
        assertTrue(ResponseCacheControl.builder()
                .noCache()
                .build().isNoCache());
    }

    @Test
    public void isMustRevalidate() {
        assertTrue(ResponseCacheControl.parse("must-revalidate").isMustRevalidate());
        assertTrue(ResponseCacheControl.builder()
                .mustRevalidate()
                .build().isMustRevalidate());
    }

    @Test
    public void isProxyRevalidate() {
        assertTrue(ResponseCacheControl.parse("proxy-revalidate").isProxyRevalidate());
        assertTrue(ResponseCacheControl.builder()
                .proxyRevalidate()
                .build().isProxyRevalidate());
    }

    @Test
    public void isNoStore() {
        assertTrue(ResponseCacheControl.parse("no-store").isNoStore());
        assertTrue(ResponseCacheControl.builder()
                .noStore()
                .build().isNoStore());
    }

    @Test
    public void isPrivate() {
        assertTrue(ResponseCacheControl.parse("private").isPrivate());
        assertTrue(ResponseCacheControl.builder()
                ._private()
                .build().isPrivate());
    }

    @Test
    public void isPublic() {
        assertTrue(ResponseCacheControl.parse("public").isPublic());
        assertTrue(ResponseCacheControl.builder()
                ._public()
                .build().isPublic());
    }

    @Test
    public void isMustUnderstand() {
        assertTrue(ResponseCacheControl.parse("must-understand").isMustUnderstand());
        assertTrue(ResponseCacheControl.builder()
                .mustUnderstand()
                .build().isMustUnderstand());
    }

    @Test
    public void isNoTransform() {
        assertTrue(ResponseCacheControl.parse("no-transform").isNoTransform());
        assertTrue(ResponseCacheControl.builder()
                .noTransform()
                .build().isNoTransform());
    }

    @Test
    public void isImmutable() {
        assertTrue(ResponseCacheControl.parse("immutable").isImmutable());
        assertTrue(ResponseCacheControl.builder()
                .immutable()
                .build().isImmutable());
    }

    @Test
    public void getStaleWhileRevalidate() {
        assertEquals(1, ResponseCacheControl.parse("stale-while-revalidate=1").getStaleWhileRevalidate());
        assertEquals(1, ResponseCacheControl.builder()
                .staleWhileRevalidate(1)
                .build().getStaleWhileRevalidate());
    }

    @Test
    public void getStaleIfError() {
        assertEquals(1, ResponseCacheControl.parse("stale-if-error=1").getStaleIfError());
        assertEquals(1, ResponseCacheControl.builder()
                .staleIfError(1)
                .build().getStaleIfError());
    }

    @Test
    public void toBuilder() {
        final var responseCacheControl = ResponseCacheControl.parse("max-age=1, immutable");
        assertEquals(responseCacheControl, responseCacheControl.toBuilder().build());
        assertEquals("max-age=1, immutable, public", responseCacheControl.toBuilder()
                ._public()
                .build().toString());
    }

    @Test
    public void _toString() {
        assertEquals("max-age=10, s-maxage=1, must-understand, stale-if-error=1", ResponseCacheControl.builder()
                .maxAge(10)
                .sMaxage(1)
                .mustUnderstand()
                .staleIfError(1)
                .build().toString());
    }
}
