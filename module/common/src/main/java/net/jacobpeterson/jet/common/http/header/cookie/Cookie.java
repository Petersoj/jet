package net.jacobpeterson.jet.common.http.header.cookie;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.url.Url;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.SetCookieParser;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Long.parseLong;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.DOMAIN;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.EXPIRES;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.HTTP_ONLY;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.MAX_AGE;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.PARTITIONED;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.PATH;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.SAME_SITE;
import static net.jacobpeterson.jet.common.http.header.cookie.CookieAttribute.SECURE;
import static org.eclipse.jetty.http.HttpCookie.asJavaNetHttpCookie;
import static org.eclipse.jetty.http.Syntax.requireValidRFC2616Token;
import static org.eclipse.jetty.http.Syntax.requireValidRFC6265CookieValue;
import static org.eclipse.jetty.server.HttpCookieUtils.getRFC6265SetCookie;

/**
 * {@link Cookie} is an immutable class that represents a standardized HTTP {@link Header#SET_COOKIE}. Internally, this
 * class wraps <code>org.eclipse.jetty.http.HttpCookie</code> from <a href="https://jetty.org">Jetty</a> and adds some
 * extra functionality.
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
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class Cookie {

    /**
     * The request cookies cookie delimiter: <code>;</code>
     */
    public static final String REQUEST_COOKIE_DELIMITER = ";";

    /**
     * The request cookies {@link #getName()}-{@link #getValue()} delimiter: <code>=</code>
     */
    public static final String REQUEST_COOKIE_NAME_VALUE_DELIMITER = "=";

    private static final SetCookieParser SET_COOKIE_PARSER = SetCookieParser.newInstance();
    private static final Splitter PARSE_REQUEST_COOKIES_COOKIE_SPLITTER =
            Splitter.on(REQUEST_COOKIE_DELIMITER).trimResults().omitEmptyStrings();
    private static final Splitter PARSE_REQUEST_COOKIES_NAME_VALUE_SPLITTER =
            Splitter.on(REQUEST_COOKIE_NAME_VALUE_DELIMITER).limit(2).trimResults();

    /**
     * Parses the given {@link Header#COOKIE} value {@link String} into a {@link String} {@link ImmutableMap}.
     *
     * @param requestCookies the {@link Header#COOKIE} value {@link String}
     *
     * @return the {@link String} {@link ImmutableMap}
     *
     * @see #toRequestString()
     */
    public static ImmutableMap<String, String> parseRequestCookies(final String requestCookies) {
        final var cookies = ImmutableMap.<String, String>builder();
        for (final var cookieSplit : PARSE_REQUEST_COOKIES_COOKIE_SPLITTER.split(requestCookies)) {
            final var nameValueSplit = PARSE_REQUEST_COOKIES_NAME_VALUE_SPLITTER.splitToList(cookieSplit);
            cookies.put(nameValueSplit.getFirst(), nameValueSplit.size() == 1 ? "" : nameValueSplit.get(1));
        }
        return cookies.buildKeepingLast();
    }

    /**
     * Parses the given {@link Header#SET_COOKIE} value {@link String} into a {@link Cookie}.
     *
     * @param responseCookie the {@link Header#SET_COOKIE} value {@link String}
     *
     * @return the {@link Cookie}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     * @see #toResponseString()
     */
    public static Cookie parseResponseCookie(final String responseCookie) throws IllegalArgumentException {
        final var httpCookie = SET_COOKIE_PARSER.parse(responseCookie);
        checkArgument(httpCookie != null, "Invalid response cookie: %s", responseCookie);
        return new Cookie(httpCookie);
    }

    /**
     * Creates a {@link Header#COOKIE} value {@link String} from the given request {@link Cookie} {@link Collection} by
     * joining {@link Cookie#toRequestString()} with {@link #REQUEST_COOKIE_DELIMITER}.
     *
     * @param requestCookies the request {@link Cookie} {@link Collection}
     *
     * @return the {@link Header#COOKIE} value {@link String}
     */
    public static String multipleToRequestString(final Collection<Cookie> requestCookies) {
        return requestCookies.stream().map(Cookie::toRequestString).collect(joining(REQUEST_COOKIE_DELIMITER + " "));
    }

    /**
     * @param value the attribute value
     *
     * @return <code>true</code> if <code>value</code> is not case-insensitive equal to <code>"false"</code>,
     * <code>false</code> otherwise
     */
    public static boolean isAttributeNotSetToFalse(final String value) {
        return !value.equalsIgnoreCase("false");
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
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder(null);
    }

    /**
     * {@link Builder} is a builder class for {@link Cookie}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private HttpCookie.@Nullable Builder httpCookieBuilder;
        private @Nullable String name;

        private Builder(final HttpCookie.@Nullable Builder httpCookieBuilder) {
            this.httpCookieBuilder = httpCookieBuilder;
        }

        private void requireHttpCookieBuilder(final boolean require) {
            checkArgument(require == (httpCookieBuilder != null), "`Cookie.Builder` `name()` and `value()` methods %s",
                    (require ? "must be called first" : "can only be called once"));
        }

        /**
         * Calls {@link #name(String)} with the concatenation of {@link CookiePrefix#toString()} and the given
         * <code>name</code>.
         */
        public Builder name(final CookiePrefix prefix, final String name) {
            return name(prefix + name);
        }

        /**
         * Use {@link Url#encode(String)}/{@link Url#decode(String)} to encode/decode
         * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#cookie-namecookie-value">
         * illegal cookie characters</a>
         * <p>
         * Note: the {@link #value(String)} method must be called directly after this method is called.
         *
         * @see #getName()
         */
        public Builder name(final String name) {
            requireHttpCookieBuilder(false);
            this.name = name;
            return this;
        }

        /**
         * Use {@link Url#encode(String)}/{@link Url#decode(String)} to encode/decode
         * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#cookie-namecookie-value">
         * illegal cookie characters</a>
         * <p>
         * Note: the {@link #name(String)} method must be called directly before this method is called.
         *
         * @see #getValue()
         */
        public Builder value(final String value) {
            requireHttpCookieBuilder(false);
            checkArgument(name != null, "`name()` must be called before `value()`");
            httpCookieBuilder = HttpCookie.build(name, value);
            return this;
        }

        /**
         * Calls {@link Map#forEach(BiConsumer)} with {@link #attribute(String, String)}.
         *
         * @see #getAttributes()
         */
        public Builder attributes(final Map<String, String> attributes) {
            attributes.forEach(this::attribute);
            return this;
        }

        /**
         * Calls {@link #attribute(String, String)} with <code>name</code> set to the given
         * {@link CookieAttribute#toString()}.
         *
         * @see #getAttribute(CookieAttribute)
         */
        public Builder attribute(final CookieAttribute attribute, final String value) {
            return attribute(attribute.toString(), value);
        }

        /**
         * @see #getAttribute(String)
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder attribute(final String name, final String value) {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.attribute(name, requireNonNull(value));
            return this;
        }

        /**
         * @see #getDomain()
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder domain(final String domain) {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.domain(requireNonNull(domain));
            return this;
        }

        /**
         * @see #isHttpOnly()
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder httpOnly() {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.httpOnly(true);
            return this;
        }

        /**
         * Calls {@link #maxAge(long)} with {@link Duration#toSeconds()}.
         */
        public Builder maxAge(final Duration maxAge) {
            return maxAge(maxAge.toSeconds());
        }

        /**
         * @see #getMaxAge()
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder maxAge(final long maxAge) {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.maxAge(maxAge);
            return this;
        }

        /**
         * @see #getPath()
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder path(final String path) {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.path(requireNonNull(path)); // `requireNonNull()` prevents path resetting
            return this;
        }

        /**
         * @see #getSameSite()
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder sameSite(final CookieSameSite cookieSameSite) {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.sameSite(switch (cookieSameSite) {
                case STRICT -> HttpCookie.SameSite.STRICT;
                case LAX -> HttpCookie.SameSite.LAX;
                case NONE -> HttpCookie.SameSite.NONE;
            });
            return this;
        }

        /**
         * @see #isSecure()
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder secure() {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.secure(true);
            return this;
        }

        /**
         * @see #isPartitioned()
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Builder partitioned() {
            requireHttpCookieBuilder(true);
            httpCookieBuilder.partitioned(true);
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link Cookie} instance.
         *
         * @return the built {@link Cookie}
         */
        @SuppressWarnings({"NullAway", "DataFlowIssue"})
        public Cookie build() {
            requireHttpCookieBuilder(true);
            return new Cookie(httpCookieBuilder.build());
        }
    }

    private final @SuppressWarnings("Immutable") HttpCookie httpCookie;
    private @LazyInit @Nullable Optional<CookiePrefix> prefix;
    private @LazyInit @Nullable Optional<String> domain;
    private @LazyInit @Nullable Optional<ZonedDateTime> expires;
    private @LazyInit @Nullable Boolean expired;
    private @LazyInit @Nullable Boolean httpOnly;
    private @LazyInit @Nullable Optional<Long> maxAge;
    private @LazyInit @Nullable Optional<String> path;
    private @LazyInit @Nullable Optional<CookieSameSite> sameSite;
    private @LazyInit @Nullable Boolean secure;
    private @LazyInit @Nullable Boolean partitioned;
    private @LazyInit @Nullable String requestString;
    private @LazyInit @Nullable String responseString;

    /**
     * @return the cookie name
     */
    @EqualsAndHashCode.Include
    public String getName() {
        return httpCookie.getName();
    }

    /**
     * @return internally-cached {@link CookiePrefix#fromCookieName(String)} {@link #getName()}
     */
    public @Nullable CookiePrefix getPrefix() {
        if (prefix == null) {
            prefix = Optional.ofNullable(CookiePrefix.fromCookieName(getName()));
        }
        return prefix.orElse(null);
    }

    /**
     * @return the cookie value
     */
    @EqualsAndHashCode.Include
    public String getValue() {
        return httpCookie.getValue();
    }

    /**
     * @return the cookie attributes {@link Map}
     */
    @EqualsAndHashCode.Include
    public Map<String, String> getAttributes() {
        return httpCookie.getAttributes();
    }

    /**
     * @return {@link #getAttribute(String)} {@link CookieAttribute#toString()}
     */
    public @Nullable String getAttribute(final CookieAttribute cookieAttribute) {
        return getAttribute(cookieAttribute.toString());
    }

    /**
     * @return {@link #getAttributes()} {@link Map#get(Object)}
     */
    public @Nullable String getAttribute(final String attribute) {
        return getAttributes().get(attribute);
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#DOMAIN}
     */
    public @Nullable String getDomain() {
        if (domain == null) {
            domain = Optional.ofNullable(getAttribute(DOMAIN));
        }
        return domain.orElse(null);
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#EXPIRES}
     * {@link ZonedDateTime#parse(CharSequence, DateTimeFormatter)} {@link DateTimeFormatter#RFC_1123_DATE_TIME}
     */
    public @Nullable ZonedDateTime getExpires() {
        if (expires == null) {
            final var expires = getAttribute(EXPIRES);
            this.expires = Optional.ofNullable(expires == null ? null :
                    ZonedDateTime.parse(expires, RFC_1123_DATE_TIME));
        }
        return expires.orElse(null);
    }

    /**
     * @return internally-cached <code>true</code> if this {@link Cookie} is expired (<code>{@link #getMaxAge()}
     * &lt;= 0</code> or {@link #getExpires()} {@link ZonedDateTime#isAfter(ChronoZonedDateTime)}
     * {@link ZonedDateTime#now()}), <code>false</code> otherwise
     */
    public boolean isExpired() {
        if (expired == null) {
            final var maxAge = getMaxAge();
            if (maxAge != null && maxAge <= 0) {
                expired = true;
            } else {
                final var expires = getExpires();
                expired = expires != null && ZonedDateTime.now(UTC).isAfter(expires);
            }
        }
        return expired;
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#HTTP_ONLY} parsed into
     * <code>boolean</code>
     */
    public boolean isHttpOnly() {
        if (httpOnly == null) {
            final var httpOnly = getAttribute(HTTP_ONLY);
            this.httpOnly = httpOnly != null && isAttributeNotSetToFalse(httpOnly);
        }
        return httpOnly;
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#MAX_AGE} parsed into
     * {@link Long}
     */
    public @Nullable Long getMaxAge() {
        if (maxAge == null) {
            final var maxAge = getAttribute(MAX_AGE);
            this.maxAge = maxAge == null ? Optional.empty() :
                    Optional.of(parseLong(maxAge)); // `HttpCookie` always checks if `max-age` string is a number
        }
        return maxAge.orElse(null);
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#PATH}
     */
    public @Nullable String getPath() {
        if (path == null) {
            path = Optional.ofNullable(getAttribute(PATH));
        }
        return path.orElse(null);
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#SAME_SITE} parsed into
     * {@link CookieSameSite}
     */
    public @Nullable CookieSameSite getSameSite() {
        if (sameSite == null) {
            final var sameSite = getAttribute(SAME_SITE);
            this.sameSite = Optional.ofNullable(sameSite == null ? null : CookieSameSite.forString(sameSite));
        }
        return sameSite.orElse(null);
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#SECURE} parsed into
     * <code>boolean</code>
     */
    public boolean isSecure() {
        if (secure == null) {
            final var secure = getAttribute(SECURE);
            this.secure = secure != null && isAttributeNotSetToFalse(secure);
        }
        return secure;
    }

    /**
     * @return internally-cached {@link #getAttribute(CookieAttribute)} {@link CookieAttribute#PARTITIONED} parsed into
     * <code>boolean</code>
     */
    public boolean isPartitioned() {
        if (partitioned == null) {
            final var partitioned = getAttribute(PARTITIONED);
            this.partitioned = partitioned != null && isAttributeNotSetToFalse(partitioned);
        }
        return partitioned;
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
     * @return this {@link Cookie} copied into a new {@link Builder} instance
     */
    public Builder toBuilder() {
        return new Builder(HttpCookie.build(httpCookie));
    }

    /**
     * @return internally-cached
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies#see_also">RFC6265</a>
     * {@link Header#COOKIE} value {@link String} (the concatenation of {@link #getName()},
     * {@link #REQUEST_COOKIE_NAME_VALUE_DELIMITER}, and {@link #getValue()})
     *
     * @throws IllegalArgumentException thrown for invalid {@link Cookie} values during the serialization process
     * @see #parseRequestCookies(String)
     */
    public String toRequestString() throws IllegalArgumentException {
        if (requestString == null) {
            final var name = getName();
            requireValidRFC2616Token(name, "ERROR");
            final var value = getValue();
            requireValidRFC6265CookieValue(value);
            requestString = name + REQUEST_COOKIE_NAME_VALUE_DELIMITER + value;
        }
        return requestString;
    }

    /**
     * @return internally-cached
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies#see_also">RFC6265</a>
     * {@link Header#SET_COOKIE} value {@link String}
     *
     * @throws IllegalArgumentException thrown for invalid {@link Cookie} values during the serialization process
     * @see #parseResponseCookie(String)
     */
    public String toResponseString() throws IllegalArgumentException {
        if (responseString == null) {
            responseString = getRFC6265SetCookie(httpCookie);
        }
        return responseString;
    }

    /**
     * @return {@link #toResponseString()}
     */
    @Override
    public String toString() {
        return toResponseString();
    }
}
