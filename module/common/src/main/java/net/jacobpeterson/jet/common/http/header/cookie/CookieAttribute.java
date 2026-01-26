package net.jacobpeterson.jet.common.http.header.cookie;

import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.http.HttpCookie;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * {@link CookieAttribute} is an enum that represents a standardized HTTP cookie attribute.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#attributes">
 * developer.mozilla.org</a>
 * @see Cookie
 */
@NullMarked
@RequiredArgsConstructor
public enum CookieAttribute {

    /**
     * Defines the host to which the cookie will be sent.
     * <p>
     * Only the current domain can be set as the value, or a domain of a higher order, unless it is a public suffix.
     * Setting the domain will make the cookie available to it, as well as to all its subdomains.
     * <p>
     * If omitted, the cookie is returned only to the host that sent them (i.e., it becomes a "host-only cookie").
     * This is more restrictive than setting the host name, as the cookie is not made available to subdomains of the
     * host.
     * <p>
     * Contrary to earlier specifications, leading dots in domain names (<code>.example.com</code>) are ignored.
     * <p>
     * Multiple host/domain values are <em>not</em> allowed, but if a domain <em>is</em> specified, then subdomains
     * are always included.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#domaindomain-value">
     * developer.mozilla.org</a>
     */
    DOMAIN(HttpCookie.DOMAIN_ATTRIBUTE),

    /**
     * Indicates the maximum lifetime of the cookie as an HTTP-date timestamp. See
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Date"><code>Date</code></a>
     * for the required formatting.
     * <p>
     * If unspecified, the cookie becomes a <strong>session cookie</strong>. A session finishes when the client shuts
     * down, after which the session cookie is removed.
     * <p>
     * <strong>Warning:</strong> Many web browsers have a <em>session restore</em> feature that will save all tabs
     * and restore them the next time the browser is used. Session cookies will also be restored, as if the browser
     * was never closed.
     * <p>
     * The <code>Expires</code> attribute is set by the server with a value relative to its own internal clock, which
     * may differ from that of the client browser. Firefox and Chromium-based browsers internally use an expiry
     * (max-age) value that is adjusted to compensate for clock difference, storing and expiring cookies based on the
     * time intended by the server. The adjustment for clock skew is calculated from the value of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Date"><code>DATE</code></a>
     * header. Note that the specification explains how the attribute should be parsed, but does not indicate if/how
     * the value should be corrected by the recipient.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#expiresdate">
     * developer.mozilla.org</a>
     */
    EXPIRES(HttpCookie.EXPIRES_ATTRIBUTE),

    /**
     * Forbids JavaScript from accessing the cookie, for example, through the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie"><code>Document.cookie</code></a>
     * property. Note that a cookie that has been created with <code>HttpOnly</code> will still be sent with
     * JavaScript-initiated requests, for example, when calling
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/send">
     * <code>XMLHttpRequest.send()</code></a> or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/fetch"><code>fetch()</code></a>. This mitigates
     * attacks against cross-site scripting
     * (<a href="https://developer.mozilla.org/en-US/docs/Glossary/Cross-site_scripting">XSS</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#httponly">
     * developer.mozilla.org</a>
     */
    HTTP_ONLY(HttpCookie.HTTP_ONLY_ATTRIBUTE),

    /**
     * Indicates the number of seconds until the cookie expires. A zero or negative number will expire the cookie
     * immediately. If both <code>Expires</code> and <code>Max-Age</code> are set, <code>Max-Age</code> has precedence.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#max-agenumber">
     * developer.mozilla.org</a>
     */
    MAX_AGE(HttpCookie.MAX_AGE_ATTRIBUTE),

    /**
     * Indicates the path that <em>must</em> exist in the requested URL for the browser to send the
     * <code>Cookie</code> header.
     * <p>
     * If omitted, this attribute defaults to the path component of the request URL. For example, if a cookie is set
     * by a request to <code>https://example.com/docs/Web/HTTP/index.html</code>, the default path would be
     * <code>/docs/Web/HTTP/</code>.
     * <p>
     * The forward slash (<code>/</code>) character is interpreted as a directory separator, and subdirectories are
     * matched as well. For example, for <code>Path=/docs</code>,
     * <ul>
     * <li>
     *     the request paths <code>/docs</code>, <code>/docs/</code>, <code>/docs/Web/</code>, and
     *     <code>/docs/Web/HTTP</code> will all match.
     * </li>
     * <li>
     *     the request paths <code>/</code>, <code>/docsets</code>, <code>/fr/docs</code> will not match.
     * </li>
     * </ul>
     * <strong>Note:</strong> The <code>path</code> attribute lets you control what cookies the browser sends based
     * on the different parts of a site. It is not intended as a security measure, and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie#security">does not protect</a>
     * against unauthorized reading of the cookie from a different path.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#pathpath-value">
     * developer.mozilla.org</a>
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    PATH(HttpCookie.PATH_ATTRIBUTE),

    /**
     * @see CookieSameSite
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#samesitesamesite-value">
     * developer.mozilla.org</a>
     */
    SAME_SITE(HttpCookie.SAME_SITE_ATTRIBUTE),

    /**
     * <p>Indicates that the cookie is sent to the server only when a request is made with the <code>https:</code>
     * scheme (except on localhost), and therefore, is more resistant to
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/MitM">man-in-the-middle</a> attacks.
     * <p>
     * <strong>Note:</strong> Do not assume that <code>Secure</code> prevents all access to sensitive information in
     * cookies (session keys, login details, etc.). Cookies with this attribute can still be read/modified either with
     * access to the client's hard disk or from JavaScript if the <code>HttpOnly</code> cookie attribute is not set.
     * Insecure sites (<code>http:</code>) cannot set cookies with the <code>Secure</code> attribute. The
     * <code>https:</code> requirements are ignored when the <code>Secure</code> attribute is set by localhost.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#secure">
     * developer.mozilla.org</a>
     */
    SECURE(HttpCookie.SECURE_ATTRIBUTE),

    /**
     * Indicates that the cookie should be stored using partitioned storage. Note that if this is set, the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#secure">
     * <code>Secure</code> directive</a> must also be set. See
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Privacy/Guides/Privacy_sandbox/Partitioned_cookies">
     * Cookies Having Independent Partitioned State (CHIPS)</a> for more details.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#partitioned">
     * developer.mozilla.org</a>
     */
    PARTITIONED(HttpCookie.PARTITIONED_ATTRIBUTE);

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An unmodifiable {@link Map} of lowercased {@link #toString()} mapped to {@link CookieAttribute}.
     */
    public static final Map<String, CookieAttribute> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toUnmodifiableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link CookieAttribute} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link CookieAttribute}, or <code>null</code> if no mapping exists
     */
    public static @Nullable CookieAttribute forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
