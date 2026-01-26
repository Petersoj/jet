package net.jacobpeterson.jet.common.http.header.cookie;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * {@link CookieSameSite} is an enum for the {@link CookieAttribute#SAME_SITE} value.
 * <p>
 * Controls whether or not a cookie is sent with cross-site requests: that is, requests originating from a different
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Site">site</a>, including the scheme, from the site that
 * set the cookie. This provides some protection against certain cross-site attacks, including
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/CSRF">cross-site request forgery (CSRF)</a> attacks.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#samesitesamesite-value">
 * developer.mozilla.org</a>
 * @see Cookie
 */
@NullMarked
@RequiredArgsConstructor
public enum CookieSameSite {

    /**
     * Send the cookie only for requests originating from the same
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Site">site</a> that set the cookie.
     */
    STRICT("Strict"),

    /**
     * Send the cookie only for requests originating from the same
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Site">site</a> that set the cookie, and for cross-site
     * requests that meet both of the following criteria:
     * <ul>
     * <li>
     * The request is a top-level navigation: this essentially means that the request causes the URL shown in the
     * browser's address bar to change.
     * <ul>
     * <li>
     * This would exclude, for example, requests made using the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/fetch"><code>fetch()</code></a> API, or requests
     * for subresources from
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/img"><code>&lt;img&gt;</code></a>
     * or <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/script">
     * <code>&lt;script&gt;</code></a> elements, or navigations inside
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a> elements.
     * </li>
     * <li>
     * It would include requests made when the user clicks a link in the top-level browsing context from one site to
     * another, or an assignment to
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Document/location"><code>document.location</code></a>,
     * or a <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/form">
     * <code>&lt;form&gt;</code></a> submission.
     * </li>
     * </ul>
     * </li>
     * <li>
     * The request uses a <a href="https://developer.mozilla.org/en-US/docs/Glossary/Safe/HTTP">safe</a> method: in
     * particular, this excludes
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT"><code>PUT</code></a>,
     * and <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/DELETE"><code>DELETE</code></a>.
     * </li>
     * </ul>
     * Some browsers use <code>Lax</code> as the default value if <code>SameSite</code> is not specified: see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#browser_compatibility">
     * Browser compatibility</a> for details.
     * <p>
     * <strong>Note:</strong> When <code>Lax</code> is applied as a default, a more permissive version is used. In this
     * more permissive version, cookies are also included in
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a>
     * requests, as long as they were set no more than two minutes before the request was made.
     */
    LAX("Lax"),

    /**
     * Send the cookie with both cross-site and same-site requests. The <code>Secure</code> attribute must also be
     * set when using this value.
     */
    NONE("None");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An unmodifiable {@link Map} of lowercased {@link #toString()} mapped to {@link CookieSameSite}.
     */
    public static final Map<String, CookieSameSite> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toUnmodifiableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link CookieSameSite} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link CookieSameSite}, or <code>null</code> if no mapping exists
     */
    public static @Nullable CookieSameSite forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
