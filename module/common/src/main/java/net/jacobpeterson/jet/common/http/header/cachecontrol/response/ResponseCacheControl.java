package net.jacobpeterson.jet.common.http.header.cachecontrol.response;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.AccessLevel;
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
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.IMMUTABLE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.MAX_AGE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.MUST_REVALIDATE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.MUST_UNDERSTAND;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.NO_TRANSFORM;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.PRIVATE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.PROXY_REVALIDATE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.PUBLIC;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.STALE_IF_ERROR;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.STALE_WHILE_REVALIDATE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseDirectiveKey.S_MAXAGE;

/**
 * {@link ResponseCacheControl} is an immutable class that represents a standardized HTTP response
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
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class ResponseCacheControl {

    /**
     * A {@link ResponseCacheControl} with {@link #isNoCache()} set to <code>true</code>.
     */
    public static final ResponseCacheControl NO_CACHE = builder().noCache().build();

    /**
     * A {@link ResponseCacheControl} with {@link #isNoStore()} set to <code>true</code>.
     */
    public static final ResponseCacheControl NO_STORE = builder().noStore().build();

    /**
     * The number of seconds in one year: 31,536,000
     */
    public static final long SECONDS_IN_ONE_YEAR = 31536000;

    /**
     * A {@link ResponseCacheControl} with {@link #getMaxAge()} set to {@link #SECONDS_IN_ONE_YEAR},
     * {@link #isImmutable()} set to <code>true</code>, and {@link #isPublic()} set to <code>true</code>.
     */
    public static final ResponseCacheControl MAX_AGE_1_YEAR_IMMUTABLE_PUBLIC = builder()
            .maxAge(SECONDS_IN_ONE_YEAR)
            .immutable()
            ._public()
            .build();

    /**
     * A {@link ResponseCacheControl} with {@link #getMaxAge()} set to {@link #SECONDS_IN_ONE_YEAR},
     * {@link #isImmutable()} set to <code>true</code>, and {@link #isPrivate()} set to <code>true</code>.
     */
    public static final ResponseCacheControl MAX_AGE_1_YEAR_IMMUTABLE_PRIVATE = builder()
            .maxAge(SECONDS_IN_ONE_YEAR)
            .immutable()
            ._private()
            .build();

    /**
     * Parses the given response {@link Header#CACHE_CONTROL} value {@link String} into a {@link ResponseCacheControl}.
     *
     * @param responseCacheControl the response {@link Header#CACHE_CONTROL} value {@link String}
     *
     * @return the {@link ResponseCacheControl}
     *
     * @see CacheControlUtil#parse(String)
     * @see #toString()
     */
    public static ResponseCacheControl parse(final String responseCacheControl) {
        return new ResponseCacheControl(CacheControlUtil.parse(responseCacheControl));
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
     * {@link Builder} is a builder class for {@link ResponseCacheControl}.
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
        public Builder putDirectiveValue(final ResponseDirectiveKey responseDirectiveKey, final String value) {
            directives.put(responseDirectiveKey.toString(), value);
            return this;
        }

        /**
         * @return {@link #putDirectiveValue(String, String)} with <code>value</code> set to an empty {@link String}
         */
        public Builder putDirectiveValueless(final String key) {
            return putDirectiveValue(key, "");
        }

        /**
         * @return {@link #putDirectiveValue(ResponseDirectiveKey, String)} with <code>value</code> set to an empty
         * {@link String}
         */
        public Builder putDirectiveValueless(final ResponseDirectiveKey responseDirectiveKey) {
            return putDirectiveValue(responseDirectiveKey, "");
        }

        /**
         * @return {@link #putDirectiveValue(String, String)} with {@link Long#toString()}
         */
        public Builder putDirectiveValueLong(final String key, final long value) {
            return putDirectiveValue(key, Long.toString(value));
        }

        /**
         * @return {@link #putDirectiveValue(ResponseDirectiveKey, String)} with {@link Long#toString()}
         */
        public Builder putDirectiveValueLong(final ResponseDirectiveKey responseDirectiveKey, final long value) {
            return putDirectiveValue(responseDirectiveKey, Long.toString(value));
        }

        /**
         * @return {@link #putDirectiveValueLong(ResponseDirectiveKey, long)}
         * {@link ResponseDirectiveKey#MAX_AGE}
         *
         * @see #getMaxAge()
         */
        public Builder maxAge(final long maxAge) {
            return putDirectiveValueLong(MAX_AGE, maxAge);
        }

        /**
         * @return {@link #putDirectiveValueLong(ResponseDirectiveKey, long)}
         * {@link ResponseDirectiveKey#S_MAXAGE}
         *
         * @see #getSMaxage()
         */
        public Builder sMaxage(final long sMaxage) {
            return putDirectiveValueLong(S_MAXAGE, sMaxage);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#NO_CACHE}
         *
         * @see #isNoCache()
         */
        public Builder noCache() {
            return putDirectiveValueless(ResponseDirectiveKey.NO_CACHE);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#MUST_REVALIDATE}
         *
         * @see #isMustRevalidate()
         */
        public Builder mustRevalidate() {
            return putDirectiveValueless(MUST_REVALIDATE);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#PROXY_REVALIDATE}
         *
         * @see #isProxyRevalidate()
         */
        public Builder proxyRevalidate() {
            return putDirectiveValueless(PROXY_REVALIDATE);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#NO_STORE}
         *
         * @see #isNoStore()
         */
        public Builder noStore() {
            return putDirectiveValueless(ResponseDirectiveKey.NO_STORE);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#PRIVATE}
         *
         * @see #isPrivate()
         */
        public Builder _private() {
            return putDirectiveValueless(PRIVATE);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#PUBLIC}
         *
         * @see #isPublic()
         */
        public Builder _public() {
            return putDirectiveValueless(PUBLIC);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#MUST_UNDERSTAND}
         *
         * @see #isMustUnderstand()
         */
        public Builder mustUnderstand() {
            return putDirectiveValueless(MUST_UNDERSTAND);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#NO_TRANSFORM}
         *
         * @see #isNoTransform()
         */
        public Builder noTransform() {
            return putDirectiveValueless(NO_TRANSFORM);
        }

        /**
         * @return {@link #putDirectiveValueless(ResponseDirectiveKey)}
         * {@link ResponseDirectiveKey#IMMUTABLE}
         *
         * @see #isImmutable()
         */
        public Builder immutable() {
            return putDirectiveValueless(IMMUTABLE);
        }

        /**
         * @return {@link #putDirectiveValueLong(ResponseDirectiveKey, long)}
         * {@link ResponseDirectiveKey#STALE_WHILE_REVALIDATE}
         *
         * @see #getStaleWhileRevalidate()
         */
        public Builder staleWhileRevalidate(final long staleWhileRevalidate) {
            return putDirectiveValueLong(STALE_WHILE_REVALIDATE, staleWhileRevalidate);
        }

        /**
         * @return {@link #putDirectiveValueLong(ResponseDirectiveKey, long)}
         * {@link ResponseDirectiveKey#STALE_IF_ERROR}
         *
         * @see #getStaleIfError()
         */
        public Builder staleIfError(final long staleIfError) {
            return putDirectiveValueLong(STALE_IF_ERROR, staleIfError);
        }

        /**
         * Builds this {@link Builder} into a new {@link ResponseCacheControl} instance.
         *
         * @return the built {@link ResponseCacheControl}
         */
        public ResponseCacheControl build() {
            return new ResponseCacheControl(ImmutableMap.copyOf(directives));
        }
    }

    /**
     * An {@link ImmutableMap} containing all directive key {@link String}s and their value {@link String}. If a
     * directive is present, but has no value in {@link Header#CACHE_CONTROL}, then it maps to an empty {@link String}
     * in this {@link ImmutableMap}.
     */
    private final @EqualsAndHashCode.Include @Getter ImmutableMap<String, String> directives;
    private @LazyInit @Nullable Optional<Long> maxAge;
    private @LazyInit @Nullable Optional<Long> sMaxage;
    private @LazyInit @Nullable Boolean noCache;
    private @LazyInit @Nullable Boolean mustRevalidate;
    private @LazyInit @Nullable Boolean proxyRevalidate;
    private @LazyInit @Nullable Boolean noStore;
    private @LazyInit @Nullable Boolean _private;
    private @LazyInit @Nullable Boolean _public;
    private @LazyInit @Nullable Boolean mustUnderstand;
    private @LazyInit @Nullable Boolean noTransform;
    private @LazyInit @Nullable Boolean immutable;
    private @LazyInit @Nullable Optional<Long> staleWhileRevalidate;
    private @LazyInit @Nullable Optional<Long> staleIfError;
    private @LazyInit @Nullable String string;

    /**
     * @return {@link #containsKey(String)} {@link ResponseDirectiveKey#toString()}
     */
    public boolean containsKey(final ResponseDirectiveKey responseDirectiveKey) {
        return directives.containsKey(responseDirectiveKey.toString());
    }

    /**
     * @return {@link #getDirectives()} {@link ImmutableMap#containsKey(Object)}
     */
    public boolean containsKey(final String responseDirectiveKey) {
        return directives.containsKey(responseDirectiveKey);
    }

    /**
     * @return {@link #parseValueLong(String)} {@link ResponseDirectiveKey#toString()}
     */
    public Optional<Long> parseValueLong(final ResponseDirectiveKey responseDirectiveKey) {
        return parseValueLong(responseDirectiveKey.toString());
    }

    /**
     * @return {@link CacheControlUtil#parseValueLong(Map, String)}
     */
    public Optional<Long> parseValueLong(final String responseDirectiveKey) {
        return CacheControlUtil.parseValueLong(directives, responseDirectiveKey);
    }

    /**
     * @return internally-cached {@link #parseValueLong(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#MAX_AGE}
     */
    public @Nullable Long getMaxAge() {
        if (maxAge == null) {
            maxAge = parseValueLong(MAX_AGE);
        }
        return maxAge.orElse(null);
    }

    /**
     * @return internally-cached {@link #parseValueLong(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#S_MAXAGE}
     */
    public @Nullable Long getSMaxage() {
        if (sMaxage == null) {
            sMaxage = parseValueLong(S_MAXAGE);
        }
        return sMaxage.orElse(null);
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#NO_CACHE}
     */
    public boolean isNoCache() {
        if (noCache == null) {
            noCache = containsKey(ResponseDirectiveKey.NO_CACHE);
        }
        return noCache;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#MUST_REVALIDATE}
     */
    public boolean isMustRevalidate() {
        if (mustRevalidate == null) {
            mustRevalidate = containsKey(MUST_REVALIDATE);
        }
        return mustRevalidate;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#PROXY_REVALIDATE}
     */
    public boolean isProxyRevalidate() {
        if (proxyRevalidate == null) {
            proxyRevalidate = containsKey(PROXY_REVALIDATE);
        }
        return proxyRevalidate;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#NO_STORE}
     */
    public boolean isNoStore() {
        if (noStore == null) {
            noStore = containsKey(ResponseDirectiveKey.NO_STORE);
        }
        return noStore;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#PRIVATE}
     */
    public boolean isPrivate() {
        if (_private == null) {
            _private = containsKey(PRIVATE);
        }
        return _private;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#PUBLIC}
     */
    public boolean isPublic() {
        if (_public == null) {
            _public = containsKey(PUBLIC);
        }
        return _public;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#MUST_UNDERSTAND}
     */
    public boolean isMustUnderstand() {
        if (mustUnderstand == null) {
            mustUnderstand = containsKey(MUST_UNDERSTAND);
        }
        return mustUnderstand;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#NO_TRANSFORM}
     */
    public boolean isNoTransform() {
        if (noTransform == null) {
            noTransform = containsKey(NO_TRANSFORM);
        }
        return noTransform;
    }

    /**
     * @return internally-cached {@link #containsKey(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#IMMUTABLE}
     */
    public boolean isImmutable() {
        if (immutable == null) {
            immutable = containsKey(IMMUTABLE);
        }
        return immutable;
    }

    /**
     * @return internally-cached {@link #parseValueLong(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#STALE_WHILE_REVALIDATE}
     */
    public @Nullable Long getStaleWhileRevalidate() {
        if (staleWhileRevalidate == null) {
            staleWhileRevalidate = parseValueLong(STALE_WHILE_REVALIDATE);
        }
        return staleWhileRevalidate.orElse(null);
    }

    /**
     * @return internally-cached {@link #parseValueLong(ResponseDirectiveKey)}
     * {@link ResponseDirectiveKey#STALE_IF_ERROR}
     */
    public @Nullable Long getStaleIfError() {
        if (staleIfError == null) {
            staleIfError = parseValueLong(STALE_IF_ERROR);
        }
        return staleIfError.orElse(null);
    }

    /**
     * @return this {@link ResponseCacheControl} copied into a new {@link Builder} instance
     */
    public Builder toBuilder() {
        return new Builder(directives);
    }

    /**
     * @return internally-cached {@link String} value for response {@link Header#CACHE_CONTROL}
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
