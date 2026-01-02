package net.jacobpeterson.jet.common.http.header.cookie;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * {@link CookiePrefix} is an enum that represents a standardized HTTP cookie prefix.
 * <p>
 * Some cookie names contain prefixes that impose specific restrictions on the cookie's attributes in supporting
 * user-agents. All cookie prefixes start with a double-underscore (<code>__</code>) and end in a dash (<code>-</code>).
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#cookie_prefixes">
 * developer.mozilla.org</a>
 * @see Cookie
 */
@NullMarked
@RequiredArgsConstructor
public enum CookiePrefix {

    /**
     * Cookies with names starting with <code>__Secure-</code> must be set with the <code>Secure</code> attribute by a
     * secure page (HTTPS).
     */
    SECURE(CookiePrefix.PREFIX_TOKEN + "Secure" + CookiePrefix.SUFFIX_TOKEN),

    /**
     * Cookies with names starting with <code>__Host-</code> must be set with the <code>Secure</code> attribute by a
     * secure page (HTTPS). In addition, they must not have a <code>Domain</code> attribute specified, and the
     * <code>Path</code> attribute must be set to <code>/</code>. This guarantees that such cookies are only sent to
     * the host that set them, and not to any other host on the domain. It also guarantees that they are set host-wide
     * and cannot be overridden on any path on that host. This combination yields a cookie that is as close as can be to
     * treating the origin as a security boundary.
     */
    HOST(CookiePrefix.PREFIX_TOKEN + "Host" + CookiePrefix.SUFFIX_TOKEN),

    /**
     * Cookies with names starting with <code>__Http-</code> must be set with the <code>Secure</code> flag by a secure
     * page (HTTPS) and in addition must have the <code>HttpOnly</code> attribute set to prove that they were set via
     * the <code>Set-Cookie</code> header (they can't be set or modified via JavaScript features such as
     * <code>Document.cookie</code> or the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Cookie_Store_API">Cookie Store API</a>).
     */
    HTTP(CookiePrefix.PREFIX_TOKEN + "Http" + CookiePrefix.SUFFIX_TOKEN),

    /**
     * Cookies with names starting with <code>__Host-Http-</code> must be set with the <code>Secure</code> flag by a
     * secure page (HTTPS) and must have the <code>HttpOnly</code> attribute set to prove that they were set via the
     * <code>Set-Cookie</code> header. In addition, they also have the same restrictions as
     * <code>__Host-</code>-prefixed cookies. This combination yields a cookie that is as close as can be to treating
     * the origin as a security boundary while at the same time ensuring developers and server operators know that its
     * scope is limited to HTTP requests.
     */
    HOST_HTTP(CookiePrefix.PREFIX_TOKEN + "Host-Http" + CookiePrefix.SUFFIX_TOKEN);

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * The double-underscore (<code>__</code>) prefix token.
     */
    public static final String PREFIX_TOKEN = "__";

    /**
     * The hyphen (<code>-</code>) suffix token.
     */
    public static final String SUFFIX_TOKEN = "-";

    /**
     * An unmodifiable {@link Map} of uppercased {@link #toString()} mapped to {@link CookiePrefix}.
     */
    public static final Map<String, CookiePrefix> VALUES_OF_UPPERCASED_STRINGS = stream(values())
            .collect(toUnmodifiableMap(value -> value.toString().toUpperCase(ROOT), identity()));

    private static final Map<CookiePrefix, String> UPPERCASED_STRINGS_OF_VALUES =
            VALUES_OF_UPPERCASED_STRINGS.entrySet().stream()
                    .collect(toUnmodifiableMap(Entry::getValue, Entry::getKey));
    private static final List<CookiePrefix> FROM_COOKIE_NAME_SEARCH_LIST =
            List.of(HOST_HTTP, SECURE, HOST, HTTP); // From longest to shortest

    /**
     * Gets the {@link CookiePrefix} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link CookiePrefix}, or <code>null</code> if no mapping exists
     */
    public static @Nullable CookiePrefix forString(final String string) {
        return VALUES_OF_UPPERCASED_STRINGS.get(string.toUpperCase(ROOT));
    }

    /**
     * Gets the {@link CookiePrefix} from the given cookie <code>cookieName</code>.
     *
     * @param cookieName the case-insensitive {@link Cookie#getName()}
     *
     * @return the {@link CookiePrefix}, or <code>null</code> if no {@link CookiePrefix} was found
     */
    public static @Nullable CookiePrefix fromCookieName(final String cookieName) {
        if (!cookieName.startsWith(PREFIX_TOKEN)) {
            return null;
        }
        final var uppercasedCookieName = cookieName.toUpperCase(ROOT);
        // Brute force search for only 4 possibilities is fine.
        for (final var cookiePrefix : FROM_COOKIE_NAME_SEARCH_LIST) {
            if (uppercasedCookieName.startsWith(UPPERCASED_STRINGS_OF_VALUES.get(cookiePrefix))) {
                return cookiePrefix;
            }
        }
        return null;
    }
}
