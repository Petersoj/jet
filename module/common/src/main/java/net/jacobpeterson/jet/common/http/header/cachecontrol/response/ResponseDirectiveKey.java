package net.jacobpeterson.jet.common.http.header.cachecontrol.response;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link ResponseDirectiveKey} is an enum for a {@link ResponseCacheControl} directive key.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#response_directives">
 * developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum ResponseDirectiveKey {

    /**
     * The <code>max-age=N</code> response directive indicates that the response remains
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * until <em>N</em> seconds after the response is generated.
     * <div><code>
     * Cache-Control: max-age=604800
     * </code></div>
     * <p>
     * Indicates that caches can store this response and reuse it for subsequent requests while it's
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">
     * fresh</a>.
     * <p>
     * Note that <code>max-age</code> is not the elapsed time since the response was received; it is the elapsed time
     * since the response was generated on the origin server. So if the other cache(s) — on the network route taken by
     * the response — store the response for 100 seconds (indicated using the <code>Age</code> response header field),
     * the browser cache would deduct 100 seconds from its
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">freshness
     * lifetime</a>.
     * <p>
     * If the <code>max-age</code> value is negative (for example, <code>-1</code>) or isn't an integer (for example,
     * <code>3599.99</code>), then the caching behavior is unspecified. Caches are encouraged to treat the value as if
     * it were <code>0</code> (this is noted in the
     * <a href="https://httpwg.org/specs/rfc9111.html#calculating.freshness.lifetime">Calculating Freshness
     * Lifetime</a> section of the HTTP specification).
     * <div><code>
     * Cache-Control: max-age=604800
     * Age: 100
     * </code></div>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#max-age">
     * developer.mozilla.org</a>
     */
    MAX_AGE("max-age"),

    /**
     * The <code>s-maxage</code> response directive indicates how long the response remains
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * in a shared cache. The <code>s-maxage</code> directive is ignored by private caches, and overrides the value
     * specified by the <code>max-age</code> directive or the <code>Expires</code> header for shared caches, if they
     * are present.
     * <div><code>
     * Cache-Control: s-maxage=604800
     * </code></div>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#s-maxage">
     * developer.mozilla.org</a>
     */
    S_MAXAGE("s-maxage"),

    /**
     * The <code>no-cache</code> response directive indicates that the response can be stored in caches, but the
     * response must be validated with the origin server before each reuse, even when the cache is disconnected from the
     * origin server.
     * <div><code>
     * Cache-Control: no-cache
     * </code></div>
     * <p>
     * If you want caches to always check for content updates while reusing stored content, <code>no-cache</code> is the
     * directive to use. It does this by requiring caches to revalidate each request with the origin server.
     * <p>
     * Note that <code>no-cache</code> does not mean "don't cache". <code>no-cache</code> allows caches to store a
     * response but requires them to revalidate it before reuse. If the sense of "don't cache" that you want is
     * actually "don't store", then <code>no-store</code> is the directive to use.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#no-cache">
     * developer.mozilla.org</a>
     */
    NO_CACHE("no-cache"),

    /**
     * The <code>must-revalidate</code> response directive indicates that the response can be stored in caches and can
     * be reused while
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">
     * fresh</a>. If the response becomes
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">
     * stale</a>, it must be validated with the origin server before reuse.
     * <p>
     * Typically, <code>must-revalidate</code> is used with <code>max-age</code>.
     * <div><code>
     * Cache-Control: max-age=604800, must-revalidate
     * </code></div>
     * <p>
     * HTTP allows caches to reuse
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">stale
     * responses</a> when they are disconnected from the origin server. <code>must-revalidate</code> is a way to prevent
     * this from happening - either the stored response is revalidated with the origin server or a 504 (Gateway Timeout)
     * response is generated.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#must-revalidate">
     * developer.mozilla.org</a>
     */
    MUST_REVALIDATE("must-revalidate"),

    /**
     * The <code>proxy-revalidate</code> response directive is the equivalent of <code>must-revalidate</code>, but
     * specifically for shared caches only.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#proxy-revalidate">
     * developer.mozilla.org</a>
     */
    PROXY_REVALIDATE("proxy-revalidate"),

    /**
     * The <code>no-store</code> response directive indicates that any caches of any kind (private or shared) should not
     * store this response.
     * <div><code>
     * Cache-Control: no-store
     * </code></div>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#no-store">
     * developer.mozilla.org</a>
     */
    NO_STORE("no-store"),

    /**
     * The <code>private</code> response directive indicates that the response can be stored only in a private cache
     * (e.g., local caches in browsers).
     * <div><code>
     * Cache-Control: private
     * </code></div>
     * <p>
     * You should add the <code>private</code> directive for user-personalized content, especially for responses
     * received after login and for sessions managed via cookies.
     * <p>
     * If you forget to add <code>private</code> to a response with personalized content, then that response can be
     * stored in a shared cache and end up being reused for multiple users, which can cause personal information to
     * leak.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#private">
     * developer.mozilla.org</a>
     */
    PRIVATE("private"),

    /**
     * The <code>public</code> response directive indicates that the response can be stored in a shared cache. Responses
     * for requests with <code>Authorization</code> header fields must not be stored in a shared cache; however, the
     * <code>public</code> directive will cause such responses to be stored in a shared cache.
     * <div><code>
     * Cache-Control: public
     * </code></div>
     * <p>
     * In general, when pages are under Basic Auth or Digest Auth, the browser sends requests with the
     * <code>Authorization</code> header. This means that the response is access-controlled for restricted users (who
     * have accounts), and it's fundamentally not shared-cacheable, even if it has <code>max-age</code>.
     * <p>
     * You can use the <code>public</code> directive to unlock that restriction.
     * <div><code>
     * Cache-Control: public, max-age=604800
     * </code></div>
     * <p>
     * Note that <code>s-maxage</code> or <code>must-revalidate</code> also unlock that restriction.
     * <p>
     * If a request doesn't have an <code>Authorization</code> header, or you are already using <code>s-maxage</code> or
     * <code>must-revalidate</code> in the response, then you don't need to use <code>public</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#public">
     * developer.mozilla.org</a>
     */
    PUBLIC("public"),

    /**
     * The <code>must-understand</code> response directive indicates that a cache should store the response only if it
     * understands the requirements for caching based on status code.
     * <p>
     * <code>must-understand</code> should be coupled with <code>no-store</code> for fallback behavior.
     * <div><code>
     * Cache-Control: must-understand, no-store
     * </code></div>
     * <p>
     * If a cache doesn't support <code>must-understand</code>, it will be ignored. If <code>no-store</code> is also
     * present, the response isn't stored.
     * <p>
     * If a cache supports <code>must-understand</code>, it stores the response with an understanding of cache
     * requirements based on its status code.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#must-understand">
     * developer.mozilla.org</a>
     */
    MUST_UNDERSTAND("must-understand"),

    /**
     * Some intermediaries transform content for various reasons. For example, some convert images to reduce transfer
     * size. In some cases, this is undesirable for the content provider.
     * <p>
     * <code>no-transform</code> indicates that any intermediary (regardless of whether it implements a cache) shouldn't
     * transform the response contents.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#no-transform">
     * developer.mozilla.org</a>
     */
    NO_TRANSFORM("no-transform"),

    /**
     * The <code>immutable</code> response directive indicates that the response will not be updated while it's
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">
     * fresh</a>.
     * <div><code>
     * Cache-Control: public, max-age=604800, immutable
     * </code></div>
     * <p>
     * A modern best practice for static resources is to include version/hashes in their URLs, while never modifying the
     * resources — but instead, when necessary, <em>updating</em> the resources with newer versions that have new
     * version-numbers/hashes, so that their URLs are different. That's called the <strong>cache-busting</strong>
     * pattern.
     * <div><code>
     * &lt;script src="https://example.com/react.0.0.0.js"&gt;&lt;/script&gt;
     * </code></div>
     * <p>
     * When a user reloads the browser, the browser will send conditional requests for validating to the origin server.
     * But it's not necessary to revalidate those kinds of static resources even when a user reloads the browser,
     * because they're never modified. <code>immutable</code> tells a cache that the response is immutable while it's
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * and avoids those kinds of unnecessary conditional requests to the server.
     * <p>
     * When you use a cache-busting pattern for resources and apply them to a long <code>max-age</code>, you can also
     * add <code>immutable</code> to avoid revalidation.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#immutable">
     * developer.mozilla.org</a>
     */
    IMMUTABLE("immutable"),

    /**
     * The <code>stale-while-revalidate</code> response directive indicates that the cache could reuse a stale response
     * while it revalidates it to a cache.
     * <div><code>
     * Cache-Control: max-age=604800, stale-while-revalidate=86400
     * </code></div>
     * <p>
     * In the example above, the response is
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * for 7 days (604800s). After 7 days it becomes
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">
     * stale</a>, but the cache is allowed to reuse it for any requests that are made in the following day (86400s),
     * provided that they revalidate the response in the background.
     * <p>
     * Revalidation will make the cache be
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * again, so it appears to clients that it was always
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * during that period — effectively hiding the latency penalty of revalidation from them.
     * <p>
     * If no request happened during that period, the cache became
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">stale</a>
     * and the next request will revalidate normally.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#stale-while-revalidate">
     * developer.mozilla.org</a>
     */
    STALE_WHILE_REVALIDATE("stale-while-revalidate"),

    /**
     * The <code>stale-if-error</code> response directive indicates that the cache can reuse a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">stale
     * response</a> when an upstream server generates an error, or when the error is generated locally. Here, an error
     * is considered any response with a status code of 500, 502, 503, or 504.
     * <div><code>
     * Cache-Control: max-age=604800, stale-if-error=86400
     * </code></div>
     * <p>
     * In the example above, the response is
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
     * for 7 days (604800s). Afterwards, it becomes
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">
     * stale</a>, but can be used for an extra 1 day (86400s) when an error is encountered.
     * <p>
     * After the stale-if-error period passes, the client will receive any error generated.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#stale-if-error">
     * developer.mozilla.org</a>
     */
    STALE_IF_ERROR("stale-if-error");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link ResponseDirectiveKey}.
     */
    public static final ImmutableMap<String, ResponseDirectiveKey> VALUES_OF_LOWERCASED_STRINGS =
            stream(values()).collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link ResponseDirectiveKey} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link ResponseDirectiveKey}, or <code>null</code> if no mapping exists
     */
    public static @Nullable ResponseDirectiveKey forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
