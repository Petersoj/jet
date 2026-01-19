package net.jacobpeterson.jet.common.http.header.cookie;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.HttpCookie;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class CookieTest {

    @Test
    public void parseRequestCookies() {
        assertEquals("[a=b, c=d]", Cookie.parseRequestCookies("a=b; c=d").toString());
        assertEquals("[a=, c=d]", Cookie.parseRequestCookies("a; c=d").toString());
        assertEquals("[a=, c=d]", Cookie.parseRequestCookies("a; c=d; ").toString());
        assertEquals("[a=, c=d]", Cookie.parseRequestCookies("a; c=d ;").toString());
        assertEquals("[]", Cookie.parseRequestCookies("").toString());
        assertEquals("[]", Cookie.parseRequestCookies(" ").toString());
        assertEquals("[]", Cookie.parseRequestCookies(";").toString());
        assertEquals("[]", Cookie.parseRequestCookies(";;").toString());
        assertEquals("[]", Cookie.parseRequestCookies("; ; ").toString());
        assertTrue(Cookie.parseRequestCookies(" ").isEmpty());
    }

    @Test
    public void parseResponseCookie() {
        assertEquals("a=b", Cookie.parseResponseCookie("a=b").toString());
        assertEquals("a=b; SameSite=Strict", Cookie.parseResponseCookie("a=b; SameSite=Strict").toString());
        assertEquals("a=b; SameSite=Strict", Cookie.parseResponseCookie("a=b; samesite=strict").toString());
        assertThrows(IllegalArgumentException.class, () -> Cookie.parseResponseCookie("\u0000=b"));
        assertThrows(IllegalArgumentException.class, () -> Cookie.parseResponseCookie("a=b; SameSite=invalid"));
        assertThrows(IllegalArgumentException.class, () -> Cookie.parseResponseCookie("a=b; Max-Age=invalid"));
    }

    @Test
    public void fromJava() {
        assertEquals("a=b", Cookie.fromJava(new HttpCookie("a", "b")).toString());
    }

    @Test
    public void builderCookiePrefix() {
        assertEquals("__Host-Http-a=b", Cookie.builder(CookiePrefix.HOST_HTTP, "a", "b").build().toString());
    }

    @Test
    public void builder() {
        assertEquals("a=b", Cookie.builder("a", "b")
                .build().toString());
    }

    public static class BuilderTest {

        @Test
        public void attributes() {
            assertEquals("a=b; c=d", Cookie.builder("a", "b")
                    .attributes(Map.of("c", "d"))
                    .build().toString());
        }

        @Test
        public void attributeCookieAttribute() {
            assertEquals("a=b; Path=/a", Cookie.builder("a", "b")
                    .attribute(CookieAttribute.PATH, "/a")
                    .build().toString());
        }

        @Test
        public void attributeString() {
            assertEquals("a=b; c=d", Cookie.builder("a", "b")
                    .attribute("c", "d")
                    .build().toString());
        }

        @Test
        public void domain() {
            assertEquals("a=b; Domain=example.com", Cookie.builder("a", "b")
                    .domain("example.com")
                    .build().toString());
        }

        @Test
        public void httpOnly() {
            assertEquals("a=b; HttpOnly", Cookie.builder("a", "b")
                    .httpOnly(true)
                    .build().toString());
        }

        @Test
        public void maxAgeDuration() {
            assertTrue(Cookie.builder("a", "b")
                    .maxAge(Duration.ofSeconds(1))
                    .build().toString()
                    .contains("Max-Age=1"));
        }

        @Test
        public void maxAgeLong() {
            assertTrue(Cookie.builder("a", "b")
                    .maxAge(1)
                    .build().toString()
                    .contains("Max-Age=1"));
        }

        @Test
        public void path() {
            assertEquals("a=b; Path=/", Cookie.builder("a", "b")
                    .path("/")
                    .build().toString());
        }

        @Test
        public void sameSiteStrict() {
            assertEquals("a=b; SameSite=Strict", Cookie.builder("a", "b")
                    .sameSite(SameSite.STRICT)
                    .build().toString());
        }

        @Test
        public void sameSiteLax() {
            assertEquals("a=b; SameSite=Lax", Cookie.builder("a", "b")
                    .sameSite(SameSite.LAX)
                    .build().toString());
        }

        @Test
        public void sameSiteNone() {
            assertEquals("a=b; SameSite=None", Cookie.builder("a", "b")
                    .sameSite(SameSite.NONE)
                    .build().toString());
        }

        @Test
        public void secure() {
            assertEquals("a=b; Secure", Cookie.builder("a", "b")
                    .secure(true)
                    .build().toString());
        }

        @Test
        public void partitioned() {
            assertEquals("a=b; Partitioned", Cookie.builder("a", "b")
                    .partitioned(true)
                    .build().toString());
        }
    }

    @Test
    public void getAttributes() {
        assertEquals(Map.of("c", "d"), Cookie.builder("a", "b")
                .attributes(Map.of("c", "d"))
                .build().getAttributes());
    }

    @Test
    public void getAttributeCookieAttribute() {
        assertEquals("/a", Cookie.builder("a", "b")
                .attribute(CookieAttribute.PATH, "/a")
                .build().getAttribute(CookieAttribute.PATH));
    }

    @Test
    public void getAttributeString() {
        assertEquals("d", Cookie.builder("a", "b")
                .attribute("c", "d")
                .build().getAttribute("c"));
    }

    @Test
    public void getName() {
        assertEquals("a", Cookie.builder("a", "b")
                .build().getName());
    }

    @Test
    public void getPrefix() {
        assertEquals(CookiePrefix.HOST_HTTP, Cookie.builder(CookiePrefix.HOST_HTTP, "a", "b")
                .build().getPrefix());
        assertEquals(CookiePrefix.HOST_HTTP, Cookie.builder("__Host-Http-a", "b")
                .build().getPrefix());
        assertEquals(CookiePrefix.HOST, Cookie.builder(CookiePrefix.HOST, "a", "b")
                .build().getPrefix());
        assertEquals(CookiePrefix.HOST, Cookie.builder("__Host-a", "b")
                .build().getPrefix());
    }

    @Test
    public void getValue() {
        assertEquals("b", Cookie.builder("a", "b")
                .build().getValue());
    }

    @Test
    public void getDomain() {
        assertEquals("example.com", Cookie.builder("a", "b")
                .domain("example.com")
                .build().getDomain());
    }

    @Test
    public void getExpires() {
        assertEquals(ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                Cookie.parseResponseCookie("a=b; Expires=Thu, 01 Jan 2026 00:00:00 GMT").getExpires());
        assertNull(Cookie.builder("a", "b").build().getExpires());
    }

    @Test
    public void isExpired() {
        assertFalse(Cookie.builder("a", "b")
                .maxAge(1)
                .build().isExpired());
        assertTrue(Cookie.builder("a", "b")
                .maxAge(0)
                .build().isExpired());
        assertTrue(Cookie.builder("a", "b")
                .maxAge(-1)
                .build().isExpired());
        assertFalse(Cookie.builder("a", "b").build().isExpired());
        assertTrue(Cookie.parseResponseCookie("a=b; Expires=Thu, 01 Jan 2026 00:00:00 GMT").isExpired());
        assertFalse(Cookie.parseResponseCookie("a=b; Expires=" +
                ZonedDateTime.now(ZoneOffset.UTC).plusDays(1).format(RFC_1123_DATE_TIME)).isExpired());
    }

    @Test
    public void isHttpOnly() {
        assertTrue(Cookie.builder("a", "b")
                .httpOnly(true)
                .build().isHttpOnly());
        assertFalse(Cookie.builder("a", "b").build().isHttpOnly());
    }

    @Test
    public void getMaxAge() {
        assertEquals(1, Cookie.builder("a", "b")
                .maxAge(1)
                .build().getMaxAge());
        assertEquals(0, Cookie.builder("a", "b")
                .maxAge(0)
                .build().getMaxAge());
        assertEquals(0, Cookie.builder("a", "b")
                .maxAge(-1)
                .build().getMaxAge());
    }

    @Test
    public void getPath() {
        assertEquals("/a", Cookie.builder("a", "b")
                .path("/a")
                .build().getPath());
        assertEquals("/a", Cookie.parseResponseCookie("a=b; Path=/a").getPath());
    }

    @Test
    public void getSameSite() {
        assertEquals(SameSite.STRICT, Cookie.builder("a", "b")
                .sameSite(SameSite.STRICT)
                .build().getSameSite());
        assertEquals(SameSite.STRICT, Cookie.parseResponseCookie("a=b; SAMESITE=STRICT").getSameSite());
        assertNull(Cookie.builder("a", "b").build().getSameSite());
    }

    @Test
    public void isSecure() {
        assertTrue(Cookie.builder("a", "b")
                .secure(true)
                .build().isSecure());
        assertFalse(Cookie.builder("a", "b").build().isSecure());
    }

    @Test
    public void isPartitioned() {
        assertTrue(Cookie.builder("a", "b")
                .partitioned(true)
                .build().isPartitioned());
        assertFalse(Cookie.builder("a", "b").build().isPartitioned());
    }

    @Test
    public void toJava() {
        assertEquals(HttpCookie.parse("a=b; Path=/a").getFirst(), Cookie.builder("a", "b")
                .path("/a")
                .build().toJava());
    }

    @Test
    public void toBuilder() {
        final var cookie = Cookie.builder("a", "b").build();
        assertEquals(cookie, cookie.toBuilder().build());
    }

    @Test
    public void toRequestString() {
        assertEquals("a=b", Cookie.builder("a", "b")
                .secure(true)
                .build().toRequestString());
    }

    @Test
    public void toResponseString() {
        assertEquals("a=b; Secure", Cookie.builder("a", "b")
                .secure(true)
                .build().toResponseString());
    }

    @Test
    public void _toString() {
        assertEquals("a=b", Cookie.builder("a", "b").build().toString());
    }

    @Test
    public void _equals() {
        assertEquals(Cookie.builder("a", "b").build(), Cookie.builder("a", "b").build());
        assertEquals(Cookie.builder("a", "b").secure(true).build(), Cookie.builder("a", "b").secure(true).build());
        assertNotEquals(Cookie.builder("a", "b").secure(true).build(), Cookie.builder("a", "b").build());
    }
}
