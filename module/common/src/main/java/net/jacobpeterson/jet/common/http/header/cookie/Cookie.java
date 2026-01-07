package net.jacobpeterson.jet.common.http.header.cookie;

import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.url.Url;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpDateTime;
import org.eclipse.jetty.http.SetCookieParser;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.ZoneOffset.UTC;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.EXPIRES;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.SAME_SITE;
import static org.eclipse.jetty.http.HttpCookie.asJavaNetHttpCookie;
import static org.eclipse.jetty.http.Syntax.requireValidRFC2616Token;
import static org.eclipse.jetty.http.Syntax.requireValidRFC6265CookieValue;
import static org.eclipse.jetty.server.HttpCookieUtils.getRFC6265SetCookie;

/**
 * {@link Cookie} is an immutable class that represents a standardized HTTP cookie. Internally, this class wraps
 * <code>org.eclipse.jetty.http.HttpCookie</code> from <a href="https://jetty.org">Jetty</a> and adds some extra
 * functionality.
 * <p>
 * A <strong>cookie</strong> (also known as a web cookie or browser cookie) is a small piece of data a server sends to a
 * user's web browser. The browser may store cookies, create new cookies, modify existing ones, and send them back to
 * the same server with later requests. Cookies enable web applications to store limited amounts of data and remember
 * state information; by default the HTTP protocol is
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Overview#http_is_stateless_but_not_sessionless">
 * stateless</a>.
 * <p>
 * The HTTP <strong><code>Set-Cookie</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Response_header">response header</a>
 * is used to send a cookie from the server to the user agent, so that the user agent can send it back to the server
 * later. To send multiple cookies, multiple <code>Set-Cookie</code> headers should be sent in the same response. For
 * more information, see the guide on <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies">Using
 * HTTP cookies</a>.
 * <p>
 * The HTTP <strong><code>Cookie</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Request_header">request header</a>
 * contains stored <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies">HTTP cookies</a>
 * associated with the server (i.e., previously sent by the server with the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie"><code>Set-Cookie</code></a>
 * header or set in JavaScript using
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie"><code>Document.cookie</code></a>).
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie">
 * developer.mozilla.org</a>
 * @see Header#SET_COOKIE
 * @see Header#COOKIE
 */
@NullMarked
@Immutable
@EqualsAndHashCode(cacheStrategy = LAZY)
public final class Cookie {

    private static final SetCookieParser SET_COOKIE_PARSER = SetCookieParser.newInstance();

    /**
     * Parses the given {@link Header#COOKIE} value into a {@link List} of {@link Cookie}s.
     *
     * @param headerValue the {@link Header#COOKIE} value
     *
     * @return the {@link Cookie} {@link List}
     *
     * @see #toRequestString()
     */
    public static List<Cookie> parseRequestCookies(final String headerValue) {
        if (headerValue.isBlank()) {
            return List.of();
        }
        final var cookies = new ArrayList<Cookie>();
        for (final var semicolonSplit : Splitter.on(';').split(headerValue)) {
            if (semicolonSplit.isBlank()) {
                continue;
            }
            final var equalsSplit = Splitter.on('=').limit(2).trimResults().splitToList(semicolonSplit);
            cookies.add(builder(equalsSplit.getFirst(),
                    equalsSplit.size() > 1 ? equalsSplit.get(1).trim() : "").build());
        }
        return cookies;
    }

    /**
     * Parses the given {@link Header#SET_COOKIE} value into a {@link Cookie}.
     *
     * @param headerValue the {@link Header#SET_COOKIE} value
     *
     * @return the {@link Cookie}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     * @see #toResponseString()
     */
    public static Cookie parseResponseCookie(final String headerValue) throws IllegalArgumentException {
        final var httpCookie = SET_COOKIE_PARSER.parse(headerValue);
        if (httpCookie == null) {
            throw new IllegalArgumentException();
        }
        return new Cookie(httpCookie);
    }

    /**
     * Creates a {@link Cookie} from the given Java {@link java.net.HttpCookie}.
     *
     * @param javaCookie the Java {@link java.net.HttpCookie}
     *
     * @return the {@link Cookie}
     *
     * @see #toJava()
     */
    public static Cookie fromJava(final java.net.HttpCookie javaCookie) {
        return new Cookie(HttpCookie.from(javaCookie));
    }

    /**
     * Calls {@link #builder(String, String)} with <code>name</code> set to the concatenation of
     * {@link CookiePrefix#toString()} and the given <code>name</code>.
     */
    public static Builder builder(final CookiePrefix prefix, final String name, final String value) {
        return builder(prefix + name, value);
    }

    /**
     * Creates a {@link Cookie} {@link Builder} instance.
     * <p>
     * Use {@link Url#encode(String)} to encode
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#cookie-namecookie-value">
     * illegal cookie characters</a>.
     *
     * @param name  the name
     * @param value the value
     *
     * @return the {@link Builder}
     */
    public static Builder builder(final String name, final String value) {
        return new Builder(HttpCookie.build(name, value));
    }

    /**
     * {@link Builder} is a nonreusable builder class for {@link Cookie}.
     *
     * @see #builder(CookiePrefix, String, String)
     * @see #builder(String, String)
     */
    public static final class Builder {

        private final HttpCookie.Builder httpCookieBuilder;

        private Builder(final HttpCookie.Builder httpCookieBuilder) {
            this.httpCookieBuilder = httpCookieBuilder;
        }

        /**
         * @see Cookie#getAttributes()
         */
        public Builder attributes(final Map<String, String> attributes) {
            attributes.forEach(this::attribute);
            return this;
        }

        /**
         * Calls {@link #attribute(String, String)} with <code>name</code> set to the given
         * {@link CookieAttribute#toString()}.
         *
         * @see Cookie#getAttribute(CookieAttribute)
         */
        public Builder attribute(final CookieAttribute attribute, final String value) {
            return attribute(attribute.toString(), value);
        }

        /**
         * @see Cookie#getAttribute(String)
         */
        public Builder attribute(final String name, final String value) {
            httpCookieBuilder.attribute(name, value);
            return this;
        }

        /**
         * @see Cookie#getDomain()
         */
        public Builder domain(final String domain) {
            httpCookieBuilder.domain(domain);
            return this;
        }

        /**
         * @see Cookie#isHttpOnly()
         */
        public Builder httpOnly(final boolean httpOnly) {
            httpCookieBuilder.httpOnly(httpOnly);
            return this;
        }

        /**
         * Calls {@link #maxAge(long)} with {@link Duration#toSeconds()}.
         */
        public Builder maxAge(final Duration maxAge) {
            return maxAge(maxAge.toSeconds());
        }

        /**
         * @param maxAge set to <code>0</code> to expire immediately. A negative number will set this to <code>0</code>.
         *
         * @see Cookie#getMaxAge()
         */
        public Builder maxAge(final long maxAge) {
            httpCookieBuilder.maxAge(maxAge < 0 ? 0 : maxAge);
            return this;
        }

        /**
         * @see Cookie#getPath()
         */
        public Builder path(final String path) {
            httpCookieBuilder.path(path);
            return this;
        }

        /**
         * @see Cookie#getSameSite()
         */
        public Builder sameSite(final SameSite sameSite) {
            httpCookieBuilder.sameSite(switch (sameSite) {
                case STRICT -> HttpCookie.SameSite.STRICT;
                case LAX -> HttpCookie.SameSite.LAX;
                case NONE -> HttpCookie.SameSite.NONE;
            });
            return this;
        }

        /**
         * @see Cookie#isSecure()
         */
        public Builder secure(final boolean secure) {
            httpCookieBuilder.secure(secure);
            return this;
        }

        /**
         * @see Cookie#isPartitioned()
         */
        public Builder partitioned(final boolean partitioned) {
            httpCookieBuilder.partitioned(partitioned);
            return this;
        }

        /**
         * @return the built {@link Cookie} instance
         */
        public Cookie build() {
            return new Cookie(httpCookieBuilder.build());
        }
    }

    @SuppressWarnings("Immutable")
    private final HttpCookie httpCookie;

    private Cookie(final HttpCookie httpCookie) {
        this.httpCookie = httpCookie;
    }

    /**
     * @return the attributes {@link Map}
     */
    public Map<String, String> getAttributes() {
        return httpCookie.getAttributes();
    }

    /**
     * Calls {@link #getAttribute(String)} with the given {@link CookieAttribute#toString()}.
     */
    public @Nullable String getAttribute(final CookieAttribute cookieAttribute) {
        return getAttribute(cookieAttribute.toString());
    }

    /**
     * Calls {@link Map#get(Object)} on {@link #getAttributes()} with the given <code>attribute</code>.
     */
    public @Nullable String getAttribute(final String attribute) {
        return getAttributes().get(attribute);
    }

    /**
     * @return the name
     */
    public String getName() {
        return httpCookie.getName();
    }

    /**
     * @return {@link CookiePrefix#fromCookieName(String)} {@link #getName()}
     */
    public @Nullable CookiePrefix getPrefix() {
        return CookiePrefix.fromCookieName(getName());
    }

    /**
     * @return the value
     */
    public String getValue() {
        return httpCookie.getValue();
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#DOMAIN}
     */
    public @Nullable String getDomain() {
        return httpCookie.getDomain();
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#EXPIRES} parsed into {@link ZonedDateTime}
     */
    public @Nullable ZonedDateTime getExpires() {
        final var string = getAttribute(EXPIRES);
        return string == null ? null : HttpDateTime.parse(string);
    }

    /**
     * @return <code>true</code> if this {@link Cookie} is expired (<code>{@link #getMaxAge()} &lt;= 0</code> or
     * {@link #getExpires()} {@link ZonedDateTime#isAfter(ChronoZonedDateTime)} {@link ZonedDateTime#now()}),
     * <code>false</code> otherwise
     */
    public boolean isExpired() {
        final var maxAge = getMaxAge();
        if (maxAge != null && maxAge <= 0) {
            return true;
        }
        final var expires = getExpires();
        return expires != null && ZonedDateTime.now(UTC).isAfter(expires);
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#HTTP_ONLY} parsed into <code>boolean</code>
     */
    public boolean isHttpOnly() {
        return httpCookie.isHttpOnly();
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#MAX_AGE} parsed into {@link Long}
     */
    public @Nullable Long getMaxAge() {
        final var value = httpCookie.getMaxAge();
        return value == -1 ? null : value;
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#PATH}
     */
    public @Nullable String getPath() {
        return httpCookie.getPath();
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#SAME_SITE} parsed into {@link SameSite}
     */
    public @Nullable SameSite getSameSite() {
        final var string = getAttribute(SAME_SITE);
        return string == null ? null : SameSite.forString(string);
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#SECURE} parsed into <code>boolean</code>
     */
    public boolean isSecure() {
        return httpCookie.isSecure();
    }

    /**
     * @return {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#PARTITIONED} parsed into
     * <code>boolean</code>
     */
    public boolean isPartitioned() {
        return httpCookie.isPartitioned();
    }

    /**
     * @return a Java {@link java.net.HttpCookie} created from this {@link Cookie}
     *
     * @throws IllegalArgumentException thrown for invalid {@link Cookie} values during the conversion process
     * @see #fromJava(java.net.HttpCookie)
     */
    public java.net.HttpCookie toJava() throws IllegalArgumentException {
        return asJavaNetHttpCookie(httpCookie);
    }

    /**
     * @return copies this {@link Cookie} into a {@link Builder} instance
     */
    public Builder toBuilder() {
        return new Builder(HttpCookie.build(httpCookie));
    }

    /**
     * @return the <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies#see_also">RFC6265</a>
     * {@link Header#COOKIE} value {@link String} (the concatenation of {@link #getName()}, equals, and
     * {@link #getValue()})
     *
     * @throws IllegalArgumentException thrown for invalid {@link Cookie} values during the serialization process
     * @see #parseRequestCookies(String)
     */
    public String toRequestString() throws IllegalArgumentException {
        final var name = getName();
        requireValidRFC2616Token(name, "ERROR");
        final var value = getValue();
        requireValidRFC6265CookieValue(value);
        return getName() + "=" + getValue();
    }

    /**
     * @return the <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies#see_also">RFC6265</a>
     * {@link Header#SET_COOKIE} value {@link String}
     *
     * @throws IllegalArgumentException thrown for invalid {@link Cookie} values during the serialization process
     * @see #parseResponseCookie(String)
     */
    public String toResponseString() throws IllegalArgumentException {
        return getRFC6265SetCookie(httpCookie);
    }

    /**
     * @see #toResponseString()
     */
    @Override
    public String toString() {
        return toResponseString();
    }
}
