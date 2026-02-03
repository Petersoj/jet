package net.jacobpeterson.jet.common.http.header.cachecontrol.request;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class RequestCacheControlTest {

    @Test
    public void parse() {
        assertEquals(Map.of(), RequestCacheControl.parse("").getDirectives());
        assertEquals(Map.of(), RequestCacheControl.parse(" ").getDirectives());
        assertEquals(Map.of(), RequestCacheControl.parse(" , , ").getDirectives());
        assertEquals(Map.of(), RequestCacheControl.parse("=, ==, ,,=").getDirectives());
        assertEquals(Map.of("a", ""), RequestCacheControl.parse(",a").getDirectives());
        assertEquals(Map.of("a", "B"), RequestCacheControl.parse("A=B,").getDirectives());
        assertEquals(Map.of("a", "B", "c", ""), RequestCacheControl.parse("A=B,c").getDirectives());
    }

    @Test
    public void builder() {
        assertEquals(RequestCacheControl.builder().build(), RequestCacheControl.builder().build());
        assertEquals(RequestCacheControl.parse(""), RequestCacheControl.builder().build());
    }

    @Test
    public void builderMap() {
        assertEquals(Map.of(),
                RequestCacheControl.builder(Map.of()).build().getDirectives());
        assertEquals(Map.of("", ""),
                RequestCacheControl.builder(Map.of("", "")).build().getDirectives());
        assertEquals(Map.of("a", ""),
                RequestCacheControl.builder(Map.of("a", "")).build().getDirectives());
        assertEquals(Map.of("a", "a"),
                RequestCacheControl.builder(Map.of("a", "a")).build().getDirectives());
        assertEquals(Map.of("a", "A", "b", "b"),
                RequestCacheControl.builder(Map.of("A", "A", "B", "b")).build().getDirectives());

        assertEquals("", RequestCacheControl.builder(Map.of("", "")).build().toString());
        assertEquals("a", RequestCacheControl.builder(Map.of("A", "")).build().toString());
    }

    public static final class BuilderTest {

        @Test
        public void putDirectiveValueString() {
            assertEquals(RequestCacheControl.parse("a=b, c=d"), RequestCacheControl.builder()
                    .putDirectiveValue("a", "b")
                    .putDirectiveValue("c", "d")
                    .build());
        }

        @Test
        public void putDirectiveValueRequestDirectiveKey() {
            assertEquals(RequestCacheControl.parse("max-age=1, min-fresh=1"), RequestCacheControl.builder()
                    .putDirectiveValue(RequestDirectiveKey.MAX_AGE, "1")
                    .putDirectiveValue(RequestDirectiveKey.MIN_FRESH, "1")
                    .build());
        }

        @Test
        public void putDirectiveValuelessString() {
            assertEquals(RequestCacheControl.parse("a, b"), RequestCacheControl.builder()
                    .putDirectiveValueless("a")
                    .putDirectiveValueless("b")
                    .build());
        }

        @Test
        public void putDirectiveValuelessRequestDirectiveKey() {
            assertEquals(RequestCacheControl.parse("only-if-cached, stale-if-error"), RequestCacheControl.builder()
                    .putDirectiveValueless(RequestDirectiveKey.ONLY_IF_CACHED)
                    .putDirectiveValueless(RequestDirectiveKey.STALE_IF_ERROR)
                    .build());
        }

        @Test
        public void putDirectiveValueLongString() {
            assertEquals(RequestCacheControl.parse("a=1, b=1"), RequestCacheControl.builder()
                    .putDirectiveValueLong("a", 1)
                    .putDirectiveValueLong("b", 1)
                    .build());
        }

        @Test
        public void putDirectiveValueLongRequestDirectiveKey() {
            assertEquals(RequestCacheControl.parse("max-age=1, min-fresh=1"), RequestCacheControl.builder()
                    .putDirectiveValueLong(RequestDirectiveKey.MAX_AGE, 1)
                    .putDirectiveValueLong(RequestDirectiveKey.MIN_FRESH, 1)
                    .build());
        }

        @Test
        public void noCache() {

        }

        @Test
        public void noStore() {

        }

        @Test
        public void maxAge() {

        }

        @Test
        public void maxStale() {

        }

        @Test
        public void minFresh() {

        }

        @Test
        public void noTransform() {

        }

        @Test
        public void onlyIfCached() {

        }

        @Test
        public void staleIfError() {

        }
    }

    @Test
    public void containsKeyRequestDirectiveKey() {
        assertTrue(RequestCacheControl.parse("no-cache").containsKey(RequestDirectiveKey.NO_CACHE));
    }

    @Test
    public void containsKeyString() {
        assertTrue(RequestCacheControl.parse("a").containsKey("a"));
    }

    @Test
    public void parseValueLongRequestDirectiveKey() {
        assertEquals(1, RequestCacheControl.parse("max-age=1")
                .parseValueLong(RequestDirectiveKey.MAX_AGE).orElseThrow());
        assertThrows(IllegalArgumentException.class, () ->
                RequestCacheControl.parse("max-age=a").parseValueLong(RequestDirectiveKey.MAX_AGE));
    }

    @Test
    public void parseValueLongString() {
        assertEquals(1, RequestCacheControl.parse("a=1")
                .parseValueLong("a").orElseThrow());
        assertThrows(IllegalArgumentException.class, () ->
                RequestCacheControl.parse("a=a").parseValueLong("a"));
    }

    @Test
    public void isNoCache() {
        assertTrue(RequestCacheControl.parse("no-cache").isNoCache());
        assertTrue(RequestCacheControl.builder()
                .noCache()
                .build().isNoCache());
    }

    @Test
    public void isNoStore() {
        assertTrue(RequestCacheControl.parse("no-store").isNoStore());
        assertTrue(RequestCacheControl.builder()
                .noStore()
                .build().isNoStore());
    }

    @Test
    public void getMaxAge() {
        assertEquals(1, RequestCacheControl.parse("max-age=1").getMaxAge());
        assertEquals(1, RequestCacheControl.builder()
                .maxAge(1)
                .build().getMaxAge());
    }

    @Test
    public void getMaxStale() {
        assertEquals(1, RequestCacheControl.parse("max-stale=1").getMaxStale());
        assertEquals(1, RequestCacheControl.builder()
                .maxStale(1)
                .build().getMaxStale());
    }

    @Test
    public void getMinFresh() {
        assertEquals(1, RequestCacheControl.parse("min-fresh=1").getMinFresh());
        assertEquals(1, RequestCacheControl.builder()
                .minFresh(1)
                .build().getMinFresh());
    }

    @Test
    public void isNoTransform() {
        assertTrue(RequestCacheControl.parse("no-transform").isNoTransform());
        assertTrue(RequestCacheControl.builder()
                .noTransform()
                .build().isNoTransform());
    }

    @Test
    public void isOnlyIfCached() {
        assertTrue(RequestCacheControl.parse("only-if-cached").isOnlyIfCached());
        assertTrue(RequestCacheControl.builder()
                .onlyIfCached()
                .build().isOnlyIfCached());
    }

    @Test
    public void isStaleIfError() {
        assertTrue(RequestCacheControl.parse("stale-if-error").isStaleIfError());
        assertTrue(RequestCacheControl.builder()
                .staleIfError()
                .build().isStaleIfError());
    }

    @Test
    public void toBuilder() {
        final var requestCacheControl = RequestCacheControl.parse("max-age=1, only-if-cached");
        assertEquals(requestCacheControl, requestCacheControl.toBuilder().build());
        assertEquals("max-age=1, only-if-cached, no-transform", requestCacheControl.toBuilder()
                .noTransform()
                .build().toString());
    }

    @Test
    public void _toString() {
        assertEquals("max-age=10, max-stale=1, no-transform, only-if-cached", RequestCacheControl.builder()
                .maxAge(10)
                .maxStale(1)
                .noTransform()
                .onlyIfCached()
                .build().toString());
    }
}
