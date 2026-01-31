package net.jacobpeterson.jet.common.http.header;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.ContentSecurityPolicy;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.cookie.Cookie;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link Header} is an enum that represents a standardized HTTP header.
 * <p>
 * <strong>HTTP headers</strong> let the client and the server pass additional information with a message in a request
 * or response. In HTTP/1.X, a header is a case-insensitive name followed by a colon, then optional whitespace which
 * will be ignored, and finally by its value (for example: <code>Allow: POST</code>). In HTTP/2 and above, headers are
 * displayed in lowercase when viewed in developer tools (<code>accept: {@literal *}/{@literal *}</code>), and prefixed
 * with a colon for a special group of
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Messages#pseudo-headers">pseudo-headers</a>
 * (<code>:status: 200</code>). You can find more information on the syntax in each protocol version in the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Messages">HTTP messages</a> page.
 * <p>
 * Custom proprietary headers have historically been used with an <code>X-</code> prefix, but this convention was
 * deprecated in 2012 because of the inconveniences it caused when nonstandard fields became standard in
 * <a href="https://datatracker.ietf.org/doc/html/rfc6648">RFC 6648</a>; others are listed in the
 * <a href="https://www.iana.org/assignments/http-fields/http-fields.xhtml">IANA HTTP Field Name Registry</a>, whose
 * original content was defined in <a href="https://datatracker.ietf.org/doc/html/rfc4229">RFC 4229</a>. The IANA
 * registry lists headers, including
 * <a href="https://github.com/protocol-registries/http-fields?tab=readme-ov-file#choosing-the-right-status">
 * information about their status</a>.
 * <p>
 * Headers can be grouped according to their contexts:
 * <p>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Request_header">Request headers</a>: Contain more
 * information about the resource to be fetched, or about the client requesting the resource.
 * <p>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Response_header">Response headers</a>: Hold additional
 * information about the response, like its location or about the server providing it.
 * <p>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Representation_header">Representation headers</a>:
 * Contain information about the body of the resource, like its
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/MIME_types">MIME type</a>, or encoding/compression
 * applied.
 * <p>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Payload_header">Payload headers</a>: Contain
 * representation-independent information about payload data, including content length and the encoding used for
 * transport.
 * <p>
 * Headers can also be grouped according to how
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Proxy_server">proxies</a> handle them:
 * <p>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers#end-to-end_headers">
 * End-to-end headers</a>: These headers <em>must</em> be transmitted to the final recipient of the message: the server
 * for a request, or the client for a response. Intermediate proxies must retransmit these headers unmodified and caches
 * must store them.
 * <p>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers#hop-by-hop_headers">
 * Hop-by-hop headers</a>: These headers are meaningful only for a single transport-level connection, and <em>must
 * not</em> be retransmitted by proxies or cached. Note that only hop-by-hop headers may be set using the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Connection"><code>Connection</code></a>
 * header.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers">developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum Header {

    /**
     * Defines the authentication method that should be used to access a resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/WWW-Authenticate">
     * developer.mozilla.org</a>
     */
    WWW_AUTHENTICATE("WWW-Authenticate"),

    /**
     * Contains the credentials to authenticate a user-agent with a server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Authorization">
     * developer.mozilla.org</a>
     */
    AUTHORIZATION("Authorization"),

    /**
     * Defines the authentication method that should be used to access a resource behind a proxy server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Proxy-Authenticate">
     * developer.mozilla.org</a>
     */
    PROXY_AUTHENTICATE("Proxy-Authenticate"),

    /**
     * Contains the credentials to authenticate a user agent with a proxy server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Proxy-Authorization">
     * developer.mozilla.org</a>
     */
    PROXY_AUTHORIZATION("Proxy-Authorization"),

    /**
     * The time, in seconds, that the object has been in a proxy cache.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Age">
     * developer.mozilla.org</a>
     */
    AGE("Age"),

    /**
     * Directives for caching mechanisms in both requests and responses.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control">
     * developer.mozilla.org</a>
     */
    CACHE_CONTROL("Cache-Control"),

    /**
     * Clears browsing data (e.g., cookies, storage, cache) associated with the requesting website.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Clear-Site-Data">
     * developer.mozilla.org</a>
     */
    CLEAR_SITE_DATA("Clear-Site-Data"),

    /**
     * The date/time after which the response is considered stale.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expires">
     * developer.mozilla.org</a>
     */
    EXPIRES("Expires"),

    /**
     * Specifies a set of rules that define how a URL's query parameters will affect cache matching. These rules dictate
     * whether the same URL with different URL parameters should be saved as separate browser cache entries.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/No-Vary-Search">
     * developer.mozilla.org</a>
     */
    NO_VARY_SEARCH("No-Vary-Search"),

    /**
     * The last modification date of the resource, used to compare several versions of the same resource. It is less
     * accurate than
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ETag"><code>ETag</code></a>, but
     * easier to calculate in some environments. Conditional requests using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Modified-Since">
     * <code>If-Modified-Since</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Unmodified-Since">
     * <code>If-Unmodified-Since</code></a> use this value to change the behavior of the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Last-Modified">
     * developer.mozilla.org</a>
     */
    LAST_MODIFIED("Last-Modified"),

    /**
     * A unique string identifying the version of the resource. Conditional requests using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Match"><code>If-Match</code></a>
     * and <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-None-Match">
     * <code>If-None-Match</code></a> use this value to change the behavior of the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ETag">
     * developer.mozilla.org</a>
     */
    ETAG("ETag"),

    /**
     * Makes the request conditional, and applies the method only if the stored resource matches one of the given
     * ETags.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Match">
     * developer.mozilla.org</a>
     */
    IF_MATCH("If-Match"),

    /**
     * Makes the request conditional, and applies the method only if the stored resource <em>doesn't</em> match any of
     * the given ETags. This is used to update caches (for safe requests), or to prevent uploading a new resource when
     * one already exists.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-None-Match">
     * developer.mozilla.org</a>
     */
    IF_NONE_MATCH("If-None-Match"),

    /**
     * Makes the request conditional, and expects the resource to be transmitted only if it has been modified after the
     * given date. This is used to transmit data only when the cache is out of date.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Modified-Since">
     * developer.mozilla.org</a>
     */
    IF_MODIFIED_SINCE("If-Modified-Since"),

    /**
     * Makes the request conditional, and expects the resource to be transmitted only if it has not been modified after
     * the given date. This ensures the coherence of a new fragment of a specific range with previous ones, or to
     * implement an optimistic concurrency control system when modifying existing documents.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Unmodified-Since">
     * developer.mozilla.org</a>
     */
    IF_UNMODIFIED_SINCE("If-Unmodified-Since"),

    /**
     * Determines how to match request headers to decide whether a cached response can be used rather than requesting a
     * fresh one from the origin server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Vary">
     * developer.mozilla.org</a>
     */
    VARY("Vary"),

    /**
     * Controls whether the network connection stays open after the current transaction finishes.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Connection">
     * developer.mozilla.org</a>
     */
    CONNECTION("Connection"),

    /**
     * Controls how long a persistent connection should stay open.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Keep-Alive">
     * developer.mozilla.org</a>
     */
    KEEP_ALIVE("Keep-Alive"),

    /**
     * Informs the server about the <a href="https://developer.mozilla.org/en-US/docs/Glossary/MIME_type">types</a> of
     * data that can be sent back.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept">
     * developer.mozilla.org</a>
     */
    ACCEPT("Accept"),

    /**
     * The encoding algorithm, usually a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Compression">compression algorithm</a>, that
     * can be used on the resource sent back.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Encoding">
     * developer.mozilla.org</a>
     */
    ACCEPT_ENCODING("Accept-Encoding"),

    /**
     * Informs the server about the human language the server is expected to send back. This is a hint and is not
     * necessarily under the full control of the user: the server should always pay attention not to override an
     * explicit user choice (like selecting a language from a dropdown).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Language">
     * developer.mozilla.org</a>
     */
    ACCEPT_LANGUAGE("Accept-Language"),

    /**
     * A <em>request content negotiation</em> response header that advertises which
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/MIME_types">media type</a> the server is able
     * to understand in a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PATCH"><code>PATCH</code></a>
     * request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Patch">
     * developer.mozilla.org</a>
     */
    ACCEPT_PATCH("Accept-Patch"),

    /**
     * A <em>request content negotiation</em> response header that advertises which
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/MIME_types">media type</a> the server is able
     * to understand in a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a> request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Post">
     * developer.mozilla.org</a>
     */
    ACCEPT_POST("Accept-Post"),

    /**
     * Indicates expectations that need to be fulfilled by the server to properly handle the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expect">
     * developer.mozilla.org</a>
     */
    EXPECT("Expect"),

    /**
     * When using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/TRACE"><code>TRACE</code></a>,
     * indicates the maximum number of hops the request can do before being reflected to the sender.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Max-Forwards">
     * developer.mozilla.org</a>
     */
    MAX_FORWARDS("Max-Forwards"),

    /**
     * Contains stored <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cookies">HTTP cookies</a>
     * previously sent by the server with the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie">
     * <code>Set-Cookie</code></a> header.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cookie">
     * developer.mozilla.org</a>
     * @see Cookie
     */
    COOKIE("Cookie"),

    /**
     * Send cookies from the server to the user-agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie">
     * developer.mozilla.org</a>
     * @see Cookie
     */
    SET_COOKIE("Set-Cookie"),

    /**
     * Indicates whether the response to the request can be exposed when the credentials flag is true.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Credentials">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),

    /**
     * Used in response to a <a href="https://developer.mozilla.org/en-US/docs/Glossary/Preflight_request">preflight
     * request</a> to indicate which HTTP headers can be used when making the actual request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Headers">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),

    /**
     * Specifies the methods allowed when accessing the resource in response to a preflight request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Methods">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),

    /**
     * Indicates whether the response can be shared.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Origin">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),

    /**
     * Indicates which headers can be exposed as part of the response by listing their names.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Expose-Headers">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_EXPOSE_HEADERS("Access-Control-Expose-Headers"),

    /**
     * Indicates how long the results of a preflight request can be cached.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Max-Age">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age"),

    /**
     * Used when issuing a preflight request to let the server know which HTTP headers will be used when the actual
     * request is made.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Request-Headers">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_REQUEST_HEADERS("Access-Control-Request-Headers"),

    /**
     * Used when issuing a preflight request to let the server know which
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods">HTTP method</a> will be used when
     * the actual request is made.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Request-Method">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_REQUEST_METHOD("Access-Control-Request-Method"),

    /**
     * Indicates where a fetch originates from.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Origin">
     * developer.mozilla.org</a>
     */
    ORIGIN("Origin"),

    /**
     * Specifies origins that are allowed to see values of attributes retrieved via features of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Performance_API/Resource_timing">
     * Resource Timing API</a>, which would otherwise be reported as zero due to cross-origin restrictions.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Timing-Allow-Origin">
     * developer.mozilla.org</a>
     */
    TIMING_ALLOW_ORIGIN("Timing-Allow-Origin"),

    /**
     * Indicates if the resource transmitted should be displayed inline (default behavior without the header), or if it
     * should be handled like a download and the browser should present a "Save As" dialog.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition">
     * developer.mozilla.org</a>
     */
    CONTENT_DISPOSITION("Content-Disposition"),

    /**
     * Provides a <a href="https://developer.mozilla.org/en-US/docs/Glossary/Hash_function">digest</a> of the stream of
     * octets framed in an HTTP message (the message content) dependent on
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">
     * <code>Content-Encoding</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range">
     * <code>Content-Range</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Digest">
     * developer.mozilla.org</a>
     */
    CONTENT_DIGEST("Content-Digest"),

    /**
     * Provides a <a href="https://developer.mozilla.org/en-US/docs/Glossary/Hash_function">digest</a> of the selected
     * representation of the target resource before transmission. Unlike the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Digest">
     * <code>Content-Digest</code></a>, the digest does not consider
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">
     * <code>Content-Encoding</code></a>
     * or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range">
     * <code>Content-Range</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Repr-Digest">
     * developer.mozilla.org</a>
     */
    REPR_DIGEST("Repr-Digest"),

    /**
     * States the wish for a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Digest">
     * <code>Content-Digest</code></a> header. It is the <code>Content-</code> analogue of
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Want-Repr-Digest">
     * <code>Want-Repr-Digest</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Want-Content-Digest">
     * developer.mozilla.org</a>
     */
    WANT_CONTENT_DIGEST("Want-Content-Digest"),

    /**
     * States the wish for a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Repr-Digest">
     * <code>Repr-Digest</code></a> header. It is the <code>Repr-</code> analogue of
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Want-Content-Digest">
     * <code>Want-Content-Digest</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Want-Repr-Digest">
     * developer.mozilla.org</a>
     */
    WANT_REPR_DIGEST("Want-Repr-Digest"),

    /**
     * Ensures that all resources the user agent loads (of a certain type) have
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity">Subresource Integrity</a>
     * guarantees.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Integrity-Policy">
     * developer.mozilla.org</a>
     */
    INTEGRITY_POLICY("Integrity-Policy"),

    /**
     * Reports on resources that the user agent loads that would violate
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity">Subresource Integrity</a>
     * guarantees if the integrity policy were enforced (using the <code>Integrity-Policy</code> header).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Integrity-Policy-Report-Only">
     * developer.mozilla.org</a>
     */
    INTEGRITY_POLICY_REPORT_ONLY("Integrity-Policy-Report-Only"),

    /**
     * The size of the resource, in decimal number of bytes.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Length">
     * developer.mozilla.org</a>
     */
    CONTENT_LENGTH("Content-Length"),

    /**
     * Indicates the media type of the resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
     * developer.mozilla.org</a>
     * @see ContentType
     */
    CONTENT_TYPE("Content-Type"),

    /**
     * Used to specify the compression algorithm.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">
     * developer.mozilla.org</a>
     */
    CONTENT_ENCODING("Content-Encoding"),

    /**
     * Describes the human language(s) intended for the audience, so that it allows a user to differentiate according to
     * the users' own preferred language.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Language">
     * developer.mozilla.org</a>
     */
    CONTENT_LANGUAGE("Content-Language"),

    /**
     * Indicates an alternate location for the returned data.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Location">
     * developer.mozilla.org</a>
     */
    CONTENT_LOCATION("Content-Location"),

    /**
     * Indicates preferences for specific server behaviors during request processing. For example, it can request
     * minimal response content (<code>return=minimal</code>) or asynchronous processing (<code>respond-async</code>).
     * The server processes the request normally if the header is unsupported.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Prefer">
     * developer.mozilla.org</a>
     */
    PREFER("Prefer"),

    /**
     * Informs the client which preferences specified in the <code>Prefer</code> header were applied by the server. It
     * is a response-only header providing transparency about preference handling.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Preference-Applied">
     * developer.mozilla.org</a>
     */
    PREFERENCE_APPLIED("Preference-Applied"),

    /**
     * Contains information from the client-facing side of proxy servers that is altered or lost when a proxy is
     * involved in the path of the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Forwarded">
     * developer.mozilla.org</a>
     */
    FORWARDED("Forwarded"),

    /**
     * Added by proxies, both forward and reverse proxies, and can appear in the request headers and the response
     * headers.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Via">
     * developer.mozilla.org</a>
     */
    VIA("Via"),

    /**
     * Indicates if the server supports range requests, and if so in which unit the range can be expressed.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Ranges">
     * developer.mozilla.org</a>
     */
    ACCEPT_RANGES("Accept-Ranges"),

    /**
     * Indicates the part of a document that the server should return.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range">
     * developer.mozilla.org</a>
     */
    RANGE("Range"),

    /**
     * Creates a conditional range request that is only fulfilled if the given etag or date matches the remote resource.
     * Used to prevent downloading two ranges from incompatible version of the resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Range">
     * developer.mozilla.org</a>
     */
    IF_RANGE("If-Range"),

    /**
     * Indicates where in a full body message a partial message belongs.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range">
     * developer.mozilla.org</a>
     */
    CONTENT_RANGE("Content-Range"),

    /**
     * Indicates the URL to redirect a page to.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Location">
     * developer.mozilla.org</a>
     */
    LOCATION("Location"),

    /**
     * Directs the browser to reload the page or redirect to another. Takes the same value as the <code>meta</code>
     * element with
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/meta/http-equiv">
     * <code>http-equiv="refresh"</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Refresh">
     * developer.mozilla.org</a>
     */
    REFRESH("Refresh"),

    /**
     * Contains an Internet email address for a human user who controls the requesting user agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/From">
     * developer.mozilla.org</a>
     */
    FROM("From"),

    /**
     * Specifies the domain name of the server (for virtual hosting), and (optionally) the TCP port number on which the
     * server is listening.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Host">
     * developer.mozilla.org</a>
     */
    HOST("Host"),

    /**
     * The address of the previous web page from which a link to the currently requested page was followed.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Referer">
     * developer.mozilla.org</a>
     */
    REFERER("Referer"),

    /**
     * Governs which referrer information sent in the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Referer"><code>Referer</code></a>
     * header should be included with requests made.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Referrer-Policy">
     * developer.mozilla.org</a>
     */
    REFERRER_POLICY("Referrer-Policy"),

    /**
     * Contains a characteristic string that allows the network protocol peers to identify the application type,
     * operating system, software vendor or software version of the requesting software user agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/User-Agent">
     * developer.mozilla.org</a>
     */
    USER_AGENT("User-Agent"),

    /**
     * Lists the set of HTTP request methods supported by a resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Allow">
     * developer.mozilla.org</a>
     */
    ALLOW("Allow"),

    /**
     * Contains information about the software used by the origin server to handle the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Server">
     * developer.mozilla.org</a>
     */
    SERVER("Server"),

    /**
     * Allows a server to declare an embedder policy for a given document.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Embedder-Policy">
     * developer.mozilla.org</a>
     */
    CROSS_ORIGIN_EMBEDDER_POLICY("Cross-Origin-Embedder-Policy"),

    /**
     * Prevents other domains from opening/controlling a window.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Opener-Policy">
     * developer.mozilla.org</a>
     */
    CROSS_ORIGIN_OPENER_POLICY("Cross-Origin-Opener-Policy"),

    /**
     * Prevents other domains from reading the response of the resources to which this header is applied. See also
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cross-Origin_Resource_Policy">
     * CORP explainer article</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Resource-Policy">
     * developer.mozilla.org</a>
     */
    CROSS_ORIGIN_RESOURCE_POLICY("Cross-Origin-Resource-Policy"),

    /**
     * Controls resources the user agent is allowed to load for a given page.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy">
     * developer.mozilla.org</a>
     * @see ContentSecurityPolicy
     */
    CONTENT_SECURITY_POLICY("Content-Security-Policy"),

    /**
     * Allows web developers to experiment with policies by monitoring, but not enforcing, their effects. These
     * violation reports consist of <a href="https://developer.mozilla.org/en-US/docs/Glossary/JSON">JSON</a> documents
     * sent via an HTTP <code>POST</code> request to the specified URI.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy-Report-Only">
     * developer.mozilla.org</a>
     */
    CONTENT_SECURITY_POLICY_REPORT_ONLY("Content-Security-Policy-Report-Only"),

    /**
     * Lets sites opt in to reporting and enforcement of
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Certificate_Transparency">Certificate
     * Transparency</a> to detect use of misissued certificates for that site.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expect-CT">
     * developer.mozilla.org</a>
     */
    EXPECT_CT("Expect-CT"),

    /**
     * Provides a mechanism to allow and deny the use of browser features in a website's own frame, and in
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a>s that it embeds.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Permissions-Policy">
     * developer.mozilla.org</a>
     */
    PERMISSIONS_POLICY("Permissions-Policy"),

    /**
     * Response header that allows website owners to specify one or more endpoints used to receive errors such as CSP
     * violation reports,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Opener-Policy">
     * <code>Cross-Origin-Opener-Policy</code></a> reports, or other generic violations.
     * Response header used to specify server endpoints where the browser should send warning and error reports when
     * using the <a href="https://developer.mozilla.org/en-US/docs/Web/API/Reporting_API">Reporting API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Reporting-Endpoints">
     * developer.mozilla.org</a>
     */
    REPORTING_ENDPOINTS("Reporting-Endpoints"),

    /**
     * Force communication using HTTPS instead of HTTP.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Strict-Transport-Security">
     * developer.mozilla.org</a>
     */
    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security"),

    /**
     * Sends a signal to the server expressing the client's preference for an encrypted and authenticated response, and
     * that it can successfully handle the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/upgrade-insecure-requests">
     * <code>upgrade-insecure-requests</code></a> directive.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade-Insecure-Requests">
     * developer.mozilla.org</a>
     */
    UPGRADE_INSECURE_REQUESTS("Upgrade-Insecure-Requests"),

    /**
     * Disables MIME sniffing and forces browser to use the type given in
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
     * <code>Content-Type</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Content-Type-Options">
     * developer.mozilla.org</a>
     */
    X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options"),

    /**
     * Indicates whether a browser should be allowed to render a page in a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/frame">
     * <code>&lt;frame&gt;</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a>,
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/embed">
     * <code>&lt;embed&gt;</code></a> or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/object">
     * <code>&lt;object&gt;</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Frame-Options">
     * developer.mozilla.org</a>
     */
    X_FRAME_OPTIONS("X-Frame-Options"),

    /**
     * A cross-domain policy file may grant clients, such as Adobe Acrobat or Apache Flex (among others), permission to
     * handle data across domains that would otherwise be restricted due to the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Same-origin_policy">Same-Origin Policy</a>. The
     * <code>X-Permitted-Cross-Domain-Policies</code> header overrides such policy files so that clients still block
     * unwanted requests.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Permitted-Cross-Domain-Policies">
     * developer.mozilla.org</a>
     */
    X_PERMITTED_CROSS_DOMAIN_POLICIES("X-Permitted-Cross-Domain-Policies"),

    /**
     * May be set by hosting environments or other frameworks and contains information about them while not providing
     * any usefulness to the application or its visitors. Unset this header to avoid exposing potential
     * vulnerabilities.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Powered-By">
     * developer.mozilla.org</a>
     */
    X_POWERED_BY("X-Powered-By"),

    /**
     * Enables cross-site scripting filtering.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-XSS-Protection">
     * developer.mozilla.org</a>
     */
    X_XSS_PROTECTION("X-XSS-Protection"),

    /**
     * Indicates the relationship between a request initiator's origin and its target's origin. It is a Structured
     * Header whose value is a token with possible values <code>cross-site</code>, <code>same-origin</code>,
     * <code>same-site</code>, and <code>none</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-Site">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_SITE("Sec-Fetch-Site"),

    /**
     * Indicates the request's mode to a server. It is a Structured Header whose value is a token with possible values
     * <code>cors</code>, <code>navigate</code>, <code>no-cors</code>, <code>same-origin</code>, and
     * <code>websocket</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-Mode">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_MODE("Sec-Fetch-Mode"),

    /**
     * Indicates whether or not a navigation request was triggered by user activation. It is a Structured Header whose
     * value is a boolean so possible values are <code>?0</code> for false and <code>?1</code> for true.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-User">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_USER("Sec-Fetch-User"),

    /**
     * Indicates the request's destination. It is a Structured Header whose value is a token with possible values
     * <code>audio</code>, <code>audioworklet</code>, <code>document</code>, <code>embed</code>, <code>empty</code>,
     * <code>font</code>, <code>image</code>, <code>manifest</code>, <code>object</code>, <code>paintworklet</code>,
     * <code>report</code>, <code>script</code>, <code>serviceworker</code>, <code>sharedworker</code>,
     * <code>style</code>, <code>track</code>, <code>video</code>, <code>worker</code>, and <code>xslt</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-Dest">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_DEST("Sec-Fetch-Dest"),

    /**
     * Indicates the purpose of the request, when the purpose is something other than immediate use by the user-agent.
     * The header currently has one possible value, <code>prefetch</code>, which indicates that the resource is being
     * fetched preemptively for a possible future navigation.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Purpose">
     * developer.mozilla.org</a>
     */
    SEC_PURPOSE("Sec-Purpose"),

    /**
     * A request header sent in preemptive request to
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/fetch"><code>fetch()</code></a> a resource
     * during service worker boot. The value, which is set with
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/NavigationPreloadManager/setHeaderValue">
     * <code>NavigationPreloadManager.setHeaderValue()</code></a>, can be used to inform a server that a different
     * resource should be returned than in a normal <code>fetch()</code> operation.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Service-Worker-Navigation-Preload">
     * developer.mozilla.org</a>
     */
    SERVICE_WORKER_NAVIGATION_PRELOAD("Service-Worker-Navigation-Preload"),

    /**
     * Indicates the "storage access status" for the current fetch context, which will be one of <code>none</code>,
     * <code>inactive</code>, or <code>active</code>. The server may respond with <code>Activate-Storage-Access</code>
     * to request that the browser activate an <code>inactive</code> permission and retry the request, or to load a
     * resource with access to its third-party cookies if the status is <code>active</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-Storage-Access">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_STORAGE_ACCESS("Sec-Fetch-Storage-Access"),

    /**
     * Used in response to <code>Sec-Fetch-Storage-Access</code> to indicate that the browser can activate an existing
     * permission for secure access and retry the request with cookies, or load a resource with cookie access if it
     * already has an activated permission.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Activate-Storage-Access">
     * developer.mozilla.org</a>
     */
    ACTIVATE_STORAGE_ACCESS("Activate-Storage-Access"),

    /**
     * Response header used to specify server endpoints where the browser should send warning and error reports when
     * using the <a href="https://developer.mozilla.org/en-US/docs/Web/API/Reporting_API">Reporting API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Report-To">
     * developer.mozilla.org</a>
     */
    REPORT_TO("Report-To"),

    /**
     * Specifies the form of encoding used to safely transfer the resource to the user.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Transfer-Encoding">
     * developer.mozilla.org</a>
     */
    TRANSFER_ENCODING("Transfer-Encoding"),

    /**
     * Specifies the transfer encodings the user agent is willing to accept.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/TE">
     * developer.mozilla.org</a>
     */
    TE("TE"),

    /**
     * Allows the sender to include additional fields at the end of chunked message.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Trailer">
     * developer.mozilla.org</a>
     */
    TRAILER("Trailer"),

    /**
     * Response header that indicates that the server is willing to upgrade to a WebSocket connection.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Accept">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_ACCEPT("Sec-WebSocket-Accept"),

    /**
     * In requests, this header indicates the WebSocket extensions supported by the client in preferred order. In
     * responses, it indicates the extension selected by the server from the client's preferences.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Extensions">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_EXTENSIONS("Sec-WebSocket-Extensions"),

    /**
     * Request header containing a key that verifies that the client explicitly intends to open a
     * <code>WebSocket</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Key">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_KEY("Sec-WebSocket-Key"),

    /**
     * In requests, this header indicates the sub-protocols supported by the client in preferred order. In responses, it
     * indicates the sub-protocol selected by the server from the client's preferences.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Protocol">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_PROTOCOL("Sec-WebSocket-Protocol"),

    /**
     * In requests, this header indicates the version of the WebSocket protocol used by the client. In responses, it is
     * sent only if the requested protocol version is not supported by the server, and lists the versions that the
     * server supports.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Version">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_VERSION("Sec-WebSocket-Version"),

    /**
     * Used to list alternate ways to reach this service.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Alt-Svc">
     * developer.mozilla.org</a>
     */
    ALT_SVC("Alt-Svc"),

    /**
     * Used to identify the alternative service in use.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Alt-Used">
     * developer.mozilla.org</a>
     */
    ALT_USED("Alt-Used"),

    /**
     * Contains the date and time at which the message was originated.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Date">
     * developer.mozilla.org</a>
     */
    DATE("Date"),

    /**
     * This entity-header field provides a means for serializing one or more links in HTTP headers. It is semantically
     * equivalent to the HTML
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/link"><code>&lt;link&gt;</code></a>
     * element.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Link">
     * developer.mozilla.org</a>
     */
    LINK("Link"),

    /**
     * Indicates how long the user agent should wait before making a follow-up request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Retry-After">
     * developer.mozilla.org</a>
     */
    RETRY_AFTER("Retry-After"),

    /**
     * Communicates one or more metrics and descriptions for the given request-response cycle.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Server-Timing">
     * developer.mozilla.org</a>
     */
    SERVER_TIMING("Server-Timing"),

    /**
     * Included in fetches for a service worker's script resource. This header helps administrators log service worker
     * script requests for monitoring purposes.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Service-Worker">
     * developer.mozilla.org</a>
     */
    SERVICE_WORKER("Service-Worker"),

    /**
     * Used to remove the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API/Using_Service_Workers#why_is_my_service_worker_failing_to_register">
     * path restriction</a> by including this header
     * <a href="https://w3c.github.io/ServiceWorker/#service-worker-script-response">in the response of the Service
     * Worker script</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Service-Worker-Allowed">
     * developer.mozilla.org</a>
     */
    SERVICE_WORKER_ALLOWED("Service-Worker-Allowed"),

    /**
     * Links to a <a href="https://developer.mozilla.org/en-US/docs/Glossary/Source_map">source map</a> so that
     * debuggers can step through original source code instead of generated or transformed code.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/SourceMap">
     * developer.mozilla.org</a>
     */
    SOURCEMAP("SourceMap"),

    /**
     * This HTTP/1.1 (only) header can be used to upgrade an already established client/server connection to a different
     * protocol (over the same transport protocol). For example, it can be used by a client to upgrade a connection from
     * HTTP 1.1 to HTTP 2.0, or an HTTP or HTTPS connection into a WebSocket.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade">
     * developer.mozilla.org</a>
     */
    UPGRADE("Upgrade"),

    /**
     * Provides a hint from about the priority of a particular resource request on a particular connection. The value
     * can be sent in a request to indicate the client priority, or in a response if the server chooses to reprioritize
     * the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Priority">
     * developer.mozilla.org</a>
     */
    PRIORITY("Priority"),

    /**
     * Used to indicate that the response corresponding to the current request is eligible to take part in attribution
     * reporting, by registering either an attribution source or trigger.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Attribution-Reporting-Eligible">
     * developer.mozilla.org</a>
     */
    ATTRIBUTION_REPORTING_ELIGIBLE("Attribution-Reporting-Eligible"),

    /**
     * Included as part of a response to a request that included an <code>Attribution-Reporting-Eligible</code> header,
     * this is used to register an attribution source.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Attribution-Reporting-Register-Source">
     * developer.mozilla.org</a>
     */
    ATTRIBUTION_REPORTING_REGISTER_SOURCE("Attribution-Reporting-Register-Source"),

    /**
     * Included as part of a response to a request that included an <code>Attribution-Reporting-Eligible</code> header,
     * this is used to register an attribution trigger.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Attribution-Reporting-Register-Trigger">
     * developer.mozilla.org</a>
     */
    ATTRIBUTION_REPORTING_REGISTER_TRIGGER("Attribution-Reporting-Register-Trigger"),

    /**
     * Servers can advertise support for Client Hints using the <code>Accept-CH</code> header field or an equivalent
     * HTML <code>&lt;meta&gt;</code> element with
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/meta/http-equiv">
     * <code>http-equiv</code></a> attribute.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-CH">
     * developer.mozilla.org</a>
     */
    ACCEPT_CH("Accept-CH"),

    /**
     * Servers use <code>Critical-CH</code> along with
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-CH">
     * <code>Accept-CH</code></a> to specify that accepted client hints are also
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Client_hints#critical_client_hints">critical
     * client hints</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Critical-CH">
     * developer.mozilla.org</a>
     */
    CRITICAL_CH("Critical-CH"),

    /**
     * User agent's branding and version.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA("Sec-CH-UA"),

    /**
     * User agent's underlying platform architecture.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Arch">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_ARCH("Sec-CH-UA-Arch"),

    /**
     * User agent's underlying CPU architecture bitness (for example "64" bit).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Bitness">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_BITNESS("Sec-CH-UA-Bitness"),

    /**
     * User agent's form-factors, describing how the user interacts with the user-agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Form-Factors">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_FORM_FACTORS("Sec-CH-UA-Form-Factors"),

    /**
     * User agent's full version string.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Full-Version">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_FULL_VERSION("Sec-CH-UA-Full-Version"),

    /**
     * Full version for each brand in the user agent's brand list.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Full-Version-List">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_FULL_VERSION_LIST("Sec-CH-UA-Full-Version-List"),

    /**
     * User agent is running on a mobile device or, more generally, prefers a "mobile" user experience.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Mobile">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_MOBILE("Sec-CH-UA-Mobile"),

    /**
     * User agent's device model.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Model">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_MODEL("Sec-CH-UA-Model"),

    /**
     * User agent's underlying operation system/platform.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Platform">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_PLATFORM("Sec-CH-UA-Platform"),

    /**
     * User agent's underlying operation system version.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Platform-Version">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_PLATFORM_VERSION("Sec-CH-UA-Platform-Version"),

    /**
     * Whether or not the user agent binary is running in 32-bit mode on 64-bit Windows.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-WoW64">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_WOW64("Sec-CH-UA-WoW64"),

    /**
     * User's preference of dark or light color scheme.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-Prefers-Color-Scheme">
     * developer.mozilla.org</a>
     */
    SEC_CH_PREFERS_COLOR_SCHEME("Sec-CH-Prefers-Color-Scheme"),

    /**
     * User's preference to see fewer animations and content layout shifts.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-Prefers-Reduced-Motion">
     * developer.mozilla.org</a>
     */
    SEC_CH_PREFERS_REDUCED_MOTION("Sec-CH-Prefers-Reduced-Motion"),

    /**
     * Request header indicates the user agent's preference for reduced transparency.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-Prefers-Reduced-Transparency">
     * developer.mozilla.org</a>
     */
    SEC_CH_PREFERS_REDUCED_TRANSPARENCY("Sec-CH-Prefers-Reduced-Transparency"),

    /**
     * Response header used to confirm the image device to pixel ratio (DPR) in requests where the screen
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/DPR"><code>DPR</code></a> client
     * hint was used to select an image resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-DPR">
     * developer.mozilla.org</a>
     */
    CONTENT_DPR("Content-DPR"),

    /**
     * Approximate amount of available client RAM memory. This is part of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Device_Memory_API">Device Memory API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Device-Memory">
     * developer.mozilla.org</a>
     */
    DEVICE_MEMORY("Device-Memory"),

    /**
     * Request header that provides the client device pixel ratio (the number of physical
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Device_pixel">device pixels</a> for each
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/CSS_pixel">CSS pixel</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/DPR">
     * developer.mozilla.org</a>
     */
    DPR("DPR"),

    /**
     * Request header provides the client's layout viewport width in
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/CSS_pixel">CSS pixels</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Viewport-Width">
     * developer.mozilla.org</a>
     */
    VIEWPORT_WIDTH("Viewport-Width"),

    /**
     * Request header indicates the desired resource width in physical pixels (the intrinsic size of an image).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Width">
     * developer.mozilla.org</a>
     */
    WIDTH("Width"),

    /**
     * Approximate bandwidth of the client's connection to the server, in Mbps. This is part of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Network_Information_API">Network Information API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Downlink">
     * developer.mozilla.org</a>
     */
    DOWNLINK("Downlink"),

    /**
     * The <a href="https://developer.mozilla.org/en-US/docs/Glossary/Effective_connection_type">effective connection
     * type</a> ("network profile") that best matches the connection's latency and bandwidth. This is part of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Network_Information_API">Network Information API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ECT">
     * developer.mozilla.org</a>
     */
    ECT("ECT"),

    /**
     * Application layer round trip time (RTT) in milliseconds, which includes the server processing time. This is part
     * of the <a href="https://developer.mozilla.org/en-US/docs/Web/API/Network_Information_API">Network Information
     * API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/RTT">
     * developer.mozilla.org</a>
     */
    RTT("RTT"),

    /**
     * A string <code>on</code> that indicates the user agent's preference for reduced data usage.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Save-Data">
     * developer.mozilla.org</a>
     */
    SAVE_DATA("Save-Data"),

    /**
     * A browser can use this request header to indicate the best dictionary it has available for the server to use for
     * compression.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Available-Dictionary">
     * developer.mozilla.org</a>
     */
    AVAILABLE_DICTIONARY("Available-Dictionary"),

    /**
     * Used when a browser already has a dictionary available for a resource and the server provided an <code>id</code>
     * for the dictionary in the <code>Use-As-Dictionary</code> header. Requests for resources that can use the
     * dictionary have an <code>Available-Dictionary</code> header and the server-provided dictionary <code>id</code> in
     * the <code>Dictionary-ID</code> header.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Dictionary-ID">
     * developer.mozilla.org</a>
     */
    DICTIONARY_ID("Dictionary-ID"),

    /**
     * Lists the matching criteria that the dictionary can be used for in future requests.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Use-As-Dictionary">
     * developer.mozilla.org</a>
     */
    USE_AS_DICTIONARY("Use-As-Dictionary"),

    /**
     * Request header that indicates the user's tracking preference (Do Not Track). Deprecated in favor of Global
     * Privacy Control (GPC), which is communicated to servers using the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-GPC"><code>Sec-GPC</code></a>
     * header, and accessible to clients via
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Navigator/globalPrivacyControl">
     * <code>navigator.globalPrivacyControl</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/DNT">
     * developer.mozilla.org</a>
     */
    DNT("DNT"),

    /**
     * Response header that indicates the tracking status that applied to the corresponding request. Used in conjunction
     * with DNT.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Tk">
     * developer.mozilla.org</a>
     */
    TK("Tk"),

    /**
     * Indicates whether the user consents to a website or service selling or sharing their personal information with
     * third parties.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-GPC">
     * developer.mozilla.org</a>
     */
    SEC_GPC("Sec-GPC"),

    /**
     * Response header used to indicate that the associated
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Document"><code>Document</code></a> should be placed in
     * an <em>origin-keyed <a href="https://tc39.es/ecma262/#sec-agent-clusters">agent cluster</a></em>. This isolation
     * allows user agents to allocate implementation-specific resources for agent clusters, such as processes or
     * threads, more efficiently.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Origin-Agent-Cluster">
     * developer.mozilla.org</a>
     */
    ORIGIN_AGENT_CLUSTER("Origin-Agent-Cluster"),

    /**
     * Defines a mechanism that enables developers to declare a network error reporting policy.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/NEL">
     * developer.mozilla.org</a>
     */
    NEL("NEL"),

    /**
     * Response header used to mark topics of interest inferred from a calling site's URL as observed in the response to
     * a request generated by a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Topics_API/Using#what_api_features_enable_the_topics_api">
     * feature that enables the Topics API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Observe-Browsing-Topics">
     * developer.mozilla.org</a>
     */
    OBSERVE_BROWSING_TOPICS("Observe-Browsing-Topics"),

    /**
     * Request header that sends the selected topics for the current user along with the associated request, which are
     * used by an ad tech platform to choose a personalized ad to display.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Browsing-Topics">
     * developer.mozilla.org</a>
     */
    SEC_BROWSING_TOPICS("Sec-Browsing-Topics"),

    /**
     * A client can send the
     * <a href="https://wicg.github.io/webpackage/draft-yasskin-http-origin-signed-responses.html#name-the-accept-signature-header">
     * <code>Accept-Signature</code></a> header field to indicate intention to take advantage of any available
     * signatures and to indicate what kinds of signatures it supports.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Signature">
     * developer.mozilla.org</a>
     */
    ACCEPT_SIGNATURE("Accept-Signature"),

    /**
     * Indicates that the request has been conveyed in TLS early data.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Early-Data">
     * developer.mozilla.org</a>
     */
    EARLY_DATA("Early-Data"),

    /**
     * Provides a unique key for <code>POST</code> and <code>PATCH</code> requests, allowing them to be made
     * idempotent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Idempotency-Key">
     * developer.mozilla.org</a>
     */
    IDEMPOTENCY_KEY("Idempotency-Key"),

    /**
     * Response header sent by a federated identity provider (IdP) to set its login status, meaning whether any users
     * are logged into the IdP on the current browser or not. This is stored by the browser and used by the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/FedCM_API">FedCM API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Login">
     * developer.mozilla.org</a>
     */
    SET_LOGIN("Set-Login"),

    /**
     * The
     * <a href="https://wicg.github.io/webpackage/draft-yasskin-http-origin-signed-responses.html#name-the-signature-header">
     * <code>Signature</code></a> header field conveys a list of signatures for an exchange, each one accompanied by
     * information about how to determine the authority of and refresh that signature.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Signature">
     * developer.mozilla.org</a>
     */
    SIGNATURE("Signature"),

    /**
     * The
     * <a href="https://wicg.github.io/webpackage/draft-yasskin-http-origin-signed-responses.html#name-the-signed-headers-header">
     * <code>Signed-Headers</code></a> header field identifies an ordered list of response header fields to include in a
     * signature.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Signed-Headers">
     * developer.mozilla.org</a>
     */
    SIGNED_HEADERS("Signed-Headers"),

    /**
     * Provides a list of URLs pointing to text resources containing
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Speculation_Rules_API">speculation rule</a> JSON
     * definitions. When the response is an HTML document, these rules will be added to the document's speculation rule
     * set.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Speculation-Rules">
     * developer.mozilla.org</a>
     */
    SPECULATION_RULES("Speculation-Rules"),

    /**
     * Contains one or more tag values from the speculation rules that resulted in the speculation so a server can
     * identify which rule(s) caused a speculation and potentially block them.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Speculation-Tags">
     * developer.mozilla.org</a>
     */
    SEC_SPECULATION_TAGS("Sec-Speculation-Tags"),

    /**
     * Set by a navigation target to opt-in to using various higher-risk loading modes. For example, cross-origin,
     * same-site
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Speculation_Rules_API#using_prerendering">
     * prerendering</a> requires a <code>Supports-Loading-Mode</code> value of <code>credentialed-prerender</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Supports-Loading-Mode">
     * developer.mozilla.org</a>
     */
    SUPPORTS_LOADING_MODE("Supports-Loading-Mode"),

    /**
     * Identifies the originating IP addresses of a client connecting to a web server through an HTTP proxy or a load
     * balancer.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Forwarded-For">
     * developer.mozilla.org</a>
     */
    X_FORWARDED_FOR("X-Forwarded-For"),

    /**
     * Identifies the original host requested that a client used to connect to your proxy or load balancer.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Forwarded-Host">
     * developer.mozilla.org</a>
     */
    X_FORWARDED_HOST("X-Forwarded-Host"),

    /**
     * Identifies the protocol (HTTP or HTTPS) that a client used to connect to your proxy or load balancer.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Forwarded-Proto">
     * developer.mozilla.org</a>
     */
    X_FORWARDED_PROTO("X-Forwarded-Proto"),

    /**
     * Controls DNS prefetching, a feature by which browsers proactively perform domain name resolution on both links
     * that the user may choose to follow as well as URLs for items referenced by the document, including images, CSS,
     * JavaScript, and so forth.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-DNS-Prefetch-Control">
     * developer.mozilla.org</a>
     */
    X_DNS_PREFETCH_CONTROL("X-DNS-Prefetch-Control"),

    /**
     * The
     * <a href="https://developers.google.com/search/docs/crawling-indexing/robots-meta-tag">
     * <code>X-Robots-Tag</code></a> HTTP header is used to indicate how a web page is to be indexed within public
     * search engine results. The header is equivalent to
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/meta/name/robots">
     * <code>&lt;meta name="robots"&gt;</code></a> elements.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Robots-Tag">
     * developer.mozilla.org</a>
     */
    X_ROBOTS_TAG("X-Robots-Tag"),

    /**
     * Implementation-specific header that may have various effects anywhere along the request-response chain. Used for
     * backwards compatibility with HTTP/1.0 caches where the <code>Cache-Control</code> header is not yet present.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Pragma">
     * developer.mozilla.org</a>
     */
    PRAGMA("Pragma"),

    /**
     * General warning information about possible problems.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Warning">
     * developer.mozilla.org</a>
     */
    WARNING("Warning"),

    // BEGIN https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers

    /**
     * For Nginx proxy module: set the parameters of response
     * <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_cache_valid">caching</a>.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_EXPIRES("X-Accel-Expires"),

    /**
     * For Nginx proxy module: performs an
     * <a href="https://nginx.org/en/docs/http/ngx_http_core_module.html#internal">internal redirect</a> to the
     * specified URI.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_REDIRECT("X-Accel-Redirect"),

    /**
     * For Nginx proxy module: sets the
     * <a href="https://nginx.org/en/docs/http/ngx_http_core_module.html#limit_rate">rate limit</a> for transmission of
     * a response to a client.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_LIMIT_RATE("X-Accel-Limit-Rate"),

    /**
     * For Nginx proxy module: enables or disables
     * <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_buffering">buffering</a> of a response.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_BUFFERING("X-Accel-Buffering"),

    /**
     * For Nginx proxy module: sets the desired
     * <a href="https://nginx.org/en/docs/http/ngx_http_charset_module.html#charset">charset</a> of a response.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_CHARSET("X-Accel-Charset");

    // END https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link Header}.
     */
    public static final ImmutableMap<String, Header> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link Header} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link Header}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Header forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
