package net.jacobpeterson.jet.server.http.method;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Locale.ROOT;

/**
 * {@link Method} is an enum that represents a standardized HTTP request method.
 * <p>
 * HTTP defines a set of request methods to indicate the purpose of the request and what is expected if the request is
 * successful. Although they can also be nouns, these request methods are sometimes referred to as HTTP verbs. Each
 * request method has its own semantics, but some characteristics are shared across multiple methods, specifically
 * request methods can be {@link #isSafe()}, {@link #isIdempotent()}, or {@link #isCacheable()}.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods>developer.mozilla.org</a>
 */
@NullMarked
public enum Method {

    /**
     * The <code>GET</code> method requests a representation of the specified resource. Requests using <code>GET</code>
     * should only retrieve data and should not contain a request content.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET">
     * developer.mozilla.org</a>
     */
    GET,

    /**
     * The <code>HEAD</code> method asks for a response identical to a <code>GET</code> request, but without a response
     * body.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/HEAD">
     * developer.mozilla.org</a>
     */
    HEAD,

    /**
     * The <code>POST</code> method submits an entity to the specified resource, often causing a change in state or side
     * effects on the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST">
     * developer.mozilla.org</a>
     */
    POST,

    /**
     * The <code>PUT</code> method replaces all current representations of the target resource with the request
     * content.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT">
     * developer.mozilla.org</a>
     */
    PUT,

    /**
     * The <code>DELETE</code> method deletes the specified resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/DELETE">
     * developer.mozilla.org</a>
     */
    DELETE,

    /**
     * The <code>CONNECT</code> method establishes a tunnel to the server identified by the target resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/CONNECT">
     * developer.mozilla.org</a>
     */
    CONNECT,

    /**
     * The <code>OPTIONS</code> method describes the communication options for the target resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/OPTIONS">
     * developer.mozilla.org</a>
     */
    OPTIONS,

    /**
     * The <code>TRACE</code> method performs a message loop-back test along the path to the target resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/TRACE">
     * developer.mozilla.org</a>
     */
    TRACE,

    /**
     * The <code>PATCH</code> method applies partial modifications to a resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PATCH">
     * developer.mozilla.org</a>
     */
    PATCH;

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
     * Gets the {@link Method} for the given <code>name</code>.
     *
     * @param name the case-insensitive HTTP method name {@link String}
     *
     * @return the {@link Method}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Method forName(final String name) {
        try {
            return valueOf(name.toUpperCase(ROOT));
        } catch (final IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }
}
