package net.jacobpeterson.jet.server.handle.response;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDisposition;
import net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding;
import net.jacobpeterson.jet.common.http.header.contentrange.ContentRange;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.ContentSecurityPolicy;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.cookie.Cookie;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.header.headers.Headers;
import net.jacobpeterson.jet.common.http.header.range.Range;
import net.jacobpeterson.jet.common.http.header.stricttransportsecurity.StrictTransportSecurity;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.common.util.string.StringUtil;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.compression.CompressionConfig;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handle.response.resource.Resource;
import net.jacobpeterson.jet.server.handle.response.sse.Sse;
import net.jacobpeterson.jet.server.handler.directory.FileDirectoryHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.google.common.util.concurrent.Uninterruptibles.awaitUninterruptibly;
import static com.google.common.util.concurrent.Uninterruptibles.joinUninterruptibly;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofSeconds;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Objects.requireNonNull;
import static net.jacobpeterson.jet.common.http.header.Header.ACCEPT_RANGES;
import static net.jacobpeterson.jet.common.http.header.Header.CACHE_CONTROL;
import static net.jacobpeterson.jet.common.http.header.Header.CONNECTION;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_DISPOSITION;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_ENCODING;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_LENGTH;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_RANGE;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_SECURITY_POLICY;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_TYPE;
import static net.jacobpeterson.jet.common.http.header.Header.ETAG;
import static net.jacobpeterson.jet.common.http.header.Header.LAST_MODIFIED;
import static net.jacobpeterson.jet.common.http.header.Header.LOCATION;
import static net.jacobpeterson.jet.common.http.header.Header.SET_COOKIE;
import static net.jacobpeterson.jet.common.http.header.Header.STRICT_TRANSPORT_SECURITY;
import static net.jacobpeterson.jet.common.http.header.Header.X_ACCEL_BUFFERING;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.NO_CACHE;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_JSON_UTF_8;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_OCTET_STREAM;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_EVENT_STREAM_UTF_8;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_HTML_UTF_8;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_PLAIN_UTF_8;
import static net.jacobpeterson.jet.common.http.header.range.Range.BYTES_UNIT;
import static net.jacobpeterson.jet.common.http.method.Method.HEAD;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_MODIFIED_304;
import static net.jacobpeterson.jet.common.http.status.Status.OK_200;
import static net.jacobpeterson.jet.common.http.status.Status.PARTIAL_CONTENT_206;
import static net.jacobpeterson.jet.common.http.status.Status.PERMANENT_REDIRECT_308;
import static net.jacobpeterson.jet.common.http.status.Status.RANGE_NOT_SATISFIABLE_416;
import static net.jacobpeterson.jet.common.http.status.Status.TEMPORARY_REDIRECT_307;
import static net.jacobpeterson.jet.common.util.string.StringUtil.isLikelyUtf8;
import static net.jacobpeterson.jet.server.handle.response.resource.Resource.DEFAULT_PEEK_LENGTH;

/**
 * {@link Response} is a class that represents a web server request.
 * <p>
 * Note: this class is not thread-safe.
 */
@RequiredArgsConstructor @Slf4j
@NullMarked
public final class Response {

    /**
     * The default SSE keep-alive period: <code>15 seconds</code>
     */
    public static final Duration DEFAULT_SSE_KEEP_ALIVE_PERIOD = ofSeconds(15);

    private final Handle handle;

    /**
     * The {@link Status} code.
     * <p>
     * Defaults to {@link Status#OK_200} {@link Status#getCode()}.
     */
    private @Getter @Setter int statusCode = OK_200.getCode();

    /**
     * The {@link Headers}.
     */
    private final @Getter Headers headers = Headers.create();

    /**
     * The {@link CompressionConfig}, or <code>null</code> to disable compression.
     * <p>
     * Defaults to {@link CompressionConfig#DEFAULT}.
     */
    private @Getter @Setter @Nullable CompressionConfig compressionConfig = CompressionConfig.DEFAULT;

    /**
     * The body {@link OutputStream} applier used to write the body.
     * <p>
     * Note: this is not guaranteed to be called.
     * <p>
     * Note: the provided {@link OutputStream} is thread-safe.
     */
    private @Getter @Setter @Nullable Consumer<OutputStream> bodyOutputStreamApplier;

    private @Nullable List<Runnable> afters;

    /**
     * Calls {@link #setStatusCode(int)} with {@link Status#getCode()}.
     */
    public void setStatus(final Status status) {
        setStatusCode(status.getCode());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CONTENT_LENGTH} and {@link String#valueOf(int)}.
     */
    public void setContentLength(final Long contentLength) {
        headers.set(CONTENT_LENGTH.toString(), String.valueOf(contentLength));
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#LAST_MODIFIED} and
     * {@link DateTimeFormatter#RFC_1123_DATE_TIME} {@link DateTimeFormatter#format(TemporalAccessor)}
     * {@link ZonedDateTime#withZoneSameInstant(ZoneId)} {@link ZoneOffset#UTC}.
     */
    public void setLastModified(final ZonedDateTime lastModified) {
        headers.set(LAST_MODIFIED.toString(), RFC_1123_DATE_TIME.format(lastModified.withZoneSameInstant(UTC)));
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#LAST_MODIFIED} and
     * {@link DateTimeFormatter#RFC_1123_DATE_TIME} {@link DateTimeFormatter#format(TemporalAccessor)}
     * {@link Instant#atZone(ZoneId)} {@link ZoneOffset#UTC}.
     */
    public void setLastModified(final Instant lastModified) {
        headers.set(LAST_MODIFIED.toString(), RFC_1123_DATE_TIME.format(lastModified.atZone(UTC)));
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CONTENT_TYPE} and {@link ContentType#toString()}.
     */
    public void setContentType(final ContentType contentType) {
        headers.set(CONTENT_TYPE.toString(), contentType.toString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CONTENT_ENCODING} and
     * {@link ContentEncoding#toString()}.
     */
    public void setContentEncoding(final ContentEncoding contentEncoding) {
        headers.set(CONTENT_ENCODING.toString(), contentEncoding.toString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CONTENT_RANGE} and {@link ContentRange#toString()}.
     */
    public void setContentRange(final ContentRange contentRange) {
        headers.set(CONTENT_RANGE.toString(), contentRange.toString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CONTENT_DISPOSITION} and
     * {@link ContentDisposition#toString()}.
     */
    public void setContentDisposition(final ContentDisposition contentDisposition) {
        headers.set(CONTENT_DISPOSITION.toString(), contentDisposition.toString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#ETAG} and {@link ETag#toString()}.
     */
    public void setETag(final ETag eTag) {
        headers.set(ETAG.toString(), eTag.toString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CACHE_CONTROL} and
     * {@link ResponseCacheControl#toString()}.
     */
    public void setCacheControl(final ResponseCacheControl responseCacheControl) {
        headers.set(CACHE_CONTROL.toString(), responseCacheControl.toString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#STRICT_TRANSPORT_SECURITY} and
     * {@link StrictTransportSecurity#toString()}.
     */
    public void setStrictTransportSecurity(final StrictTransportSecurity strictTransportSecurity) {
        headers.set(STRICT_TRANSPORT_SECURITY.toString(), strictTransportSecurity.toString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CONTENT_SECURITY_POLICY} and
     * {@link ContentSecurityPolicy#toString()}.
     */
    public void setContentSecurityPolicy(final ContentSecurityPolicy contentSecurityPolicy) {
        headers.set(CONTENT_SECURITY_POLICY.toString(), contentSecurityPolicy.toString());
    }

    /**
     * Calls {@link Headers#put(Object, Object)} with {@link Header#SET_COOKIE} and {@link Cookie#toResponseString()}.
     */
    public void addCookie(final Cookie cookie) {
        headers.put(SET_COOKIE.toString(), cookie.toResponseString());
    }

    /**
     * Calls {@link Headers#set(String, String)} with {@link Header#CONNECTION} and <code>"close"</code>.
     */
    public void setConnectionClose() {
        headers.set(CONNECTION.toString(), "close");
    }

    /**
     * {@link RedirectType} represents a type of HTTP redirect, either temporary or permanent.
     */
    @RequiredArgsConstructor @Getter
    public enum RedirectType {

        /**
         * @see Status#TEMPORARY_REDIRECT_307
         */
        TEMPORARY(TEMPORARY_REDIRECT_307),

        /**
         * @see Status#PERMANENT_REDIRECT_308
         */
        PERMANENT(PERMANENT_REDIRECT_308);

        /**
         * The {@link Status} used to define the {@link RedirectType} in a {@link Response}.
         */
        final Status status;
    }

    /**
     * Calls {@link #redirect(RedirectType, String)} with {@link Url#toString()}.
     */
    public void redirect(final RedirectType type, final Url location) {
        redirect(type, location.toString());
    }

    /**
     * Calls {@link #setStatus(Status)} with {@link RedirectType#getStatus()}, then calls
     * {@link Headers#set(String, String)} with {@link Header#LOCATION}.
     */
    public void redirect(final RedirectType type, final String location) {
        setStatus(type.getStatus());
        headers.set(LOCATION.toString(), location);
    }

    /**
     * Calls {@link #setCompressionConfig(CompressionConfig)} with <code>null</code>.
     */
    public void disableCompression() {
        setCompressionConfig(null);
    }

    /**
     * Calls {@link #setBodyInputStream(InputStream, boolean)} with <code>close</code> set to <code>true</code>.
     */
    public void setBodyInputStream(final @Nullable InputStream bodyInputStream) {
        setBodyInputStream(bodyInputStream, true);
    }

    /**
     * Sets {@link #getBodyOutputStreamApplier()} using {@link InputStream#transferTo(OutputStream)}.
     *
     * @param closeAfter <code>true</code> to call {@link #addAfter(Runnable)} with {@link InputStream#close()},
     *                   <code>false</code> otherwise
     */
    public void setBodyInputStream(final @Nullable InputStream bodyInputStream, final boolean closeAfter) {
        if (bodyInputStream == null) {
            bodyOutputStreamApplier = null;
        } else {
            bodyOutputStreamApplier = outputStream -> {
                try (bodyInputStream) {
                    bodyInputStream.transferTo(outputStream);
                } catch (final IOException ioException) {
                    throw new UncheckedIOException(ioException);
                }
            };
            if (closeAfter) {
                addAfter(() -> {
                    try {
                        bodyInputStream.close();
                    } catch (final IOException ioException) {
                        throw new UncheckedIOException(ioException);
                    }
                });
            }
        }
    }

    /**
     * Calls {@link #setContentLength(Long)} and {@link #setBodyInputStream(InputStream)} with
     * {@link ByteArrayInputStream#ByteArrayInputStream(byte[])}.
     */
    public void setBodyBytes(final byte[] bytes) {
        setContentLength((long) bytes.length);
        setBodyInputStream(new ByteArrayInputStream(bytes));
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
     * <code>acceptRanges</code> set to <code>true</code>,
     * <code>contentTypePeekLength</code> set to {@link Resource#DEFAULT_PEEK_LENGTH}, and
     * <code>setBodyInputStream</code> to the negation of {@link Request#getMethodEnum()}
     * {@link Method#hasNoResponseBody()}.
     */
    public void responseResource(final Resource resource) {
        final var requestMethod = handle.getRequest().getMethodEnum();
        responseResource(resource, true, DEFAULT_PEEK_LENGTH,
                requestMethod == null || !requestMethod.hasNoResponseBody());
    }

    /**
     * Applies the given {@link Resource} to this {@link Response}, with logic for applying
     * {@link Status#NOT_MODIFIED_304}.
     *
     * @param resource              the {@link Resource}
     * @param acceptRanges          <code>true</code> to {@link Headers#put(Object, Object)}
     *                              {@link Header#ACCEPT_RANGES} {@link Range#BYTES_UNIT} and to use
     *                              {@link Resource#withRange(Range)} with {@link Request#getRange()} if
     *                              non-<code>null</code>, <code>false</code> otherwise
     * @param contentTypePeekLength if non-<code>null</code> and the given {@link Resource#getContentType()} is
     *                              <code>null</code>, then peek this many bytes into {@link Resource#getContent()} and
     *                              use {@link StringUtil#isLikelyUtf8(byte[])} to apply either
     *                              {@link ContentType#TEXT_PLAIN_UTF_8} or {@link ContentType#APPLICATION_OCTET_STREAM}
     * @param setBodyInputStream    <code>true</code> to {@link #setBodyInputStream(InputStream)} to
     *                              {@link Resource#getContent()}, <code>false</code> to not
     *                              {@link #setBodyInputStream(InputStream)}
     */
    public void responseResource(Resource resource, final boolean acceptRanges,
            final @Nullable Integer contentTypePeekLength, boolean setBodyInputStream) {
        final var request = handle.getRequest();
        if (acceptRanges) {
            headers.put(ACCEPT_RANGES.toString(), BYTES_UNIT);
            final var requestRange = request.getRange();
            if (requestRange != null && resource.getContentRange() == null && resource.getContentLength() != null) {
                resource = resource.withRange(requestRange);
            }
        }
        var notModified = false;
        final var lastModified = resource.getLastModified();
        if (lastModified != null) {
            setLastModified(lastModified);
            final var ifModifiedSince = request.getIfModifiedSince();
            if (ifModifiedSince != null && !lastModified.isAfter(ifModifiedSince.toInstant())) {
                notModified = true;
            }
        }
        final var eTag = resource.getETag();
        if (eTag != null) {
            setETag(eTag);
            final var ifNoneMatch = request.getIfNoneMatch();
            if (ifNoneMatch != null && ifNoneMatch.equalsWithoutCompressionType(eTag)) {
                notModified = true;
            }
        }
        final var contentLength = resource.getContentLength();
        if (contentLength != null && (setBodyInputStream || request.getMethodEnum() == HEAD) && !notModified) {
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
            setContentType(isLikelyUtf8(peekedBytes) ? TEXT_PLAIN_UTF_8 : APPLICATION_OCTET_STREAM);
        } else {
            setContentType(APPLICATION_OCTET_STREAM);
        }
        final var contentRange = resource.getContentRange();
        if (contentRange != null) {
            final var ifRange = handle.getRequest().getIfRange();
            if (ifRange != null) {
                final var ifRangeDateTime = ifRange.getDateTime();
                if (ifRangeDateTime != null && lastModified != null &&
                        lastModified.isAfter(ifRangeDateTime.toInstant())) {
                    throw new StatusException(RANGE_NOT_SATISFIABLE_416);
                }
                final var ifRangeETag = ifRange.getETag();
                if (ifRangeETag != null && eTag != null && !ifRangeETag.equalsWithoutCompressionType(eTag)) {
                    throw new StatusException(RANGE_NOT_SATISFIABLE_416);
                }
            }
            setContentRange(contentRange);
            setStatus(PARTIAL_CONTENT_206);
        }
        final var contentDisposition = resource.getContentDisposition();
        if (contentDisposition != null) {
            setContentDisposition(contentDisposition);
        }
        if (notModified) {
            setStatus(NOT_MODIFIED_304);
            setBodyInputStream(null);
            return;
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
    public void responseFile(final Path file, final boolean strongETag, final boolean trustedContentType,
            final @Nullable ContentType untrustedContentType, final @Nullable Integer peekLength,
            final @Nullable ContentEncoding contentEncoding, final boolean exposeFilename) {
        responseResource(Resource.ofFile(file, strongETag, trustedContentType, untrustedContentType, peekLength,
                contentEncoding, exposeFilename));
    }

    /**
     * @return an {@link ImmutableList} of all {@link Runnable}s given to {@link #addAfter(Runnable)}s, or
     * <code>null</code>
     */
    public @Nullable ImmutableList<Runnable> getAfters() {
        return afters == null ? null : ImmutableList.copyOf(afters);
    }

    /**
     * Adds a {@link Runnable} guaranteed to run after this {@link Response} has been written successfully or
     * unsuccessfully.
     */
    public void addAfter(final Runnable after) {
        if (afters == null) {
            afters = new ArrayList<>();
        }
        afters.add(after);
    }

    /**
     * Removes a {@link Runnable} added by {@link #addAfter(Runnable)}.
     *
     * @return <code>true</code> if removed, <code>false</code> otherwise
     */
    public boolean removeAfter(final Runnable after) {
        if (afters != null) {
            return afters.remove(after);
        }
        return false;
    }

    /**
     * Calls {@link #sse(Consumer, boolean)} with <code>keepAliveSeparateThread</code> set to <code>true</code>.
     */
    public void sse(final Consumer<Sse> sseApplier) {
        sse(sseApplier, true);
    }

    /**
     * Calls {@link #sse(Consumer, Duration, boolean)} with <code>keepAlivePeriod</code> set to
     * {@link #DEFAULT_SSE_KEEP_ALIVE_PERIOD}.
     */
    public void sse(final Consumer<Sse> sseApplier, final boolean keepAliveSeparateThread) {
        sse(sseApplier, DEFAULT_SSE_KEEP_ALIVE_PERIOD, keepAliveSeparateThread);
    }

    /**
     * Calls {@link #sse(Consumer, Duration, boolean)} with <code>keepAliveSeparateThread</code> set to
     * <code>true</code>.
     */
    public void sse(final Consumer<Sse> sseApplier, final @Nullable Duration keepAlivePeriod) {
        sse(sseApplier, keepAlivePeriod, true);
    }

    /**
     * Converts this {@link Response} into a Server-Sent Events (SSE) {@link Response}.
     *
     * @param sseApplier              the {@link Sse} applier
     * @param keepAlivePeriod         the period {@link Duration} at which to call {@link Sse#comment(String)} with an
     *                                empty string, or <code>null</code> to disable. If disabled,
     *                                <code>sseApplier</code> must be blocking to keep the {@link Response} thread
     *                                alive.
     * @param keepAliveSeparateThread <code>true</code> if a separate {@link Thread#ofVirtual()} should be used for the
     *                                <code>keepAlivePeriod</code>, <code>false</code> to use the current
     *                                {@link Response} thread. If <code>sseApplier</code> is blocking, this should be
     *                                set to <code>true</code>, whereas if <code>sseApplier</code> is non-blocking, this
     *                                should be set to <code>false</code>.
     */
    public void sse(final Consumer<Sse> sseApplier, final @Nullable Duration keepAlivePeriod,
            final boolean keepAliveSeparateThread) {
        setStatus(OK_200);
        setConnectionClose();
        setContentType(TEXT_EVENT_STREAM_UTF_8);
        setCacheControl(NO_CACHE);
        headers.set(X_ACCEL_BUFFERING.toString(), "no");
        disableCompression();
        setBodyOutputStreamApplier(bodyOutputStream -> {
            try {
                bodyOutputStream.flush();
            } catch (final IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
            final var sse = new Sse(bodyOutputStream);
            if (keepAlivePeriod != null) {
                if (keepAliveSeparateThread) {
                    final var keepAliveThread = Thread.ofVirtual().start(() -> {
                        try {
                            sseKeepAlive(sse, keepAlivePeriod);
                        } catch (final Throwable throwable) {
                            LOGGER.error("SSE keep-alive thread threw", throwable);
                        }
                    });
                    sseApplier.accept(sse);
                    joinUninterruptibly(keepAliveThread);
                } else {
                    sseApplier.accept(sse);
                    sseKeepAlive(sse, keepAlivePeriod);
                }
            } else {
                sseApplier.accept(sse);
            }
        });
    }

    private void sseKeepAlive(final Sse sse, final Duration keepAlivePeriod) {
        final var latch = new CountDownLatch(1);
        final Runnable latchCountDown = latch::countDown;
        final var jetServer = handle.getInternals().getJetServer();
        jetServer.addStopListener(latchCountDown);
        addAfter(() -> jetServer.removeStopListener(latchCountDown));
        while (sse.comment("")) {
            if (awaitUninterruptibly(latch, keepAlivePeriod)) {
                break;
            }
        }
    }
}
