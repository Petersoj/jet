package net.jacobpeterson.jet.common.http.method;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link Method} is an enum that represents a standardized HTTP request method.
 * <p>
 * HTTP defines a set of request methods to indicate the purpose of the request and what is expected if the request is
 * successful. Although they can also be nouns, these request methods are sometimes referred to as HTTP verbs. Each
 * request method has its own semantics, but some characteristics are shared across multiple methods, specifically
 * request methods can be {@link #isSafe()}, {@link #isIdempotent()}, or {@link #isCacheable()}.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods">developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum Method {

    /**
     * The <code>GET</code> method requests a representation of the specified resource. Requests using <code>GET</code>
     * should only retrieve data and should not contain a request content.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET">
     * developer.mozilla.org</a>
     */
    GET(ToString.GET),

    /**
     * The <code>HEAD</code> method asks for a response identical to a <code>GET</code> request, but without a response
     * body.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/HEAD">
     * developer.mozilla.org</a>
     */
    HEAD(ToString.HEAD),

    /**
     * The <code>POST</code> method submits an entity to the specified resource, often causing a change in state or side
     * effects on the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST">
     * developer.mozilla.org</a>
     */
    POST(ToString.POST),

    /**
     * The <code>PUT</code> method replaces all current representations of the target resource with the request
     * content.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT">
     * developer.mozilla.org</a>
     */
    PUT(ToString.PUT),

    /**
     * The <code>DELETE</code> method deletes the specified resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/DELETE">
     * developer.mozilla.org</a>
     */
    DELETE(ToString.DELETE),

    /**
     * The <code>CONNECT</code> method establishes a tunnel to the server identified by the target resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/CONNECT">
     * developer.mozilla.org</a>
     */
    CONNECT(ToString.CONNECT),

    /**
     * The <code>OPTIONS</code> method describes the communication options for the target resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/OPTIONS">
     * developer.mozilla.org</a>
     */
    OPTIONS(ToString.OPTIONS),

    /**
     * The <code>TRACE</code> method performs a message loop-back test along the path to the target resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/TRACE">
     * developer.mozilla.org</a>
     */
    TRACE(ToString.TRACE),

    /**
     * The <code>PATCH</code> method applies partial modifications to a resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PATCH">
     * developer.mozilla.org</a>
     */
    PATCH(ToString.PATCH);

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * @return <code>true</code> if this {@link Method} is
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Safe/HTTP">safe</a>, <code>false</code> otherwise
     */
    public boolean isSafe() {
        return switch (this) {
            case GET, HEAD, OPTIONS, TRACE -> true;
            case POST, PUT, DELETE, CONNECT, PATCH -> false;
        };
    }

    /**
     * @return <code>true</code> if this {@link Method} is
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Idempotent">idempotent</a>, <code>false</code>
     * otherwise
     */
    public boolean isIdempotent() {
        return switch (this) {
            case GET, HEAD, PUT, DELETE, OPTIONS, TRACE -> true;
            case POST, CONNECT, PATCH -> false;
        };
    }

    /**
     * @return <code>true</code> if this {@link Method} is
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Cacheable">cacheable</a>, <code>false</code>
     * otherwise
     */
    public boolean isCacheable() {
        return switch (this) {
            case GET, HEAD -> true;
            case POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH -> false;
        };
    }

    /**
     * @return <code>true</code> if this {@link Method} is for metadata and should not contain a response body,
     * <code>false</code> otherwise
     */
    public boolean hasNoResponseBody() {
        return switch (this) {
            case HEAD, OPTIONS, TRACE -> true;
            case GET, POST, PUT, DELETE, CONNECT, PATCH -> false;
        };
    }

    /**
     * {@link ToString} contains the {@link #toString()} constants for all {@link Method} enums so they can be used
     * within annotations.
     */
    public static final class ToString {

        /**
         * @see Method#GET
         */
        public static final String GET = "GET";

        /**
         * @see Method#HEAD
         */
        public static final String HEAD = "HEAD";

        /**
         * @see Method#POST
         */
        public static final String POST = "POST";

        /**
         * @see Method#PUT
         */
        public static final String PUT = "PUT";

        /**
         * @see Method#DELETE
         */
        public static final String DELETE = "DELETE";

        /**
         * @see Method#CONNECT
         */
        public static final String CONNECT = "CONNECT";

        /**
         * @see Method#OPTIONS
         */
        public static final String OPTIONS = "OPTIONS";

        /**
         * @see Method#TRACE
         */
        public static final String TRACE = "TRACE";

        /**
         * @see Method#PATCH
         */
        public static final String PATCH = "PATCH";
    }

    /**
     * An {@link ImmutableMap} of uppercased {@link #toString()} mapped to {@link Method}.
     */
    public static final ImmutableMap<String, Method> VALUES_OF_UPPERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toUpperCase(ROOT), identity()));

    /**
     * Gets the {@link Method} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link Method}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Method forString(final String string) {
        return VALUES_OF_UPPERCASED_STRINGS.get(string.toUpperCase(ROOT));
    }
}
