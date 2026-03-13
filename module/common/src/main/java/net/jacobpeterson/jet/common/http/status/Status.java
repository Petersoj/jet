package net.jacobpeterson.jet.common.http.status;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link Status} is an enum that represents a standardized HTTP response status.
 * <p>
 * HTTP response status codes indicate whether a specific
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP">HTTP</a> request has been successfully completed.
 * Responses are grouped in five classes:
 * <ol>
 * <li>{@link #isInformational()}</li>
 * <li>{@link #isSuccessful()}</li>
 * <li>{@link #isRedirection()}</li>
 * <li>{@link #isClientError()}</li>
 * <li>{@link #isServerError()}</li>
 * </ol>
 * <p>
 * The status codes listed below are defined by
 * <a href="https://httpwg.org/specs/rfc9110.html#overview.of.status.codes">RFC 9110</a>.
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
    CONTINUE_100(
            Code.CONTINUE_100,
            Description.CONTINUE_100),

    /**
     * This code is sent in response to an
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade"><code>Upgrade</code></a>
     * request header from the client and indicates the protocol the server is switching to.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/101">developer.mozilla.org</a>
     */
    SWITCHING_PROTOCOLS_101(
            Code.SWITCHING_PROTOCOLS_101,
            Description.SWITCHING_PROTOCOLS_101),

    /**
     * This code was used in <a href="https://developer.mozilla.org/en-US/docs/Glossary/WebDAV">WebDAV</a> contexts to
     * indicate that a request has been received by the server, but no status was available at the time of the
     * response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/102">developer.mozilla.org</a>
     */
    PROCESSING_102(
            Code.PROCESSING_102,
            Description.PROCESSING_102),

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
    EARLY_HINTS_103(
            Code.EARLY_HINTS_103,
            Description.EARLY_HINTS_103),

    /**
     * The request succeeded. The result and meaning of "success" depends on the HTTP method:
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
    OK_200(
            Code.OK_200,
            Description.OK_200),

    /**
     * The request succeeded, and a new resource was created as a result. This is typically the response sent after
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a>
     * requests, or some <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT">
     * <code>PUT</code></a> requests.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/201">developer.mozilla.org</a>
     */
    CREATED_201(
            Code.CREATED_201,
            Description.CREATED_201),

    /**
     * The request has been received but not yet acted upon. It is noncommittal, since there is no way in HTTP to later
     * send an asynchronous response indicating the outcome of the request. It is intended for cases where another
     * process or server handles the request, or for batch processing.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/202">developer.mozilla.org</a>
     */
    ACCEPTED_202(
            Code.ACCEPTED_202,
            Description.ACCEPTED_202),

    /**
     * This response code means the returned metadata is not exactly the same as is available from the origin server,
     * but is collected from a local or a third-party copy. This is mostly used for mirrors or backups of another
     * resource. Except for that specific case, the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/200"><code>200 OK</code></a>
     * response is preferred to this status.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/203">developer.mozilla.org</a>
     */
    NON_AUTHORITATIVE_INFORMATION_203(
            Code.NON_AUTHORITATIVE_INFORMATION_203,
            Description.NON_AUTHORITATIVE_INFORMATION_203),

    /**
     * There is no content to send for this request, but the headers are useful. The user agent may update its cached
     * headers for this resource with the new ones.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/204">developer.mozilla.org</a>
     */
    NO_CONTENT_204(
            Code.NO_CONTENT_204,
            Description.NO_CONTENT_204),

    /**
     * Tells the user agent to reset the document which sent this request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/205">developer.mozilla.org</a>
     */
    RESET_CONTENT_205(
            Code.RESET_CONTENT_205,
            Description.RESET_CONTENT_205),

    /**
     * This response code is used in response to a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Range_requests">range request</a> when the
     * client has requested a part or parts of a resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/206">developer.mozilla.org</a>
     */
    PARTIAL_CONTENT_206(
            Code.PARTIAL_CONTENT_206,
            Description.PARTIAL_CONTENT_206),

    /**
     * Conveys information about multiple resources, for situations where multiple status codes might be appropriate.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/207">developer.mozilla.org</a>
     */
    MULTI_STATUS_207(
            Code.MULTI_STATUS_207,
            Description.MULTI_STATUS_207),

    /**
     * Used inside a <code>&lt;dav:propstat&gt;</code> response element to avoid repeatedly enumerating the internal
     * members of multiple bindings to the same collection.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/208">developer.mozilla.org</a>
     */
    ALREADY_REPORTED_208(
            Code.ALREADY_REPORTED_208,
            Description.ALREADY_REPORTED_208),

    /**
     * The server has fulfilled a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a> request
     * for the resource, and the response is a representation of the result of one or more instance-manipulations
     * applied to the current instance.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/226">developer.mozilla.org</a>
     */
    IM_USED_226(
            Code.IM_USED_226,
            Description.IM_USED_226),

    /**
     * In
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Content_negotiation#agent-driven_negotiation">
     * agent-driven content negotiation</a>, the request has more than one possible response and the user agent or user
     * should choose one of them. There is no standardized way for clients to automatically choose one of the responses,
     * so this is rarely used.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/300">developer.mozilla.org</a>
     */
    MULTIPLE_CHOICES_300(
            Code.MULTIPLE_CHOICES_300,
            Description.MULTIPLE_CHOICES_300),

    /**
     * The URL of the requested resource has been changed permanently. The new URL is given in the response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/301">developer.mozilla.org</a>
     */
    MOVED_PERMANENTLY_301(
            Code.MOVED_PERMANENTLY_301,
            Description.MOVED_PERMANENTLY_301),

    /**
     * This response code means that the URI of requested resource has been changed <em>temporarily</em>. Further
     * changes in the URI might be made in the future, so the same URI should be used by the client in future requests.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/302">developer.mozilla.org</a>
     */
    FOUND_302(
            Code.FOUND_302,
            Description.FOUND_302),

    /**
     * The server sent this response to direct the client to get the requested resource at another URI with a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a> request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/303">developer.mozilla.org</a>
     */
    SEE_OTHER_303(
            Code.SEE_OTHER_303,
            Description.SEE_OTHER_303),

    /**
     * This is used for caching purposes. It tells the client that the response has not been modified, so the client can
     * continue to use the same <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Caching">cached</a>
     * version of the response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/304">developer.mozilla.org</a>
     */
    NOT_MODIFIED_304(
            Code.NOT_MODIFIED_304,
            Description.NOT_MODIFIED_304),

    /**
     * Defined in a previous version of the HTTP specification to indicate that a requested response must be accessed by
     * a proxy. It has been deprecated due to security concerns regarding in-band configuration of a proxy.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/305">developer.mozilla.org</a>
     */
    USE_PROXY_305(
            Code.USE_PROXY_305,
            Description.USE_PROXY_305),

    /**
     * This response code is no longer used; but is reserved. It was used in a previous version of the HTTP/1.1
     * specification.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/306">developer.mozilla.org</a>
     */
    UNUSED_306(
            Code.UNUSED_306,
            Description.UNUSED_306),

    /**
     * This response code is no longer used; but is reserved. It was used in a previous version of the HTTP/1.1
     * specification.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/307">developer.mozilla.org</a>
     */
    TEMPORARY_REDIRECT_307(
            Code.TEMPORARY_REDIRECT_307,
            Description.TEMPORARY_REDIRECT_307),

    /**
     * The server sends this response to direct the client to get the requested resource at another URI with the same
     * method that was used in the prior request. This has the same semantics as the <code>302 Found</code> response
     * code, with the exception that the user agent <em>must not</em> change the HTTP method used: if a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a> was
     * used in the first request, a <code>POST</code> must be used in the redirected request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/308">developer.mozilla.org</a>
     */
    PERMANENT_REDIRECT_308(
            Code.PERMANENT_REDIRECT_308,
            Description.PERMANENT_REDIRECT_308),

    /**
     * The server cannot or will not process the request due to something that is perceived to be a client error (e.g.,
     * malformed request syntax, invalid request message framing, or deceptive request routing).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/400">developer.mozilla.org</a>
     */
    BAD_REQUEST_400(
            Code.BAD_REQUEST_400,
            Description.BAD_REQUEST_400),

    /**
     * Although the HTTP standard specifies "unauthorized", semantically this response means "unauthenticated". That is,
     * the client must authenticate itself to get the requested response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/401">developer.mozilla.org</a>
     */
    UNAUTHORIZED_401(
            Code.UNAUTHORIZED_401,
            Description.UNAUTHORIZED_401),

    /**
     * The initial purpose of this code was for digital payment systems, however this status code is rarely used and no
     * standard convention exists.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/402">developer.mozilla.org</a>
     */
    PAYMENT_REQUIRED_402(
            Code.PAYMENT_REQUIRED_402,
            Description.PAYMENT_REQUIRED_402),

    /**
     * The client does not have access rights to the content; that is, it is unauthorized, so the server is refusing to
     * give the requested resource. Unlike <code>401 Unauthorized</code>, the client's identity is known to the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/403">developer.mozilla.org</a>
     */
    FORBIDDEN_403(
            Code.FORBIDDEN_403,
            Description.FORBIDDEN_403),

    /**
     * The server cannot find the requested resource. In the browser, this means the URL is not recognized. In an API,
     * this can also mean that the endpoint is valid but the resource itself does not exist. Servers may also send this
     * response instead of <code>403 Forbidden</code> to hide the existence of a resource from an unauthorized client.
     * This response code is probably the most well known due to its frequent occurrence on the web.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/404">developer.mozilla.org</a>
     */
    NOT_FOUND_404(
            Code.NOT_FOUND_404,
            Description.NOT_FOUND_404),

    /**
     * The <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods">request method</a> is known by
     * the server but is not supported by the target resource. For example, an API may not allow <code>DELETE</code> on
     * a resource, or the <code>TRACE</code> method entirely.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/405">developer.mozilla.org</a>
     */
    METHOD_NOT_ALLOWED_405(
            Code.METHOD_NOT_ALLOWED_405,
            Description.METHOD_NOT_ALLOWED_405),

    /**
     * This response is sent when the web server, after performing
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Content_negotiation#server-driven_content_negotiation">
     * server-driven content negotiation</a>, doesn't find any content that conforms to the criteria given by the user
     * agent.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/406">developer.mozilla.org</a>
     */
    NOT_ACCEPTABLE_406(
            Code.NOT_ACCEPTABLE_406,
            Description.NOT_ACCEPTABLE_406),

    /**
     * This is similar to <code>401 Unauthorized</code> but authentication is needed to be done by a proxy.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/407">developer.mozilla.org</a>
     */
    PROXY_AUTHENTICATION_REQUIRED_407(
            Code.PROXY_AUTHENTICATION_REQUIRED_407,
            Description.PROXY_AUTHENTICATION_REQUIRED_407),

    /**
     * This response is sent on an idle connection by some servers, even without any previous request by the client. It
     * means that the server would like to shut down this unused connection. This response is used much more since some
     * browsers use HTTP pre-connection mechanisms to speed up browsing. Some servers may shut down a connection without
     * sending this message.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/408">developer.mozilla.org</a>
     */
    REQUEST_TIMEOUT_408(
            Code.REQUEST_TIMEOUT_408,
            Description.REQUEST_TIMEOUT_408),

    /**
     * This response is sent when a request conflicts with the current state of the server. In
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/WebDAV">WebDAV</a> remote web authoring,
     * <code>409</code> responses are errors sent to the client so that a user might be able to resolve a conflict and
     * resubmit the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/409">developer.mozilla.org</a>
     */
    CONFLICT_409(
            Code.CONFLICT_409,
            Description.CONFLICT_409),

    /**
     * This response is sent when the requested content has been permanently deleted from server, with no forwarding
     * address. Clients are expected to remove their caches and links to the resource. The HTTP specification intends
     * this status code to be used for "limited-time, promotional services". APIs should not feel compelled to indicate
     * resources that have been deleted with this status code.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/410">developer.mozilla.org</a>
     */
    GONE_410(
            Code.GONE_410,
            Description.GONE_410),

    /**
     * Server rejected the request because the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Length">
     * <code>Content-Length</code></a> header field is not defined and the server requires it.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/411">developer.mozilla.org</a>
     */
    LENGTH_REQUIRED_411(
            Code.LENGTH_REQUIRED_411,
            Description.LENGTH_REQUIRED_411),

    /**
     * In <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Conditional_requests">conditional
     * requests</a>, the client has indicated preconditions in its headers which the server does not meet.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/412">developer.mozilla.org</a>
     */
    PRECONDITION_FAILED_412(
            Code.PRECONDITION_FAILED_412,
            Description.PRECONDITION_FAILED_412),

    /**
     * The request body is larger than limits defined by server. The server might close the connection or return a
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Retry-After">
     * <code>Retry-After</code></a> header field.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/413">developer.mozilla.org</a>
     */
    CONTENT_TOO_LARGE_413(
            Code.CONTENT_TOO_LARGE_413,
            Description.CONTENT_TOO_LARGE_413),

    /**
     * The URI requested by the client is longer than the server is willing to interpret.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/414">developer.mozilla.org</a>
     */
    URI_TOO_LONG_414(
            Code.URI_TOO_LONG_414,
            Description.URI_TOO_LONG_414),

    /**
     * The media format of the requested data is not supported by the server, so the server is rejecting the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/415">developer.mozilla.org</a>
     */
    UNSUPPORTED_MEDIA_TYPE_415(
            Code.UNSUPPORTED_MEDIA_TYPE_415,
            Description.UNSUPPORTED_MEDIA_TYPE_415),

    /**
     * The <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Range_requests">ranges</a> specified by
     * the
     * <code>Range</code> header field in the request cannot be fulfilled. It's possible that the range is outside the
     * size of the target resource's data.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/416">developer.mozilla.org</a>
     */
    RANGE_NOT_SATISFIABLE_416(
            Code.RANGE_NOT_SATISFIABLE_416,
            Description.RANGE_NOT_SATISFIABLE_416),

    /**
     * This response code means the expectation indicated by the
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Expect"><code>Expect</code></a>
     * request header field cannot be met by the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/417">developer.mozilla.org</a>
     */
    EXPECTATION_FAILED_417(
            Code.EXPECTATION_FAILED_417,
            Description.EXPECTATION_FAILED_417),

    /**
     * The server refuses the attempt to brew coffee with a teapot.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/418">developer.mozilla.org</a>
     */
    IM_A_TEAPOT_418(
            Code.IM_A_TEAPOT_418,
            Description.IM_A_TEAPOT_418),

    /**
     * The request was directed at a server that is not able to produce a response. This can be sent by a server that is
     * not configured to produce responses for the combination of scheme and authority that are included in the request
     * URI.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/421">developer.mozilla.org</a>
     */
    MISDIRECTED_REQUEST_421(
            Code.MISDIRECTED_REQUEST_421,
            Description.MISDIRECTED_REQUEST_421),

    /**
     * The request was well-formed but was unable to be followed due to semantic errors.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/422">developer.mozilla.org</a>
     */
    UNPROCESSABLE_CONTENT_422(
            Code.UNPROCESSABLE_CONTENT_422,
            Description.UNPROCESSABLE_CONTENT_422),

    /**
     * The resource that is being accessed is locked.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/423">developer.mozilla.org</a>
     */
    LOCKED_423(
            Code.LOCKED_423,
            Description.LOCKED_423),

    /**
     * The request failed due to failure of a previous request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/424">developer.mozilla.org</a>
     */
    FAILED_DEPENDENCY_424(
            Code.FAILED_DEPENDENCY_424,
            Description.FAILED_DEPENDENCY_424),

    /**
     * Indicates that the server is unwilling to risk processing a request that might be replayed.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/425">developer.mozilla.org</a>
     */
    TOO_EARLY_425(
            Code.TOO_EARLY_425,
            Description.TOO_EARLY_425),

    /**
     * The server refuses to perform the request using the current protocol but might be willing to do so after the
     * client upgrades to a different protocol. The server sends an
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Upgrade"><code>Upgrade</code></a>
     * header in a 426 response to indicate the required protocol(s).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/426">developer.mozilla.org</a>
     */
    UPGRADE_REQUIRED_426(
            Code.UPGRADE_REQUIRED_426,
            Description.UPGRADE_REQUIRED_426),

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
    PRECONDITION_REQUIRED_428(
            Code.PRECONDITION_REQUIRED_428,
            Description.PRECONDITION_REQUIRED_428),

    /**
     * The user has sent too many requests in a given amount of time
     * (<a href="https://developer.mozilla.org/en-US/docs/Glossary/Rate_limit">rate limiting</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/429">developer.mozilla.org</a>
     */
    TOO_MANY_REQUESTS_429(
            Code.TOO_MANY_REQUESTS_429,
            Description.TOO_MANY_REQUESTS_429),

    /**
     * The server is unwilling to process the request because its header fields are too large. The request may be
     * resubmitted after reducing the size of the request header fields.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/431">developer.mozilla.org</a>
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE_431(
            Code.REQUEST_HEADER_FIELDS_TOO_LARGE_431,
            Description.REQUEST_HEADER_FIELDS_TOO_LARGE_431),

    /**
     * The user agent requested a resource that cannot legally be provided, such as a web page censored by a
     * government.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/451">developer.mozilla.org</a>
     */
    UNAVAILABLE_FOR_LEGAL_REASONS_451(
            Code.UNAVAILABLE_FOR_LEGAL_REASONS_451,
            Description.UNAVAILABLE_FOR_LEGAL_REASONS_451),

    /**
     * The server has encountered a situation it does not know how to handle. This error is generic, indicating that the
     * server cannot find a more appropriate <code>5XX</code> status code to respond with.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/500">developer.mozilla.org</a>
     */
    INTERNAL_SERVER_ERROR_500(
            Code.INTERNAL_SERVER_ERROR_500,
            Description.INTERNAL_SERVER_ERROR_500),

    /**
     * The request method is not supported by the server and cannot be handled. The only methods that servers are
     * required to support (and therefore that must not return this code) are
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/GET"><code>GET</code></a> and
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/HEAD"><code>HEAD</code></a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/501">developer.mozilla.org</a>
     */
    NOT_IMPLEMENTED_501(
            Code.NOT_IMPLEMENTED_501,
            Description.NOT_IMPLEMENTED_501),

    /**
     * This error response means that the server, while working as a gateway to get a response needed to handle the
     * request, got an invalid response.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/502">developer.mozilla.org</a>
     */
    BAD_GATEWAY_502(
            Code.BAD_GATEWAY_502,
            Description.BAD_GATEWAY_502),

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
    SERVICE_UNAVAILABLE_503(
            Code.SERVICE_UNAVAILABLE_503,
            Description.SERVICE_UNAVAILABLE_503),

    /**
     * This error response is given when the server is acting as a gateway and cannot get a response in time.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/504">developer.mozilla.org</a>
     */
    GATEWAY_TIMEOUT_504(
            Code.GATEWAY_TIMEOUT_504,
            Description.GATEWAY_TIMEOUT_504),

    /**
     * The HTTP version used in the request is not supported by the server.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/505">developer.mozilla.org</a>
     */
    HTTP_VERSION_NOT_SUPPORTED_505(
            Code.HTTP_VERSION_NOT_SUPPORTED_505,
            Description.HTTP_VERSION_NOT_SUPPORTED_505),

    /**
     * The server has an internal configuration error: during content negotiation, the chosen variant is configured to
     * engage in content negotiation itself, which results in circular references when creating responses.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/506">developer.mozilla.org</a>
     */
    VARIANT_ALSO_NEGOTIATES_506(
            Code.VARIANT_ALSO_NEGOTIATES_506,
            Description.VARIANT_ALSO_NEGOTIATES_506),

    /**
     * The method could not be performed on the resource because the server is unable to store the representation needed
     * to successfully complete the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/507">developer.mozilla.org</a>
     */
    INSUFFICIENT_STORAGE_507(
            Code.INSUFFICIENT_STORAGE_507,
            Description.INSUFFICIENT_STORAGE_507),

    /**
     * The server detected an infinite loop while processing the request.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/508">developer.mozilla.org</a>
     */
    LOOP_DETECTED_508(
            Code.LOOP_DETECTED_508,
            Description.LOOP_DETECTED_508),

    /**
     * The client request declares an HTTP Extension (<a href="https://datatracker.ietf.org/doc/html/rfc2774">RFC
     * 2774</a>) that should be used to process the request, but the extension is not supported.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/510">developer.mozilla.org</a>
     */
    NOT_EXTENDED_510(
            Code.NOT_EXTENDED_510,
            Description.NOT_EXTENDED_510),

    /**
     * Indicates that the client needs to authenticate to gain network access.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/511">developer.mozilla.org</a>
     */
    NETWORK_AUTHENTICATION_REQUIRED_511(
            Code.NETWORK_AUTHENTICATION_REQUIRED_511,
            Description.NETWORK_AUTHENTICATION_REQUIRED_511);

    private final @Getter int code;
    private final @Getter String description;

    /**
     * @return {@link #isInformational(int)} {@link #getCode()}
     */
    public boolean isInformational() {
        return isInformational(code);
    }

    /**
     * @return {@link #isSuccessful(int)} {@link #getCode()}
     */
    public boolean isSuccessful() {
        return isSuccessful(code);
    }

    /**
     * @return {@link #isRedirection(int)} {@link #getCode()}
     */
    public boolean isRedirection() {
        return isRedirection(code);
    }

    /**
     * @return {@link #isClientError(int)} {@link #getCode()}
     */
    public boolean isClientError() {
        return isClientError(code);
    }

    /**
     * @return {@link #isServerError(int)} {@link #getCode()}
     */
    public boolean isServerError() {
        return isServerError(code);
    }

    /**
     * @return {@link #isError(int)} {@link #getCode()}
     */
    public boolean isError() {
        return isError(code);
    }

    /**
     * @return the concatenation of {@link #getCode()}, space, and {@link #getDescription()}
     */
    @Override
    public String toString() {
        return code + " " + description;
    }

    /**
     * {@link Code} contains the {@link #getCode()} constants for all {@link Status} enums so they can be used within
     * annotations.
     */
    public static final class Code {

        /**
         * @see Status#CONTINUE_100
         */
        public static final int CONTINUE_100 = 100;

        /**
         * @see Status#SWITCHING_PROTOCOLS_101
         */
        public static final int SWITCHING_PROTOCOLS_101 = 101;

        /**
         * @see Status#PROCESSING_102
         */
        public static final int PROCESSING_102 = 102;

        /**
         * @see Status#EARLY_HINTS_103
         */
        public static final int EARLY_HINTS_103 = 103;

        /**
         * @see Status#OK_200
         */
        public static final int OK_200 = 200;

        /**
         * @see Status#CREATED_201
         */
        public static final int CREATED_201 = 201;

        /**
         * @see Status#ACCEPTED_202
         */
        public static final int ACCEPTED_202 = 202;

        /**
         * @see Status#NON_AUTHORITATIVE_INFORMATION_203
         */
        public static final int NON_AUTHORITATIVE_INFORMATION_203 = 203;

        /**
         * @see Status#NO_CONTENT_204
         */
        public static final int NO_CONTENT_204 = 204;

        /**
         * @see Status#RESET_CONTENT_205
         */
        public static final int RESET_CONTENT_205 = 205;

        /**
         * @see Status#PARTIAL_CONTENT_206
         */
        public static final int PARTIAL_CONTENT_206 = 206;

        /**
         * @see Status#MULTI_STATUS_207
         */
        public static final int MULTI_STATUS_207 = 207;

        /**
         * @see Status#ALREADY_REPORTED_208
         */
        public static final int ALREADY_REPORTED_208 = 208;

        /**
         * @see Status#IM_USED_226
         */
        public static final int IM_USED_226 = 226;

        /**
         * @see Status#MULTIPLE_CHOICES_300
         */
        public static final int MULTIPLE_CHOICES_300 = 300;

        /**
         * @see Status#MOVED_PERMANENTLY_301
         */
        public static final int MOVED_PERMANENTLY_301 = 301;

        /**
         * @see Status#FOUND_302
         */
        public static final int FOUND_302 = 302;

        /**
         * @see Status#SEE_OTHER_303
         */
        public static final int SEE_OTHER_303 = 303;

        /**
         * @see Status#NOT_MODIFIED_304
         */
        public static final int NOT_MODIFIED_304 = 304;

        /**
         * @see Status#USE_PROXY_305
         */
        public static final int USE_PROXY_305 = 305;

        /**
         * @see Status#UNUSED_306
         */
        public static final int UNUSED_306 = 306;

        /**
         * @see Status#TEMPORARY_REDIRECT_307
         */
        public static final int TEMPORARY_REDIRECT_307 = 307;

        /**
         * @see Status#PERMANENT_REDIRECT_308
         */
        public static final int PERMANENT_REDIRECT_308 = 308;

        /**
         * @see Status#BAD_REQUEST_400
         */
        public static final int BAD_REQUEST_400 = 400;

        /**
         * @see Status#UNAUTHORIZED_401
         */
        public static final int UNAUTHORIZED_401 = 401;

        /**
         * @see Status#PAYMENT_REQUIRED_402
         */
        public static final int PAYMENT_REQUIRED_402 = 402;

        /**
         * @see Status#FORBIDDEN_403
         */
        public static final int FORBIDDEN_403 = 403;

        /**
         * @see Status#NOT_FOUND_404
         */
        public static final int NOT_FOUND_404 = 404;

        /**
         * @see Status#METHOD_NOT_ALLOWED_405
         */
        public static final int METHOD_NOT_ALLOWED_405 = 405;

        /**
         * @see Status#NOT_ACCEPTABLE_406
         */
        public static final int NOT_ACCEPTABLE_406 = 406;

        /**
         * @see Status#PROXY_AUTHENTICATION_REQUIRED_407
         */
        public static final int PROXY_AUTHENTICATION_REQUIRED_407 = 407;

        /**
         * @see Status#REQUEST_TIMEOUT_408
         */
        public static final int REQUEST_TIMEOUT_408 = 408;

        /**
         * @see Status#CONFLICT_409
         */
        public static final int CONFLICT_409 = 409;

        /**
         * @see Status#GONE_410
         */
        public static final int GONE_410 = 410;

        /**
         * @see Status#LENGTH_REQUIRED_411
         */
        public static final int LENGTH_REQUIRED_411 = 411;

        /**
         * @see Status#PRECONDITION_FAILED_412
         */
        public static final int PRECONDITION_FAILED_412 = 412;

        /**
         * @see Status#CONTENT_TOO_LARGE_413
         */
        public static final int CONTENT_TOO_LARGE_413 = 413;

        /**
         * @see Status#URI_TOO_LONG_414
         */
        public static final int URI_TOO_LONG_414 = 414;

        /**
         * @see Status#UNSUPPORTED_MEDIA_TYPE_415
         */
        public static final int UNSUPPORTED_MEDIA_TYPE_415 = 415;

        /**
         * @see Status#RANGE_NOT_SATISFIABLE_416
         */
        public static final int RANGE_NOT_SATISFIABLE_416 = 416;

        /**
         * @see Status#EXPECTATION_FAILED_417
         */
        public static final int EXPECTATION_FAILED_417 = 417;

        /**
         * @see Status#IM_A_TEAPOT_418
         */
        public static final int IM_A_TEAPOT_418 = 418;

        /**
         * @see Status#MISDIRECTED_REQUEST_421
         */
        public static final int MISDIRECTED_REQUEST_421 = 421;

        /**
         * @see Status#UNPROCESSABLE_CONTENT_422
         */
        public static final int UNPROCESSABLE_CONTENT_422 = 422;

        /**
         * @see Status#LOCKED_423
         */
        public static final int LOCKED_423 = 423;

        /**
         * @see Status#FAILED_DEPENDENCY_424
         */
        public static final int FAILED_DEPENDENCY_424 = 424;

        /**
         * @see Status#TOO_EARLY_425
         */
        public static final int TOO_EARLY_425 = 425;

        /**
         * @see Status#UPGRADE_REQUIRED_426
         */
        public static final int UPGRADE_REQUIRED_426 = 426;

        /**
         * @see Status#PRECONDITION_REQUIRED_428
         */
        public static final int PRECONDITION_REQUIRED_428 = 428;

        /**
         * @see Status#TOO_MANY_REQUESTS_429
         */
        public static final int TOO_MANY_REQUESTS_429 = 429;

        /**
         * @see Status#REQUEST_HEADER_FIELDS_TOO_LARGE_431
         */
        public static final int REQUEST_HEADER_FIELDS_TOO_LARGE_431 = 431;

        /**
         * @see Status#UNAVAILABLE_FOR_LEGAL_REASONS_451
         */
        public static final int UNAVAILABLE_FOR_LEGAL_REASONS_451 = 451;

        /**
         * @see Status#INTERNAL_SERVER_ERROR_500
         */
        public static final int INTERNAL_SERVER_ERROR_500 = 500;

        /**
         * @see Status#NOT_IMPLEMENTED_501
         */
        public static final int NOT_IMPLEMENTED_501 = 501;

        /**
         * @see Status#BAD_GATEWAY_502
         */
        public static final int BAD_GATEWAY_502 = 502;

        /**
         * @see Status#SERVICE_UNAVAILABLE_503
         */
        public static final int SERVICE_UNAVAILABLE_503 = 503;

        /**
         * @see Status#GATEWAY_TIMEOUT_504
         */
        public static final int GATEWAY_TIMEOUT_504 = 504;

        /**
         * @see Status#HTTP_VERSION_NOT_SUPPORTED_505
         */
        public static final int HTTP_VERSION_NOT_SUPPORTED_505 = 505;

        /**
         * @see Status#VARIANT_ALSO_NEGOTIATES_506
         */
        public static final int VARIANT_ALSO_NEGOTIATES_506 = 506;

        /**
         * @see Status#INSUFFICIENT_STORAGE_507
         */
        public static final int INSUFFICIENT_STORAGE_507 = 507;

        /**
         * @see Status#LOOP_DETECTED_508
         */
        public static final int LOOP_DETECTED_508 = 508;

        /**
         * @see Status#NOT_EXTENDED_510
         */
        public static final int NOT_EXTENDED_510 = 510;

        /**
         * @see Status#NETWORK_AUTHENTICATION_REQUIRED_511
         */
        public static final int NETWORK_AUTHENTICATION_REQUIRED_511 = 511;
    }

    /**
     * {@link Description} contains the {@link #getDescription()} constants for all {@link Status} enums so they can be
     * used within annotations.
     */
    public static final class Description {

        /**
         * @see Status#CONTINUE_100
         */
        public static final String CONTINUE_100 = "Continue";

        /**
         * @see Status#SWITCHING_PROTOCOLS_101
         */
        public static final String SWITCHING_PROTOCOLS_101 = "Switching Protocols";

        /**
         * @see Status#PROCESSING_102
         */
        public static final String PROCESSING_102 = "Processing";

        /**
         * @see Status#EARLY_HINTS_103
         */
        public static final String EARLY_HINTS_103 = "Early Hints";

        /**
         * @see Status#OK_200
         */
        public static final String OK_200 = "OK";

        /**
         * @see Status#CREATED_201
         */
        public static final String CREATED_201 = "Created";

        /**
         * @see Status#ACCEPTED_202
         */
        public static final String ACCEPTED_202 = "Accepted";

        /**
         * @see Status#NON_AUTHORITATIVE_INFORMATION_203
         */
        public static final String NON_AUTHORITATIVE_INFORMATION_203 = "Non-Authoritative Information";

        /**
         * @see Status#NO_CONTENT_204
         */
        public static final String NO_CONTENT_204 = "No Content";

        /**
         * @see Status#RESET_CONTENT_205
         */
        public static final String RESET_CONTENT_205 = "Reset Content";

        /**
         * @see Status#PARTIAL_CONTENT_206
         */
        public static final String PARTIAL_CONTENT_206 = "Partial Content";

        /**
         * @see Status#MULTI_STATUS_207
         */
        public static final String MULTI_STATUS_207 = "Multi-Status";

        /**
         * @see Status#ALREADY_REPORTED_208
         */
        public static final String ALREADY_REPORTED_208 = "Already Reported";

        /**
         * @see Status#IM_USED_226
         */
        public static final String IM_USED_226 = "IM Used";

        /**
         * @see Status#MULTIPLE_CHOICES_300
         */
        public static final String MULTIPLE_CHOICES_300 = "Multiple Choices";

        /**
         * @see Status#MOVED_PERMANENTLY_301
         */
        public static final String MOVED_PERMANENTLY_301 = "Moved Permanently";

        /**
         * @see Status#FOUND_302
         */
        public static final String FOUND_302 = "Found";

        /**
         * @see Status#SEE_OTHER_303
         */
        public static final String SEE_OTHER_303 = "See Other";

        /**
         * @see Status#NOT_MODIFIED_304
         */
        public static final String NOT_MODIFIED_304 = "Not Modified";

        /**
         * @see Status#USE_PROXY_305
         */
        public static final String USE_PROXY_305 = "Use Proxy";

        /**
         * @see Status#UNUSED_306
         */
        public static final String UNUSED_306 = "Unused";

        /**
         * @see Status#TEMPORARY_REDIRECT_307
         */
        public static final String TEMPORARY_REDIRECT_307 = "Temporary Redirect";

        /**
         * @see Status#PERMANENT_REDIRECT_308
         */
        public static final String PERMANENT_REDIRECT_308 = "Permanent Redirect";

        /**
         * @see Status#BAD_REQUEST_400
         */
        public static final String BAD_REQUEST_400 = "Bad Request";

        /**
         * @see Status#UNAUTHORIZED_401
         */
        public static final String UNAUTHORIZED_401 = "Unauthorized";

        /**
         * @see Status#PAYMENT_REQUIRED_402
         */
        public static final String PAYMENT_REQUIRED_402 = "Payment Required";

        /**
         * @see Status#FORBIDDEN_403
         */
        public static final String FORBIDDEN_403 = "Forbidden";

        /**
         * @see Status#NOT_FOUND_404
         */
        public static final String NOT_FOUND_404 = "Not Found";

        /**
         * @see Status#METHOD_NOT_ALLOWED_405
         */
        public static final String METHOD_NOT_ALLOWED_405 = "Method Not Allowed";

        /**
         * @see Status#NOT_ACCEPTABLE_406
         */
        public static final String NOT_ACCEPTABLE_406 = "Not Acceptable";

        /**
         * @see Status#PROXY_AUTHENTICATION_REQUIRED_407
         */
        public static final String PROXY_AUTHENTICATION_REQUIRED_407 = "Proxy Authentication Required";

        /**
         * @see Status#REQUEST_TIMEOUT_408
         */
        public static final String REQUEST_TIMEOUT_408 = "Request Timeout";

        /**
         * @see Status#CONFLICT_409
         */
        public static final String CONFLICT_409 = "Conflict";

        /**
         * @see Status#GONE_410
         */
        public static final String GONE_410 = "Gone";

        /**
         * @see Status#LENGTH_REQUIRED_411
         */
        public static final String LENGTH_REQUIRED_411 = "Length Required";

        /**
         * @see Status#PRECONDITION_FAILED_412
         */
        public static final String PRECONDITION_FAILED_412 = "Precondition Failed";

        /**
         * @see Status#CONTENT_TOO_LARGE_413
         */
        public static final String CONTENT_TOO_LARGE_413 = "Content Too Large";

        /**
         * @see Status#URI_TOO_LONG_414
         */
        public static final String URI_TOO_LONG_414 = "URI Too Long";

        /**
         * @see Status#UNSUPPORTED_MEDIA_TYPE_415
         */
        public static final String UNSUPPORTED_MEDIA_TYPE_415 = "Unsupported Media Type";

        /**
         * @see Status#RANGE_NOT_SATISFIABLE_416
         */
        public static final String RANGE_NOT_SATISFIABLE_416 = "Range Not Satisfiable";

        /**
         * @see Status#EXPECTATION_FAILED_417
         */
        public static final String EXPECTATION_FAILED_417 = "Expectation Failed";

        /**
         * @see Status#IM_A_TEAPOT_418
         */
        public static final String IM_A_TEAPOT_418 = "I'm a teapot";

        /**
         * @see Status#MISDIRECTED_REQUEST_421
         */
        public static final String MISDIRECTED_REQUEST_421 = "Misdirected Request";

        /**
         * @see Status#UNPROCESSABLE_CONTENT_422
         */
        public static final String UNPROCESSABLE_CONTENT_422 = "Unprocessable Content";

        /**
         * @see Status#LOCKED_423
         */
        public static final String LOCKED_423 = "Locked";

        /**
         * @see Status#FAILED_DEPENDENCY_424
         */
        public static final String FAILED_DEPENDENCY_424 = "Failed Dependency";

        /**
         * @see Status#TOO_EARLY_425
         */
        public static final String TOO_EARLY_425 = "Too Early";

        /**
         * @see Status#UPGRADE_REQUIRED_426
         */
        public static final String UPGRADE_REQUIRED_426 = "Upgrade Required";

        /**
         * @see Status#PRECONDITION_REQUIRED_428
         */
        public static final String PRECONDITION_REQUIRED_428 = "Precondition Required";

        /**
         * @see Status#TOO_MANY_REQUESTS_429
         */
        public static final String TOO_MANY_REQUESTS_429 = "Too Many Requests";

        /**
         * @see Status#REQUEST_HEADER_FIELDS_TOO_LARGE_431
         */
        public static final String REQUEST_HEADER_FIELDS_TOO_LARGE_431 = "Request Header Fields Too Large";

        /**
         * @see Status#UNAVAILABLE_FOR_LEGAL_REASONS_451
         */
        public static final String UNAVAILABLE_FOR_LEGAL_REASONS_451 = "Unavailable For Legal Reasons";

        /**
         * @see Status#INTERNAL_SERVER_ERROR_500
         */
        public static final String INTERNAL_SERVER_ERROR_500 = "Internal Server Error";

        /**
         * @see Status#NOT_IMPLEMENTED_501
         */
        public static final String NOT_IMPLEMENTED_501 = "Not Implemented";

        /**
         * @see Status#BAD_GATEWAY_502
         */
        public static final String BAD_GATEWAY_502 = "Bad Gateway";

        /**
         * @see Status#SERVICE_UNAVAILABLE_503
         */
        public static final String SERVICE_UNAVAILABLE_503 = "Service Unavailable";

        /**
         * @see Status#GATEWAY_TIMEOUT_504
         */
        public static final String GATEWAY_TIMEOUT_504 = "Gateway Timeout";

        /**
         * @see Status#HTTP_VERSION_NOT_SUPPORTED_505
         */
        public static final String HTTP_VERSION_NOT_SUPPORTED_505 = "HTTP Version Not Supported";

        /**
         * @see Status#VARIANT_ALSO_NEGOTIATES_506
         */
        public static final String VARIANT_ALSO_NEGOTIATES_506 = "Variant Also Negotiates";

        /**
         * @see Status#INSUFFICIENT_STORAGE_507
         */
        public static final String INSUFFICIENT_STORAGE_507 = "Insufficient Storage";

        /**
         * @see Status#LOOP_DETECTED_508
         */
        public static final String LOOP_DETECTED_508 = "Loop Detected";

        /**
         * @see Status#NOT_EXTENDED_510
         */
        public static final String NOT_EXTENDED_510 = "Not Extended";

        /**
         * @see Status#NETWORK_AUTHENTICATION_REQUIRED_511
         */
        public static final String NETWORK_AUTHENTICATION_REQUIRED_511 = "Network Authentication Required";
    }

    /**
     * An {@link ImmutableMap} of {@link #getCode()} mapped to {@link Status}.
     */
    public static final ImmutableMap<Integer, Status> VALUES_OF_CODES = stream(values())
            .collect(toImmutableMap(Status::getCode, identity()));

    /**
     * An {@link ImmutableMap} of lowercased {@link #getDescription()} mapped to {@link Status}.
     */
    public static final ImmutableMap<String, Status> VALUES_OF_LOWERCASED_DESCRIPTIONS = stream(values())
            .collect(toImmutableMap(value -> value.getDescription().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link Status} for the given <code>code</code>.
     *
     * @param code the {@link #getCode()}
     *
     * @return the {@link Status}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Status forCode(final int code) {
        return VALUES_OF_CODES.get(code);
    }

    /**
     * Gets the {@link Status} for the given <code>description</code>.
     *
     * @param description the case-insensitive {@link #getDescription()}
     *
     * @return the {@link Status}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Status forDescription(final String description) {
        return VALUES_OF_LOWERCASED_DESCRIPTIONS.get(description.toLowerCase(ROOT));
    }

    /**
     * Gets the {@link Status} for the given <code>string</code>.
     *
     * @param string the case-insensitive HTTP status {@link String} (e.g. a code, a description, or a code and a
     *               description)
     *
     * @return the {@link Status}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Status forString(final String string) {
        final var trimmed = string.trim();
        final var spaceIndex = trimmed.indexOf(' ');
        try {
            return forCode(Integer.parseInt(spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex)));
        } catch (final NumberFormatException numberFormatException) {
            return forDescription(trimmed);
        }
    }

    /**
     * @param code the HTTP status code <code>int</code>
     *
     * @return <code>true</code> if the given <code>code</code> represents an informational status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#informational_responses">
     * developer.mozilla.org</a>
     */
    public static boolean isInformational(final int code) {
        return code >= 100 && code <= 199;
    }

    /**
     * @param code the HTTP status code <code>int</code>
     *
     * @return <code>true</code> if the given <code>code</code> represents a successful status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#successful_responses">
     * developer.mozilla.org</a>
     */
    public static boolean isSuccessful(final int code) {
        return code >= 200 && code <= 299;
    }

    /**
     * @param code the HTTP status code <code>int</code>
     *
     * @return <code>true</code> if the given <code>code</code> represents a redirection status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#redirection_responses">
     * developer.mozilla.org</a>
     */
    public static boolean isRedirection(final int code) {
        return code >= 300 && code <= 399;
    }

    /**
     * @param code the HTTP status code <code>int</code>
     *
     * @return <code>true</code> if the given <code>code</code> represents a client error status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#client_error_responses">
     * developer.mozilla.org</a>
     */
    public static boolean isClientError(final int code) {
        return code >= 400 && code <= 499;
    }

    /**
     * @param code the HTTP status code <code>int</code>
     *
     * @return <code>true</code> if the given <code>code</code> represents a server error status, <code>false</code>
     * otherwise
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status#server_error_responses">
     * developer.mozilla.org</a>
     */
    public static boolean isServerError(final int code) {
        return code >= 500 && code <= 599;
    }

    /**
     * @param code the HTTP status code <code>int</code>
     *
     * @return <code>true</code> if {@link #isClientError(int)} or {@link #isServerError(int)}, <code>false</code>
     * otherwise
     */
    public static boolean isError(final int code) {
        return isClientError(code) || isServerError(code);
    }
}
