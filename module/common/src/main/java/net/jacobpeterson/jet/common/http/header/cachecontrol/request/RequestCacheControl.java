package net.jacobpeterson.jet.common.http.header.cachecontrol.request;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.cachecontrol.util.CacheControlUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Locale.ROOT;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.MAX_AGE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.MAX_STALE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.MIN_FRESH;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.NO_CACHE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.NO_STORE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.NO_TRANSFORM;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.ONLY_IF_CACHED;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestDirectiveKey.STALE_IF_ERROR;

/**
 * {@link RequestCacheControl} is an immutable class that represents a standardized HTTP request
 * {@link Header#CACHE_CONTROL}.
 * <p>
 * The HTTP <strong><code>Cache-Control</code></strong> header holds <em>directives</em> (instructions) in both requests
 * and responses that control <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching">caching</a> in
 * browsers and shared caches (e.g., Proxies, CDNs).
 * <p>
 * Cache directives follow these rules:
 * <ul>
 * <li>Caching directives are case-insensitive. However, lowercase is recommended because some implementations do not
 * recognize uppercase directives.</li>
 * <li>Multiple directives are permitted and must be comma-separated (e.g., <code>Cache-control: max-age=180,
 * public</code>).</li>
 * <li>Some directives have an optional argument. When an argument is provided, it is separated from the directive name
 * by an equals symbol (<code>=</code>). Typically, arguments for the directives are integers and are therefore not
 * enclosed in quote characters (e.g., <code>Cache-control: max-age=12</code>).</li>
 * </ul>
 * <h2>Vocabulary</h2>
 * This section defines the terms used in this document, some of which are from the specification.
 * <dl>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#http_cache">
 * (HTTP) cache</a></dt>
 * <dd>Implementation that holds requests and responses for reusing in subsequent requests. It can be either a shared
 * cache or a private cache.</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#shared_cache">
 * Shared cache</a></dt>
 * <dd>Cache that exists between the origin server and clients (e.g., Proxy, CDN). It stores a single response and
 * reuses it with multiple users — so developers should avoid storing personalized contents to be cached in the shared
 * cache.</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#private_cache">
 * Private cache</a></dt>
 * <dd>Cache that exists in the client. It is also called <em>local cache</em> or <em>browser cache</em>. It can store
 * and reuse personalized content for a single user.</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#store_response">
 * Store response</a></dt>
 * <dd>Store a response in caches when the response is cacheable. However, the cached response is not always reused
 * as-is. (Usually, "cache" means storing a response.)</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#reuse_response">
 * Reuse response</a></dt>
 * <dd>Reuse cached responses for subsequent requests.</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#revalidate_response">
 * Revalidate response</a></dt>
 * <dd>Ask the origin server whether or not the stored response is still
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>.
 * Usually, the revalidation is done through a conditional request.</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#fresh_response">
 * Fresh response</a></dt>
 * <dd>Indicates that the response is
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>.
 * This usually means the response can be reused for subsequent requests, depending on request directives.</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#stale_response">
 * Stale response</a></dt>
 * <dd>Indicates that the response is a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">stale
 * response</a>. This usually means the response can't be reused as-is. Cache storage isn't required to remove stale
 * responses immediately because revalidation could change the response from being stale to being
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh</a>
 * again.</dd>
 * <dt><a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control#age">
 * Age</a></dt>
 * <dd>The time since a response was generated. It is a criterion for whether a response is
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching#fresh_and_stale_based_on_age">fresh or
 * stale</a>.</dd>
 * </dl>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control">
 * developer.mozilla.org</a>
 * @see Header#CACHE_CONTROL
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class RequestCacheControl {

    /**
     * Parses the given request {@link Header#CACHE_CONTROL} value {@link String} into a {@link RequestCacheControl}.
     *
     * @param requestCacheControl the request {@link Header#CACHE_CONTROL} value {@link String}
     *
     * @return the {@link RequestCacheControl}
     *
     * @see CacheControlUtil#parse(String)
     * @see #toString()
     */
    public static RequestCacheControl parse(final String requestCacheControl) {
        return new RequestCacheControl(CacheControlUtil.parse(requestCacheControl));
    }

    /**
     * @return {@link #builder(Map)} with <code>existingDirectives</code> set to <code>null</code>
     */
    public static Builder builder() {
        return builder(null);
    }

    /**
     * Creates a {@link Builder}.
     *
     * @param existingDirectives the {@link String} {@link Map} of existing directives, or <code>null</code>
     *
     * @return the {@link Builder}
     */
    public static Builder builder(final @Nullable Map<String, String> existingDirectives) {
        return new Builder(existingDirectives);
    }

    /**
     * {@link Builder} is a builder class for {@link RequestCacheControl}.
     *
     * @see #builder()
     * @see #builder(Map)
     */
    public static final class Builder {

        private final Map<String, String> directives;

        private Builder(final @Nullable Map<String, String> existingDirectives) {
            this.directives = new LinkedHashMap<>();
            if (existingDirectives != null) {
                existingDirectives.forEach(this::putDirectiveValue);
            }
        }

        /**
         * @see #getDirectives()
         */
        public Builder putDirectiveValue(final String key, final String value) {
            directives.put(key.toLowerCase(ROOT), value);
            return this;
        }

        /**
         * @see #getDirectives()
         */
        public Builder putDirectiveValue(final RequestDirectiveKey requestDirectiveKey, final String value) {
            directives.put(requestDirectiveKey.toString(), value);
            return this;
        }

        /**
         * @return {@link #putDirectiveValue(String, String)} with <code>value</code> set to an empty {@link String}
         */
        public Builder putDirectiveValueless(final String key) {
            return putDirectiveValue(key, "");
        }

        /**
         * @return {@link #putDirectiveValue(RequestDirectiveKey, String)} with <code>value</code> set to an empty
         * {@link String}
         */
        public Builder putDirectiveValueless(final RequestDirectiveKey requestDirectiveKey) {
            return putDirectiveValue(requestDirectiveKey, "");
        }

        /**
         * @return {@link #putDirectiveValue(String, String)} with {@link Long#toString()}
         */
        public Builder putDirectiveValueLong(final String key, final long value) {
            return putDirectiveValue(key, Long.toString(value));
        }

        /**
         * @return {@link #putDirectiveValue(RequestDirectiveKey, String)} with {@link Long#toString()}
         */
        public Builder putDirectiveValueLong(final RequestDirectiveKey requestDirectiveKey, final long value) {
            return putDirectiveValue(requestDirectiveKey, Long.toString(value));
        }

        /**
         * @return {@link #putDirectiveValueless(RequestDirectiveKey)} {@link RequestDirectiveKey#NO_CACHE}
         *
         * @see #isNoCache
         */
        public Builder noCache() {
            return putDirectiveValueless(NO_CACHE);
        }

        /**
         * @return {@link #putDirectiveValueless(RequestDirectiveKey)} {@link RequestDirectiveKey#NO_STORE}
         *
         * @see #isNoStore
         */
        public Builder noStore() {
            return putDirectiveValueless(NO_STORE);
        }

        /**
         * @return {@link #putDirectiveValueLong(RequestDirectiveKey, long)} {@link RequestDirectiveKey#MAX_AGE}
         *
         * @see #getMaxAge
         */
        public Builder maxAge(final long maxAge) {
            return putDirectiveValueLong(MAX_AGE, maxAge);
        }

        /**
         * @return {@link #putDirectiveValueLong(RequestDirectiveKey, long)} {@link RequestDirectiveKey#MAX_STALE}
         *
         * @see #getMaxStale
         */
        public Builder maxStale(final long maxStale) {
            return putDirectiveValueLong(MAX_STALE, maxStale);
        }

        /**
         * @return {@link #putDirectiveValueLong(RequestDirectiveKey, long)} {@link RequestDirectiveKey#MIN_FRESH}
         *
         * @see #getMinFresh
         */
        public Builder minFresh(final long minFresh) {
            return putDirectiveValueLong(MIN_FRESH, minFresh);
        }

        /**
         * @return {@link #putDirectiveValueless(RequestDirectiveKey)} {@link RequestDirectiveKey#NO_TRANSFORM}
         *
         * @see #isNoTransform
         */
        public Builder noTransform() {
            return putDirectiveValueless(NO_TRANSFORM);
        }

        /**
         * @return {@link #putDirectiveValueless(RequestDirectiveKey)} {@link RequestDirectiveKey#ONLY_IF_CACHED}
         *
         * @see #isOnlyIfCached
         */
        public Builder onlyIfCached() {
            return putDirectiveValueless(ONLY_IF_CACHED);
        }

        /**
         * @return {@link #putDirectiveValueless(RequestDirectiveKey)} {@link RequestDirectiveKey#STALE_IF_ERROR}
         *
         * @see #isStaleIfError
         */
        public Builder staleIfError() {
            return putDirectiveValueless(STALE_IF_ERROR);
        }

        /**
         * Builds this {@link Builder} into a new {@link RequestCacheControl} instance.
         *
         * @return the built {@link RequestCacheControl}
         */
        public RequestCacheControl build() {
            return new RequestCacheControl(ImmutableMap.copyOf(directives));
        }
    }

    /**
     * An {@link ImmutableMap} containing all directive key {@link String}s and their value {@link String}. If a
     * directive is present, but has no value in {@link Header#CACHE_CONTROL}, then it maps to an empty {@link String}
     * in this {@link ImmutableMap}.
     */
    private final @EqualsAndHashCode.Include @Getter ImmutableMap<String, String> directives;
    private @LazyInit @Nullable Boolean noCache;
    private @LazyInit @Nullable Boolean noStore;
    private @LazyInit @Nullable Optional<Long> maxAge;
    private @LazyInit @Nullable Optional<Long> maxStale;
    private @LazyInit @Nullable Optional<Long> minFresh;
    private @LazyInit @Nullable Boolean noTransform;
    private @LazyInit @Nullable Boolean onlyIfCached;
    private @LazyInit @Nullable Boolean staleIfError;
    private @LazyInit @Nullable String string;

    /**
     * @return {@link #containsKey(String)} {@link RequestDirectiveKey#toString()}
     */
    public boolean containsKey(final RequestDirectiveKey requestDirectiveKey) {
        return directives.containsKey(requestDirectiveKey.toString());
    }

    /**
     * @return {@link #getDirectives()} {@link ImmutableMap#containsKey(Object)}
     */
    public boolean containsKey(final String requestDirectiveKey) {
        return directives.containsKey(requestDirectiveKey);
    }

    /**
     * @return {@link #parseValueLong(String)} {@link RequestDirectiveKey#toString()}
     */
    public Optional<Long> parseValueLong(final RequestDirectiveKey requestDirectiveKey) {
        return parseValueLong(requestDirectiveKey.toString());
    }

    /**
     * @return {@link CacheControlUtil#parseValueLong(Map, String)}
     */
    public Optional<Long> parseValueLong(final String requestDirectiveKey) {
        return CacheControlUtil.parseValueLong(directives, requestDirectiveKey);
    }

    /**
     * @return internally-cached {@link #containsKey(RequestDirectiveKey)} {@link RequestDirectiveKey#NO_CACHE}
     */
    public boolean isNoCache() {
        if (noCache == null) {
            noCache = containsKey(NO_CACHE);
        }
        return noCache;
    }

    /**
     * @return internally-cached {@link #containsKey(RequestDirectiveKey)} {@link RequestDirectiveKey#NO_STORE}
     */
    public boolean isNoStore() {
        if (noStore == null) {
            noStore = containsKey(NO_STORE);
        }
        return noStore;
    }

    /**
     * @return internally-cached {@link #parseValueLong(RequestDirectiveKey)} {@link RequestDirectiveKey#MAX_AGE}
     */
    public @Nullable Long getMaxAge() {
        if (maxAge == null) {
            maxAge = parseValueLong(MAX_AGE);
        }
        return maxAge.orElse(null);
    }

    /**
     * @return internally-cached {@link #parseValueLong(RequestDirectiveKey)} {@link RequestDirectiveKey#MAX_STALE}
     */
    public @Nullable Long getMaxStale() {
        if (maxStale == null) {
            maxStale = parseValueLong(MAX_STALE);
        }
        return maxStale.orElse(null);
    }

    /**
     * @return internally-cached {@link #parseValueLong(RequestDirectiveKey)} {@link RequestDirectiveKey#MIN_FRESH}
     */
    public @Nullable Long getMinFresh() {
        if (minFresh == null) {
            minFresh = parseValueLong(MIN_FRESH);
        }
        return minFresh.orElse(null);
    }

    /**
     * @return internally-cached {@link #containsKey(RequestDirectiveKey)} {@link RequestDirectiveKey#NO_TRANSFORM}
     */
    public boolean isNoTransform() {
        if (noTransform == null) {
            noTransform = containsKey(NO_TRANSFORM);
        }
        return noTransform;
    }

    /**
     * @return internally-cached {@link #containsKey(RequestDirectiveKey)} {@link RequestDirectiveKey#ONLY_IF_CACHED}
     */
    public boolean isOnlyIfCached() {
        if (onlyIfCached == null) {
            onlyIfCached = containsKey(ONLY_IF_CACHED);
        }
        return onlyIfCached;
    }

    /**
     * @return internally-cached {@link #containsKey(RequestDirectiveKey)} {@link RequestDirectiveKey#STALE_IF_ERROR}
     */
    public boolean isStaleIfError() {
        if (staleIfError == null) {
            staleIfError = containsKey(STALE_IF_ERROR);
        }
        return staleIfError;
    }

    /**
     * @return this {@link RequestCacheControl} copied into a new {@link Builder} instance
     */
    public Builder toBuilder() {
        return new Builder(directives);
    }

    /**
     * @return internally-cached {@link String} value for request {@link Header#CACHE_CONTROL}
     *
     * @see CacheControlUtil#toString(Map)
     */
    @Override
    public String toString() {
        if (string == null) {
            string = CacheControlUtil.toString(directives);
        }
        return string;
    }
}
