package net.jacobpeterson.jet.server.handle.response.resource;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.errorprone.annotations.Immutable;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDisposition;
import net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding;
import net.jacobpeterson.jet.common.http.header.contentrange.ContentRange;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.header.range.Range;
import net.jacobpeterson.jet.common.util.string.StringUtil;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handler.handler.directory.FileDirectoryHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.Instant;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.DAYS;
import static net.jacobpeterson.jet.common.http.header.Header.RANGE;
import static net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDispositionType.ATTACHMENT;
import static net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDispositionType.INLINE;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_OCTET_STREAM;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_PLAIN_UTF_8;
import static net.jacobpeterson.jet.common.http.header.range.Range.BYTES_UNIT;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static net.jacobpeterson.jet.common.http.status.Status.RANGE_NOT_SATISFIABLE_416;
import static net.jacobpeterson.jet.common.util.string.StringUtil.isLikelyPlainText;

/**
 * {@link Resource} is a class that represents arbitrary data with associated metadata (e.g. a file) to serve as a web
 * server response.
 */
@NullMarked
@Immutable
@Getter @Builder(toBuilder = true) @ToString
public final class Resource {

    /**
     * The default peek length in bytes: <code>1024</code>
     */
    public static final int DEFAULT_PEEK_LENGTH = 1024;

    @Immutable
    @Value
    private static class OfClasspathCacheKey {

        Class<?> clazz;
        String resourcePath;
        boolean trustedContentType;
        @Nullable ContentType untrustedContentType;
        @Nullable Integer peekLength;
        @Nullable ContentEncoding contentEncoding;
        boolean exposeFilename;
    }

    private static final Cache<OfClasspathCacheKey, Resource> OF_CLASSPATH_CACHE = Caffeine.newBuilder()
            // Long enough that the cost of recomputing a strong ETag is negligible, but short enough to reduce
            // long-term memory usage.
            .expireAfterAccess(7, DAYS)
            .softValues()
            .build();

    /**
     * @return {@link #ofClasspath(Class, String, boolean, ContentType, Integer, ContentEncoding, boolean)} with
     * <code>trustedContentType</code> set to <code>true</code>,
     * <code>untrustedContentType</code> set to <code>null</code>,
     * <code>peekLength</code> set to {@link #DEFAULT_PEEK_LENGTH},
     * <code>contentEncoding</code> set to <code>null</code>, and
     * <code>exposeFilename</code> set to <code>true</code>
     */
    public static Resource ofClasspath(final Class<?> clazz, final String resourcePath) {
        return ofClasspath(clazz, resourcePath, true, null, DEFAULT_PEEK_LENGTH, null, true);
    }

    /**
     * Gets a {@link Resource} instance for the given {@link Class#getResource(String)}, with various
     * {@link Resource} fields set using the metadata of the given {@link Class#getResource(String)}, and
     * {@link #getContent()} set using {@link Class#getResourceAsStream(String)}.
     * <p>
     * Note: a classpath resource is immutable and cannot be deleted, so this method internally caches the computed
     * {@link Resource} instance and uses {@link ETag#computeStrong(InputStream)}.
     *
     * @param clazz                the {@link Class} to call {@link Class#getResource(String)} on
     * @param resourcePath         the {@link String} to pass to {@link Class#getResource(String)}
     * @param trustedContentType   for the {@link ContentType} returned from {@link ContentType#forFilename(String)}
     *                             with <code>resourcePath</code>, <code>true</code> will designate it as trusted and
     *                             apply it if non-<code>null</code>, <code>false</code> will designate it as untrusted
     *                             and apply it if non-<code>null</code> and {@link ContentType#isXssSafeHtmlTag()}.
     *                             <p>
     *                             For example, a user might upload a file named "code.js" that contains safe or unsafe
     *                             JavaScript code, but user-uploaded files are always untrusted, so
     *                             {@link ContentType#APPLICATION_JAVASCRIPT} should never be used, and a safe
     *                             {@link ContentType} like {@link ContentType#TEXT_PLAIN} should be used instead.
     * @param untrustedContentType if non-<code>null</code> and the logic described by the
     *                             <code>trustedContentType</code> argument is not applied, then apply this
     *                             {@link ContentType} instead
     * @param peekLength           if non-<code>null</code> and the logic described by the
     *                             <code>trustedContentType</code> and <code>untrustedContentType</code> arguments are
     *                             not applied, then peek this many bytes into {@link #getContent()} and use
     *                             {@link StringUtil#isLikelyPlainText(Charset, byte[])} to apply either
     *                             {@link ContentType#TEXT_PLAIN_UTF_8} or {@link ContentType#APPLICATION_OCTET_STREAM}
     * @param contentEncoding      the {@link ContentEncoding} of the classpath resource, or <code>null</code> for none
     * @param exposeFilename       <code>true</code> to set {@link ContentDisposition#getFilename()} to
     *                             the filename defined in <code>resourcePath</code>, <code>false</code> to not set
     *                             {@link ContentDisposition#getFilename()}
     *
     * @return the {@link Resource} instance
     */
    public static Resource ofClasspath(final Class<?> clazz, final String resourcePath,
            final boolean trustedContentType, final @Nullable ContentType untrustedContentType,
            final @Nullable Integer peekLength, final @Nullable ContentEncoding contentEncoding,
            final boolean exposeFilename) throws StatusException {
        return OF_CLASSPATH_CACHE.get(new OfClasspathCacheKey(clazz, resourcePath, trustedContentType,
                untrustedContentType, peekLength, contentEncoding, exposeFilename), _ -> {
            final var url = clazz.getResource(resourcePath);
            if (url == null) {
                throw new StatusException(NOT_FOUND_404, "`%s` class resource not found: %s"
                        .formatted(clazz.getName(), resourcePath));
            }
            final var lastIndexOfSlash = resourcePath.lastIndexOf('/');
            final var filename = lastIndexOfSlash != -1 ? resourcePath.substring(lastIndexOfSlash + 1) : resourcePath;
            final URLConnection urlConnection;
            try {
                urlConnection = url.openConnection();
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
            final var contentTypeForFilename = ContentType.forFilename(filename);
            final ContentType contentType;
            if (contentTypeForFilename != null && (trustedContentType || contentTypeForFilename.isXssSafeHtmlTag())) {
                contentType = contentTypeForFilename;
            } else if (untrustedContentType != null) {
                contentType = untrustedContentType;
            } else if (peekLength != null &&
                    (contentEncoding == null || !contentEncoding.getType().isDictionaryRequired())) {
                try (final var content = requireNonNull(clazz.getResourceAsStream(resourcePath))) {
                    final byte[] peekedBytes;
                    if (contentEncoding != null) {
                        try (final var decompressed = contentEncoding.getType().decompress(content)) {
                            peekedBytes = decompressed.readNBytes(peekLength);
                        }
                    } else {
                        peekedBytes = content.readNBytes(peekLength);
                    }
                    contentType = isLikelyPlainText(UTF_8, peekedBytes) ? TEXT_PLAIN_UTF_8 : APPLICATION_OCTET_STREAM;
                } catch (final IOException ioException) {
                    throw new RuntimeException(ioException);
                }
            } else {
                contentType = null;
            }
            final var contentDisposition = ContentDisposition.builder()
                    .type(trustedContentType ||
                            (contentType != null && contentType.isXssSafeHtmlTag()) ? INLINE : ATTACHMENT);
            if (exposeFilename) {
                contentDisposition.filename(filename);
            }
            return builder()
                    .contentLength(urlConnection.getContentLengthLong())
                    .lastModified(Instant.ofEpochMilli(urlConnection.getLastModified()))
                    .eTag(ETag.computeStrong(requireNonNull(clazz.getResourceAsStream(resourcePath))))
                    .contentType(contentType)
                    .contentEncoding(contentEncoding)
                    .contentDisposition(contentDisposition.build())
                    .content(() -> requireNonNull(clazz.getResourceAsStream(resourcePath)))
                    .build();
        });
    }

    /**
     * Gets a {@link Resource} instance for the given file {@link Path}, with various {@link Resource} fields set using
     * the metadata of the given file {@link Path}, and {@link #getContent()} set using
     * {@link Files#newInputStream(Path, OpenOption...)}.
     * <p>
     * Note: it is recommended to use {@link FileDirectoryHandler} instead of calling this method directly.
     *
     * @param file                 the file {@link Path}
     * @param strongETag           <code>true</code> to use {@link ETag#computeStrong(InputStream)}, <code>false</code>
     *                             to use {@link ETag#computeWeak(String, long, long)}
     * @param trustedContentType   for the {@link ContentType} returned from {@link ContentType#forFilename(String)}
     *                             with {@link Path#getFileName()}, <code>true</code> will designate it as trusted and
     *                             apply it if non-<code>null</code>, <code>false</code> will designate it as untrusted
     *                             and apply it if non-<code>null</code> and {@link ContentType#isXssSafeHtmlTag()}.
     *                             <p>
     *                             For example, a user might upload a file named "code.js" that contains safe or unsafe
     *                             JavaScript code, but user-uploaded files are always untrusted, so
     *                             {@link ContentType#APPLICATION_JAVASCRIPT} should never be used, and a safe
     *                             {@link ContentType} like {@link ContentType#TEXT_PLAIN} should be used instead.
     * @param untrustedContentType if non-<code>null</code> and the logic described by the
     *                             <code>trustedContentType</code> argument is not applied, then apply this
     *                             {@link ContentType} instead
     * @param peekLength           if non-<code>null</code> and the logic described by the
     *                             <code>trustedContentType</code> and <code>untrustedContentType</code> arguments are
     *                             not applied, then peek this many bytes into {@link #getContent()} and use
     *                             {@link StringUtil#isLikelyPlainText(Charset, byte[])} to apply either
     *                             {@link ContentType#TEXT_PLAIN_UTF_8} or {@link ContentType#APPLICATION_OCTET_STREAM}
     * @param contentEncoding      the {@link ContentEncoding} of the file {@link Path}, or <code>null</code> for none
     * @param exposeFilename       <code>true</code> to set {@link ContentDisposition#getFilename()} to
     *                             {@link Path#getFileName()}, <code>false</code> to not set
     *                             {@link ContentDisposition#getFilename()}
     *
     * @return the {@link Resource} instance
     */
    public static Resource ofFile(final Path file, final boolean strongETag, final boolean trustedContentType,
            final @Nullable ContentType untrustedContentType, final @Nullable Integer peekLength,
            final @Nullable ContentEncoding contentEncoding, final boolean exposeFilename) throws StatusException {
        if (!Files.isRegularFile(file)) {
            throw new StatusException(NOT_FOUND_404, "Invalid file: " + file);
        }
        final var filename = file.getFileName().toString();
        final long fileLength;
        final long fileLastModified;
        try {
            fileLength = Files.size(file);
            fileLastModified = Files.getLastModifiedTime(file).toMillis();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
        final ETag eTag;
        if (strongETag) {
            try (final var content = Files.newInputStream(file)) {
                eTag = ETag.computeStrong(content);
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
        } else {
            eTag = ETag.computeWeak(filename, fileLength, fileLastModified);
        }
        final var contentTypeForFilename = ContentType.forFilename(filename);
        final ContentType contentType;
        if (contentTypeForFilename != null && (trustedContentType || contentTypeForFilename.isXssSafeHtmlTag())) {
            contentType = contentTypeForFilename;
        } else if (untrustedContentType != null) {
            contentType = untrustedContentType;
        } else if (peekLength != null &&
                (contentEncoding == null || !contentEncoding.getType().isDictionaryRequired())) {
            try (final var content = Files.newInputStream(file)) {
                final byte[] peekedBytes;
                if (contentEncoding != null) {
                    try (final var decompressed = contentEncoding.getType().decompress(content)) {
                        peekedBytes = decompressed.readNBytes(peekLength);
                    }
                } else {
                    peekedBytes = content.readNBytes(peekLength);
                }
                contentType = isLikelyPlainText(UTF_8, peekedBytes) ? TEXT_PLAIN_UTF_8 : APPLICATION_OCTET_STREAM;
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
        } else {
            contentType = null;
        }
        final var contentDisposition = ContentDisposition.builder()
                .type(trustedContentType ||
                        (contentType != null && contentType.isXssSafeHtmlTag()) ? INLINE : ATTACHMENT);
        if (exposeFilename) {
            contentDisposition.filename(filename);
        }
        return builder()
                .contentLength(fileLength)
                .lastModified(Instant.ofEpochMilli(fileLastModified))
                .eTag(eTag)
                .contentType(contentType)
                .contentDisposition(contentDisposition.build())
                .content(() -> {
                    try {
                        return Files.newInputStream(file);
                    } catch (final NoSuchFileException noSuchFileException) {
                        throw new StatusException(NOT_FOUND_404, noSuchFileException);
                    } catch (final IOException ioException) {
                        throw new RuntimeException(ioException);
                    }
                }).build();
    }

    /**
     * The {@link Header#CONTENT_LENGTH} {@link Long}, or <code>null</code> if unknown.
     */
    private final @Nullable Long contentLength;

    /**
     * The {@link Header#LAST_MODIFIED} {@link Instant}, or <code>null</code> if unknown.
     */
    private final @Nullable Instant lastModified;

    /**
     * The {@link ETag}, or <code>null</code>.
     */
    private final @Nullable ETag eTag;

    /**
     * The {@link ContentType}, or <code>null</code>.
     */
    private final @Nullable ContentType contentType;

    /**
     * The {@link ContentEncoding}, or <code>null</code>.
     */
    private final @Nullable ContentEncoding contentEncoding;

    /**
     * The {@link ContentDisposition}, or <code>null</code>.
     */
    private final @Nullable ContentDisposition contentDisposition;

    /**
     * The {@link ContentRange}, or <code>null</code>.
     */
    private final @Nullable ContentRange contentRange;

    /**
     * The content {@link InputStream} {@link Supplier}.
     * <p>
     * Note: {@link Supplier#get()} of this {@link Supplier} is not guaranteed to be called.
     */
    private final @SuppressWarnings("Immutable") @ToString.Exclude Supplier<InputStream> content;

    /**
     * Returns a copy of this {@link Resource}, with {@link #getContentRange()} set to
     * {@link ContentRange#forRange(Range, long)} using the given {@link Range} and this {@link #getContentLength()}
     * (which must be non-<code>null</code>), and {@link #getContent()} set to
     * {@link ContentRange#forInputStream(InputStream)} using this {@link #getContent()}. If this
     * {@link #getContentRange()} is already set, an {@link IllegalArgumentException} is thrown.
     *
     * @param range the {@link Range}
     *
     * @return this {@link Resource} with the given {@link Range}
     */
    public Resource withRange(final Range range) throws StatusException, IllegalArgumentException {
        checkArgument(contentRange == null, "`contentRange` cannot already be set");
        if (!range.getUnit().equals(BYTES_UNIT)) {
            throw new StatusException(RANGE_NOT_SATISFIABLE_416, "`%s` unit must be: %s".formatted(RANGE, BYTES_UNIT));
        }
        final ContentRange contentRange;
        try {
            contentRange = ContentRange.forRange(range, requireNonNull(contentLength));
        } catch (final Exception exception) {
            throw new StatusException(RANGE_NOT_SATISFIABLE_416, exception);
        }
        return toBuilder()
                .contentLength(contentRange.getContentLength())
                .contentRange(contentRange)
                .content(() -> {
                    final var content = this.content.get();
                    try {
                        return contentRange.forInputStream(content);
                    } catch (final Throwable throwable) {
                        try {
                            content.close();
                        } catch (final Throwable closeThrowable) {
                            throwable.addSuppressed(closeThrowable);
                        }
                        throw new StatusException(RANGE_NOT_SATISFIABLE_416, throwable);
                    }
                }).build();
    }
}
