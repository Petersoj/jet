package net.jacobpeterson.jet.server.http.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

/**
 * {@link Status} is an enum that represents a standardized HTTP response status.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status">developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum Status {

    /**
     * This interim response indicates that the client should continue the request or ignore the response if the request
     * is already finished.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/100">developer.mozilla.org</a>
     */
    CONTINUE_100(100, "Continue"),

    /**
     * This code is sent in response to an
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade"><code>Upgrade</code></a>
     * request header from the client and indicates the protocol the server is switching to.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/101">developer.mozilla.org</a>
     */
    SWITCHING_PROTOCOLS_101(101, "Switching Protocols"),

    /**
     * This code was used in <a href="https://developer.mozilla.org/en-US/docs/Glossary/WebDAV">WebDAV</a> contexts to
     * indicate that a request has been received by the server, but no status was available at the time of the
     * response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/102">developer.mozilla.org</a>
     */
    PROCESSING_102(102, "Processing"),

    /**
     * This status code is primarily intended to be used with the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Link"><code>Link</code></a> header,
     * letting the user agent start
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Attributes/rel/preload">preloading</a>
     * resources while the server prepares a response or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Attributes/rel/preconnect">preconnect</a>
     * to an origin from which the page will need resources.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/103">developer.mozilla.org</a>
     */
    EARLY_HINTS_103(103, "Early Hints"),

    /**
     * The request succeeded. The result and meaning of "success" depends on the HTTP method:</p>
     * <ul>
     * <li>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a>:
     * The resource has been fetched and transmitted in the message body.
     * </li>
     * <li>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/HEAD"><code>HEAD</code></a>:
     * Representation headers are included in the response without any message body.
     * </li>
     * <li>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT"><code>PUT</code></a> or
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a>:
     * The resource describing the result of the action is transmitted in the message body.
     * </li>
     * <li>
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/TRACE"><code>TRACE</code></a>:
     * The message body contains the request as received by the server.
     * </li>
     * </ul>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/200">developer.mozilla.org</a>
     */
    OK_200(200, "OK"),

    /**
     * The request succeeded, and a new resource was created as a result. This is typically the response sent after
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a>
     * requests, or some <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT">
     * <code>PUT</code></a> requests.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/201">developer.mozilla.org</a>
     */
    CREATED_201(201, "Created"),

    /**
     * The request has been received but not yet acted upon. It is noncommittal, since there is no way in HTTP to later
     * send an asynchronous response indicating the outcome of the request. It is intended for cases where another
     * process or server handles the request, or for batch processing.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/202">developer.mozilla.org</a>
     */
    ACCEPTED_202(202, "Accepted"),

    /**
     * This response code means the returned metadata is not exactly the same as is available from the origin server,
     * but is collected from a local or a third-party copy. This is mostly used for mirrors or backups of another
     * resource. Except for that specific case, the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/200"><code>200 OK</code></a>
     * response is preferred to this status.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/203">developer.mozilla.org</a>
     */
    NON_AUTHORITATIVE_INFORMATION_203(203, "Non-Authoritative Information"),

    /**
     * There is no content to send for this request, but the headers are useful. The user agent may update its cached
     * headers for this resource with the new ones.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/204">developer.mozilla.org</a>
     */
    NO_CONTENT_204(204, "No Content"),

    /**
     * Tells the user agent to reset the document which sent this request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/205">developer.mozilla.org</a>
     */
    RESET_CONTENT_205(205, "Reset Content"),

    /**
     * This response code is used in response to a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Range_requests">range request</a> when the
     * client has requested a part or parts of a resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/206">developer.mozilla.org</a>
     */
    PARTIAL_CONTENT_206(206, "Partial Content"),

    /**
     * Conveys information about multiple resources, for situations where multiple status codes might be appropriate.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/207">developer.mozilla.org</a>
     */
    MULTI_STATUS_207(207, "Multi-Status"),

    /**
     * Used inside a <code>&lt;dav:propstat&gt;</code> response element to avoid repeatedly enumerating the internal
     * members of multiple bindings to the same collection.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/208">developer.mozilla.org</a>
     */
    ALREADY_REPORTED_208(208, "Already Reported"),

    /**
     * The server has fulfilled a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a> request
     * for the resource, and the response is a representation of the result of one or more instance-manipulations
     * applied to the current instance.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/226">developer.mozilla.org</a>
     */
    IM_USED_226(226, "IM Used"),

    /**
     * In
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Content_negotiation#agent-driven_negotiation">
     * agent-driven content negotiation</a>, the request has more than one possible response and the user agent or user
     * should choose one of them. There is no standardized way for clients to automatically choose one of the responses,
     * so this is rarely used.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/300">developer.mozilla.org</a>
     */
    MULTIPLE_CHOICES_300(300, "Multiple Choices"),

    /**
     * The URL of the requested resource has been changed permanently. The new URL is given in the response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/301">developer.mozilla.org</a>
     */
    MOVED_PERMANENTLY_301(301, "Moved Permanently"),

    /**
     * This response code means that the URI of requested resource has been changed <em>temporarily</em>. Further
     * changes in the URI might be made in the future, so the same URI should be used by the client in future requests.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/302">developer.mozilla.org</a>
     */
    FOUND_302(302, "Found"),

    /**
     * The server sent this response to direct the client to get the requested resource at another URI with a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a> request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/303">developer.mozilla.org</a>
     */
    SEE_OTHER_303(303, "See Other"),

    /**
     * This is used for caching purposes. It tells the client that the response has not been modified, so the client can
     * continue to use the same <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching">cached</a>
     * version of the response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/304">developer.mozilla.org</a>
     */
    NOT_MODIFIED_304(304, "Not Modified"),

    /**
     * Defined in a previous version of the HTTP specification to indicate that a requested response must be accessed by
     * a proxy. It has been deprecated due to security concerns regarding in-band configuration of a proxy.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/305">developer.mozilla.org</a>
     */
    USE_PROXY_305(305, "Use Proxy"),

    /**
     * This response code is no longer used; but is reserved. It was used in a previous version of the HTTP/1.1
     * specification.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/306">developer.mozilla.org</a>
     */
    UNUSED_306(306, "Unused"),

    /**
     * This response code is no longer used; but is reserved. It was used in a previous version of the HTTP/1.1
     * specification.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/307">developer.mozilla.org</a>
     */
    TEMPORARY_REDIRECT_307(307, "Temporary Redirect"),

    /**
     * The server sends this response to direct the client to get the requested resource at another URI with the same
     * method that was used in the prior request. This has the same semantics as the <code>302 Found</code> response
     * code, with the exception that the user agent <em>must not</em> change the HTTP method used: if a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a> was
     * used in the first request, a <code>POST</code> must be used in the redirected request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/308">developer.mozilla.org</a>
     */
    PERMANENT_REDIRECT_308(308, "Permanent Redirect"),

    /**
     * The server cannot or will not process the request due to something that is perceived to be a client error (e.g.,
     * malformed request syntax, invalid request message framing, or deceptive request routing).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/400">developer.mozilla.org</a>
     */
    BAD_REQUEST_400(400, "Bad Request"),

    /**
     * Although the HTTP standard specifies "unauthorized", semantically this response means "unauthenticated". That is,
     * the client must authenticate itself to get the requested response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/401">developer.mozilla.org</a>
     */
    UNAUTHORIZED_401(401, "Unauthorized"),

    /**
     * The initial purpose of this code was for digital payment systems, however this status code is rarely used and no
     * standard convention exists.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/402">developer.mozilla.org</a>
     */
    PAYMENT_REQUIRED_402(402, "Payment Required"),

    /**
     * The client does not have access rights to the content; that is, it is unauthorized, so the server is refusing to
     * give the requested resource. Unlike <code>401 Unauthorized</code>, the client's identity is known to the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/403">developer.mozilla.org</a>
     */
    FORBIDDEN_403(403, "Forbidden"),

    /**
     * The server cannot find the requested resource. In the browser, this means the URL is not recognized. In an API,
     * this can also mean that the endpoint is valid but the resource itself does not exist. Servers may also send this
     * response instead of <code>403 Forbidden</code> to hide the existence of a resource from an unauthorized client.
     * This response code is probably the most well known due to its frequent occurrence on the web.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/404">developer.mozilla.org</a>
     */
    NOT_FOUND_404(404, "Not Found"),

    /**
     * The <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods">request method</a> is known by
     * the server but is not supported by the target resource. For example, an API may not allow <code>DELETE</code> on
     * a resource, or the <code>TRACE</code> method entirely.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/405">developer.mozilla.org</a>
     */
    METHOD_NOT_ALLOWED_405(405, "Method Not Allowed"),

    /**
     * This response is sent when the web server, after performing <a
     * href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Content_negotiation#server
     * -driven_content_negotiation"> server-driven content negotiation</a>, doesn't find any content that conforms to
     * the criteria given by the user agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/406">developer.mozilla.org</a>
     */
    NOT_ACCEPTABLE_406(406, "Not Acceptable"),

    /**
     * This is similar to <code>401 Unauthorized</code> but authentication is needed to be done by a proxy.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/407">developer.mozilla.org</a>
     */
    PROXY_AUTHENTICATION_REQUIRED_407(407, "Proxy Authentication Required"),

    /**
     * This response is sent on an idle connection by some servers, even without any previous request by the client. It
     * means that the server would like to shut down this unused connection. This response is used much more since some
     * browsers use HTTP pre-connection mechanisms to speed up browsing. Some servers may shut down a connection without
     * sending this message.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/408">developer.mozilla.org</a>
     */
    REQUEST_TIMEOUT_408(408, "Request Timeout"),

    /**
     * This response is sent when a request conflicts with the current state of the server. In
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/WebDAV">WebDAV</a> remote web authoring,
     * <code>409</code> responses are errors sent to the client so that a user might be able to resolve a conflict and
     * resubmit the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/409">developer.mozilla.org</a>
     */
    CONFLICT_409(409, "Conflict"),

    /**
     * This response is sent when the requested content has been permanently deleted from server, with no forwarding
     * address. Clients are expected to remove their caches and links to the resource. The HTTP specification intends
     * this status code to be used for "limited-time, promotional services". APIs should not feel compelled to indicate
     * resources that have been deleted with this status code.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/410">developer.mozilla.org</a>
     */
    GONE_410(410, "Gone"),

    /**
     * Server rejected the request because the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Length">
     * <code>Content-Length</code></a> header field is not defined and the server requires it.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/411">developer.mozilla.org</a>
     */
    LENGTH_REQUIRED_411(411, "Length Required"),

    /**
     * In <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Conditional_requests">conditional
     * requests</a>, the client has indicated preconditions in its headers which the server does not meet.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/412">developer.mozilla.org</a>
     */
    PRECONDITION_FAILED_412(412, "Precondition Failed"),

    /**
     * The request body is larger than limits defined by server. The server might close the connection or return a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Retry-After">
     * <code>Retry-After</code></a> header field.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/413">developer.mozilla.org</a>
     */
    CONTENT_TOO_LARGE_413(413, "Content Too Large"),

    /**
     * The URI requested by the client is longer than the server is willing to interpret.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/414">developer.mozilla.org</a>
     */
    URI_TOO_LONG_414(414, "URI Too Long"),

    /**
     * The media format of the requested data is not supported by the server, so the server is rejecting the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/415">developer.mozilla.org</a>
     */
    UNSUPPORTED_MEDIA_TYPE_415(415, "Unsupported Media Type"),

    /**
     * The <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Range_requests">ranges</a> specified by
     * the
     * <code>Range</code> header field in the request cannot be fulfilled. It's possible that the range is outside the
     * size of the target resource's data.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/416">developer.mozilla.org</a>
     */
    RANGE_NOT_SATISFIABLE_416(416, "Range Not Satisfiable"),

    /**
     * This response code means the expectation indicated by the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expect"><code>Expect</code></a>
     * request header field cannot be met by the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/417">developer.mozilla.org</a>
     */
    EXPECTATION_FAILED_417(417, "Expectation Failed"),

    /**
     * The server refuses the attempt to brew coffee with a teapot.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/418">developer.mozilla.org</a>
     */
    IM_A_TEAPOT_418(418, "I'm a teapot"),

    /**
     * The request was directed at a server that is not able to produce a response. This can be sent by a server that is
     * not configured to produce responses for the combination of scheme and authority that are included in the request
     * URI.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/421">developer.mozilla.org</a>
     */
    MISDIRECTED_REQUEST_421(421, "Misdirected Request"),

    /**
     * The request was well-formed but was unable to be followed due to semantic errors.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/422">developer.mozilla.org</a>
     */
    UNPROCESSABLE_CONTENT_422(422, "Unprocessable Content"),

    /**
     * The resource that is being accessed is locked.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/423">developer.mozilla.org</a>
     */
    LOCKED_423(423, "Locked"),

    /**
     * The request failed due to failure of a previous request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/424">developer.mozilla.org</a>
     */
    FAILED_DEPENDENCY_424(424, "Failed Dependency"),

    /**
     * Indicates that the server is unwilling to risk processing a request that might be replayed.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/425">developer.mozilla.org</a>
     */
    TOO_EARLY_425(425, "Too Early"),

    /**
     * The server refuses to perform the request using the current protocol but might be willing to do so after the
     * client upgrades to a different protocol. The server sends an
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade"><code>Upgrade</code></a>
     * header in a 426 response to indicate the required protocol(s).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/426">developer.mozilla.org</a>
     */
    UPGRADE_REQUIRED_426(426, "Upgrade Required"),

    /**
     * The origin server requires the request to be
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Conditional_requests">conditional</a>. This
     * response is intended to prevent the 'lost update' problem, where a client
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a>s a
     * resource's state, modifies it and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT"><code>PUT</code></a>s it back
     * to the server, when meanwhile a third party has modified the state on the server, leading to a conflict.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/428">developer.mozilla.org</a>
     */
    PRECONDITION_REQUIRED_428(428, "Precondition Required"),

    /**
     * The user has sent too many requests in a given amount of time (<a
     * href="https://developer.mozilla.org/en-US/docs/Glossary/Rate_limit">rate limiting</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/429">developer.mozilla.org</a>
     */
    TOO_MANY_REQUESTS_429(429, "Too Many Requests"),

    /**
     * The server is unwilling to process the request because its header fields are too large. The request may be
     * resubmitted after reducing the size of the request header fields.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/431">developer.mozilla.org</a>
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE_431(431, "Request Header Fields Too Large"),

    /**
     * The user agent requested a resource that cannot legally be provided, such as a web page censored by a
     * government.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/451">developer.mozilla.org</a>
     */
    UNAVAILABLE_FOR_LEGAL_REASONS_451(451, "Unavailable For Legal Reasons"),

    /**
     * The server has encountered a situation it does not know how to handle. This error is generic, indicating that the
     * server cannot find a more appropriate <code>5XX</code> status code to respond with.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/500">developer.mozilla.org</a>
     */
    INTERNAL_SERVER_ERROR_500(500, "Internal Server Error"),

    /**
     * The request method is not supported by the server and cannot be handled. The only methods that servers are
     * required to support (and therefore that must not return this code) are
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/HEAD"><code>HEAD</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/501">developer.mozilla.org</a>
     */
    NOT_IMPLEMENTED_501(501, "Not Implemented"),

    /**
     * This error response means that the server, while working as a gateway to get a response needed to handle the
     * request, got an invalid response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/502">developer.mozilla.org</a>
     */
    BAD_GATEWAY_502(502, "Bad Gateway"),

    /**
     * The server is not ready to handle the request. Common causes are a server that is down for maintenance or that is
     * overloaded. Note that together with this response, a user-friendly page explaining the problem should be sent.
     * This response should be used for temporary conditions and the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Retry-After">
     * <code>Retry-After</code></a> HTTP header should, if possible, contain the estimated time before the recovery of
     * the service. The webmaster must also take care about the caching-related headers that are sent along with this
     * response, as these temporary condition responses should usually not be cached.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/503">developer.mozilla.org</a>
     */
    SERVICE_UNAVAILABLE_503(503, "Service Unavailable"),

    /**
     * This error response is given when the server is acting as a gateway and cannot get a response in time.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/504">developer.mozilla.org</a>
     */
    GATEWAY_TIMEOUT_504(504, "Gateway Timeout"),

    /**
     * The HTTP version used in the request is not supported by the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/505">developer.mozilla.org</a>
     */
    HTTP_VERSION_NOT_SUPPORTED_505(505, "HTTP Version Not Supported"),

    /**
     * The server has an internal configuration error: during content negotiation, the chosen variant is configured to
     * engage in content negotiation itself, which results in circular references when creating responses.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/506">developer.mozilla.org</a>
     */
    VARIANT_ALSO_NEGOTIATES_506(506, "Variant Also Negotiates"),

    /**
     * The method could not be performed on the resource because the server is unable to store the representation needed
     * to successfully complete the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/507">developer.mozilla.org</a>
     */
    INSUFFICIENT_STORAGE_507(507, "Insufficient Storage"),

    /**
     * The server detected an infinite loop while processing the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/508">developer.mozilla.org</a>
     */
    LOOP_DETECTED_508(508, "Loop Detected"),

    /**
     * The client request declares an HTTP Extension (<a href="https://datatracker.ietf.org/doc/html/rfc2774">RFC
     * 2774</a>) that should be used to process the request, but the extension is not supported.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/510">developer.mozilla.org</a>
     */
    NOT_EXTENDED_510(510, "Not Extended"),

    /**
     * Indicates that the client needs to authenticate to gain network access.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/511">developer.mozilla.org</a>
     */
    NETWORK_AUTHENTICATION_REQUIRED_511(511, "Network Authentication Required");

    private final @Getter int code;
    private final @Getter String description;

    /**
     * @return <code>true</code> if this {@link Status} represents an informational status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#informational_responses">
     * developer.mozilla.org</a>
     */
    public boolean isInformational() {
        return code > 100 && code <= 199;
    }

    /**
     * @return <code>true</code> if this {@link Status} represents a successful status, <code>false</code> otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#successful_responses">
     * developer.mozilla.org</a>
     */
    public boolean isSuccessful() {
        return code > 200 && code <= 299;
    }

    /**
     * @return <code>true</code> if this {@link Status} represents a redirection status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#redirection_responses">
     * developer.mozilla.org</a>
     */
    public boolean isRedirection() {
        return code > 300 && code <= 399;
    }

    /**
     * @return <code>true</code> if this {@link Status} represents a client error status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#client_error_responses">
     * developer.mozilla.org</a>
     */
    public boolean isClientError() {
        return code > 400 && code <= 499;
    }

    /**
     * @return <code>true</code> if this {@link Status} represents a server error status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#server_error_responses">
     * developer.mozilla.org</a>
     */
    public boolean isServerError() {
        return code > 500 && code <= 599;
    }

    @Override
    public String toString() {
        return code + " " + description;
    }

    private static final Map<Integer, Status> STATUSES_OF_CODES = stream(values())
            .collect(toUnmodifiableMap(Status::getCode, identity()));
    private static final Map<String, Status> STATUSES_OF_DESCRIPTIONS = stream(values())
            .collect(toUnmodifiableMap(status -> status.getDescription().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link Status} of the given <code>code</code>.
     *
     * @param code the HTTP status code <code>int</code>
     *
     * @return the {@link Status}
     *
     * @throws IllegalArgumentException thrown if the given <code>code</code> is invalid
     */
    public static Status ofCode(final int code) throws IllegalArgumentException {
        final var status = STATUSES_OF_CODES.get(code);
        checkArgument(status != null, "Invalid `code`!");
        return status;
    }

    /**
     * Gets the {@link Status} of the given <code>description</code>.
     *
     * @param description the case-insensitive HTTP status description {@link String}
     *
     * @return the {@link Status}
     *
     * @throws IllegalArgumentException thrown if the given <code>description</code> is invalid
     */
    public static Status ofDescription(final String description) throws IllegalArgumentException {
        final var status = STATUSES_OF_DESCRIPTIONS.get(description.toLowerCase(ROOT));
        checkArgument(status != null, "Invalid `description`!");
        return status;
    }
}
