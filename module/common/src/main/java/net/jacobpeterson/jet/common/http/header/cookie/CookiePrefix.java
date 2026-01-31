package net.jacobpeterson.jet.common.http.header.cookie;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map.Entry;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

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
     * The prefix token: <code>"__"</code>
     */
    public static final String PREFIX_TOKEN = "__";

    /**
     * The suffix token: <code>"-"</code>
     */
    public static final String SUFFIX_TOKEN = "-";

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link CookiePrefix}.
     */
    public static final ImmutableMap<String, CookiePrefix> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * The inverse of {@link #VALUES_OF_LOWERCASED_STRINGS}.
     */
    public static final ImmutableMap<CookiePrefix, String> LOWERCASED_STRINGS_OF_VALUES =
            VALUES_OF_LOWERCASED_STRINGS.entrySet().stream().collect(toImmutableMap(Entry::getValue, Entry::getKey));

    private static final ImmutableList<CookiePrefix> FROM_COOKIE_NAME_SEARCH_LIST =
            ImmutableList.of(HOST_HTTP, SECURE, HOST, HTTP); // From longest to shortest

    /**
     * Gets the {@link CookiePrefix} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link CookiePrefix}, or <code>null</code> if no mapping exists
     */
    public static @Nullable CookiePrefix forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
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
        final var lowercasedCookieName = cookieName.toLowerCase(ROOT);
        // Brute-force search for only 4 possibilities is fine.
        for (final var cookiePrefix : FROM_COOKIE_NAME_SEARCH_LIST) {
            if (lowercasedCookieName.startsWith(requireNonNull(LOWERCASED_STRINGS_OF_VALUES.get(cookiePrefix)))) {
                return cookiePrefix;
            }
        }
        return null;
    }
}
