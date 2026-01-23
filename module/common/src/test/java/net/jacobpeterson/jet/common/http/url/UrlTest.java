package net.jacobpeterson.jet.common.http.url;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class UrlTest {

    @Test
    public void encode() {
        assertEquals("", Url.encode(""));
        assertEquals("a", Url.encode("a"));
        assertEquals("%2Fa%2F", Url.encode("/a/"));
        assertEquals("%F0%9F%91%8D", Url.encode("👍"));
        assertEquals("%2Fa%20.%2520%3F%23%E2%80%A2%2F%20a", Url.encode("/a .%20?#•/ a"));
    }

    @Test
    public void encodePath() {
        assertEquals("", Url.encodePath(""));
        assertEquals("a", Url.encodePath("a"));
        assertEquals("/a/", Url.encodePath("/a/"));
        assertEquals("%F0%9F%91%8D", Url.encodePath("👍"));
        assertEquals("/a%20.%2520%3F%23%E2%80%A2/%20a", Url.encodePath("/a .%20?#•/ a"));
    }

    @Test
    public void decode() {
        assertEquals("", Url.decode(""));
        assertEquals("a", Url.decode("a"));
        assertEquals("/a/", Url.decode("%2Fa%2F"));
        assertEquals("👍", Url.decode("%F0%9F%91%8D"));
        assertEquals("/a .%20?#•/ a", Url.decode("%2Fa%20.%2520%3F%23%E2%80%A2%2F%20a"));
    }

    @Test
    public void checkCharsValid() {
        assertDoesNotThrow(() -> Url.checkCharsValid(""));
        assertDoesNotThrow(() -> Url.checkCharsValid("a"));
        assertThrows(IllegalArgumentException.class, () -> Url.checkCharsValid("\u0000"));
        assertThrows(IllegalArgumentException.class, () -> Url.checkCharsValid("a•"));
        assertThrows(IllegalArgumentException.class, () -> Url.checkCharsValid("👍"));
    }

    @Test
    public void encodedPathTrimLeading() {
        assertEquals("", Url.encodedPathTrimLeading(""));
        assertEquals(" ", Url.encodedPathTrimLeading(" "));
        assertEquals("a", Url.encodedPathTrimLeading("a"));
        assertEquals("a", Url.encodedPathTrimLeading("/a"));
        assertEquals("a/", Url.encodedPathTrimLeading("a/"));
        assertEquals("a", Url.encodedPathTrimLeading("//a"));
        assertEquals("a//", Url.encodedPathTrimLeading("a//"));
        assertEquals("a/a", Url.encodedPathTrimLeading("a/a"));
        assertEquals("a//a", Url.encodedPathTrimLeading("a//a"));
        assertEquals("a/a/", Url.encodedPathTrimLeading("/a/a/"));
        assertEquals("a/a//", Url.encodedPathTrimLeading("//a/a//"));
        assertEquals("a/a///", Url.encodedPathTrimLeading("///a/a///"));
    }

    @Test
    public void encodedPathTrimTrailing() {
        assertEquals("", Url.encodedPathTrimTrailing(""));
        assertEquals(" ", Url.encodedPathTrimTrailing(" "));
        assertEquals("a", Url.encodedPathTrimTrailing("a"));
        assertEquals("/a", Url.encodedPathTrimTrailing("/a"));
        assertEquals("a", Url.encodedPathTrimTrailing("a/"));
        assertEquals("//a", Url.encodedPathTrimTrailing("//a"));
        assertEquals("a", Url.encodedPathTrimTrailing("a//"));
        assertEquals("a/a", Url.encodedPathTrimTrailing("a/a"));
        assertEquals("a//a", Url.encodedPathTrimTrailing("a//a"));
        assertEquals("/a/a", Url.encodedPathTrimTrailing("/a/a/"));
        assertEquals("//a/a", Url.encodedPathTrimTrailing("//a/a//"));
        assertEquals("///a/a", Url.encodedPathTrimTrailing("///a/a///"));
    }

    @Test
    public void encodedPathTrim() {
        assertEquals("", Url.encodedPathTrim(""));
        assertEquals(" ", Url.encodedPathTrim(" "));
        assertEquals("a", Url.encodedPathTrim("a"));
        assertEquals("a", Url.encodedPathTrim("/a"));
        assertEquals("a", Url.encodedPathTrim("a/"));
        assertEquals("a", Url.encodedPathTrim("//a"));
        assertEquals("a", Url.encodedPathTrim("a//"));
        assertEquals("a/a", Url.encodedPathTrim("a/a"));
        assertEquals("a//a", Url.encodedPathTrim("a//a"));
        assertEquals("a/a", Url.encodedPathTrim("/a/a/"));
        assertEquals("a/a", Url.encodedPathTrim("//a/a//"));
        assertEquals("a/a", Url.encodedPathTrim("///a/a///"));
    }

    @Test
    public void encodedPathSegmentsToStream() {
        assertEquals(List.of(), Url.encodedPathSegmentsToStream("").toList());
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToStream("a/b/c").toList());
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToStream("/a/b/c").toList());
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToStream("/a/b/c/").toList());
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToStream("/a/b/c//").toList());
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToStream("/a/b/c///").toList());
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToStream("//a/b/c//").toList());
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToStream("//a//b//c//").toList());
        assertEquals(List.of("a", "%E2%80%A2"), Url.encodedPathSegmentsToStream("/a/%E2%80%A2").toList());
        assertEquals(List.of("a", "%F0%9F%91%8D"), Url.encodedPathSegmentsToStream("/a/%F0%9F%91%8D").toList());
        assertEquals(List.of("a", "%2F%25"), Url.encodedPathSegmentsToStream("/a/%2F%25").toList());
    }

    @Test
    public void encodedPathSegmentsToList() {
        assertEquals(List.of(), Url.encodedPathSegmentsToList(""));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToList("a/b/c"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToList("/a/b/c"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToList("/a/b/c/"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToList("/a/b/c//"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToList("/a/b/c///"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToList("//a/b/c//"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToList("//a//b//c//"));
        assertEquals(List.of("a", "%E2%80%A2"), Url.encodedPathSegmentsToList("/a/%E2%80%A2"));
        assertEquals(List.of("a", "%F0%9F%91%8D"), Url.encodedPathSegmentsToList("/a/%F0%9F%91%8D"));
        assertEquals(List.of("a", "%2F%25"), Url.encodedPathSegmentsToList("/a/%2F%25"));
    }

    @Test
    public void encodedPathSegmentsToDecodedList() {
        assertEquals(List.of(), Url.encodedPathSegmentsToDecodedList(""));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToDecodedList("a/b/c"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToDecodedList("/a/b/c"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToDecodedList("/a/b/c/"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToDecodedList("/a/b/c//"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToDecodedList("/a/b/c///"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToDecodedList("//a/b/c//"));
        assertEquals(List.of("a", "b", "c"), Url.encodedPathSegmentsToDecodedList("//a//b//c//"));
        assertEquals(List.of("a", "•"), Url.encodedPathSegmentsToDecodedList("/a/%E2%80%A2"));
        assertEquals(List.of("a", "👍"), Url.encodedPathSegmentsToDecodedList("/a/%F0%9F%91%8D"));
        assertEquals(List.of("a", "/%"), Url.encodedPathSegmentsToDecodedList("/a/%2F%25"));
    }

    @Test
    public void normalizeEncodedPath() {
        assertEquals("", Url.normalizeEncodedPath(""));
        assertEquals("", Url.normalizeEncodedPath(".."));
        assertEquals("", Url.normalizeEncodedPath("../"));
        assertEquals("/", Url.normalizeEncodedPath("/"));
        assertEquals("/", Url.normalizeEncodedPath("//"));
        assertEquals("/", Url.normalizeEncodedPath("/../"));
        assertEquals("/", Url.normalizeEncodedPath("//../"));
        assertEquals("/", Url.normalizeEncodedPath("//..//"));
        assertEquals("/", Url.normalizeEncodedPath("/a/b/c/../../../"));
        assertEquals("/", Url.normalizeEncodedPath("/a/b/c/../../../../"));
        assertEquals("", Url.normalizeEncodedPath("a/b/c/../../../"));
        assertEquals("", Url.normalizeEncodedPath("a/b/c/../../../../"));
        assertEquals("a/b/c", Url.normalizeEncodedPath("a/b/c"));
        assertEquals("/a/b/c", Url.normalizeEncodedPath("/a/b/c"));
        assertEquals("/a/b/c", Url.normalizeEncodedPath("/a/b/c/"));
        assertEquals("/a/b/c", Url.normalizeEncodedPath("/a/b/c//"));
        assertEquals("/a/b/c", Url.normalizeEncodedPath("/a/b/c///"));
        assertEquals("/a/b/c", Url.normalizeEncodedPath("//a/b/c//"));
        assertEquals("/a/b/c", Url.normalizeEncodedPath("//a//b//c//"));
        assertEquals("a/b/c..", Url.normalizeEncodedPath("a/b/c../"));
        assertEquals("/a/b/c..", Url.normalizeEncodedPath("/a/b/c../"));
        assertEquals("/a/b", Url.normalizeEncodedPath("/a/b/c/../"));
        assertEquals("/a/b", Url.normalizeEncodedPath("/a/b/c//../"));
        assertEquals("/a/b", Url.normalizeEncodedPath("/a/b/c///../"));
        assertEquals("/a/b", Url.normalizeEncodedPath("//a/b/c//../"));
        assertEquals("/a/b", Url.normalizeEncodedPath("//a//b//c//../"));
    }

    @Test
    public void parseEncodedQueryParameters() {
        assertEquals(ImmutableListMultimap.of(), Url.parseEncodedQueryParameters(""));
        assertEquals(ImmutableListMultimap.of(), Url.parseEncodedQueryParameters("="));
        assertEquals(ImmutableListMultimap.of(), Url.parseEncodedQueryParameters("=a"));
        assertEquals(ImmutableListMultimap.of("a", ""), Url.parseEncodedQueryParameters("a"));
        assertEquals(ImmutableListMultimap.of("a", ""), Url.parseEncodedQueryParameters("a="));
        assertEquals(ImmutableListMultimap.of("a", "a"), Url.parseEncodedQueryParameters("a=a"));
        assertEquals(ImmutableListMultimap.of("a", ""), Url.parseEncodedQueryParameters("a=&="));
        assertEquals(ImmutableListMultimap.of(), Url.parseEncodedQueryParameters("&"));
        assertEquals(ImmutableListMultimap.of(), Url.parseEncodedQueryParameters("&&"));
        assertEquals(ImmutableListMultimap.of(), Url.parseEncodedQueryParameters("&=&"));
        assertEquals(ImmutableListMultimap.of(), Url.parseEncodedQueryParameters("=&=&="));
        assertEquals(ImmutableListMultimap.of("a", "a", "b", "b"), Url.parseEncodedQueryParameters("a=a&b=b"));
        assertEquals(ImmutableListMultimap.of("a", "a", "a", "b"), Url.parseEncodedQueryParameters("a=a&a=b"));
        assertEquals(ImmutableListMultimap.of("a", "%3D"), Url.parseEncodedQueryParameters("a=%3D"));
    }

    @Test
    public void decodeParsedEncodedQueryParameters() {
        assertEquals(ImmutableListMultimap.of("=", "="),
                Url.decodeParsedEncodedQueryParameters(ImmutableListMultimap.of("%3D", "%3D")));
    }

    @Test
    public void parseDecodeEncodedQueryParameters() {
        assertEquals(ImmutableListMultimap.of(), Url.parseDecodeEncodedQueryParameters(""));
        assertEquals(ImmutableListMultimap.of(), Url.parseDecodeEncodedQueryParameters("="));
        assertEquals(ImmutableListMultimap.of(), Url.parseDecodeEncodedQueryParameters("=a"));
        assertEquals(ImmutableListMultimap.of("a", ""), Url.parseDecodeEncodedQueryParameters("a"));
        assertEquals(ImmutableListMultimap.of("a", ""), Url.parseDecodeEncodedQueryParameters("a="));
        assertEquals(ImmutableListMultimap.of("a", "a"), Url.parseDecodeEncodedQueryParameters("a=a"));
        assertEquals(ImmutableListMultimap.of("a", ""), Url.parseDecodeEncodedQueryParameters("a=&="));
        assertEquals(ImmutableListMultimap.of(), Url.parseDecodeEncodedQueryParameters("&"));
        assertEquals(ImmutableListMultimap.of(), Url.parseDecodeEncodedQueryParameters("&&"));
        assertEquals(ImmutableListMultimap.of(), Url.parseDecodeEncodedQueryParameters("&=&"));
        assertEquals(ImmutableListMultimap.of(), Url.parseDecodeEncodedQueryParameters("=&=&="));
        assertEquals(ImmutableListMultimap.of("a", "a", "b", "b"), Url.parseDecodeEncodedQueryParameters("a=a&b=b"));
        assertEquals(ImmutableListMultimap.of("a", "a", "a", "b"), Url.parseDecodeEncodedQueryParameters("a=a&a=b"));
        assertEquals(ImmutableListMultimap.of("a", "="), Url.parseDecodeEncodedQueryParameters("a=%3D"));
    }

    @Test
    public void concatComponents() {
        assertEquals("https://a.com", Url.concatComponents("https", null, "a.com", null, "/", null, null));
        assertEquals("https://a@a.com", Url.concatComponents("https", "a", "a.com", null, "", null, null));
        assertEquals("http://127.0.0.1", Url.concatComponents("http", null, "127.0.0.1", null, "", null, null));
        assertEquals("http://127.0.0.1:8080", Url.concatComponents("http", null, "127.0.0.1", 8080, "", null, null));
        assertEquals("http://[::1]", Url.concatComponents("http", null, "[::1]", null, "", null, null));
        assertEquals("http://[::1]:8080", Url.concatComponents("http", null, "[::1]", 8080, "", null, null));
        assertEquals("https://a.com/a", Url.concatComponents("https", null, "a.com", null, "/a", null, null));
        assertEquals("https://a.com/a/b", Url.concatComponents("https", null, "a.com", null, "/a/b", null, null));
        assertEquals("https://a.com/a/b/", Url.concatComponents("https", null, "a.com", null, "/a/b/", null, null));
        assertEquals("https://a.com/a/b/", Url.concatComponents("https", null, "a.com", null, "a/b/", null, null));
        assertEquals("https://a.com/?a=b", Url.concatComponents("https", null, "a.com", null, "/", "a=b", null));
        assertEquals("https://a.com/?a=b", Url.concatComponents("https", null, "a.com", null, "", "a=b", null));
        assertEquals("https://a.com/#a", Url.concatComponents("https", null, "a.com", null, "/", null, "a"));
        assertEquals("https://a.com/#a", Url.concatComponents("https", null, "a.com", null, "", null, "a"));
        assertEquals("https://a.com/?a=b#a", Url.concatComponents("https", null, "a.com", null, "/", "a=b", "a"));
        assertEquals("https://a.com/?a=b#a", Url.concatComponents("https", null, "a.com", null, "", "a=b", "a"));
        assertEquals("https://a.com/a?a=b#a", Url.concatComponents("https", null, "a.com", null, "/a", "a=b", "a"));
        assertEquals("https://a.com/a/b?a=b#a", Url.concatComponents("https", null, "a.com", null, "/a/b", "a=b", "a"));
        assertEquals("https://a@a.com/a/b?a=b#a",
                Url.concatComponents("https", "a", "a.com", null, "/a/b", "a=b", "a"));
        assertEquals("https://a@a.com:8080/a/b?a=b#a",
                Url.concatComponents("https", "a", "a.com", 8080, "/a/b", "a=b", "a"));
    }

    @Test
    public void parse() {
        assertDoesNotThrow(() -> Url.parse("https://a.com"));
        assertDoesNotThrow(() -> Url.parse("https://a@a.com"));
        assertDoesNotThrow(() -> Url.parse("http://127.0.0.1"));
        assertDoesNotThrow(() -> Url.parse("http://127.0.0.1:8080"));
        assertDoesNotThrow(() -> Url.parse("http://[::1]"));
        assertDoesNotThrow(() -> Url.parse("http://[::1]:8080"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/a"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/a/b"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/a/b/"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/a/b/"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/?a=b"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/?a=b"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/#a"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/#a"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/?a=b#a"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/?a=b#a"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/a?a=b#a"));
        assertDoesNotThrow(() -> Url.parse("https://a.com/a/b?a=b#a"));
        assertDoesNotThrow(() -> Url.parse("https://a@a.com/a/b?a=b#a"));
        assertDoesNotThrow(() -> Url.parse("https://a@a.com:8080/a/b?a=b#a"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse(""));
        assertThrows(IllegalArgumentException.class, () -> Url.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("//"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("://"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse(":"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("a"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("?"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("#"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("/"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("•"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("👍"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://a.com/a/•"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://a.com/👍"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://?/"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("?://"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse(":///"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse(":// "));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("://a"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("a:// "));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://a@@"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://a@?"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://@"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://?@"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https:///@a.com"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://?@a.com"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://#@a.com"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https:///a.com"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://?a.com"));
        assertThrows(IllegalArgumentException.class, () -> Url.parse("https://#a.com"));
    }

    @Test
    public void fromJava() {
        assertDoesNotThrow(() -> Url.fromJava(URI.create("a://a")));
        assertDoesNotThrow(() -> Url.fromJava(URI.create("https://a.com")));
        assertThrows(IllegalArgumentException.class, () -> Url.fromJava(URI.create("")));
        assertThrows(IllegalArgumentException.class, () -> Url.fromJava(URI.create("://")));
        assertThrows(IllegalArgumentException.class, () -> Url.fromJava(URI.create("https://")));
        assertThrows(IllegalArgumentException.class, () -> Url.fromJava(URI.create("://a.com")));
    }

    public static class BuilderTest {

        @Test
        public void scheme() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme(" ")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme("a")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme("")
                    .host("a.com")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme(" ")
                    .host("a.com")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme("•")
                    .host("a.com")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .build().toString());
        }

        @Test
        public void schemeEnum() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme(Scheme.HTTPS)
                    .build().toString());
            assertEquals("https://a.com", Url.builder()
                    .scheme(Scheme.HTTPS)
                    .host("a.com")
                    .build().toString());
        }

        @Test
        public void encodedUserInfo() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .encodedUserInfo("a")
                    .build().toString());
            assertEquals("a://%20@a.com", Url.builder()
                    .scheme("a")
                    .encodedUserInfo("%20")
                    .host("a.com")
                    .build().toString());
        }

        @Test
        public void userInfo() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .userInfo("a")
                    .build().toString());
            assertEquals("a://%20@a.com", Url.builder()
                    .scheme("a")
                    .userInfo(" ")
                    .host("a.com")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .userInfo(" ")
                    .userInfo(null)
                    .host("a.com")
                    .build().toString());
        }

        @Test
        public void host() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .host("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .host(" ")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .host("a")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme(Scheme.HTTP)
                    .host("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme(Scheme.HTTP)
                    .host(" ")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .scheme(Scheme.HTTP)
                    .host("a•")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .build().toString());
        }

        @Test
        public void port() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .port(8080)
                    .build().toString());
            assertEquals("a://a.com:8080", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .port(8080)
                    .build().toString());
        }

        @Test
        public void encodedPath() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .encodedPath("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .encodedPath("/")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedPath("")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedPath("/")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedPath("/a")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedPath("/a/")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedPath("/a///")
                    .build().toString());
            assertEquals("a://a.com/a/b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedPath("/a/b")
                    .build().toString());
            assertEquals("a://a.com/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedPath("%20")
                    .build().toString());
        }

        @Test
        public void path() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .path("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .path("/")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .path("")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .path("/")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .path("/a")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .path("/a/")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .path("/a///")
                    .build().toString());
            assertEquals("a://a.com/a/b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .path("/a/b")
                    .build().toString());
            assertEquals("a://a.com/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .path(" ")
                    .build().toString());
        }

        @Test
        public void addEncodedPathSegment() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedPathSegment("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedPathSegment("/")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegment("")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegment("a")
                    .build().toString());
            assertEquals("a://a.com/a/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegment("a")
                    .addEncodedPathSegment("a")
                    .build().toString());
            assertEquals("a://a.com/a%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegment("a%20")
                    .build().toString());
            assertEquals("a://a.com/%20/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegment("%20")
                    .addEncodedPathSegment("%20")
                    .build().toString());
        }

        @Test
        public void addPathSegment() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addPathSegment("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addPathSegment("/")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegment("")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegment("a")
                    .build().toString());
            assertEquals("a://a.com/a/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegment("a")
                    .addPathSegment("a")
                    .build().toString());
            assertEquals("a://a.com/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegment(" ")
                    .build().toString());
            assertEquals("a://a.com/%20/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegment(" ")
                    .addPathSegment(" ")
                    .build().toString());
        }

        @Test
        public void addEncodedPathSegments() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedPathSegments("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedPathSegments("/")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegments("")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegments("a")
                    .build().toString());
            assertEquals("a://a.com/a/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegments("a/a/")
                    .build().toString());
            assertEquals("a://a.com/a/a/a/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegments("a/a/")
                    .addEncodedPathSegments("a/a/")
                    .build().toString());
            assertEquals("a://a.com/a/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegments("a/%20")
                    .build().toString());
            assertEquals("a://a.com/a/%20/a/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedPathSegments("a/%20")
                    .addEncodedPathSegments("a/%20")
                    .build().toString());
        }

        @Test
        public void addPathSegments() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addPathSegments("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addPathSegments("/")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegments("")
                    .build().toString());
            assertEquals("a://a.com/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegments("a")
                    .build().toString());
            assertEquals("a://a.com/a/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegments("a/a/")
                    .build().toString());
            assertEquals("a://a.com/a/a/a/a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegments("a/a/")
                    .addPathSegments("a/a/")
                    .build().toString());
            assertEquals("a://a.com/a/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegments("a/ ")
                    .build().toString());
            assertEquals("a://a.com/a/%20/a/%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addPathSegments("a/ ")
                    .addPathSegments("a/ ")
                    .build().toString());
        }

        @Test
        public void encodedQuery() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .encodedQuery("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .encodedQuery("%20")
                    .build().toString());
            assertEquals("a://a.com/?", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedQuery("")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedQuery("")
                    .encodedQuery(null)
                    .build().toString());
            assertEquals("a://a.com/?a=a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedQuery("a=a")
                    .build().toString());
            assertEquals("a://a.com/?a=%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedQuery("a=%20")
                    .build().toString());
            assertEquals("a://a.com/?a=a&b=b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedQuery("a=a&b=b")
                    .build().toString());
            assertEquals("a://a.com/?a=%20&b=%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedQuery("a=%20&b=%F0%9F%91%8D")
                    .build().toString());
        }

        @Test
        public void query() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .query("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .query(" ")
                    .build().toString());
            assertEquals("a://a.com/?", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .query("")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .query("")
                    .query(null)
                    .build().toString());
            assertEquals("a://a.com/?a%3Da", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .query("a=a")
                    .build().toString());
            assertEquals("a://a.com/?a%3D%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .query("a= ")
                    .build().toString());
            assertEquals("a://a.com/?a%3Da%26b%3Db", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .query("a=a&b=b")
                    .build().toString());
            assertEquals("a://a.com/?a%3D%20%26b%3D%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .query("a= &b=👍")
                    .build().toString());
        }

        @Test
        public void addEncodedQueryParameter() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedQueryParameter("", "")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedQueryParameter("", "")
                    .build().toString());
            assertEquals("a://a.com/?=", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameter("", "")
                    .build().toString());
            assertEquals("a://a.com/?a=a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameter("a", "a")
                    .build().toString());
            assertEquals("a://a.com/?a=%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameter("a", "%20")
                    .build().toString());
            assertEquals("a://a.com/?a=a&b=b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameter("a", "a")
                    .addEncodedQueryParameter("b", "b")
                    .build().toString());
            assertEquals("a://a.com/?a=%20&b=%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameter("a", "%20")
                    .addEncodedQueryParameter("b", "%F0%9F%91%8D")
                    .build().toString());
        }

        @Test
        public void addQueryParameter() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addQueryParameter("", "")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addQueryParameter(" ", " ")
                    .build().toString());
            assertEquals("a://a.com/?=", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameter("", "")
                    .build().toString());
            assertEquals("a://a.com/?a=a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameter("a", "a")
                    .build().toString());
            assertEquals("a://a.com/?a=%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameter("a", " ")
                    .build().toString());
            assertEquals("a://a.com/?a=a&b=b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameter("a", "a")
                    .addQueryParameter("b", "b")
                    .build().toString());
            assertEquals("a://a.com/?a=%20&b=%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameter("a", " ")
                    .addQueryParameter("b", "👍")
                    .build().toString());
        }

        @Test
        public void addEncodedQueryParametersMap() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedQueryParameters(Map.of())
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedQueryParameters(Map.of(" ", " "))
                    .build().toString());
            assertEquals("a://a.com/?=", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(Map.of("", ""))
                    .build().toString());
            assertEquals("a://a.com/?a=a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(Map.of("a", "a"))
                    .build().toString());
            assertEquals("a://a.com/?a=%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(Map.of("a", "%20"))
                    .build().toString());

            final var aabb = new LinkedHashMap<String, String>();
            aabb.put("a", "a");
            aabb.put("b", "b");
            assertEquals("a://a.com/?a=a&b=b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(aabb)
                    .build().toString());

            final var aSpaceBThumbsUp = new LinkedHashMap<String, String>();
            aSpaceBThumbsUp.put("a", "%20");
            aSpaceBThumbsUp.put("b", "%F0%9F%91%8D");
            assertEquals("a://a.com/?a=%20&b=%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(aSpaceBThumbsUp)
                    .build().toString());
        }

        @Test
        public void addEncodedQueryParametersMultimap() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedQueryParameters(ImmutableListMultimap.of())
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addEncodedQueryParameters(ImmutableListMultimap.of(" ", " "))
                    .build().toString());
            assertEquals("a://a.com/?=", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(ImmutableListMultimap.of("", ""))
                    .build().toString());
            assertEquals("a://a.com/?a=a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(ImmutableListMultimap.of("a", "a"))
                    .build().toString());
            assertEquals("a://a.com/?a=%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(ImmutableListMultimap.of("a", "%20"))
                    .build().toString());
            assertEquals("a://a.com/?a=a&b=b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(ImmutableListMultimap.of("a", "a", "b", "b"))
                    .build().toString());
            assertEquals("a://a.com/?a=%20&b=%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addEncodedQueryParameters(ImmutableListMultimap.of("a", "%20", "b", "%F0%9F%91%8D"))
                    .build().toString());
        }

        @Test
        public void addQueryParametersMap() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addQueryParameters(Map.of())
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addQueryParameters(Map.of(" ", " "))
                    .build().toString());
            assertEquals("a://a.com/?=", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(Map.of("", ""))
                    .build().toString());
            assertEquals("a://a.com/?a=a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(Map.of("a", "a"))
                    .build().toString());
            assertEquals("a://a.com/?a=%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(Map.of("a", " "))
                    .build().toString());

            final var aabb = new LinkedHashMap<String, String>();
            aabb.put("a", "a");
            aabb.put("b", "b");
            assertEquals("a://a.com/?a=a&b=b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(aabb)
                    .build().toString());

            final var aSpaceBThumbsUp = new LinkedHashMap<String, String>();
            aSpaceBThumbsUp.put("a", " ");
            aSpaceBThumbsUp.put("b", "👍");
            assertEquals("a://a.com/?a=%20&b=%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(aSpaceBThumbsUp)
                    .build().toString());
        }

        @Test
        public void addQueryParametersMultimap() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addQueryParameters(ImmutableListMultimap.of())
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .addQueryParameters(ImmutableListMultimap.of(" ", " "))
                    .build().toString());
            assertEquals("a://a.com/?=", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(ImmutableListMultimap.of("", ""))
                    .build().toString());
            assertEquals("a://a.com/?a=a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(ImmutableListMultimap.of("a", "a"))
                    .build().toString());
            assertEquals("a://a.com/?a=%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(ImmutableListMultimap.of("a", " "))
                    .build().toString());
            assertEquals("a://a.com/?a=a&b=b", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(ImmutableListMultimap.of("a", "a", "b", "b"))
                    .build().toString());
            assertEquals("a://a.com/?a=%20&b=%F0%9F%91%8D", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .addQueryParameters(ImmutableListMultimap.of("a", " ", "b", "👍"))
                    .build().toString());
        }

        @Test
        public void encodedFragment() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .encodedFragment("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .encodedFragment("%20")
                    .build().toString());
            assertEquals("a://a.com/#", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedFragment("")
                    .build().toString());
            assertEquals("a://a.com/#a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedFragment("a")
                    .build().toString());
            assertEquals("a://a.com/#%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .encodedFragment("%20")
                    .build().toString());
        }

        @Test
        public void fragment() {
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .fragment("")
                    .build().toString());
            assertThrows(IllegalArgumentException.class, () -> Url.builder()
                    .fragment(" ")
                    .build().toString());
            assertEquals("a://a.com/#", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .fragment("")
                    .build().toString());
            assertEquals("a://a.com", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .fragment("")
                    .fragment(null)
                    .build().toString());
            assertEquals("a://a.com/#a", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .fragment("a")
                    .build().toString());
            assertEquals("a://a.com/#%20", Url.builder()
                    .scheme("a")
                    .host("a.com")
                    .fragment(" ")
                    .build().toString());
        }
    }

    @Test
    public void getScheme() {
        assertEquals("a", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getScheme());
    }

    @Test
    public void getSchemeEnum() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getSchemeEnum());
        assertEquals(Scheme.HTTPS, Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build().getSchemeEnum());
    }

    @Test
    public void getEncodedUserInfo() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getEncodedUserInfo());
        assertEquals("a%20", Url.builder()
                .scheme("a")
                .encodedUserInfo("a%20")
                .host("a.com")
                .build().getEncodedUserInfo());
    }

    @Test
    public void getUserInfo() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getUserInfo());
        assertEquals("a ", Url.builder()
                .scheme("a")
                .userInfo("a ")
                .host("a.com")
                .build().getUserInfo());
    }

    @Test
    public void getHost() {
        assertEquals("a.com", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getHost());
    }

    @Test
    public void getHostAsInetAddress() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getHostAsInetAddress());
        assertEquals(InetAddresses.forString("127.0.0.1"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("127.0.0.1")
                .build().getHostAsInetAddress());
        assertEquals(InetAddresses.forString("::1"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("[::1]")
                .build().getHostAsInetAddress());
    }

    @Test
    public void getHostAsDomainName() {
        assertNull(Url.builder()
                .scheme("a")
                .host("127.0.0.1")
                .build().getHostAsDomainName());
        assertNull(Url.builder()
                .scheme("a")
                .host("[::1]")
                .build().getHostAsDomainName());
        assertEquals(InternetDomainName.from("a.com"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build().getHostAsDomainName());
    }

    @Test
    public void getPort() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getPort());
        assertEquals(8080, Url.builder()
                .scheme("a")
                .host("a.com")
                .port(8080)
                .build().getPort());
    }

    @Test
    public void getPortOrDefault() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getPortOrDefault());
        assertEquals(8080, Url.builder()
                .scheme("a")
                .host("a.com")
                .port(8080)
                .build().getPortOrDefault());
        assertEquals(Scheme.HTTPS.getDefaultPort(), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build().getPortOrDefault());
    }

    @Test
    public void getCustomPort() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getCustomPort());
        assertEquals(8080, Url.builder()
                .scheme("a")
                .host("a.com")
                .port(8080)
                .build().getCustomPort());
        assertEquals(8080, Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .port(8080)
                .build().getCustomPort());
        assertEquals(Scheme.HTTPS.getDefaultPort(), Url.builder()
                .scheme("a")
                .host("a.com")
                .port(Scheme.HTTPS.getDefaultPort())
                .build().getCustomPort());
        assertNull(Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .port(Scheme.HTTPS.getDefaultPort())
                .build().getCustomPort());
    }

    @Test
    public void getEncodedAuthority() {
        assertEquals("a.com", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getEncodedAuthority());
        assertEquals("a@a.com", Url.builder()
                .scheme("a")
                .userInfo("a")
                .host("a.com")
                .build().getEncodedAuthority());
        assertEquals("a.com:8080", Url.builder()
                .scheme("a")
                .host("a.com")
                .port(8080)
                .build().getEncodedAuthority());
        assertEquals("a@a.com:8080", Url.builder()
                .scheme("a")
                .userInfo("a")
                .host("a.com")
                .port(8080)
                .build().getEncodedAuthority());
        assertEquals("%20@a.com:8080", Url.builder()
                .scheme("a")
                .userInfo(" ")
                .host("a.com")
                .port(8080)
                .build().getEncodedAuthority());
    }

    @Test
    public void getAuthority() {
        assertEquals("a.com", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getAuthority());
        assertEquals("a@a.com", Url.builder()
                .scheme("a")
                .userInfo("a")
                .host("a.com")
                .build().getAuthority());
        assertEquals("a.com:8080", Url.builder()
                .scheme("a")
                .host("a.com")
                .port(8080)
                .build().getAuthority());
        assertEquals("a@a.com:8080", Url.builder()
                .scheme("a")
                .userInfo("a")
                .host("a.com")
                .port(8080)
                .build().getAuthority());
        assertEquals(" @a.com:8080", Url.builder()
                .scheme("a")
                .userInfo(" ")
                .host("a.com")
                .port(8080)
                .build().getAuthority());
    }

    @Test
    public void getEncodedPath() {
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getEncodedPath());
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("")
                .build().getEncodedPath());
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/")
                .build().getEncodedPath());
        assertEquals("/a", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/a")
                .build().getEncodedPath());
        assertEquals("/%20", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/%20")
                .build().getEncodedPath());
    }

    @Test
    public void getPath() {
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getPath());
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("")
                .build().getPath());
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/")
                .build().getPath());
        assertEquals("/a", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/a")
                .build().getPath());
        assertEquals("/ ", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/%20")
                .build().getPath());
    }

    @Test
    public void getEncodedPathSegments() {
        assertEquals(List.of(), Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getEncodedPathSegments());
        assertEquals(List.of("a", "b"), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b")
                .build().getEncodedPathSegments());
        assertEquals(List.of("a", "b", "%20"), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b")
                .addPathSegment(" ")
                .build().getEncodedPathSegments());
    }

    @Test
    public void getPathSegments() {
        assertEquals(List.of(), Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getPathSegments());
        assertEquals(List.of("a", "b"), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b")
                .build().getPathSegments());
        assertEquals(List.of("a", "b", " "), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b")
                .addPathSegment(" ")
                .build().getPathSegments());
    }

    @Test
    public void getEncodedNormalizedPath() {
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getEncodedNormalizedPath());
        assertEquals("/a/b", Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b/")
                .build().getEncodedNormalizedPath());
        assertEquals("/a", Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b/..")
                .build().getEncodedNormalizedPath());
        assertEquals("/a/%20", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/a/b///../%20/")
                .build().getEncodedNormalizedPath());
    }

    @Test
    public void getNormalizedPath() {
        assertEquals("/", Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getNormalizedPath());
        assertEquals("/a/b", Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b/")
                .build().getNormalizedPath());
        assertEquals("/a", Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b/..")
                .build().getNormalizedPath());
        assertEquals("/a/ ", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/a/b///../%20/")
                .build().getNormalizedPath());
    }

    @Test
    public void getEncodedNormalizedPathSegments() {
        assertEquals(List.of(), Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getEncodedNormalizedPathSegments());
        assertEquals(List.of("a", "b"), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b")
                .build().getEncodedNormalizedPathSegments());
        assertEquals(List.of("a"), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b/..")
                .build().getEncodedNormalizedPathSegments());
        assertEquals(List.of("a", "%20"), Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/a/b///../%20/")
                .build().getEncodedNormalizedPathSegments());
    }

    @Test
    public void getNormalizedPathSegments() {
        assertEquals(List.of(), Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getNormalizedPathSegments());
        assertEquals(List.of("a", "b"), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b")
                .build().getNormalizedPathSegments());
        assertEquals(List.of("a"), Url.builder()
                .scheme("a")
                .host("a.com")
                .path("/a/b/..")
                .build().getNormalizedPathSegments());
        assertEquals(List.of("a", " "), Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedPath("/a/b///../%20/")
                .build().getNormalizedPathSegments());
    }

    @Test
    public void getEncodedQuery() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getEncodedQuery());
        assertEquals("", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedQuery("")
                .build().getEncodedQuery());
        assertEquals("a=a", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedQuery("a=a")
                .build().getEncodedQuery());
        assertEquals("a=%20", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedQuery("a=%20")
                .build().getEncodedQuery());
    }

    @Test
    public void getQuery() {
        assertNull(Url.builder()
                .scheme("a")
                .host("a.com")
                .build().getQuery());
        assertEquals("", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedQuery("")
                .build().getQuery());
        assertEquals("a=a", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedQuery("a=a")
                .build().getQuery());
        assertEquals("a= ", Url.builder()
                .scheme("a")
                .host("a.com")
                .encodedQuery("a=%20")
                .build().getQuery());
    }

    @Test
    public void getEncodedQueryParameters() {
        assertEquals(ImmutableListMultimap.of("a", "a", "b", "b", "b", "%20"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addEncodedQueryParameter("a", "a")
                .addEncodedQueryParameter("b", "b")
                .addEncodedQueryParameter("b", "%20")
                .build().getEncodedQueryParameters());
        assertEquals(ImmutableListMultimap.of("1", "%E2%80%A2", "2", "%F0%9F%91%8D"),
                Url.parse("https://a.com/?1=%E2%80%A2&2=%F0%9F%91%8D").getEncodedQueryParameters());
    }

    @Test
    public void getQueryParameters() {
        assertEquals(ImmutableListMultimap.of("a", "a", "b", "b", "b", " "), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .addQueryParameter("b", "b")
                .addQueryParameter("b", " ")
                .build().getQueryParameters());
        assertEquals(ImmutableListMultimap.of("1", "•", "2", "👍"),
                Url.parse("https://a.com/?1=%E2%80%A2&2=%F0%9F%91%8D").getQueryParameters());
    }

    @Test
    public void getEncodedQueryKeys() {
        assertEquals(ImmutableMultiset.of("a", "b", "b"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .addQueryParameter("b", "b")
                .addQueryParameter("b", " ")
                .build().getEncodedQueryKeys());
    }

    @Test
    public void getQueryKeys() {
        assertEquals(ImmutableMultiset.of(" ", " "), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter(" ", "a")
                .addQueryParameter(" ", "b")
                .build().getQueryKeys());
    }

    @Test
    public void getEncodedQueryValues() {
        assertEquals(List.of("a"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .build().getEncodedQueryValues("a"));
        assertEquals(List.of("%20"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", " ")
                .build().getEncodedQueryValues("a"));
        assertEquals(List.of("a", "b"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .addQueryParameter("a", "b")
                .build().getEncodedQueryValues("a"));
        assertEquals(List.of("a", "%20"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .addQueryParameter("a", " ")
                .build().getEncodedQueryValues("a"));
        assertTrue(Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build().getEncodedQueryValues("a").isEmpty());
    }

    @Test
    public void getQueryValues() {
        assertEquals(List.of("a"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .build().getQueryValues("a"));
        assertEquals(List.of(" "), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", " ")
                .build().getQueryValues("a"));
        assertEquals(List.of("a", "b"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .addQueryParameter("a", "b")
                .build().getQueryValues("a"));
        assertEquals(List.of("a", " "), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .addQueryParameter("a", " ")
                .build().getQueryValues("a"));
        assertTrue(Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build().getQueryValues("a").isEmpty());
    }

    @Test
    public void getEncodedQueryValue() {
        assertEquals("a", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .build().getEncodedQueryValue("a"));
        assertEquals("%20", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", " ")
                .build().getEncodedQueryValue("a"));
        assertNull(Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build().getEncodedQueryValue("a"));
    }

    @Test
    public void getQueryValue() {
        assertEquals("a", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "a")
                .build().getQueryValue("a"));
        assertEquals(" ", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", " ")
                .build().getQueryValue("a"));
        assertNull(Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build().getQueryValue("a"));
    }

    @Test
    public void getEncodedFragment() {
        assertEquals("a", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .fragment("a")
                .build().getEncodedFragment());
        assertEquals("a%20b", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .fragment("a b")
                .build().getEncodedFragment());
    }

    @Test
    public void getFragment() {
        assertEquals("a", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .fragment("a")
                .build().getFragment());
        assertEquals("a b", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .fragment("a b")
                .build().getFragment());
    }

    @Test
    public void getEncodedPathQuery() {
        assertEquals("/a/%20", Url.parse("https://a.com/a/%20").getEncodedPathQuery());
        assertEquals("/a/%20?a=a", Url.parse("https://a.com/a/%20?a=a").getEncodedPathQuery());
        assertEquals("/a/%20?a=a", Url.parse("https://a.com/a/%20?a=a#a%20").getEncodedPathQuery());
    }

    @Test
    public void getPathQuery() {
        assertEquals("/a/ ", Url.parse("https://a.com/a/%20").getPathQuery());
        assertEquals("/a/ ?a=a", Url.parse("https://a.com/a/%20?a=a").getPathQuery());
        assertEquals("/a/ ?a=a", Url.parse("https://a.com/a/%20?a=a#a%20").getPathQuery());
    }

    @Test
    public void getEncodedPathQueryFragment() {
        assertEquals("/a/%20", Url.parse("https://a.com/a/%20").getEncodedPathQueryFragment());
        assertEquals("/a/%20?a=a", Url.parse("https://a.com/a/%20?a=a").getEncodedPathQueryFragment());
        assertEquals("/a/%20?a=a#a%20", Url.parse("https://a.com/a/%20?a=a#a%20").getEncodedPathQueryFragment());
    }

    @Test
    public void getPathQueryFragment() {
        assertEquals("/a/ ", Url.parse("https://a.com/a/%20").getPathQueryFragment());
        assertEquals("/a/ ?a=a", Url.parse("https://a.com/a/%20?a=a").getPathQueryFragment());
        assertEquals("/a/ ?a=a#a ", Url.parse("https://a.com/a/%20?a=a#a%20").getPathQueryFragment());
    }

    @Test
    public void toJava() {
        assertEquals(URI.create("https://a.com"), Url.parse("https://a.com").toJava());
    }

    @Test
    public void toBuilder() {
        final var url = Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .build();
        assertEquals(url, url.toBuilder().build());

        assertEquals(Url.parse("https://a.com/a/b?a=a&b=b#b"), Url.parse("https://a.com/a/?b=b").toBuilder()
                .addPathSegment("b")
                .addQueryParameter("a", "a")
                .fragment("b")
                .build());
    }

    @Test
    public void toEncodedString() {
        assertEquals("https://a.com", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .path("")
                .build().toString());
        assertEquals("https://a.com", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .path("/")
                .build().toString());
        assertEquals("https://a.com:789/a/b/%20/c/%F0%9F%91%8D", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .port(789)
                .path("/a/b/ /c/👍/")
                .build().toString());
        assertEquals("https://a.com/?", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .query("")
                .build().toString());
        assertEquals("https://a.com/?%20", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .query(" ")
                .build().toString());
        assertEquals("https://a.com/b/%F0%9F%91%8D?a=%F0%9F%91%8D", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addQueryParameter("a", "👍")
                .addPathSegment("b")
                .addPathSegments("👍/")
                .build().toString());
        assertEquals("https://a.com/?%20=%20#%20", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addEncodedQueryParameter("%20", "%20")
                .fragment(" ")
                .build().toString());
        assertEquals("https://%20@a.com/#%20", Url.builder()
                .scheme(Scheme.HTTPS)
                .userInfo(" ")
                .host("a.com")
                .fragment(" ")
                .build().toString());
    }

    @Test
    public void toDecodedString() {
        assertEquals("https:// @a.com/ / ? = & = # ", Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .userInfo(" ")
                .addPathSegment(" ")
                .addPathSegment(" ")
                .addQueryParameter(" ", " ")
                .addQueryParameter(" ", " ")
                .fragment(" ")
                .build().toDecodedString());
    }

    // `Url.toString()` calls `toEncodedString()`, so there is no need for a `_toString()` test here.

    @Test
    public void _equals() {
        assertEquals(Url.parse("a://a.com"), Url.builder()
                .scheme("a")
                .host("a.com")
                .build());
        assertEquals(Url.parse("A://a.com"), Url.builder()
                .scheme("a")
                .host("a.com")
                .build());
        assertEquals(Url.parse("a://A.COM"), Url.builder()
                .scheme("A")
                .host("a.com")
                .build());
        assertEquals(Url.parse("a://a.com"), Url.builder()
                .scheme("a")
                .host("a.COM")
                .build());
        assertEquals(Url.parse("https://a.com/a"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addPathSegment("a")
                .build());
        assertNotEquals(Url.parse("https://A.COM/A"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addPathSegment("a")
                .build());
        assertEquals(Url.parse("https://a.com/?"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .query("")
                .build());
        assertEquals(Url.parse("https://a.com/a?=&="), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addPathSegment("a")
                .encodedQuery("=&=&=")
                .build());
        assertEquals(Url.parse("https://a.com/a%20?a=a&b=b"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addPathSegment("a ")
                .addQueryParameter("a", "a")
                .addQueryParameter("b", "b")
                .build());
        assertEquals(Url.parse("https://a.com/a?a=a&b=b&c=c"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addPathSegment("a")
                .addQueryParameter("c", "c")
                .addQueryParameter("b", "b")
                .addQueryParameter("a", "a")
                .build());
        assertEquals(Url.parse("https://a.com/a#a"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addPathSegment("a")
                .fragment("a")
                .build());
        assertEquals(Url.parse("https://a.com:443/a/b/..?a=a&b=b#a"), Url.builder()
                .scheme(Scheme.HTTPS)
                .host("a.com")
                .addPathSegment("a")
                .addQueryParameter("a", "a")
                .addQueryParameter("b", "b")
                .fragment("a")
                .build());
    }
}
