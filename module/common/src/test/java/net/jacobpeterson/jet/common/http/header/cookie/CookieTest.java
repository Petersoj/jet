package net.jacobpeterson.jet.common.http.header.cookie;

import com.google.common.collect.ImmutableMap;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.HttpCookie;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class CookieTest {

    @Test
    public void parseRequestCookies() {
        assertEquals(ImmutableMap.of("a", "b", "c", "d"), Cookie.parseRequestCookies("a=b; c=d"));
        assertEquals(ImmutableMap.of("a", "", "c", "d"), Cookie.parseRequestCookies("a; c=d"));
        assertEquals(ImmutableMap.of("a", "", "c", "d"), Cookie.parseRequestCookies("a; c=d; "));
        assertEquals(ImmutableMap.of("a", "", "c", "d"), Cookie.parseRequestCookies("a; c=d ;"));
        assertEquals(ImmutableMap.of(), Cookie.parseRequestCookies(""));
        assertEquals(ImmutableMap.of(), Cookie.parseRequestCookies(" "));
        assertEquals(ImmutableMap.of(), Cookie.parseRequestCookies(";"));
        assertEquals(ImmutableMap.of(), Cookie.parseRequestCookies(";;"));
        assertEquals(ImmutableMap.of(), Cookie.parseRequestCookies("; ; "));
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
    public void multipleToRequestString() {
        assertEquals("", Cookie.multipleToRequestString(List.of()));
        assertEquals("a=b", Cookie.multipleToRequestString(List.of(Cookie.parseResponseCookie("a=b"))));
        assertEquals("a=b; b=c", Cookie.multipleToRequestString(List.of(Cookie.parseResponseCookie("a=b"),
                Cookie.builder()
                        .name("b")
                        .value("c")
                        .secure()
                        .build())));
    }

    @Test
    public void fromJava() {
        assertEquals("a=b", Cookie.fromJava(new HttpCookie("a", "b")).toString());
    }

    @Test
    public void builder() {
        assertEquals(Cookie.builder()
                .name("a")
                .value("b")
                .build(), Cookie.builder()
                .name("a")
                .value("b")
                .build());
        assertThrows(IllegalArgumentException.class, () -> Cookie.builder().build());
    }

    public static final class BuilderTest {

        @Test
        public void nameCookiePrefix() {
            assertEquals("__Host-Http-a", Cookie.builder()
                    .name(CookiePrefix.HOST_HTTP, "a")
                    .value("b")
                    .build().getName());
        }

        @Test
        public void name() {
            assertEquals("a", Cookie.builder()
                    .name("a")
                    .value("b")
                    .build().getName());
        }

        @Test
        public void value() {
            assertEquals("b", Cookie.builder()
                    .name("a")
                    .value("b")
                    .build().getValue());
        }

        @Test
        public void attributes() {
            assertEquals("a=b; c=d", Cookie.builder()
                    .name("a")
                    .value("b")
                    .attributes(Map.of("c", "d"))
                    .build().toString());
        }

        @Test
        public void attributeCookieAttribute() {
            assertEquals("a=b; Path=/a", Cookie.builder()
                    .name("a")
                    .value("b")
                    .attribute(CookieAttribute.PATH, "/a")
                    .build().toString());
        }

        @Test
        public void attributeString() {
            assertEquals("a=b; c=d", Cookie.builder()
                    .name("a")
                    .value("b")
                    .attribute("c", "d")
                    .build().toString());
        }

        @Test
        public void domain() {
            assertEquals("a=b; Domain=example.com", Cookie.builder()
                    .name("a")
                    .value("b")
                    .domain("example.com")
                    .build().toString());
        }

        @Test
        public void httpOnly() {
            assertEquals("a=b; HttpOnly", Cookie.builder()
                    .name("a")
                    .value("b")
                    .httpOnly()
                    .build().toString());
        }

        @Test
        public void maxAgeDuration() {
            assertTrue(Cookie.builder()
                    .name("a")
                    .value("b")
                    .maxAge(Duration.ofSeconds(1))
                    .build().toString()
                    .contains("Max-Age=1"));
        }

        @Test
        public void maxAgeLong() {
            assertTrue(Cookie.builder()
                    .name("a")
                    .value("b")
                    .maxAge(1)
                    .build().toString()
                    .contains("Max-Age=1"));
        }

        @Test
        public void path() {
            assertEquals("a=b; Path=/", Cookie.builder()
                    .name("a")
                    .value("b")
                    .path("/")
                    .build().toString());
        }

        @Test
        public void sameSiteStrict() {
            assertEquals("a=b; SameSite=Strict", Cookie.builder()
                    .name("a")
                    .value("b")
                    .sameSite(CookieSameSite.STRICT)
                    .build().toString());
        }

        @Test
        public void sameSiteLax() {
            assertEquals("a=b; SameSite=Lax", Cookie.builder()
                    .name("a")
                    .value("b")
                    .sameSite(CookieSameSite.LAX)
                    .build().toString());
        }

        @Test
        public void sameSiteNone() {
            assertEquals("a=b; SameSite=None", Cookie.builder()
                    .name("a")
                    .value("b")
                    .sameSite(CookieSameSite.NONE)
                    .build().toString());
        }

        @Test
        public void secure() {
            assertEquals("a=b; Secure", Cookie.builder()
                    .name("a")
                    .value("b")
                    .secure()
                    .build().toString());
        }

        @Test
        public void partitioned() {
            assertEquals("a=b; Partitioned", Cookie.builder()
                    .name("a")
                    .value("b")
                    .partitioned()
                    .build().toString());
        }
    }

    @Test
    public void getName() {
        assertEquals("a", Cookie.builder()
                .name("a")
                .value("b")
                .build().getName());
    }

    @Test
    public void getPrefix() {
        assertEquals(CookiePrefix.HOST_HTTP, Cookie.builder()
                .name(CookiePrefix.HOST_HTTP, "a")
                .value("b")
                .build().getPrefix());
        assertEquals(CookiePrefix.HOST_HTTP, Cookie.builder()
                .name("__Host-Http-a")
                .value("b")
                .build().getPrefix());
        assertEquals(CookiePrefix.HOST, Cookie.builder()
                .name(CookiePrefix.HOST, "a")
                .value("b")
                .build().getPrefix());
        assertEquals(CookiePrefix.HOST, Cookie.builder()
                .name("__Host-a")
                .value("b")
                .build().getPrefix());
    }

    @Test
    public void getValue() {
        assertEquals("b", Cookie.builder()
                .name("a")
                .value("b")
                .build().getValue());
    }

    @Test
    public void getAttributes() {
        assertEquals(Map.of("c", "d"), Cookie.builder()
                .name("a")
                .value("b")
                .attributes(Map.of("c", "d"))
                .build().getAttributes());
    }

    @Test
    public void getAttributeCookieAttribute() {
        assertEquals("/a", Cookie.builder()
                .name("a")
                .value("b")
                .attribute(CookieAttribute.PATH, "/a")
                .build().getAttribute(CookieAttribute.PATH));
    }

    @Test
    public void getAttributeString() {
        assertEquals("d", Cookie.builder()
                .name("a")
                .value("b")
                .attribute("c", "d")
                .build().getAttribute("c"));
    }

    @Test
    public void getDomain() {
        assertEquals("example.com", Cookie.builder()
                .name("a")
                .value("b")
                .domain("example.com")
                .build().getDomain());
    }

    @Test
    public void getExpires() {
        assertEquals(ZonedDateTime.of(2026, 1, 1, 0, 0, 0, 0, UTC),
                Cookie.parseResponseCookie("a=b; Expires=Thu, 01 Jan 2026 00:00:00 GMT").getExpires());
        assertNull(Cookie.builder()
                .name("a")
                .value("b")
                .build().getExpires());
    }

    @Test
    public void isExpired() {
        assertFalse(Cookie.builder()
                .name("a")
                .value("b")
                .maxAge(1)
                .build().isExpired());
        assertTrue(Cookie.builder()
                .name("a")
                .value("b")
                .maxAge(0)
                .build().isExpired());
        assertTrue(Cookie.builder()
                .name("a")
                .value("b")
                .maxAge(-1)
                .build().isExpired());
        assertFalse(Cookie.builder()
                .name("a")
                .value("b")
                .build().isExpired());
        assertTrue(Cookie.parseResponseCookie("a=b; Expires=Thu, 01 Jan 2026 00:00:00 GMT").isExpired());
        assertFalse(Cookie.parseResponseCookie("a=b; Expires=" +
                ZonedDateTime.now(UTC).plusDays(1).format(RFC_1123_DATE_TIME)).isExpired());
    }

    @Test
    public void isHttpOnly() {
        assertTrue(Cookie.builder()
                .name("a")
                .value("b")
                .httpOnly()
                .build().isHttpOnly());
        assertTrue(Cookie.parseResponseCookie("a=b; HttpOnly;").isHttpOnly());
        assertTrue(Cookie.parseResponseCookie("a=b; HttpOnly=true;").isHttpOnly());
        assertFalse(Cookie.parseResponseCookie("a=b; HTTPONLY=false;").isHttpOnly());
        assertFalse(Cookie.parseResponseCookie("a=b; httponly=FALSE;").isHttpOnly());
        assertFalse(Cookie.builder()
                .name("a")
                .value("b")
                .build().isHttpOnly());
    }

    @Test
    public void getMaxAge() {
        assertEquals(1, Cookie.builder()
                .name("a")
                .value("b")
                .maxAge(1)
                .build().getMaxAge());
        assertEquals(0, Cookie.builder()
                .name("a")
                .value("b")
                .maxAge(0)
                .build().getMaxAge());
        assertEquals(0, Cookie.builder()
                .name("a")
                .value("b")
                .maxAge(-1)
                .build().getMaxAge());
    }

    @Test
    public void getPath() {
        assertEquals("/a", Cookie.builder()
                .name("a")
                .value("b")
                .path("/a")
                .build().getPath());
        assertEquals("/a", Cookie.parseResponseCookie("a=b; Path=/a").getPath());
    }

    @Test
    public void getSameSite() {
        assertEquals(CookieSameSite.STRICT, Cookie.builder()
                .name("a")
                .value("b")
                .sameSite(CookieSameSite.STRICT)
                .build().getSameSite());
        assertEquals(CookieSameSite.STRICT, Cookie.parseResponseCookie("a=b; SAMESITE=STRICT").getSameSite());
        assertNull(Cookie.builder()
                .name("a")
                .value("b")
                .build().getSameSite());
    }

    @Test
    public void isSecure() {
        assertTrue(Cookie.builder()
                .name("a")
                .value("b")
                .secure()
                .build().isSecure());
        assertTrue(Cookie.parseResponseCookie("a=b; Secure;").isSecure());
        assertTrue(Cookie.parseResponseCookie("a=b; Secure=true;").isSecure());
        assertFalse(Cookie.parseResponseCookie("a=b; SECURE=false;").isSecure());
        assertFalse(Cookie.parseResponseCookie("a=b; secure=FALSE;").isSecure());
        assertFalse(Cookie.builder()
                .name("a")
                .value("b")
                .build().isSecure());
    }

    @Test
    public void isPartitioned() {
        assertTrue(Cookie.builder()
                .name("a")
                .value("b")
                .partitioned()
                .build().isPartitioned());
        assertTrue(Cookie.parseResponseCookie("a=b; Partitioned;").isPartitioned());
        assertTrue(Cookie.parseResponseCookie("a=b; Partitioned=true;").isPartitioned());
        assertFalse(Cookie.parseResponseCookie("a=b; PARTITIONED=false;").isPartitioned());
        assertFalse(Cookie.parseResponseCookie("a=b; partitioned=FALSE;").isPartitioned());
        assertFalse(Cookie.builder()
                .name("a")
                .value("b")
                .build().isPartitioned());
    }

    @Test
    public void toJava() {
        assertEquals(HttpCookie.parse("a=b; Path=/a").getFirst(), Cookie.builder()
                .name("a")
                .value("b")
                .path("/a")
                .build().toJava());
    }

    @Test
    public void toBuilder() {
        final var cookie = Cookie.builder()
                .name("a")
                .value("b")
                .build();
        assertEquals(cookie, cookie.toBuilder().build());
    }

    @Test
    public void toRequestString() {
        assertEquals("a=b", Cookie.builder()
                .name("a")
                .value("b")
                .secure()
                .build().toRequestString());
    }

    @Test
    public void toResponseString() {
        assertEquals("a=b; Secure", Cookie.builder()
                .name("a")
                .value("b")
                .secure()
                .build().toResponseString());
    }

    @Test
    public void _toString() {
        assertEquals("a=b", Cookie.builder()
                .name("a")
                .value("b")
                .build().toString());
    }

    @Test
    public void _equals() {
        assertEquals(Cookie.builder()
                .name("a")
                .value("b")
                .build(), Cookie.builder()
                .name("a")
                .value("b")
                .build());
        assertEquals(Cookie.builder()
                .name("a")
                .value("b")
                .secure().build(), Cookie.builder()
                .name("a")
                .value("b")
                .secure()
                .build());
        assertNotEquals(Cookie.builder()
                .name("a")
                .value("b")
                .secure().build(), Cookie.builder()
                .name("a")
                .value("b")
                .build());
    }
}
