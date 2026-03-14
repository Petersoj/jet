package net.jacobpeterson.jet.server.handle.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.Headers;
import net.jacobpeterson.jet.common.http.header.ImmutableHeaders;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDisposition;
import net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding;
import net.jacobpeterson.jet.common.http.header.contentrange.ContentRange;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.ContentSecurityPolicy;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.cookie.Cookie;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.header.range.Range;
import net.jacobpeterson.jet.common.http.header.stricttransportsecurity.StrictTransportSecurity;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.common.util.string.StringUtil;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.resource.Resource;
import net.jacobpeterson.jet.server.handler.handler.directory.FileDirectoryHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Objects.requireNonNull;
import static net.jacobpeterson.jet.common.http.header.Header.ACCEPT_RANGES;
import static net.jacobpeterson.jet.common.http.header.Header.CACHE_CONTROL;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_DISPOSITION;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_ENCODING;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_LENGTH;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_RANGE;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_SECURITY_POLICY;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_TYPE;
import static net.jacobpeterson.jet.common.http.header.Header.ETAG;
import static net.jacobpeterson.jet.common.http.header.Header.IF_NONE_MATCH;
import static net.jacobpeterson.jet.common.http.header.Header.LAST_MODIFIED;
import static net.jacobpeterson.jet.common.http.header.Header.LOCATION;
import static net.jacobpeterson.jet.common.http.header.Header.SET_COOKIE;
import static net.jacobpeterson.jet.common.http.header.Header.STRICT_TRANSPORT_SECURITY;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_JSON_UTF_8;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_OCTET_STREAM;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_HTML_UTF_8;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_PLAIN_UTF_8;
import static net.jacobpeterson.jet.common.http.header.range.Range.BYTES_UNIT;
import static net.jacobpeterson.jet.common.http.status.Status.FOUND_302;
import static net.jacobpeterson.jet.common.http.status.Status.MOVED_PERMANENTLY_301;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_MODIFIED_304;
import static net.jacobpeterson.jet.common.http.status.Status.OK_200;
import static net.jacobpeterson.jet.common.http.status.Status.PARTIAL_CONTENT_206;
import static net.jacobpeterson.jet.common.util.string.StringUtil.isLikelyPlainText;

/**
 * {@link Response} is a class that represents a web server request.
 * <p>
 * Note: this class is not thread-safe.
 */
@NullMarked
@RequiredArgsConstructor
public final class Response {

    private final Handle handle;

    /**
     * The {@link Status} code.
     * <p>
     * Defaults to {@link Status#OK_200} {@link Status#getCode()}.
     */
    private @Getter int statusCode = OK_200.getCode();

    /**
     * The {@link Status}.
     * <p>
     * Defaults to {@link Status#OK_200}.
     */
    private @Getter @Nullable Status status = OK_200;

    /**
     * The {@link Headers}.
     */
    private final @Getter Headers headers = Headers.create();

    private ContentSecurityPolicy.@Nullable Builder contentSecurityPolicyBuilder;

    /**
     * The body {@link InputStream}.
     * <p>
     * {@link InputStream#close()} is guaranteed to be called.
     * <p>
     * Cannot be used with {@link #setBodyOutputStreamApplier(Consumer)}.
     */
    private @Getter @Nullable InputStream bodyInputStream;

    /**
     * The body {@link OutputStream} applier.
     * <p>
     * Cannot be used with {@link #setBodyInputStream(InputStream)}.
     */
    private @Getter @Setter @Nullable Consumer<OutputStream> bodyOutputStreamApplier;

    /**
     * Sets {@link #getStatusCode()} and {@link #getStatus()} with {@link Status#forCode(int)}.
     */
    public void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
        status = Status.forCode(statusCode);
    }

    /**
     * Sets {@link #getStatus()} and {@link #getStatusCode()} with {@link Status#getCode()}.
     */
    public void setStatus(final Status status) {
        this.status = status;
        statusCode = status.getCode();
    }

    /**
     * @return {@link #getHeader(String)} with {@link Header#toString()}
     */
    public @Nullable String getHeader(final Header header) {
        return getHeader(header.toString());
    }

    /**
     * @return {@link #getHeaders()} {@link ImmutableHeaders#get(Object)} {@link List#getFirst()} or <code>null</code>
     */
    public @Nullable String getHeader(final String header) {
        final var headers = this.headers.get(header);
        return headers.isEmpty() ? null : headers.getFirst();
    }

    /**
     * Calls {@link #setHeader(String, String)} with {@link Header#toString()}.
     */
    public void setHeader(final Header key, final String value) {
        setHeader(key.toString(), value);
    }

    /**
     * Calls {@link #getHeaders()} {@link Headers#removeAll(Object)} with the given <code>key</code>, then
     * calls {@link #getHeaders()} {@link Headers#put(Object, Object)}.
     */
    public void setHeader(final String key, final String value) {
        headers.removeAll(key);
        headers.put(key, value);
    }

    /**
     * Calls {@link #addHeader(String, String)} with {@link Header#toString()}.
     */
    public void addHeader(final Header key, final String value) {
        addHeader(key.toString(), value);
    }

    /**
     * Calls {@link #getHeaders()} {@link Headers#put(Object, Object)}.
     */
    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#CONTENT_LENGTH}.
     */
    public void setContentLength(final Long contentLength) {
        setHeader(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#LAST_MODIFIED}
     * {@link DateTimeFormatter#RFC_1123_DATE_TIME} {@link DateTimeFormatter#format(TemporalAccessor)}
     * {@link ZonedDateTime#withZoneSameInstant(ZoneId)} {@link ZoneOffset#UTC}.
     */
    public void setLastModified(final ZonedDateTime lastModified) {
        setHeader(LAST_MODIFIED, RFC_1123_DATE_TIME.format(lastModified.withZoneSameInstant(UTC)));
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#LAST_MODIFIED}
     * {@link DateTimeFormatter#RFC_1123_DATE_TIME} {@link DateTimeFormatter#format(TemporalAccessor)}
     * {@link Instant#atZone(ZoneId)} {@link ZoneOffset#UTC}.
     */
    public void setLastModified(final Instant lastModified) {
        setHeader(LAST_MODIFIED, RFC_1123_DATE_TIME.format(lastModified.atZone(UTC)));
    }

    /**
     * Calls {@link #setContentType(String)} with {@link ContentType#toString()}.
     */
    public void setContentType(final ContentType contentType) {
        setContentType(contentType.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#CONTENT_TYPE}.
     */
    public void setContentType(final String contentType) {
        setHeader(CONTENT_TYPE, contentType);
    }

    /**
     * Calls {@link #setContentEncoding(String)} with {@link ContentEncoding#toString()}.
     */
    public void setContentEncoding(final ContentEncoding contentEncoding) {
        setContentEncoding(contentEncoding.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#CONTENT_ENCODING}.
     */
    public void setContentEncoding(final String contentEncoding) {
        setHeader(CONTENT_ENCODING, contentEncoding);
    }

    /**
     * Calls {@link #setContentRange(String)} with {@link ContentRange#toString()}.
     */
    public void setContentRange(final ContentRange contentRange) {
        setContentRange(contentRange.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#CONTENT_RANGE}.
     */
    public void setContentRange(final String contentRange) {
        setHeader(CONTENT_RANGE, contentRange);
    }

    /**
     * Calls {@link #setContentDisposition(String)} with {@link ContentDisposition#toString()}.
     */
    public void setContentDisposition(final ContentDisposition contentDisposition) {
        setContentDisposition(contentDisposition.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#CONTENT_DISPOSITION}.
     */
    public void setContentDisposition(final String contentDisposition) {
        setHeader(CONTENT_DISPOSITION, contentDisposition);
    }

    /**
     * Calls {@link #setETag(String)} with {@link ETag#toString()}.
     */
    public void setETag(final ETag eTag) {
        setETag(eTag.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#ETAG}.
     */
    public void setETag(final String eTag) {
        setHeader(ETAG, eTag);
    }

    /**
     * Calls {@link #setCacheControl(String)} with {@link ResponseCacheControl#toString()}.
     */
    public void setCacheControl(final ResponseCacheControl responseCacheControl) {
        setCacheControl(responseCacheControl.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#CONTENT_RANGE}.
     */
    public void setCacheControl(final String responseCacheControl) {
        setHeader(CACHE_CONTROL, responseCacheControl);
    }

    /**
     * Calls {@link #setStrictTransportSecurity(String)} with {@link StrictTransportSecurity#toString()}.
     */
    public void setStrictTransportSecurity(final StrictTransportSecurity strictTransportSecurity) {
        setStrictTransportSecurity(strictTransportSecurity.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#STRICT_TRANSPORT_SECURITY}.
     */
    public void setStrictTransportSecurity(final String strictTransportSecurity) {
        setHeader(STRICT_TRANSPORT_SECURITY, strictTransportSecurity);
    }

    /**
     * Calls {@link #setContentSecurityPolicy(String)} with {@link ContentSecurityPolicy#toString()}.
     */
    public void setContentSecurityPolicy(final ContentSecurityPolicy contentSecurityPolicy) {
        setContentSecurityPolicy(contentSecurityPolicy.toString());
    }

    /**
     * Calls {@link #setHeader(Header, String)} {@link Header#CONTENT_SECURITY_POLICY}.
     */
    public void setContentSecurityPolicy(final String contentSecurityPolicy) {
        setHeader(CONTENT_SECURITY_POLICY, contentSecurityPolicy);
    }

    /**
     * @return internally-cached {@link ContentSecurityPolicy.Builder}
     */
    public ContentSecurityPolicy.Builder getContentSecurityPolicyBuilder() {
        if (contentSecurityPolicyBuilder == null) {
            contentSecurityPolicyBuilder = ContentSecurityPolicy.builder();
        }
        return contentSecurityPolicyBuilder;
    }

    /**
     * @return <code>true</code> if {@link #getContentSecurityPolicyBuilder()} has been called, <code>false</code>
     * otherwise
     */
    public boolean hasContentSecurityPolicyBuilder() {
        return contentSecurityPolicyBuilder != null;
    }

    /**
     * Calls {@link #addHeader(Header, String)} {@link Header#SET_COOKIE} {@link Cookie#toResponseString()}.
     */
    public void addCookie(final Cookie cookie) {
        addHeader(SET_COOKIE, cookie.toResponseString());
    }

    /**
     * Calls {@link #redirectTemporarily(String)} with {@link Url#toString()}.
     */
    public void redirectTemporarily(final Url location) {
        redirectTemporarily(location.toString());
    }

    /**
     * Calls {@link #setHeader(String, String)} with {@link Header#LOCATION} and calls {@link #setStatus(Status)} with
     * {@link Status#FOUND_302}.
     */
    public void redirectTemporarily(final String location) {
        setStatus(FOUND_302);
        setHeader(LOCATION, location);
    }

    /**
     * Calls {@link #redirectPermanently(String)} with {@link Url#toString()}.
     */
    public void redirectPermanently(final Url location) {
        redirectPermanently(location.toString());
    }

    /**
     * Calls {@link #setHeader(String, String)} with {@link Header#LOCATION} and calls {@link #setStatus(Status)} with
     * {@link Status#MOVED_PERMANENTLY_301}.
     */
    public void redirectPermanently(final String location) {
        setStatus(MOVED_PERMANENTLY_301);
        setHeader(LOCATION, location);
    }

    /**
     * Sets {@link #getBodyInputStream()}.
     */
    public void setBodyInputStream(final @Nullable InputStream bodyInputStream) {
        if (this.bodyInputStream != null) {
            try {
                this.bodyInputStream.close();
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
        this.bodyInputStream = bodyInputStream;
    }

    /**
     * Calls {@link #setContentLength(Long)} and {@link #setBodyInputStream(InputStream)} with
     * {@link ByteArrayInputStream#ByteArrayInputStream(byte[])}.
     */
    public void setBodyBytes(final byte[] bytes) {
        setBodyInputStream(new ByteArrayInputStream(bytes));
        setContentLength((long) bytes.length);
    }

    /**
     * Calls {@link #setBodyString(String, Charset)} with {@link StandardCharsets#UTF_8} and calls
     * {@link #setContentLength(Long)}.
     */
    public void setBodyString(final String string) {
        setBodyString(string, UTF_8);
    }

    /**
     * Calls {@link #setBodyBytes(byte[])} with {@link String#getBytes(Charset)}.
     */
    public void setBodyString(final String string, final Charset charset) {
        setBodyBytes(string.getBytes(charset));
    }

    /**
     * Calls {@link #responseInputStream(Status, ContentType, InputStream)} with {@link Status#OK_200}.
     */
    public void responseInputStream(final ContentType contentType, final InputStream inputStream) {
        responseInputStream(OK_200, contentType, inputStream);
    }

    /**
     * Calls {@link #responseInputStream(int, ContentType, InputStream)} with {@link Status#getCode()}.
     */
    public void responseInputStream(final Status status, final ContentType contentType, final InputStream inputStream) {
        responseInputStream(status.getCode(), contentType, inputStream);
    }

    /**
     * Shortcut for {@link #setStatusCode(int)}, {@link #setContentType(ContentType)}, and
     * {@link #setBodyInputStream(InputStream)}.
     */
    public void responseInputStream(final int statusCode, final ContentType contentType,
            final InputStream inputStream) {
        setStatusCode(statusCode);
        setContentType(contentType);
        setBodyInputStream(inputStream);
    }

    /**
     * Calls {@link #responseBytes(Status, ContentType, byte[])} with {@link Status#OK_200}.
     */
    public void responseBytes(final ContentType contentType, final byte[] bytes) {
        responseBytes(OK_200, contentType, bytes);
    }

    /**
     * Calls {@link #responseBytes(int, ContentType, byte[])} with {@link Status#getCode()}.
     */
    public void responseBytes(final Status status, final ContentType contentType, final byte[] bytes) {
        responseBytes(status.getCode(), contentType, bytes);
    }

    /**
     * Shortcut for {@link #setStatusCode(int)}, {@link #setContentType(ContentType)}, and
     * {@link #setBodyBytes(byte[])}.
     */
    public void responseBytes(final int statusCode, final ContentType contentType, final byte[] bytes) {
        setStatusCode(statusCode);
        setContentType(contentType);
        setBodyBytes(bytes);
    }

    /**
     * Calls {@link #responseString(Status, ContentType, String)} with {@link Status#OK_200}.
     */
    public void responseString(final ContentType contentType, final String string) {
        responseString(OK_200, contentType, string);
    }

    /**
     * Calls {@link #responseString(int, ContentType, String)} with {@link Status#getCode()}.
     */
    public void responseString(final Status status, final ContentType contentType, final String string) {
        responseString(status.getCode(), contentType, string);
    }

    /**
     * Shortcut for {@link #setStatusCode(int)}, {@link #setContentType(ContentType)}, and
     * {@link #setBodyString(String, Charset)}.
     */
    public void responseString(final int statusCode, final ContentType contentType, final String string) {
        setStatusCode(statusCode);
        final var contentTypeWithCharset = contentType.getCharset() != null ? contentType :
                contentType.withCharset(UTF_8);
        setContentType(contentTypeWithCharset);
        setBodyString(string, requireNonNull(contentTypeWithCharset.getCharset()));
    }

    /**
     * Calls {@link #responseText(Status, String)} with {@link Status#OK_200}.
     */
    public void responseText(final String text) {
        responseText(OK_200, text);
    }

    /**
     * Calls {@link #responseText(int, String)} with {@link Status#getCode()}.
     */
    public void responseText(final Status status, final String text) {
        responseText(status.getCode(), text);
    }

    /**
     * Calls {@link #responseString(int, ContentType, String)} with {@link ContentType#TEXT_PLAIN_UTF_8}.
     */
    public void responseText(final int statusCode, final String text) {
        responseString(statusCode, TEXT_PLAIN_UTF_8, text);
    }

    /**
     * Calls {@link #responseHtml(Status, String)} with {@link Status#OK_200}.
     */
    public void responseHtml(final String html) {
        responseHtml(OK_200, html);
    }

    /**
     * Calls {@link #responseHtml(int, String)} with {@link Status#getCode()}.
     */
    public void responseHtml(final Status status, final String html) {
        responseHtml(status.getCode(), html);
    }

    /**
     * Calls {@link #responseString(int, ContentType, String)} with {@link ContentType#TEXT_HTML_UTF_8}.
     */
    public void responseHtml(final int statusCode, final String html) {
        responseString(statusCode, TEXT_HTML_UTF_8, html);
    }

    /**
     * Calls {@link #responseJson(Status, String)} with {@link Status#OK_200}.
     */
    public void responseJson(final String json) {
        responseJson(OK_200, json);
    }

    /**
     * Calls {@link #responseJson(int, String)} with {@link Status#getCode()}.
     */
    public void responseJson(final Status status, final String json) {
        responseJson(status.getCode(), json);
    }

    /**
     * Calls {@link #responseString(int, ContentType, String)} with {@link ContentType#APPLICATION_JSON_UTF_8}.
     */
    public void responseJson(final int statusCode, final String json) {
        responseString(statusCode, APPLICATION_JSON_UTF_8, json);
    }

    /**
     * Calls {@link #responseResource(Resource, boolean, Integer, boolean)} with
     * <code>resource</code> set to the given {@link Resource#withRange(Range)} with {@link Request#getRange()} if
     * non-<code>null</code>,
     * <code>acceptRanges</code> set to <code>true</code>,
     * <code>contentTypePeekLength</code> set to {@link Resource#DEFAULT_PEEK_LENGTH}, and
     * <code>setBodyInputStream</code> to the negation of {@link Request#getMethodEnum()}
     * {@link Method#hasNoResponseBody()}.
     */
    public void responseResource(final Resource resource) {
        final var request = handle.getRequest();
        final var requestRange = request.getRange();
        final var requestMethod = request.getMethodEnum();
        responseResource(requestRange != null ? resource.withRange(requestRange) : resource, true,
                Resource.DEFAULT_PEEK_LENGTH, requestMethod == null || !requestMethod.hasNoResponseBody());
    }

    /**
     * Applies the given {@link Resource} to this {@link Response}.
     *
     * @param resource              the {@link Resource}
     * @param acceptRanges          <code>true</code> to {@link #setHeader(Header, String)} {@link Header#ACCEPT_RANGES}
     *                              {@link Range#BYTES_UNIT}, <code>false</code> otherwise
     * @param contentTypePeekLength if non-<code>null</code> and the given {@link Resource#getContentType()} is
     *                              <code>null</code>, then peek this many bytes into {@link Resource#getContent()} and
     *                              use {@link StringUtil#isLikelyPlainText(Charset, byte[])} to apply either
     *                              {@link ContentType#TEXT_PLAIN_UTF_8} or {@link ContentType#APPLICATION_OCTET_STREAM}
     * @param setBodyInputStream    <code>true</code> to {@link #setBodyInputStream(InputStream)} to
     *                              {@link Resource#getContent()}, <code>false</code> to not
     *                              {@link #setBodyInputStream(InputStream)}
     */
    public void responseResource(final Resource resource, final boolean acceptRanges,
            final @Nullable Integer contentTypePeekLength, boolean setBodyInputStream) {
        final var request = handle.getRequest();
        var notModified = false;
        final var lastModified = resource.getLastModified();
        if (lastModified != null) {
            setLastModified(lastModified);
            final var ifModifiedSince = request.getIfModifiedSince();
            if (ifModifiedSince != null && !lastModified.isAfter(ifModifiedSince.toInstant())) {
                notModified = true;
            }
        }
        final var etag = resource.getEtag();
        if (etag != null) {
            setETag(etag);
            final var ifNoneMatch = request.getHeader(IF_NONE_MATCH);
            if (ifNoneMatch != null && ifNoneMatch.equals(etag.getValueQuoted())) {
                notModified = true;
            }
        }
        if (notModified) {
            setStatus(NOT_MODIFIED_304);
            return;
        }
        if (acceptRanges) {
            setHeader(ACCEPT_RANGES, BYTES_UNIT);
        }

        final var contentLength = resource.getContentLength();
        if (contentLength != null) {
            setContentLength(contentLength);
        }

        final var contentEncoding = resource.getContentEncoding();
        if (contentEncoding != null) {
            setContentEncoding(contentEncoding);
        }

        final var contentType = resource.getContentType();
        if (contentType != null) {
            setContentType(contentType);
        } else if (contentTypePeekLength != null &&
                (contentEncoding == null || !contentEncoding.getType().isDictionaryRequired())) {
            final byte[] peekedBytes;
            final var content = resource.getContent().get();
            InputStream contentMarkable = null;
            try {
                contentMarkable = content.markSupported() ? content : new BufferedInputStream(content);
                contentMarkable.mark(Integer.MAX_VALUE);
                if (contentEncoding != null) {
                    try (final var decompressed = contentEncoding.getType()
                            .decompress(new FilterInputStream(contentMarkable) {
                                // Close decompression stream, but do not close underlying stream.
                                @Override public void close() {}
                            })) {
                        peekedBytes = decompressed.readNBytes(contentTypePeekLength);
                    }
                } else {
                    peekedBytes = contentMarkable.readNBytes(contentTypePeekLength);
                }
                if (setBodyInputStream) {
                    setBodyInputStream = false;
                    contentMarkable.reset();
                    setBodyInputStream(contentMarkable);
                } else {
                    contentMarkable.close();
                }
            } catch (final Throwable throwable) {
                try {
                    (contentMarkable != null ? contentMarkable : content).close();
                } catch (final Throwable closeThrowable) {
                    throwable.addSuppressed(closeThrowable);
                }
                throw new RuntimeException(throwable);
            }
            setContentType(isLikelyPlainText(UTF_8, peekedBytes) ? TEXT_PLAIN_UTF_8 : APPLICATION_OCTET_STREAM);
        } else {
            setContentType(APPLICATION_OCTET_STREAM);
        }

        final var contentRange = resource.getContentRange();
        if (contentRange != null) {
            setContentRange(contentRange);
            setStatus(PARTIAL_CONTENT_206);
        }

        final var contentDisposition = resource.getContentDisposition();
        if (contentDisposition != null) {
            setContentDisposition(contentDisposition);
        }

        if (setBodyInputStream) {
            setBodyInputStream(resource.getContent().get());
        }
    }

    /**
     * Calls {@link #responseResource(Resource)} with {@link Resource#ofClasspath(Class, String)}.
     */
    public void responseClasspath(final Class<?> clazz, final String resourcePath) {
        responseResource(Resource.ofClasspath(clazz, resourcePath));
    }

    /**
     * Calls {@link #responseResource(Resource)} with
     * {@link Resource#ofClasspath(Class, String, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    public void responseClasspath(final Class<?> clazz, final String resourcePath,
            final boolean trustedContentType, final @Nullable ContentType untrustedContentType,
            final @Nullable Integer peekLength, final @Nullable ContentEncoding contentEncoding,
            final boolean exposeFilename) {
        responseResource(Resource.ofClasspath(clazz, resourcePath, trustedContentType, untrustedContentType, peekLength,
                contentEncoding, exposeFilename));
    }

    /**
     * Calls {@link #responseResource(Resource)} with
     * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     * <p>
     * Note: it is recommended to use {@link FileDirectoryHandler} instead of calling this method directly.
     */
    public void ofFile(final Path file, final boolean strongETag, final boolean trustedContentType,
            final @Nullable ContentType untrustedContentType, final @Nullable Integer peekLength,
            final @Nullable ContentEncoding contentEncoding, final boolean exposeFilename) {
        responseResource(Resource.ofFile(file, strongETag, trustedContentType, untrustedContentType, peekLength,
                contentEncoding, exposeFilename));
    }
}
