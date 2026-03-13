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
    WWW_AUTHENTICATE(ToString.WWW_AUTHENTICATE),

    /**
     * Contains the credentials to authenticate a user-agent with a server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Authorization">
     * developer.mozilla.org</a>
     */
    AUTHORIZATION(ToString.AUTHORIZATION),

    /**
     * Defines the authentication method that should be used to access a resource behind a proxy server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Proxy-Authenticate">
     * developer.mozilla.org</a>
     */
    PROXY_AUTHENTICATE(ToString.PROXY_AUTHENTICATE),

    /**
     * Contains the credentials to authenticate a user agent with a proxy server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Proxy-Authorization">
     * developer.mozilla.org</a>
     */
    PROXY_AUTHORIZATION(ToString.PROXY_AUTHORIZATION),

    /**
     * The time, in seconds, that the object has been in a proxy cache.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Age">
     * developer.mozilla.org</a>
     */
    AGE(ToString.AGE),

    /**
     * Directives for caching mechanisms in both requests and responses.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cache-Control">
     * developer.mozilla.org</a>
     */
    CACHE_CONTROL(ToString.CACHE_CONTROL),

    /**
     * Clears browsing data (e.g., cookies, storage, cache) associated with the requesting website.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Clear-Site-Data">
     * developer.mozilla.org</a>
     */
    CLEAR_SITE_DATA(ToString.CLEAR_SITE_DATA),

    /**
     * The date/time after which the response is considered stale.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expires">
     * developer.mozilla.org</a>
     */
    EXPIRES(ToString.EXPIRES),

    /**
     * Specifies a set of rules that define how a URL's query parameters will affect cache matching. These rules dictate
     * whether the same URL with different URL parameters should be saved as separate browser cache entries.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/No-Vary-Search">
     * developer.mozilla.org</a>
     */
    NO_VARY_SEARCH(ToString.NO_VARY_SEARCH),

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
    LAST_MODIFIED(ToString.LAST_MODIFIED),

    /**
     * A unique string identifying the version of the resource. Conditional requests using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Match"><code>If-Match</code></a>
     * and <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-None-Match">
     * <code>If-None-Match</code></a> use this value to change the behavior of the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ETag">
     * developer.mozilla.org</a>
     */
    ETAG(ToString.ETAG),

    /**
     * Makes the request conditional, and applies the method only if the stored resource matches one of the given
     * ETags.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Match">
     * developer.mozilla.org</a>
     */
    IF_MATCH(ToString.IF_MATCH),

    /**
     * Makes the request conditional, and applies the method only if the stored resource <em>doesn't</em> match any of
     * the given ETags. This is used to update caches (for safe requests), or to prevent uploading a new resource when
     * one already exists.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-None-Match">
     * developer.mozilla.org</a>
     */
    IF_NONE_MATCH(ToString.IF_NONE_MATCH),

    /**
     * Makes the request conditional, and expects the resource to be transmitted only if it has been modified after the
     * given date. This is used to transmit data only when the cache is out of date.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Modified-Since">
     * developer.mozilla.org</a>
     */
    IF_MODIFIED_SINCE(ToString.IF_MODIFIED_SINCE),

    /**
     * Makes the request conditional, and expects the resource to be transmitted only if it has not been modified after
     * the given date. This ensures the coherence of a new fragment of a specific range with previous ones, or to
     * implement an optimistic concurrency control system when modifying existing documents.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Unmodified-Since">
     * developer.mozilla.org</a>
     */
    IF_UNMODIFIED_SINCE(ToString.IF_UNMODIFIED_SINCE),

    /**
     * Determines how to match request headers to decide whether a cached response can be used rather than requesting a
     * fresh one from the origin server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Vary">
     * developer.mozilla.org</a>
     */
    VARY(ToString.VARY),

    /**
     * Controls whether the network connection stays open after the current transaction finishes.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Connection">
     * developer.mozilla.org</a>
     */
    CONNECTION(ToString.CONNECTION),

    /**
     * Controls how long a persistent connection should stay open.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Keep-Alive">
     * developer.mozilla.org</a>
     */
    KEEP_ALIVE(ToString.KEEP_ALIVE),

    /**
     * Informs the server about the <a href="https://developer.mozilla.org/en-US/docs/Glossary/MIME_type">types</a> of
     * data that can be sent back.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept">
     * developer.mozilla.org</a>
     */
    ACCEPT(ToString.ACCEPT),

    /**
     * The encoding algorithm, usually a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Compression">compression algorithm</a>, that
     * can be used on the resource sent back.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Encoding">
     * developer.mozilla.org</a>
     */
    ACCEPT_ENCODING(ToString.ACCEPT_ENCODING),

    /**
     * Informs the server about the human language the server is expected to send back. This is a hint and is not
     * necessarily under the full control of the user: the server should always pay attention not to override an
     * explicit user choice (like selecting a language from a dropdown).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Language">
     * developer.mozilla.org</a>
     */
    ACCEPT_LANGUAGE(ToString.ACCEPT_LANGUAGE),

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
    ACCEPT_PATCH(ToString.ACCEPT_PATCH),

    /**
     * A <em>request content negotiation</em> response header that advertises which
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/MIME_types">media type</a> the server is able
     * to understand in a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a> request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Post">
     * developer.mozilla.org</a>
     */
    ACCEPT_POST(ToString.ACCEPT_POST),

    /**
     * Indicates expectations that need to be fulfilled by the server to properly handle the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expect">
     * developer.mozilla.org</a>
     */
    EXPECT(ToString.EXPECT),

    /**
     * When using
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/TRACE"><code>TRACE</code></a>,
     * indicates the maximum number of hops the request can do before being reflected to the sender.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Max-Forwards">
     * developer.mozilla.org</a>
     */
    MAX_FORWARDS(ToString.MAX_FORWARDS),

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
    COOKIE(ToString.COOKIE),

    /**
     * Send cookies from the server to the user-agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie">
     * developer.mozilla.org</a>
     * @see Cookie
     */
    SET_COOKIE(ToString.SET_COOKIE),

    /**
     * Indicates whether the response to the request can be exposed when the credentials flag is true.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Credentials">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_CREDENTIALS(ToString.ACCESS_CONTROL_ALLOW_CREDENTIALS),

    /**
     * Used in response to a <a href="https://developer.mozilla.org/en-US/docs/Glossary/Preflight_request">preflight
     * request</a> to indicate which HTTP headers can be used when making the actual request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Headers">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_HEADERS(ToString.ACCESS_CONTROL_ALLOW_HEADERS),

    /**
     * Specifies the methods allowed when accessing the resource in response to a preflight request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Methods">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_METHODS(ToString.ACCESS_CONTROL_ALLOW_METHODS),

    /**
     * Indicates whether the response can be shared.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Allow-Origin">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_ALLOW_ORIGIN(ToString.ACCESS_CONTROL_ALLOW_ORIGIN),

    /**
     * Indicates which headers can be exposed as part of the response by listing their names.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Expose-Headers">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_EXPOSE_HEADERS(ToString.ACCESS_CONTROL_EXPOSE_HEADERS),

    /**
     * Indicates how long the results of a preflight request can be cached.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Max-Age">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_MAX_AGE(ToString.ACCESS_CONTROL_MAX_AGE),

    /**
     * Used when issuing a preflight request to let the server know which HTTP headers will be used when the actual
     * request is made.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Request-Headers">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_REQUEST_HEADERS(ToString.ACCESS_CONTROL_REQUEST_HEADERS),

    /**
     * Used when issuing a preflight request to let the server know which
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods">HTTP method</a> will be used when
     * the actual request is made.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Access-Control-Request-Method">
     * developer.mozilla.org</a>
     */
    ACCESS_CONTROL_REQUEST_METHOD(ToString.ACCESS_CONTROL_REQUEST_METHOD),

    /**
     * Indicates where a fetch originates from.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Origin">
     * developer.mozilla.org</a>
     */
    ORIGIN(ToString.ORIGIN),

    /**
     * Specifies origins that are allowed to see values of attributes retrieved via features of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Performance_API/Resource_timing">
     * Resource Timing API</a>, which would otherwise be reported as zero due to cross-origin restrictions.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Timing-Allow-Origin">
     * developer.mozilla.org</a>
     */
    TIMING_ALLOW_ORIGIN(ToString.TIMING_ALLOW_ORIGIN),

    /**
     * Indicates if the resource transmitted should be displayed inline (default behavior without the header), or if it
     * should be handled like a download and the browser should present a "Save As" dialog.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition">
     * developer.mozilla.org</a>
     */
    CONTENT_DISPOSITION(ToString.CONTENT_DISPOSITION),

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
    CONTENT_DIGEST(ToString.CONTENT_DIGEST),

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
    REPR_DIGEST(ToString.REPR_DIGEST),

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
    WANT_CONTENT_DIGEST(ToString.WANT_CONTENT_DIGEST),

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
    WANT_REPR_DIGEST(ToString.WANT_REPR_DIGEST),

    /**
     * Ensures that all resources the user agent loads (of a certain type) have
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity">Subresource Integrity</a>
     * guarantees.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Integrity-Policy">
     * developer.mozilla.org</a>
     */
    INTEGRITY_POLICY(ToString.INTEGRITY_POLICY),

    /**
     * Reports on resources that the user agent loads that would violate
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity">Subresource Integrity</a>
     * guarantees if the integrity policy were enforced (using the <code>Integrity-Policy</code> header).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Integrity-Policy-Report-Only">
     * developer.mozilla.org</a>
     */
    INTEGRITY_POLICY_REPORT_ONLY(ToString.INTEGRITY_POLICY_REPORT_ONLY),

    /**
     * The size of the resource, in decimal number of bytes.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Length">
     * developer.mozilla.org</a>
     */
    CONTENT_LENGTH(ToString.CONTENT_LENGTH),

    /**
     * Indicates the media type of the resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
     * developer.mozilla.org</a>
     * @see ContentType
     */
    CONTENT_TYPE(ToString.CONTENT_TYPE),

    /**
     * Used to specify the compression algorithm.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">
     * developer.mozilla.org</a>
     */
    CONTENT_ENCODING(ToString.CONTENT_ENCODING),

    /**
     * Describes the human language(s) intended for the audience, so that it allows a user to differentiate according to
     * the users' own preferred language.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Language">
     * developer.mozilla.org</a>
     */
    CONTENT_LANGUAGE(ToString.CONTENT_LANGUAGE),

    /**
     * Indicates an alternate location for the returned data.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Location">
     * developer.mozilla.org</a>
     */
    CONTENT_LOCATION(ToString.CONTENT_LOCATION),

    /**
     * Indicates preferences for specific server behaviors during request processing. For example, it can request
     * minimal response content (<code>return=minimal</code>) or asynchronous processing (<code>respond-async</code>).
     * The server processes the request normally if the header is unsupported.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Prefer">
     * developer.mozilla.org</a>
     */
    PREFER(ToString.PREFER),

    /**
     * Informs the client which preferences specified in the <code>Prefer</code> header were applied by the server. It
     * is a response-only header providing transparency about preference handling.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Preference-Applied">
     * developer.mozilla.org</a>
     */
    PREFERENCE_APPLIED(ToString.PREFERENCE_APPLIED),

    /**
     * Contains information from the client-facing side of proxy servers that is altered or lost when a proxy is
     * involved in the path of the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Forwarded">
     * developer.mozilla.org</a>
     */
    FORWARDED(ToString.FORWARDED),

    /**
     * Added by proxies, both forward and reverse proxies, and can appear in the request headers and the response
     * headers.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Via">
     * developer.mozilla.org</a>
     */
    VIA(ToString.VIA),

    /**
     * Indicates if the server supports range requests, and if so in which unit the range can be expressed.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Ranges">
     * developer.mozilla.org</a>
     */
    ACCEPT_RANGES(ToString.ACCEPT_RANGES),

    /**
     * Indicates the part of a document that the server should return.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range">
     * developer.mozilla.org</a>
     */
    RANGE(ToString.RANGE),

    /**
     * Creates a conditional range request that is only fulfilled if the given etag or date matches the remote resource.
     * Used to prevent downloading two ranges from incompatible version of the resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Range">
     * developer.mozilla.org</a>
     */
    IF_RANGE(ToString.IF_RANGE),

    /**
     * Indicates where in a full body message a partial message belongs.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range">
     * developer.mozilla.org</a>
     */
    CONTENT_RANGE(ToString.CONTENT_RANGE),

    /**
     * Indicates the URL to redirect a page to.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Location">
     * developer.mozilla.org</a>
     */
    LOCATION(ToString.LOCATION),

    /**
     * Directs the browser to reload the page or redirect to another. Takes the same value as the <code>meta</code>
     * element with
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/meta/http-equiv">
     * <code>http-equiv="refresh"</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Refresh">
     * developer.mozilla.org</a>
     */
    REFRESH(ToString.REFRESH),

    /**
     * Contains an Internet email address for a human user who controls the requesting user agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/From">
     * developer.mozilla.org</a>
     */
    FROM(ToString.FROM),

    /**
     * Specifies the domain name of the server (for virtual hosting), and (optionally) the TCP port number on which the
     * server is listening.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Host">
     * developer.mozilla.org</a>
     */
    HOST(ToString.HOST),

    /**
     * The address of the previous web page from which a link to the currently requested page was followed.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Referer">
     * developer.mozilla.org</a>
     */
    REFERER(ToString.REFERER),

    /**
     * Governs which referrer information sent in the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Referer"><code>Referer</code></a>
     * header should be included with requests made.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Referrer-Policy">
     * developer.mozilla.org</a>
     */
    REFERRER_POLICY(ToString.REFERRER_POLICY),

    /**
     * Contains a characteristic string that allows the network protocol peers to identify the application type,
     * operating system, software vendor or software version of the requesting software user agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/User-Agent">
     * developer.mozilla.org</a>
     */
    USER_AGENT(ToString.USER_AGENT),

    /**
     * Lists the set of HTTP request methods supported by a resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Allow">
     * developer.mozilla.org</a>
     */
    ALLOW(ToString.ALLOW),

    /**
     * Contains information about the software used by the origin server to handle the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Server">
     * developer.mozilla.org</a>
     */
    SERVER(ToString.SERVER),

    /**
     * Allows a server to declare an embedder policy for a given document.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Embedder-Policy">
     * developer.mozilla.org</a>
     */
    CROSS_ORIGIN_EMBEDDER_POLICY(ToString.CROSS_ORIGIN_EMBEDDER_POLICY),

    /**
     * Prevents other domains from opening/controlling a window.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Opener-Policy">
     * developer.mozilla.org</a>
     */
    CROSS_ORIGIN_OPENER_POLICY(ToString.CROSS_ORIGIN_OPENER_POLICY),

    /**
     * Prevents other domains from reading the response of the resources to which this header is applied. See also
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Cross-Origin_Resource_Policy">
     * CORP explainer article</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Resource-Policy">
     * developer.mozilla.org</a>
     */
    CROSS_ORIGIN_RESOURCE_POLICY(ToString.CROSS_ORIGIN_RESOURCE_POLICY),

    /**
     * Controls resources the user agent is allowed to load for a given page.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy">
     * developer.mozilla.org</a>
     * @see ContentSecurityPolicy
     */
    CONTENT_SECURITY_POLICY(ToString.CONTENT_SECURITY_POLICY),

    /**
     * Allows web developers to experiment with policies by monitoring, but not enforcing, their effects. These
     * violation reports consist of <a href="https://developer.mozilla.org/en-US/docs/Glossary/JSON">JSON</a> documents
     * sent via an HTTP <code>POST</code> request to the specified URI.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy-Report-Only">
     * developer.mozilla.org</a>
     */
    CONTENT_SECURITY_POLICY_REPORT_ONLY(ToString.CONTENT_SECURITY_POLICY_REPORT_ONLY),

    /**
     * Lets sites opt in to reporting and enforcement of
     * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Certificate_Transparency">Certificate
     * Transparency</a> to detect use of misissued certificates for that site.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expect-CT">
     * developer.mozilla.org</a>
     */
    EXPECT_CT(ToString.EXPECT_CT),

    /**
     * Provides a mechanism to allow and deny the use of browser features in a website's own frame, and in
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/iframe">
     * <code>&lt;iframe&gt;</code></a>s that it embeds.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Permissions-Policy">
     * developer.mozilla.org</a>
     */
    PERMISSIONS_POLICY(ToString.PERMISSIONS_POLICY),

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
    REPORTING_ENDPOINTS(ToString.REPORTING_ENDPOINTS),

    /**
     * Force communication using HTTPS instead of HTTP.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Strict-Transport-Security">
     * developer.mozilla.org</a>
     */
    STRICT_TRANSPORT_SECURITY(ToString.STRICT_TRANSPORT_SECURITY),

    /**
     * Sends a signal to the server expressing the client's preference for an encrypted and authenticated response, and
     * that it can successfully handle the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Security-Policy/upgrade-insecure-requests">
     * <code>upgrade-insecure-requests</code></a> directive.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade-Insecure-Requests">
     * developer.mozilla.org</a>
     */
    UPGRADE_INSECURE_REQUESTS(ToString.UPGRADE_INSECURE_REQUESTS),

    /**
     * Disables MIME sniffing and forces browser to use the type given in
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
     * <code>Content-Type</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Content-Type-Options">
     * developer.mozilla.org</a>
     */
    X_CONTENT_TYPE_OPTIONS(ToString.X_CONTENT_TYPE_OPTIONS),

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
    X_FRAME_OPTIONS(ToString.X_FRAME_OPTIONS),

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
    X_PERMITTED_CROSS_DOMAIN_POLICIES(ToString.X_PERMITTED_CROSS_DOMAIN_POLICIES),

    /**
     * May be set by hosting environments or other frameworks and contains information about them while not providing
     * any usefulness to the application or its visitors. Unset this header to avoid exposing potential
     * vulnerabilities.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Powered-By">
     * developer.mozilla.org</a>
     */
    X_POWERED_BY(ToString.X_POWERED_BY),

    /**
     * Enables cross-site scripting filtering.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-XSS-Protection">
     * developer.mozilla.org</a>
     */
    X_XSS_PROTECTION(ToString.X_XSS_PROTECTION),

    /**
     * Indicates the relationship between a request initiator's origin and its target's origin. It is a Structured
     * Header whose value is a token with possible values <code>cross-site</code>, <code>same-origin</code>,
     * <code>same-site</code>, and <code>none</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-Site">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_SITE(ToString.SEC_FETCH_SITE),

    /**
     * Indicates the request's mode to a server. It is a Structured Header whose value is a token with possible values
     * <code>cors</code>, <code>navigate</code>, <code>no-cors</code>, <code>same-origin</code>, and
     * <code>websocket</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-Mode">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_MODE(ToString.SEC_FETCH_MODE),

    /**
     * Indicates whether or not a navigation request was triggered by user activation. It is a Structured Header whose
     * value is a boolean so possible values are <code>?0</code> for false and <code>?1</code> for true.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-User">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_USER(ToString.SEC_FETCH_USER),

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
    SEC_FETCH_DEST(ToString.SEC_FETCH_DEST),

    /**
     * Indicates the purpose of the request, when the purpose is something other than immediate use by the user-agent.
     * The header currently has one possible value, <code>prefetch</code>, which indicates that the resource is being
     * fetched preemptively for a possible future navigation.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Purpose">
     * developer.mozilla.org</a>
     */
    SEC_PURPOSE(ToString.SEC_PURPOSE),

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
    SERVICE_WORKER_NAVIGATION_PRELOAD(ToString.SERVICE_WORKER_NAVIGATION_PRELOAD),

    /**
     * Indicates the "storage access status" for the current fetch context, which will be one of <code>none</code>,
     * <code>inactive</code>, or <code>active</code>. The server may respond with <code>Activate-Storage-Access</code>
     * to request that the browser activate an <code>inactive</code> permission and retry the request, or to load a
     * resource with access to its third-party cookies if the status is <code>active</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Fetch-Storage-Access">
     * developer.mozilla.org</a>
     */
    SEC_FETCH_STORAGE_ACCESS(ToString.SEC_FETCH_STORAGE_ACCESS),

    /**
     * Used in response to <code>Sec-Fetch-Storage-Access</code> to indicate that the browser can activate an existing
     * permission for secure access and retry the request with cookies, or load a resource with cookie access if it
     * already has an activated permission.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Activate-Storage-Access">
     * developer.mozilla.org</a>
     */
    ACTIVATE_STORAGE_ACCESS(ToString.ACTIVATE_STORAGE_ACCESS),

    /**
     * Response header used to specify server endpoints where the browser should send warning and error reports when
     * using the <a href="https://developer.mozilla.org/en-US/docs/Web/API/Reporting_API">Reporting API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Report-To">
     * developer.mozilla.org</a>
     */
    REPORT_TO(ToString.REPORT_TO),

    /**
     * Specifies the form of encoding used to safely transfer the resource to the user.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Transfer-Encoding">
     * developer.mozilla.org</a>
     */
    TRANSFER_ENCODING(ToString.TRANSFER_ENCODING),

    /**
     * Specifies the transfer encodings the user agent is willing to accept.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/TE">
     * developer.mozilla.org</a>
     */
    TE(ToString.TE),

    /**
     * Allows the sender to include additional fields at the end of chunked message.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Trailer">
     * developer.mozilla.org</a>
     */
    TRAILER(ToString.TRAILER),

    /**
     * Response header that indicates that the server is willing to upgrade to a WebSocket connection.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Accept">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_ACCEPT(ToString.SEC_WEBSOCKET_ACCEPT),

    /**
     * In requests, this header indicates the WebSocket extensions supported by the client in preferred order. In
     * responses, it indicates the extension selected by the server from the client's preferences.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Extensions">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_EXTENSIONS(ToString.SEC_WEBSOCKET_EXTENSIONS),

    /**
     * Request header containing a key that verifies that the client explicitly intends to open a
     * <code>WebSocket</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Key">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_KEY(ToString.SEC_WEBSOCKET_KEY),

    /**
     * In requests, this header indicates the sub-protocols supported by the client in preferred order. In responses, it
     * indicates the sub-protocol selected by the server from the client's preferences.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Protocol">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_PROTOCOL(ToString.SEC_WEBSOCKET_PROTOCOL),

    /**
     * In requests, this header indicates the version of the WebSocket protocol used by the client. In responses, it is
     * sent only if the requested protocol version is not supported by the server, and lists the versions that the
     * server supports.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-WebSocket-Version">
     * developer.mozilla.org</a>
     */
    SEC_WEBSOCKET_VERSION(ToString.SEC_WEBSOCKET_VERSION),

    /**
     * Used to list alternate ways to reach this service.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Alt-Svc">
     * developer.mozilla.org</a>
     */
    ALT_SVC(ToString.ALT_SVC),

    /**
     * Used to identify the alternative service in use.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Alt-Used">
     * developer.mozilla.org</a>
     */
    ALT_USED(ToString.ALT_USED),

    /**
     * Contains the date and time at which the message was originated.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Date">
     * developer.mozilla.org</a>
     */
    DATE(ToString.DATE),

    /**
     * This entity-header field provides a means for serializing one or more links in HTTP headers. It is semantically
     * equivalent to the HTML
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/link"><code>&lt;link&gt;</code></a>
     * element.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Link">
     * developer.mozilla.org</a>
     */
    LINK(ToString.LINK),

    /**
     * Indicates how long the user agent should wait before making a follow-up request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Retry-After">
     * developer.mozilla.org</a>
     */
    RETRY_AFTER(ToString.RETRY_AFTER),

    /**
     * Communicates one or more metrics and descriptions for the given request-response cycle.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Server-Timing">
     * developer.mozilla.org</a>
     */
    SERVER_TIMING(ToString.SERVER_TIMING),

    /**
     * Included in fetches for a service worker's script resource. This header helps administrators log service worker
     * script requests for monitoring purposes.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Service-Worker">
     * developer.mozilla.org</a>
     */
    SERVICE_WORKER(ToString.SERVICE_WORKER),

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
    SERVICE_WORKER_ALLOWED(ToString.SERVICE_WORKER_ALLOWED),

    /**
     * Links to a <a href="https://developer.mozilla.org/en-US/docs/Glossary/Source_map">source map</a> so that
     * debuggers can step through original source code instead of generated or transformed code.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/SourceMap">
     * developer.mozilla.org</a>
     */
    SOURCEMAP(ToString.SOURCEMAP),

    /**
     * This HTTP/1.1 (only) header can be used to upgrade an already established client/server connection to a different
     * protocol (over the same transport protocol). For example, it can be used by a client to upgrade a connection from
     * HTTP 1.1 to HTTP 2.0, or an HTTP or HTTPS connection into a WebSocket.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade">
     * developer.mozilla.org</a>
     */
    UPGRADE(ToString.UPGRADE),

    /**
     * Provides a hint from about the priority of a particular resource request on a particular connection. The value
     * can be sent in a request to indicate the client priority, or in a response if the server chooses to reprioritize
     * the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Priority">
     * developer.mozilla.org</a>
     */
    PRIORITY(ToString.PRIORITY),

    /**
     * Used to indicate that the response corresponding to the current request is eligible to take part in attribution
     * reporting, by registering either an attribution source or trigger.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Attribution-Reporting-Eligible">
     * developer.mozilla.org</a>
     */
    ATTRIBUTION_REPORTING_ELIGIBLE(ToString.ATTRIBUTION_REPORTING_ELIGIBLE),

    /**
     * Included as part of a response to a request that included an <code>Attribution-Reporting-Eligible</code> header,
     * this is used to register an attribution source.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Attribution-Reporting-Register-Source">
     * developer.mozilla.org</a>
     */
    ATTRIBUTION_REPORTING_REGISTER_SOURCE(ToString.ATTRIBUTION_REPORTING_REGISTER_SOURCE),

    /**
     * Included as part of a response to a request that included an <code>Attribution-Reporting-Eligible</code> header,
     * this is used to register an attribution trigger.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Attribution-Reporting-Register-Trigger">
     * developer.mozilla.org</a>
     */
    ATTRIBUTION_REPORTING_REGISTER_TRIGGER(ToString.ATTRIBUTION_REPORTING_REGISTER_TRIGGER),

    /**
     * Servers can advertise support for Client Hints using the <code>Accept-CH</code> header field or an equivalent
     * HTML <code>&lt;meta&gt;</code> element with
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/meta/http-equiv">
     * <code>http-equiv</code></a> attribute.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-CH">
     * developer.mozilla.org</a>
     */
    ACCEPT_CH(ToString.ACCEPT_CH),

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
    CRITICAL_CH(ToString.CRITICAL_CH),

    /**
     * User agent's branding and version.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA(ToString.SEC_CH_UA),

    /**
     * User agent's underlying platform architecture.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Arch">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_ARCH(ToString.SEC_CH_UA_ARCH),

    /**
     * User agent's underlying CPU architecture bitness (for example "64" bit).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Bitness">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_BITNESS(ToString.SEC_CH_UA_BITNESS),

    /**
     * User agent's form-factors, describing how the user interacts with the user-agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Form-Factors">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_FORM_FACTORS(ToString.SEC_CH_UA_FORM_FACTORS),

    /**
     * User agent's full version string.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Full-Version">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_FULL_VERSION(ToString.SEC_CH_UA_FULL_VERSION),

    /**
     * Full version for each brand in the user agent's brand list.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Full-Version-List">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_FULL_VERSION_LIST(ToString.SEC_CH_UA_FULL_VERSION_LIST),

    /**
     * User agent is running on a mobile device or, more generally, prefers a "mobile" user experience.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Mobile">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_MOBILE(ToString.SEC_CH_UA_MOBILE),

    /**
     * User agent's device model.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Model">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_MODEL(ToString.SEC_CH_UA_MODEL),

    /**
     * User agent's underlying operation system/platform.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Platform">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_PLATFORM(ToString.SEC_CH_UA_PLATFORM),

    /**
     * User agent's underlying operation system version.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-Platform-Version">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_PLATFORM_VERSION(ToString.SEC_CH_UA_PLATFORM_VERSION),

    /**
     * Whether or not the user agent binary is running in 32-bit mode on 64-bit Windows.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-UA-WoW64">
     * developer.mozilla.org</a>
     */
    SEC_CH_UA_WOW64(ToString.SEC_CH_UA_WOW64),

    /**
     * User's preference of dark or light color scheme.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-Prefers-Color-Scheme">
     * developer.mozilla.org</a>
     */
    SEC_CH_PREFERS_COLOR_SCHEME(ToString.SEC_CH_PREFERS_COLOR_SCHEME),

    /**
     * User's preference to see fewer animations and content layout shifts.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-Prefers-Reduced-Motion">
     * developer.mozilla.org</a>
     */
    SEC_CH_PREFERS_REDUCED_MOTION(ToString.SEC_CH_PREFERS_REDUCED_MOTION),

    /**
     * Request header indicates the user agent's preference for reduced transparency.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-CH-Prefers-Reduced-Transparency">
     * developer.mozilla.org</a>
     */
    SEC_CH_PREFERS_REDUCED_TRANSPARENCY(ToString.SEC_CH_PREFERS_REDUCED_TRANSPARENCY),

    /**
     * Response header used to confirm the image device to pixel ratio (DPR) in requests where the screen
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/DPR"><code>DPR</code></a> client
     * hint was used to select an image resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-DPR">
     * developer.mozilla.org</a>
     */
    CONTENT_DPR(ToString.CONTENT_DPR),

    /**
     * Approximate amount of available client RAM memory. This is part of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Device_Memory_API">Device Memory API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Device-Memory">
     * developer.mozilla.org</a>
     */
    DEVICE_MEMORY(ToString.DEVICE_MEMORY),

    /**
     * Request header that provides the client device pixel ratio (the number of physical
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Device_pixel">device pixels</a> for each
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/CSS_pixel">CSS pixel</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/DPR">
     * developer.mozilla.org</a>
     */
    DPR(ToString.DPR),

    /**
     * Request header provides the client's layout viewport width in
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/CSS_pixel">CSS pixels</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Viewport-Width">
     * developer.mozilla.org</a>
     */
    VIEWPORT_WIDTH(ToString.VIEWPORT_WIDTH),

    /**
     * Request header indicates the desired resource width in physical pixels (the intrinsic size of an image).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Width">
     * developer.mozilla.org</a>
     */
    WIDTH(ToString.WIDTH),

    /**
     * Approximate bandwidth of the client's connection to the server, in Mbps. This is part of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Network_Information_API">Network Information API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Downlink">
     * developer.mozilla.org</a>
     */
    DOWNLINK(ToString.DOWNLINK),

    /**
     * The <a href="https://developer.mozilla.org/en-US/docs/Glossary/Effective_connection_type">effective connection
     * type</a> ("network profile") that best matches the connection's latency and bandwidth. This is part of the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Network_Information_API">Network Information API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ECT">
     * developer.mozilla.org</a>
     */
    ECT(ToString.ECT),

    /**
     * Application layer round trip time (RTT) in milliseconds, which includes the server processing time. This is part
     * of the <a href="https://developer.mozilla.org/en-US/docs/Web/API/Network_Information_API">Network Information
     * API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/RTT">
     * developer.mozilla.org</a>
     */
    RTT(ToString.RTT),

    /**
     * A string <code>on</code> that indicates the user agent's preference for reduced data usage.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Save-Data">
     * developer.mozilla.org</a>
     */
    SAVE_DATA(ToString.SAVE_DATA),

    /**
     * A browser can use this request header to indicate the best dictionary it has available for the server to use for
     * compression.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Available-Dictionary">
     * developer.mozilla.org</a>
     */
    AVAILABLE_DICTIONARY(ToString.AVAILABLE_DICTIONARY),

    /**
     * Used when a browser already has a dictionary available for a resource and the server provided an <code>id</code>
     * for the dictionary in the <code>Use-As-Dictionary</code> header. Requests for resources that can use the
     * dictionary have an <code>Available-Dictionary</code> header and the server-provided dictionary <code>id</code> in
     * the <code>Dictionary-ID</code> header.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Dictionary-ID">
     * developer.mozilla.org</a>
     */
    DICTIONARY_ID(ToString.DICTIONARY_ID),

    /**
     * Lists the matching criteria that the dictionary can be used for in future requests.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Use-As-Dictionary">
     * developer.mozilla.org</a>
     */
    USE_AS_DICTIONARY(ToString.USE_AS_DICTIONARY),

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
    DNT(ToString.DNT),

    /**
     * Response header that indicates the tracking status that applied to the corresponding request. Used in conjunction
     * with DNT.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Tk">
     * developer.mozilla.org</a>
     */
    TK(ToString.TK),

    /**
     * Indicates whether the user consents to a website or service selling or sharing their personal information with
     * third parties.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-GPC">
     * developer.mozilla.org</a>
     */
    SEC_GPC(ToString.SEC_GPC),

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
    ORIGIN_AGENT_CLUSTER(ToString.ORIGIN_AGENT_CLUSTER),

    /**
     * Defines a mechanism that enables developers to declare a network error reporting policy.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/NEL">
     * developer.mozilla.org</a>
     */
    NEL(ToString.NEL),

    /**
     * Response header used to mark topics of interest inferred from a calling site's URL as observed in the response to
     * a request generated by a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Topics_API/Using#what_api_features_enable_the_topics_api">
     * feature that enables the Topics API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Observe-Browsing-Topics">
     * developer.mozilla.org</a>
     */
    OBSERVE_BROWSING_TOPICS(ToString.OBSERVE_BROWSING_TOPICS),

    /**
     * Request header that sends the selected topics for the current user along with the associated request, which are
     * used by an ad tech platform to choose a personalized ad to display.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Browsing-Topics">
     * developer.mozilla.org</a>
     */
    SEC_BROWSING_TOPICS(ToString.SEC_BROWSING_TOPICS),

    /**
     * A client can send the
     * <a href="https://wicg.github.io/webpackage/draft-yasskin-http-origin-signed-responses.html#name-the-accept-signature-header">
     * <code>Accept-Signature</code></a> header field to indicate intention to take advantage of any available
     * signatures and to indicate what kinds of signatures it supports.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Signature">
     * developer.mozilla.org</a>
     */
    ACCEPT_SIGNATURE(ToString.ACCEPT_SIGNATURE),

    /**
     * Indicates that the request has been conveyed in TLS early data.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Early-Data">
     * developer.mozilla.org</a>
     */
    EARLY_DATA(ToString.EARLY_DATA),

    /**
     * Provides a unique key for <code>POST</code> and <code>PATCH</code> requests, allowing them to be made
     * idempotent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Idempotency-Key">
     * developer.mozilla.org</a>
     */
    IDEMPOTENCY_KEY(ToString.IDEMPOTENCY_KEY),

    /**
     * Response header sent by a federated identity provider (IdP) to set its login status, meaning whether any users
     * are logged into the IdP on the current browser or not. This is stored by the browser and used by the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/FedCM_API">FedCM API</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Login">
     * developer.mozilla.org</a>
     */
    SET_LOGIN(ToString.SET_LOGIN),

    /**
     * The
     * <a href="https://wicg.github.io/webpackage/draft-yasskin-http-origin-signed-responses.html#name-the-signature-header">
     * <code>Signature</code></a> header field conveys a list of signatures for an exchange, each one accompanied by
     * information about how to determine the authority of and refresh that signature.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Signature">
     * developer.mozilla.org</a>
     */
    SIGNATURE(ToString.SIGNATURE),

    /**
     * The
     * <a href="https://wicg.github.io/webpackage/draft-yasskin-http-origin-signed-responses.html#name-the-signed-headers-header">
     * <code>Signed-Headers</code></a> header field identifies an ordered list of response header fields to include in a
     * signature.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Signed-Headers">
     * developer.mozilla.org</a>
     */
    SIGNED_HEADERS(ToString.SIGNED_HEADERS),

    /**
     * Provides a list of URLs pointing to text resources containing
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Speculation_Rules_API">speculation rule</a> JSON
     * definitions. When the response is an HTML document, these rules will be added to the document's speculation rule
     * set.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Speculation-Rules">
     * developer.mozilla.org</a>
     */
    SPECULATION_RULES(ToString.SPECULATION_RULES),

    /**
     * Contains one or more tag values from the speculation rules that resulted in the speculation so a server can
     * identify which rule(s) caused a speculation and potentially block them.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Sec-Speculation-Tags">
     * developer.mozilla.org</a>
     */
    SEC_SPECULATION_TAGS(ToString.SEC_SPECULATION_TAGS),

    /**
     * Set by a navigation target to opt-in to using various higher-risk loading modes. For example, cross-origin,
     * same-site
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Speculation_Rules_API#using_prerendering">
     * prerendering</a> requires a <code>Supports-Loading-Mode</code> value of <code>credentialed-prerender</code>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Supports-Loading-Mode">
     * developer.mozilla.org</a>
     */
    SUPPORTS_LOADING_MODE(ToString.SUPPORTS_LOADING_MODE),

    /**
     * Identifies the originating IP addresses of a client connecting to a web server through an HTTP proxy or a load
     * balancer.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Forwarded-For">
     * developer.mozilla.org</a>
     */
    X_FORWARDED_FOR(ToString.X_FORWARDED_FOR),

    /**
     * Identifies the original host requested that a client used to connect to your proxy or load balancer.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Forwarded-Host">
     * developer.mozilla.org</a>
     */
    X_FORWARDED_HOST(ToString.X_FORWARDED_HOST),

    /**
     * Identifies the protocol (HTTP or HTTPS) that a client used to connect to your proxy or load balancer.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Forwarded-Proto">
     * developer.mozilla.org</a>
     */
    X_FORWARDED_PROTO(ToString.X_FORWARDED_PROTO),

    /**
     * Controls DNS prefetching, a feature by which browsers proactively perform domain name resolution on both links
     * that the user may choose to follow as well as URLs for items referenced by the document, including images, CSS,
     * JavaScript, and so forth.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-DNS-Prefetch-Control">
     * developer.mozilla.org</a>
     */
    X_DNS_PREFETCH_CONTROL(ToString.X_DNS_PREFETCH_CONTROL),

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
    X_ROBOTS_TAG(ToString.X_ROBOTS_TAG),

    /**
     * Implementation-specific header that may have various effects anywhere along the request-response chain. Used for
     * backwards compatibility with HTTP/1.0 caches where the <code>Cache-Control</code> header is not yet present.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Pragma">
     * developer.mozilla.org</a>
     */
    PRAGMA(ToString.PRAGMA),

    /**
     * General warning information about possible problems.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Warning">
     * developer.mozilla.org</a>
     */
    WARNING(ToString.WARNING),

    // BEGIN https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers

    /**
     * For Nginx proxy module: set the parameters of response
     * <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_cache_valid">caching</a>.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_EXPIRES(ToString.X_ACCEL_EXPIRES),

    /**
     * For Nginx proxy module: performs an
     * <a href="https://nginx.org/en/docs/http/ngx_http_core_module.html#internal">internal redirect</a> to the
     * specified URI.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_REDIRECT(ToString.X_ACCEL_REDIRECT),

    /**
     * For Nginx proxy module: sets the
     * <a href="https://nginx.org/en/docs/http/ngx_http_core_module.html#limit_rate">rate limit</a> for transmission of
     * a response to a client.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_LIMIT_RATE(ToString.X_ACCEL_LIMIT_RATE),

    /**
     * For Nginx proxy module: enables or disables
     * <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_buffering">buffering</a> of a response.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_BUFFERING(ToString.X_ACCEL_BUFFERING),

    /**
     * For Nginx proxy module: sets the desired
     * <a href="https://nginx.org/en/docs/http/ngx_http_charset_module.html#charset">charset</a> of a response.
     *
     * @see <a href="https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers">nginx.org</a>
     */
    X_ACCEL_CHARSET(ToString.X_ACCEL_CHARSET);

    // END https://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_ignore_headers

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * {@link ToString} contains the {@link #toString()} constants for all {@link Header} enums so they can be used
     * within annotations.
     */
    public static final class ToString {

        /**
         * @see Header#WWW_AUTHENTICATE
         */
        public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

        /**
         * @see Header#AUTHORIZATION
         */
        public static final String AUTHORIZATION = "Authorization";

        /**
         * @see Header#PROXY_AUTHENTICATE
         */
        public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";

        /**
         * @see Header#PROXY_AUTHORIZATION
         */
        public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

        /**
         * @see Header#AGE
         */
        public static final String AGE = "Age";

        /**
         * @see Header#CACHE_CONTROL
         */
        public static final String CACHE_CONTROL = "Cache-Control";

        /**
         * @see Header#CLEAR_SITE_DATA
         */
        public static final String CLEAR_SITE_DATA = "Clear-Site-Data";

        /**
         * @see Header#EXPIRES
         */
        public static final String EXPIRES = "Expires";

        /**
         * @see Header#NO_VARY_SEARCH
         */
        public static final String NO_VARY_SEARCH = "No-Vary-Search";

        /**
         * @see Header#LAST_MODIFIED
         */
        public static final String LAST_MODIFIED = "Last-Modified";

        /**
         * @see Header#ETAG
         */
        public static final String ETAG = "ETag";

        /**
         * @see Header#IF_MATCH
         */
        public static final String IF_MATCH = "If-Match";

        /**
         * @see Header#IF_NONE_MATCH
         */
        public static final String IF_NONE_MATCH = "If-None-Match";

        /**
         * @see Header#IF_MODIFIED_SINCE
         */
        public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

        /**
         * @see Header#IF_UNMODIFIED_SINCE
         */
        public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

        /**
         * @see Header#VARY
         */
        public static final String VARY = "Vary";

        /**
         * @see Header#CONNECTION
         */
        public static final String CONNECTION = "Connection";

        /**
         * @see Header#KEEP_ALIVE
         */
        public static final String KEEP_ALIVE = "Keep-Alive";

        /**
         * @see Header#ACCEPT
         */
        public static final String ACCEPT = "Accept";

        /**
         * @see Header#ACCEPT_ENCODING
         */
        public static final String ACCEPT_ENCODING = "Accept-Encoding";

        /**
         * @see Header#ACCEPT_LANGUAGE
         */
        public static final String ACCEPT_LANGUAGE = "Accept-Language";

        /**
         * @see Header#ACCEPT_PATCH
         */
        public static final String ACCEPT_PATCH = "Accept-Patch";

        /**
         * @see Header#ACCEPT_POST
         */
        public static final String ACCEPT_POST = "Accept-Post";

        /**
         * @see Header#EXPECT
         */
        public static final String EXPECT = "Expect";

        /**
         * @see Header#MAX_FORWARDS
         */
        public static final String MAX_FORWARDS = "Max-Forwards";

        /**
         * @see Header#COOKIE
         */
        public static final String COOKIE = "Cookie";

        /**
         * @see Header#SET_COOKIE
         */
        public static final String SET_COOKIE = "Set-Cookie";

        /**
         * @see Header#ACCESS_CONTROL_ALLOW_CREDENTIALS
         */
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

        /**
         * @see Header#ACCESS_CONTROL_ALLOW_HEADERS
         */
        public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

        /**
         * @see Header#ACCESS_CONTROL_ALLOW_METHODS
         */
        public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

        /**
         * @see Header#ACCESS_CONTROL_ALLOW_ORIGIN
         */
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

        /**
         * @see Header#ACCESS_CONTROL_EXPOSE_HEADERS
         */
        public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

        /**
         * @see Header#ACCESS_CONTROL_MAX_AGE
         */
        public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

        /**
         * @see Header#ACCESS_CONTROL_REQUEST_HEADERS
         */
        public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";

        /**
         * @see Header#ACCESS_CONTROL_REQUEST_METHOD
         */
        public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

        /**
         * @see Header#ORIGIN
         */
        public static final String ORIGIN = "Origin";

        /**
         * @see Header#TIMING_ALLOW_ORIGIN
         */
        public static final String TIMING_ALLOW_ORIGIN = "Timing-Allow-Origin";

        /**
         * @see Header#CONTENT_DISPOSITION
         */
        public static final String CONTENT_DISPOSITION = "Content-Disposition";

        /**
         * @see Header#CONTENT_DIGEST
         */
        public static final String CONTENT_DIGEST = "Content-Digest";

        /**
         * @see Header#REPR_DIGEST
         */
        public static final String REPR_DIGEST = "Repr-Digest";

        /**
         * @see Header#WANT_CONTENT_DIGEST
         */
        public static final String WANT_CONTENT_DIGEST = "Want-Content-Digest";

        /**
         * @see Header#WANT_REPR_DIGEST
         */
        public static final String WANT_REPR_DIGEST = "Want-Repr-Digest";

        /**
         * @see Header#INTEGRITY_POLICY
         */
        public static final String INTEGRITY_POLICY = "Integrity-Policy";

        /**
         * @see Header#INTEGRITY_POLICY_REPORT_ONLY
         */
        public static final String INTEGRITY_POLICY_REPORT_ONLY = "Integrity-Policy-Report-Only";

        /**
         * @see Header#CONTENT_LENGTH
         */
        public static final String CONTENT_LENGTH = "Content-Length";

        /**
         * @see Header#CONTENT_TYPE
         */
        public static final String CONTENT_TYPE = "Content-Type";

        /**
         * @see Header#CONTENT_ENCODING
         */
        public static final String CONTENT_ENCODING = "Content-Encoding";

        /**
         * @see Header#CONTENT_LANGUAGE
         */
        public static final String CONTENT_LANGUAGE = "Content-Language";

        /**
         * @see Header#CONTENT_LOCATION
         */
        public static final String CONTENT_LOCATION = "Content-Location";

        /**
         * @see Header#PREFER
         */
        public static final String PREFER = "Prefer";

        /**
         * @see Header#PREFERENCE_APPLIED
         */
        public static final String PREFERENCE_APPLIED = "Preference-Applied";

        /**
         * @see Header#FORWARDED
         */
        public static final String FORWARDED = "Forwarded";

        /**
         * @see Header#VIA
         */
        public static final String VIA = "Via";

        /**
         * @see Header#ACCEPT_RANGES
         */
        public static final String ACCEPT_RANGES = "Accept-Ranges";

        /**
         * @see Header#RANGE
         */
        public static final String RANGE = "Range";

        /**
         * @see Header#IF_RANGE
         */
        public static final String IF_RANGE = "If-Range";

        /**
         * @see Header#CONTENT_RANGE
         */
        public static final String CONTENT_RANGE = "Content-Range";

        /**
         * @see Header#LOCATION
         */
        public static final String LOCATION = "Location";

        /**
         * @see Header#REFRESH
         */
        public static final String REFRESH = "Refresh";

        /**
         * @see Header#FROM
         */
        public static final String FROM = "From";

        /**
         * @see Header#HOST
         */
        public static final String HOST = "Host";

        /**
         * @see Header#REFERER
         */
        public static final String REFERER = "Referer";

        /**
         * @see Header#REFERRER_POLICY
         */
        public static final String REFERRER_POLICY = "Referrer-Policy";

        /**
         * @see Header#USER_AGENT
         */
        public static final String USER_AGENT = "User-Agent";

        /**
         * @see Header#ALLOW
         */
        public static final String ALLOW = "Allow";

        /**
         * @see Header#SERVER
         */
        public static final String SERVER = "Server";

        /**
         * @see Header#CROSS_ORIGIN_EMBEDDER_POLICY
         */
        public static final String CROSS_ORIGIN_EMBEDDER_POLICY = "Cross-Origin-Embedder-Policy";

        /**
         * @see Header#CROSS_ORIGIN_OPENER_POLICY
         */
        public static final String CROSS_ORIGIN_OPENER_POLICY = "Cross-Origin-Opener-Policy";

        /**
         * @see Header#CROSS_ORIGIN_RESOURCE_POLICY
         */
        public static final String CROSS_ORIGIN_RESOURCE_POLICY = "Cross-Origin-Resource-Policy";

        /**
         * @see Header#CONTENT_SECURITY_POLICY
         */
        public static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";

        /**
         * @see Header#CONTENT_SECURITY_POLICY_REPORT_ONLY
         */
        public static final String CONTENT_SECURITY_POLICY_REPORT_ONLY = "Content-Security-Policy-Report-Only";

        /**
         * @see Header#EXPECT_CT
         */
        public static final String EXPECT_CT = "Expect-CT";

        /**
         * @see Header#PERMISSIONS_POLICY
         */
        public static final String PERMISSIONS_POLICY = "Permissions-Policy";

        /**
         * @see Header#REPORTING_ENDPOINTS
         */
        public static final String REPORTING_ENDPOINTS = "Reporting-Endpoints";

        /**
         * @see Header#STRICT_TRANSPORT_SECURITY
         */
        public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";

        /**
         * @see Header#UPGRADE_INSECURE_REQUESTS
         */
        public static final String UPGRADE_INSECURE_REQUESTS = "Upgrade-Insecure-Requests";

        /**
         * @see Header#X_CONTENT_TYPE_OPTIONS
         */
        public static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";

        /**
         * @see Header#X_FRAME_OPTIONS
         */
        public static final String X_FRAME_OPTIONS = "X-Frame-Options";

        /**
         * @see Header#X_PERMITTED_CROSS_DOMAIN_POLICIES
         */
        public static final String X_PERMITTED_CROSS_DOMAIN_POLICIES = "X-Permitted-Cross-Domain-Policies";

        /**
         * @see Header#X_POWERED_BY
         */
        public static final String X_POWERED_BY = "X-Powered-By";

        /**
         * @see Header#X_XSS_PROTECTION
         */
        public static final String X_XSS_PROTECTION = "X-XSS-Protection";

        /**
         * @see Header#SEC_FETCH_SITE
         */
        public static final String SEC_FETCH_SITE = "Sec-Fetch-Site";

        /**
         * @see Header#SEC_FETCH_MODE
         */
        public static final String SEC_FETCH_MODE = "Sec-Fetch-Mode";

        /**
         * @see Header#SEC_FETCH_USER
         */
        public static final String SEC_FETCH_USER = "Sec-Fetch-User";

        /**
         * @see Header#SEC_FETCH_DEST
         */
        public static final String SEC_FETCH_DEST = "Sec-Fetch-Dest";

        /**
         * @see Header#SEC_PURPOSE
         */
        public static final String SEC_PURPOSE = "Sec-Purpose";

        /**
         * @see Header#SERVICE_WORKER_NAVIGATION_PRELOAD
         */
        public static final String SERVICE_WORKER_NAVIGATION_PRELOAD = "Service-Worker-Navigation-Preload";

        /**
         * @see Header#SEC_FETCH_STORAGE_ACCESS
         */
        public static final String SEC_FETCH_STORAGE_ACCESS = "Sec-Fetch-Storage-Access";

        /**
         * @see Header#ACTIVATE_STORAGE_ACCESS
         */
        public static final String ACTIVATE_STORAGE_ACCESS = "Activate-Storage-Access";

        /**
         * @see Header#REPORT_TO
         */
        public static final String REPORT_TO = "Report-To";

        /**
         * @see Header#TRANSFER_ENCODING
         */
        public static final String TRANSFER_ENCODING = "Transfer-Encoding";

        /**
         * @see Header#TE
         */
        public static final String TE = "TE";

        /**
         * @see Header#TRAILER
         */
        public static final String TRAILER = "Trailer";

        /**
         * @see Header#SEC_WEBSOCKET_ACCEPT
         */
        public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";

        /**
         * @see Header#SEC_WEBSOCKET_EXTENSIONS
         */
        public static final String SEC_WEBSOCKET_EXTENSIONS = "Sec-WebSocket-Extensions";

        /**
         * @see Header#SEC_WEBSOCKET_KEY
         */
        public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";

        /**
         * @see Header#SEC_WEBSOCKET_PROTOCOL
         */
        public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

        /**
         * @see Header#SEC_WEBSOCKET_VERSION
         */
        public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";

        /**
         * @see Header#ALT_SVC
         */
        public static final String ALT_SVC = "Alt-Svc";

        /**
         * @see Header#ALT_USED
         */
        public static final String ALT_USED = "Alt-Used";

        /**
         * @see Header#DATE
         */
        public static final String DATE = "Date";

        /**
         * @see Header#LINK
         */
        public static final String LINK = "Link";

        /**
         * @see Header#RETRY_AFTER
         */
        public static final String RETRY_AFTER = "Retry-After";

        /**
         * @see Header#SERVER_TIMING
         */
        public static final String SERVER_TIMING = "Server-Timing";

        /**
         * @see Header#SERVICE_WORKER
         */
        public static final String SERVICE_WORKER = "Service-Worker";

        /**
         * @see Header#SERVICE_WORKER_ALLOWED
         */
        public static final String SERVICE_WORKER_ALLOWED = "Service-Worker-Allowed";

        /**
         * @see Header#SOURCEMAP
         */
        public static final String SOURCEMAP = "SourceMap";

        /**
         * @see Header#UPGRADE
         */
        public static final String UPGRADE = "Upgrade";

        /**
         * @see Header#PRIORITY
         */
        public static final String PRIORITY = "Priority";

        /**
         * @see Header#ATTRIBUTION_REPORTING_ELIGIBLE
         */
        public static final String ATTRIBUTION_REPORTING_ELIGIBLE = "Attribution-Reporting-Eligible";

        /**
         * @see Header#ATTRIBUTION_REPORTING_REGISTER_SOURCE
         */
        public static final String ATTRIBUTION_REPORTING_REGISTER_SOURCE = "Attribution-Reporting-Register-Source";

        /**
         * @see Header#ATTRIBUTION_REPORTING_REGISTER_TRIGGER
         */
        public static final String ATTRIBUTION_REPORTING_REGISTER_TRIGGER = "Attribution-Reporting-Register-Trigger";

        /**
         * @see Header#ACCEPT_CH
         */
        public static final String ACCEPT_CH = "Accept-CH";

        /**
         * @see Header#CRITICAL_CH
         */
        public static final String CRITICAL_CH = "Critical-CH";

        /**
         * @see Header#SEC_CH_UA
         */
        public static final String SEC_CH_UA = "Sec-CH-UA";

        /**
         * @see Header#SEC_CH_UA_ARCH
         */
        public static final String SEC_CH_UA_ARCH = "Sec-CH-UA-Arch";

        /**
         * @see Header#SEC_CH_UA_BITNESS
         */
        public static final String SEC_CH_UA_BITNESS = "Sec-CH-UA-Bitness";

        /**
         * @see Header#SEC_CH_UA_FORM_FACTORS
         */
        public static final String SEC_CH_UA_FORM_FACTORS = "Sec-CH-UA-Form-Factors";

        /**
         * @see Header#SEC_CH_UA_FULL_VERSION
         */
        public static final String SEC_CH_UA_FULL_VERSION = "Sec-CH-UA-Full-Version";

        /**
         * @see Header#SEC_CH_UA_FULL_VERSION_LIST
         */
        public static final String SEC_CH_UA_FULL_VERSION_LIST = "Sec-CH-UA-Full-Version-List";

        /**
         * @see Header#SEC_CH_UA_MOBILE
         */
        public static final String SEC_CH_UA_MOBILE = "Sec-CH-UA-Mobile";

        /**
         * @see Header#SEC_CH_UA_MODEL
         */
        public static final String SEC_CH_UA_MODEL = "Sec-CH-UA-Model";

        /**
         * @see Header#SEC_CH_UA_PLATFORM
         */
        public static final String SEC_CH_UA_PLATFORM = "Sec-CH-UA-Platform";

        /**
         * @see Header#SEC_CH_UA_PLATFORM_VERSION
         */
        public static final String SEC_CH_UA_PLATFORM_VERSION = "Sec-CH-UA-Platform-Version";

        /**
         * @see Header#SEC_CH_UA_WOW64
         */
        public static final String SEC_CH_UA_WOW64 = "Sec-CH-UA-WoW64";

        /**
         * @see Header#SEC_CH_PREFERS_COLOR_SCHEME
         */
        public static final String SEC_CH_PREFERS_COLOR_SCHEME = "Sec-CH-Prefers-Color-Scheme";

        /**
         * @see Header#SEC_CH_PREFERS_REDUCED_MOTION
         */
        public static final String SEC_CH_PREFERS_REDUCED_MOTION = "Sec-CH-Prefers-Reduced-Motion";

        /**
         * @see Header#SEC_CH_PREFERS_REDUCED_TRANSPARENCY
         */
        public static final String SEC_CH_PREFERS_REDUCED_TRANSPARENCY = "Sec-CH-Prefers-Reduced-Transparency";

        /**
         * @see Header#CONTENT_DPR
         */
        public static final String CONTENT_DPR = "Content-DPR";

        /**
         * @see Header#DEVICE_MEMORY
         */
        public static final String DEVICE_MEMORY = "Device-Memory";

        /**
         * @see Header#DPR
         */
        public static final String DPR = "DPR";

        /**
         * @see Header#VIEWPORT_WIDTH
         */
        public static final String VIEWPORT_WIDTH = "Viewport-Width";

        /**
         * @see Header#WIDTH
         */
        public static final String WIDTH = "Width";

        /**
         * @see Header#DOWNLINK
         */
        public static final String DOWNLINK = "Downlink";

        /**
         * @see Header#ECT
         */
        public static final String ECT = "ECT";

        /**
         * @see Header#RTT
         */
        public static final String RTT = "RTT";

        /**
         * @see Header#SAVE_DATA
         */
        public static final String SAVE_DATA = "Save-Data";

        /**
         * @see Header#AVAILABLE_DICTIONARY
         */
        public static final String AVAILABLE_DICTIONARY = "Available-Dictionary";

        /**
         * @see Header#DICTIONARY_ID
         */
        public static final String DICTIONARY_ID = "Dictionary-ID";

        /**
         * @see Header#USE_AS_DICTIONARY
         */
        public static final String USE_AS_DICTIONARY = "Use-As-Dictionary";

        /**
         * @see Header#DNT
         */
        public static final String DNT = "DNT";

        /**
         * @see Header#TK
         */
        public static final String TK = "Tk";

        /**
         * @see Header#SEC_GPC
         */
        public static final String SEC_GPC = "Sec-GPC";

        /**
         * @see Header#ORIGIN_AGENT_CLUSTER
         */
        public static final String ORIGIN_AGENT_CLUSTER = "Origin-Agent-Cluster";

        /**
         * @see Header#NEL
         */
        public static final String NEL = "NEL";

        /**
         * @see Header#OBSERVE_BROWSING_TOPICS
         */
        public static final String OBSERVE_BROWSING_TOPICS = "Observe-Browsing-Topics";

        /**
         * @see Header#SEC_BROWSING_TOPICS
         */
        public static final String SEC_BROWSING_TOPICS = "Sec-Browsing-Topics";

        /**
         * @see Header#ACCEPT_SIGNATURE
         */
        public static final String ACCEPT_SIGNATURE = "Accept-Signature";

        /**
         * @see Header#EARLY_DATA
         */
        public static final String EARLY_DATA = "Early-Data";

        /**
         * @see Header#IDEMPOTENCY_KEY
         */
        public static final String IDEMPOTENCY_KEY = "Idempotency-Key";

        /**
         * @see Header#SET_LOGIN
         */
        public static final String SET_LOGIN = "Set-Login";

        /**
         * @see Header#SIGNATURE
         */
        public static final String SIGNATURE = "Signature";

        /**
         * @see Header#SIGNED_HEADERS
         */
        public static final String SIGNED_HEADERS = "Signed-Headers";

        /**
         * @see Header#SPECULATION_RULES
         */
        public static final String SPECULATION_RULES = "Speculation-Rules";

        /**
         * @see Header#SEC_SPECULATION_TAGS
         */
        public static final String SEC_SPECULATION_TAGS = "Sec-Speculation-Tags";

        /**
         * @see Header#SUPPORTS_LOADING_MODE
         */
        public static final String SUPPORTS_LOADING_MODE = "Supports-Loading-Mode";

        /**
         * @see Header#X_FORWARDED_FOR
         */
        public static final String X_FORWARDED_FOR = "X-Forwarded-For";

        /**
         * @see Header#X_FORWARDED_HOST
         */
        public static final String X_FORWARDED_HOST = "X-Forwarded-Host";

        /**
         * @see Header#X_FORWARDED_PROTO
         */
        public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

        /**
         * @see Header#X_DNS_PREFETCH_CONTROL
         */
        public static final String X_DNS_PREFETCH_CONTROL = "X-DNS-Prefetch-Control";

        /**
         * @see Header#X_ROBOTS_TAG
         */
        public static final String X_ROBOTS_TAG = "X-Robots-Tag";

        /**
         * @see Header#PRAGMA
         */
        public static final String PRAGMA = "Pragma";

        /**
         * @see Header#WARNING
         */
        public static final String WARNING = "Warning";

        /**
         * @see Header#X_ACCEL_EXPIRES
         */
        public static final String X_ACCEL_EXPIRES = "X-Accel-Expires";

        /**
         * @see Header#X_ACCEL_REDIRECT
         */
        public static final String X_ACCEL_REDIRECT = "X-Accel-Redirect";

        /**
         * @see Header#X_ACCEL_LIMIT_RATE
         */
        public static final String X_ACCEL_LIMIT_RATE = "X-Accel-Limit-Rate";

        /**
         * @see Header#X_ACCEL_BUFFERING
         */
        public static final String X_ACCEL_BUFFERING = "X-Accel-Buffering";

        /**
         * @see Header#X_ACCEL_CHARSET
         */
        public static final String X_ACCEL_CHARSET = "X-Accel-Charset";
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
