package net.jacobpeterson.jet.server.handle.request;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.accept.Accept;
import net.jacobpeterson.jet.common.http.header.authorization.BasicAuthentication;
import net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestCacheControl;
import net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDisposition;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.cookie.Cookie;
import net.jacobpeterson.jet.common.http.header.range.Range;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.common.http.version.Version;
import net.jacobpeterson.jet.common.io.bounded.BoundException;
import net.jacobpeterson.jet.common.io.bounded.BoundedInputStream;
import net.jacobpeterson.jet.common.io.bounded.OnBoundCount;
import net.jacobpeterson.jet.server.Jet;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.multipart.MultiPart;
import net.jacobpeterson.jet.server.handle.request.multipart.MultipartConfig;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.route.route.Route;
import net.jacobpeterson.jet.server.route.route.RouteMatch;
import net.jacobpeterson.jet.server.route.route.simple.exact.PathRouteMatch;
import net.jacobpeterson.jet.server.route.route.simple.pathparameters.PathParametersRouteMatch;
import net.jacobpeterson.jet.server.route.route.simple.pathregex.PathRegexRouteMatch;
import net.jacobpeterson.jet.server.route.route.simple.pathstartswith.PathStartsWithRouteMatch;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.MultiPartConfig;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.io.ByteStreams.readFully;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.stream.StreamSupport.stream;
import static net.jacobpeterson.jet.common.http.header.Header.ACCEPT;
import static net.jacobpeterson.jet.common.http.header.Header.AUTHORIZATION;
import static net.jacobpeterson.jet.common.http.header.Header.CACHE_CONTROL;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_TYPE;
import static net.jacobpeterson.jet.common.http.header.Header.COOKIE;
import static net.jacobpeterson.jet.common.http.header.Header.IF_MODIFIED_SINCE;
import static net.jacobpeterson.jet.common.http.header.Header.IF_UNMODIFIED_SINCE;
import static net.jacobpeterson.jet.common.http.header.Header.RANGE;
import static net.jacobpeterson.jet.common.http.status.Status.BAD_REQUEST_400;
import static net.jacobpeterson.jet.common.http.status.Status.CONTENT_TOO_LARGE_413;
import static net.jacobpeterson.jet.common.http.status.Status.RANGE_NOT_SATISFIABLE_416;
import static net.jacobpeterson.jet.common.http.url.Scheme.HTTP;
import static org.eclipse.jetty.http.MultiPartFormData.getParts;
import static org.eclipse.jetty.io.Content.Source.asInputStream;

/**
 * {@link Request} is a class that represents a web server request.
 * <p>
 * Note: this class is not thread-safe.
 */
@NullMarked
@RequiredArgsConstructor
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class Request {

    private static @Nullable MultiPartConfig defaultMultiPartConfig;

    private final Handle handle;
    private @LazyInit @Nullable Version version;
    private @LazyInit @Nullable Optional<Method> methodEnum;
    private @LazyInit @Nullable Url url;
    private @LazyInit @Nullable ImmutableListMultimap<String, String> headers;
    private @LazyInit @Nullable ImmutableMap<String, String> cookies;
    private @LazyInit @Nullable Optional<Accept> accept;
    private @LazyInit @Nullable Optional<ContentType> contentType;
    private @LazyInit @Nullable ImmutableList<Range> ranges;
    private @LazyInit @Nullable Optional<ZonedDateTime> ifModifiedSince;
    private @LazyInit @Nullable Optional<ZonedDateTime> ifUnmodifiedSince;
    private @LazyInit @Nullable Optional<RequestCacheControl> cacheControl;
    private @LazyInit @Nullable Optional<BasicAuthentication> basicAuthentication;
    private @LazyInit @Nullable ImmutableList<MultiPart> bodyMultiParts;

    /**
     * @return the {@link Route} that provided the {@link RouteMatch}
     */
    public Route getRouteOfMatch() {
        return handle.getInternals().getRouteOfMatch();
    }

    /**
     * @return the {@link RouteMatch} from {@link Route#match(Handle)}
     */
    public RouteMatch getRouteMatch() {
        return handle.getInternals().getRouteMatch();
    }

    /**
     * @return {@link #getRouteMatch()} cast to {@link PathRouteMatch}
     */
    public PathRouteMatch getRoutePathExact() {
        return (PathRouteMatch) getRouteMatch();
    }

    /**
     * @return {@link #getRouteMatch()} cast to {@link PathStartsWithRouteMatch}
     */
    public PathStartsWithRouteMatch getRoutePathStartsWith() {
        return (PathStartsWithRouteMatch) getRouteMatch();
    }

    /**
     * @return {@link #getRouteMatch()} cast to {@link PathRegexRouteMatch}
     */
    public PathRegexRouteMatch getRoutePathRegex() {
        return (PathRegexRouteMatch) getRouteMatch();
    }

    /**
     * @return {@link #getRouteMatch()} cast to {@link PathParametersRouteMatch}
     */
    public PathParametersRouteMatch getRoutePathParameters() {
        return (PathParametersRouteMatch) getRouteMatch();
    }

    /**
     * @return internally-cached {@link Version}
     */
    public Version getVersion() {
        if (version == null) {
            version = switch (handle.getInternals().getRequest().getConnectionMetaData().getHttpVersion()) {
                case HTTP_0_9 -> Version.HTTP_0_9;
                case HTTP_1_0 -> Version.HTTP_1_0;
                case HTTP_1_1 -> Version.HTTP_1_1;
                case HTTP_2 -> Version.HTTP_2;
                case HTTP_3 -> Version.HTTP_3;
            };
        }
        return version;
    }

    /**
     * @return the method {@link String}
     */
    public String getMethod() {
        return handle.getInternals().getRequest().getMethod();
    }

    /**
     * @return internally-cached {@link Method}, or <code>null</code> if {@link Method} has no mapping for
     * {@link #getMethod()}
     */
    public @Nullable Method getMethodEnum() {
        if (methodEnum == null) {
            methodEnum = Optional.ofNullable(Method.forString(getMethod()));
        }
        return methodEnum.orElse(null);
    }

    /**
     * @return internally-cached {@link Url}
     */
    public Url getUrl() {
        if (url == null) {
            final var uri = handle.getInternals().getRequest().getHttpURI();
            final var url = Url.builder();

            final var scheme = uri.getScheme();
            url.scheme(scheme != null ? scheme : HTTP.toString());

            final var user = uri.getUser();
            if (user != null) {
                url.encodedUserInfo(user);
            }

            final var host = uri.getHost();
            url.host(host != null ? host : "localhost");

            final var port = uri.getPort();
            if (port != -1) {
                url.port(port);
            }

            final var path = uri.getPath();
            if (path != null) {
                url.encodedPath(path);
            }

            final var query = uri.getQuery();
            if (query != null) {
                url.encodedQuery(query);
            }

            final var fragment = uri.getFragment();
            if (fragment != null) {
                url.encodedFragment(fragment);
            }

            this.url = url.build();
        }
        return url;
    }

    /**
     * @return internally-cached headers {@link String} {@link ImmutableListMultimap}
     */
    public ImmutableListMultimap<String, String> getHeaders() {
        if (headers == null) {
            final var headers = ImmutableListMultimap.<String, String>builder();
            for (final var headerValues : HttpFields.asMap(handle.getInternals().getRequest().getHeaders())
                    .entrySet()) {
                for (final var headerValue : headerValues.getValue()) {
                    headers.put(headerValues.getKey(), headerValue);
                }
            }
            this.headers = headers.build();
        }
        return headers;
    }

    /**
     * @return {@link #getHeader(String)} with {@link Header#toString()}
     */
    public @Nullable String getHeader(final Header header) {
        return getHeader(header.toString());
    }

    /**
     * @return {@link #getHeaders()} {@link ImmutableListMultimap#get(Object)} {@link ImmutableList#getFirst()} or
     * <code>null</code>
     */
    public @Nullable String getHeader(final String header) {
        final var headers = getHeaders().get(header);
        return headers.isEmpty() ? null : headers.getFirst();
    }

    /**
     * @return the {@link Header#CONTENT_LENGTH}, or <code>null</code> if unknown
     */
    public @Nullable Long getContentLength() {
        final var length = handle.getInternals().getRequest().getLength();
        return length == -1 ? null : length;
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#ACCEPT} {@link Accept#parse(String)}
     */
    public @Nullable Accept getAccept() throws StatusException {
        if (accept == null) {
            final var accept = getHeader(ACCEPT);
            try {
                this.accept = Optional.ofNullable(accept == null ? null : Accept.parse(accept));
            } catch (final Exception exception) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(ACCEPT),
                        exception);
            }
        }
        return accept.orElse(null);
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#CONTENT_TYPE}
     * {@link ContentType#parse(String)}
     */
    public @Nullable ContentType getContentType() throws StatusException {
        if (contentType == null) {
            final var contentType = getHeader(CONTENT_TYPE);
            try {
                this.contentType = Optional.ofNullable(contentType == null ? null : ContentType.parse(contentType));
            } catch (final Exception exception) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(CONTENT_TYPE),
                        exception);
            }
        }
        return contentType.orElse(null);
    }

    /**
     * @return {@link #getContentType()} {@link ContentType#getCharset()}, defaulting to
     * {@link StandardCharsets#UTF_8}
     */
    public Charset getCharset() {
        final var contentType = getContentType();
        if (contentType == null) {
            return UTF_8;
        }
        final var charset = contentType.getCharset();
        return charset != null ? charset : UTF_8;
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#RANGE} {@link Range#parse(String)}
     */
    public ImmutableList<Range> getRanges() throws StatusException {
        if (ranges == null) {
            final var range = getHeader(RANGE);
            if (range == null) {
                this.ranges = ImmutableList.of();
            } else {
                try {
                    this.ranges = Range.parse(range);
                } catch (final Exception exception) {
                    throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(RANGE),
                            exception);
                }
            }
        }
        return ranges;
    }

    /**
     * Throws {@link StatusException} {@link Status#RANGE_NOT_SATISFIABLE_416} if {@link #getRanges()}
     * {@link ImmutableList#size()} is greater than one.
     *
     * @return {@link #getRanges()} {@link ImmutableList#getFirst()}, or <code>null</code>
     */
    public @Nullable Range getRange() {
        final var ranges = getRanges();
        if (ranges.size() > 1) {
            throw new StatusException(RANGE_NOT_SATISFIABLE_416, "Multiple ranges unsupported");
        }
        return ranges.isEmpty() ? null : ranges.getFirst();
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#IF_MODIFIED_SINCE}
     * {@link ZonedDateTime#parse(CharSequence, DateTimeFormatter)} {@link DateTimeFormatter#RFC_1123_DATE_TIME}
     */
    public @Nullable ZonedDateTime getIfModifiedSince() throws StatusException {
        if (ifModifiedSince == null) {
            final var ifModifiedSince = getHeader(IF_MODIFIED_SINCE);
            try {
                this.ifModifiedSince = Optional.ofNullable(ifModifiedSince == null ? null :
                        ZonedDateTime.parse(ifModifiedSince, RFC_1123_DATE_TIME));
            } catch (final Exception exception) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(IF_MODIFIED_SINCE),
                        exception);
            }
        }
        return ifModifiedSince.orElse(null);
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#IF_UNMODIFIED_SINCE}
     * {@link ZonedDateTime#parse(CharSequence, DateTimeFormatter)} {@link DateTimeFormatter#RFC_1123_DATE_TIME}
     */
    public @Nullable ZonedDateTime getIfUnmodifiedSince() throws StatusException {
        if (ifUnmodifiedSince == null) {
            final var ifUnmodifiedSince = getHeader(IF_UNMODIFIED_SINCE);
            try {
                this.ifUnmodifiedSince = Optional.ofNullable(ifUnmodifiedSince == null ? null :
                        ZonedDateTime.parse(ifUnmodifiedSince, RFC_1123_DATE_TIME));
            } catch (final Exception exception) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(IF_UNMODIFIED_SINCE),
                        exception);
            }
        }
        return ifUnmodifiedSince.orElse(null);
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#CACHE_CONTROL}
     * {@link RequestCacheControl#parse(String)}
     */
    public @Nullable RequestCacheControl getCacheControl() throws StatusException {
        if (cacheControl == null) {
            final var cacheControl = getHeader(CACHE_CONTROL);
            try {
                this.cacheControl = Optional.ofNullable(cacheControl == null ? null :
                        RequestCacheControl.parse(cacheControl));
            } catch (final Exception exception) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(CACHE_CONTROL),
                        exception);
            }
        }
        return cacheControl.orElse(null);
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#COOKIE}
     * {@link Cookie#parseRequestCookies(String)}
     */
    public ImmutableMap<String, String> getCookies() {
        if (cookies == null) {
            final var cookie = getHeader(COOKIE);
            cookies = cookie == null ? ImmutableMap.of() : Cookie.parseRequestCookies(cookie);
        }
        return cookies;
    }

    /**
     * @return {@link #getCookies()} {@link ImmutableMap#get(Object)}
     */
    public @Nullable String getCookie(final String name) {
        return getCookies().get(name);
    }

    /**
     * @return internally-cached {@link #getHeader(Header)} {@link Header#AUTHORIZATION}
     * {@link BasicAuthentication#parse(String)}
     */
    public @Nullable BasicAuthentication getBasicAuthentication() throws StatusException {
        if (basicAuthentication == null) {
            final var authorization = getHeader(AUTHORIZATION);
            try {
                basicAuthentication = Optional.ofNullable(authorization == null ? null :
                        BasicAuthentication.parse(authorization));
            } catch (final Exception exception) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(AUTHORIZATION),
                        exception);
            }
        }
        return basicAuthentication.orElse(null);
    }

    /**
     * @return {@link #getBodyInputStream(Long, OnBoundCount)} with <code>boundCount</code> set to <code>null</code>
     * and <code>onBoundCount</code> set to <code>null</code>
     */
    public BoundedInputStream getBodyInputStream() {
        return getBodyInputStream(null, null);
    }

    /**
     * @return {@link #getBodyInputStream(Long, OnBoundCount)} with <code>onBoundCount</code> set to
     * {@link OnBoundCount#THROW}
     */
    public BoundedInputStream getBodyInputStream(final @Nullable Long boundCount) {
        return getBodyInputStream(boundCount, OnBoundCount.THROW);
    }

    /**
     * Gets the body {@link InputStream} wrapped in a {@link BoundedInputStream}.
     *
     * @param boundCount   see {@link BoundedInputStream#BoundedInputStream(InputStream, Long, OnBoundCount)}
     * @param onBoundCount see {@link BoundedInputStream#BoundedInputStream(InputStream, Long, OnBoundCount)}
     *
     * @return the body {@link BoundedInputStream}
     */
    public BoundedInputStream getBodyInputStream(final @Nullable Long boundCount,
            final @Nullable OnBoundCount onBoundCount) {
        return new BoundedInputStream(asInputStream(handle.getInternals().getRequest()), boundCount, onBoundCount);
    }

    /**
     * @return {@link #getBodyBytes(Integer)} with {@link Jet#getDefaultRequestBodyBoundCount()}
     */
    public byte[] getBodyBytes() {
        return getBodyBytes(handle.getInternals().getJet().getDefaultRequestBodyBoundCount());
    }

    /**
     * @param boundCount the bound count, or <code>null</code> for <code>{@link Integer#MAX_VALUE} - 8</code>
     *
     * @return {@link #getBodyInputStream(Long)} {@link InputStream#readAllBytes()} or
     * {@link ByteStreams#readFully(InputStream, byte[])} if {@link #getContentLength()} is non-<code>null</code>
     */
    public byte[] getBodyBytes(final @Nullable Integer boundCount) throws StatusException {
        final var appliedBoundCount = boundCount == null ? Integer.MAX_VALUE - 8 : boundCount.longValue();
        try (final var bodyInputStream = getBodyInputStream(appliedBoundCount)) {
            final var contentLength = getContentLength();
            if (contentLength != null) {
                if (contentLength > appliedBoundCount) {
                    throw new StatusException(CONTENT_TOO_LARGE_413);
                }
                final var bytes = new byte[contentLength.intValue()];
                readFully(bodyInputStream, bytes);
                return bytes;
            }
            return bodyInputStream.readAllBytes();
        } catch (final BoundException boundException) {
            throw new StatusException(CONTENT_TOO_LARGE_413, boundException);
        } catch (final IOException ioException) {
            throw new StatusException(BAD_REQUEST_400, ioException);
        }
    }

    /**
     * @return {@link #getBodyString(Integer)} with {@link Jet#getDefaultRequestBodyBoundCount()}
     */
    public String getBodyString() {
        return getBodyString(handle.getInternals().getJet().getDefaultRequestBodyBoundCount());
    }

    /**
     * @return {@link String#String(byte[], Charset)} with {@link #getBodyBytes(Integer)} and {@link #getCharset()}
     */
    public String getBodyString(final @Nullable Integer boundCount) throws StatusException {
        final var bodyBytes = getBodyBytes(boundCount);
        final var charset = getCharset();
        try {
            return new String(bodyBytes, charset);
        } catch (final Exception exception) {
            throw new StatusException(BAD_REQUEST_400, exception);
        }
    }

    /**
     * @return {@link #getBodyMultiParts(MultipartConfig)} with {@link Jet#getDefaultMultipartConfig()}
     */
    public ImmutableList<MultiPart> getBodyMultiParts() {
        if (defaultMultiPartConfig == null) { // Racy cache access is fine here due to immutable idempotency
            defaultMultiPartConfig = multipartConfigToJetty(handle.getInternals().getJet().getDefaultMultipartConfig());
        }
        return getBodyMultiParts(defaultMultiPartConfig);
    }

    /**
     * @return the {@link MultiPart} {@link ImmutableList} read from the body
     */
    public ImmutableList<MultiPart> getBodyMultiParts(final MultipartConfig config) {
        return getBodyMultiParts(multipartConfigToJetty(config));
    }

    private MultiPartConfig multipartConfigToJetty(final MultipartConfig config) {
        return new MultiPartConfig.Builder()
                .maxSize(config.getMaxTotalSize())
                .maxParts(config.getMaxPartCount())
                .maxPartSize(config.getMaxPartSize())
                .maxMemoryPartSize(config.getPartSizeMemoryToDiskThreshold())
                .location(config.getTemporaryDirectory())
                .build();
    }

    private ImmutableList<MultiPart> getBodyMultiParts(final MultiPartConfig jettyConfig) {
        if (bodyMultiParts == null) {
            try {
                final var request = handle.getInternals().getRequest();
                bodyMultiParts = stream(getParts(request, request, getHeader(CONTENT_TYPE), jettyConfig)
                        .spliterator(), false)
                        .map(MultiPart::new)
                        .collect(toImmutableList());
            } catch (final Exception exception) {
                throw new StatusException(BAD_REQUEST_400, exception);
            }
        }
        return bodyMultiParts;
    }

    /**
     * @return {@link #getBodyMultiParts()} {@link Stream#filter(Predicate)}
     * {@link MultiPart#getContentDisposition()} {@link ContentDisposition#getName()} or <code>null</code>
     */
    public @Nullable MultiPart getBodyMultiPart(final String name) {
        return getBodyMultiPart(getBodyMultiParts(), name);
    }

    /**
     * @return {@link #getBodyMultiParts(MultipartConfig)} {@link Stream#filter(Predicate)}
     * {@link MultiPart#getContentDisposition()} {@link ContentDisposition#getName()} or <code>null</code>
     */
    public @Nullable MultiPart getBodyMultiPart(final MultipartConfig config, final String name) {
        return getBodyMultiPart(getBodyMultiParts(config), name);
    }

    private @Nullable MultiPart getBodyMultiPart(final ImmutableList<MultiPart> multiParts, final String name) {
        return multiParts.stream()
                .filter(multiPart -> multiPart.getContentDisposition() != null &&
                        name.equals(multiPart.getContentDisposition().getName()))
                .findFirst()
                .orElse(null);
    }
}
