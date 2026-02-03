package net.jacobpeterson.jet.common.http.header.cachecontrol.request;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link RequestDirectiveKey} is an enum for a {@link RequestCacheControl} directive key.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#request_directives">
 * developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum RequestDirectiveKey {

    /**
     * The <code>no-cache</code> request directive asks caches to validate the response with the origin server before
     * reuse.
     * <div><code>
     * Cache-Control: no-cache
     * </code></div>
     * <p>
     * <code>no-cache</code> allows clients to request the most up-to-date response even if the cache has a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * response.
     * <p>
     * Browsers usually add <code>no-cache</code> to requests when users are <strong>force reloading</strong> a page.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#no-cache_2">
     * developer.mozilla.org</a>
     */
    NO_CACHE("no-cache"),

    /**
     * The <code>no-store</code> request directive allows a client to request that caches refrain from storing the
     * request and corresponding response — even if the origin server's response could be stored.
     * <div><code>
     * Cache-Control: no-store
     * </code></div>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#no-store_2">
     * developer.mozilla.org</a>
     */
    NO_STORE("no-store"),

    /**
     * The <code>max-age=N</code> request directive indicates that the client allows a stored response that is generated
     * on the origin server within <em>N</em> seconds — where <em>N</em> may be any non-negative integer (including
     * <code>0</code>).
     * <div><code>
     * Cache-Control: max-age=10800
     * </code></div>
     * <p>
     * In the case above, if the response with <code>Cache-Control: max-age=10800</code> was generated more than 3 hours
     * ago (calculated from <code>max-age</code> and the <code>Age</code> header), the cache couldn't reuse that
     * response.
     * <p>
     * Many browsers use this directive for <strong>reloading</strong>, as explained below.
     * <div><code>
     * Cache-Control: max-age=0
     * </code></div>
     * <p>
     * <code>max-age=0</code> is a workaround for <code>no-cache</code>, because many old (HTTP/1.0) cache
     * implementations don't support <code>no-cache</code>. Recently browsers are still using <code>max-age=0</code> in
     * "reloading" — for backward compatibility — and alternatively using <code>no-cache</code> to cause a "force
     * reloading".
     * <p>
     * If the <code>max-age</code> value is negative (for example, <code>-1</code>) or isn't an integer (for example,
     * <code>3599.99</code>), then the caching behavior is unspecified. Caches are encouraged to treat the value as if
     * it were <code>0</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#max-age_2">
     * developer.mozilla.org</a>
     */
    MAX_AGE("max-age"),

    /**
     * The <code>max-stale=N</code> request directive indicates that the client allows a stored response that is
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">stale</a>
     * within <em>N</em> seconds. If no <em>N</em> value is specified, the client will accept a stale response of any
     * age.
     * <div><code>
     * Cache-Control: max-stale=3600
     * </code></div>
     * <p>
     * For example, a request with the header above indicates that the browser will accept a stale response from the
     * cache that has expired within the last hour.
     * <p>
     * Clients can use this header when the origin server is down or too slow and can accept cached responses from
     * caches even if they are a bit old.
     * <p>
     * Note that the major browsers do not support requests with <code>max-stale</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#max-stale">
     * developer.mozilla.org</a>
     */
    MAX_STALE("max-stale"),

    /**
     * The <code>min-fresh=N</code> request directive indicates that the client allows a stored response that is
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * for at least <em>N</em> seconds.
     * <div><code>
     * Cache-Control: min-fresh=600
     * </code></div>
     * <p>
     * In the case above, if the response with <code>Cache-Control: max-age=3600</code> was stored in caches 51 minutes
     * ago, the cache couldn't reuse that response.
     * <p>
     * Clients can use this header when the user requires the response to not only be
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">
     * fresh</a>, but also requires that it won't be updated for a period of time.
     * <p>
     * Note that the major browsers do not support requests with <code>min-fresh</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#min-fresh">
     * developer.mozilla.org</a>
     */
    MIN_FRESH("min-fresh"),

    /**
     * Same meaning that <code>no-transform</code> has for a response, but for a request instead.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#no-transform_2">
     * developer.mozilla.org</a>
     */
    NO_TRANSFORM("no-transform"),

    /**
     * The client indicates that an already-cached response should be returned. If a cache has a stored response, even a
     * stale one, it will be returned. If no cached response is available, a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/504">504 Gateway Timeout</a> response
     * will be returned.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#only-if-cached">
     * developer.mozilla.org</a>
     */
    ONLY_IF_CACHED("only-if-cached"),

    /**
     * The <code>stale-if-error</code> request directive indicates that the browser is interested in receiving stale
     * content on error from any intermediate server for a particular origin. This is not supported by any browser (see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#browser_compatibility">
     * Browser compatibility</a>).
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#stale-if-error_2">
     * developer.mozilla.org</a>
     */
    STALE_IF_ERROR("stale-if-error");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link RequestDirectiveKey}.
     */
    public static final ImmutableMap<String, RequestDirectiveKey> VALUES_OF_LOWERCASED_STRINGS =
            stream(values()).collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link RequestDirectiveKey} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link RequestDirectiveKey}, or <code>null</code> if no mapping exists
     */
    public static @Nullable RequestDirectiveKey forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
