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
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.Instant;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.DAYS;
import static net.jacobpeterson.jet.common.http.header.Header.RANGE;
import static net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDispositionType.ATTACHMENT;
import static net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDispositionType.INLINE;
import static net.jacobpeterson.jet.common.http.header.contentrange.ContentRange.forRange;
import static net.jacobpeterson.jet.common.http.header.range.Range.BYTES_UNIT;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static net.jacobpeterson.jet.common.http.status.Status.RANGE_NOT_SATISFIABLE_416;

/**
 * {@link Resource} is a class that represents arbitrary data with associated metadata (e.g. a file) to serve as a web
 * server response.
 */
@NullMarked
@Immutable
@Getter @Builder(toBuilder = true) @ToString
public final class Resource {

    private static <T> Cache<T, Resource> newCache() {
        return Caffeine.newBuilder()
                // Long enough that the cost of recomputing a strong ETag is negligible, but short enough to reduce
                // memory usage and evict unusable entries that reference a deleted file.
                .expireAfterAccess(7, DAYS)
                .softValues()
                .build();
    }

    @Immutable
    @Value
    private static class OfClasspathCacheKey {

        Class<?> clazz;
        String resourceName;
        boolean exposeFilename;
    }

    private static final Cache<OfClasspathCacheKey, Resource> OF_CLASSPATH_CACHE = newCache();

    /**
     * @return {@link #ofClasspath(Class, String, boolean, Range)} with <code>exposeFilename</code> set to
     * <code>true</code>
     */
    public static Resource ofClasspath(final Class<?> clazz, final String resourceName, final @Nullable Range range) {
        return ofClasspath(clazz, resourceName, true, range);
    }

    /**
     * Creates a {@link Resource} instance from the given {@link Class#getResource(String)}, with various
     * {@link Resource} headers set using the metadata of the given {@link Class#getResource(String)}, and
     * {@link #getContent()} set using {@link Class#getResourceAsStream(String)} with
     * {@link ContentRange#forInputStream(InputStream)} as needed.
     * <p>
     * Note: classpath resources are immutable and cannot be deleted, so this method internally caches the computed
     * {@link Resource} and uses {@link ETag#computeStrong(InputStream)}.
     *
     * @param clazz          the {@link Class} to call {@link Class#getResource(String)} on
     * @param resourceName   the {@link String} to pass to {@link Class#getResource(String)}
     * @param exposeFilename <code>true</code> to set {@link ContentDisposition#getFilename()} to
     *                       {@link File#getName()}, <code>false</code> to not set
     *                       {@link ContentDisposition#getFilename()}
     * @param range          the {@link Range}, or <code>null</code> to not set {@link #getContentRange()}
     *
     * @return the {@link Resource} instance
     */
    public static Resource ofClasspath(final Class<?> clazz, final String resourceName, final boolean exposeFilename,
            final @Nullable Range range) throws StatusException {
        final var resource = OF_CLASSPATH_CACHE.get(new OfClasspathCacheKey(clazz, resourceName, exposeFilename), _ -> {
            final var url = clazz.getResource(resourceName);
            if (url == null) {
                throw new StatusException(NOT_FOUND_404, "`%s` class resource not found: %s"
                        .formatted(clazz.getName(), resourceName));
            }
            final var lastIndexOfSlash = resourceName.lastIndexOf('/');
            final var filename = lastIndexOfSlash != -1 ? resourceName.substring(lastIndexOfSlash + 1) : resourceName;
            final URLConnection urlConnection;
            try {
                urlConnection = url.openConnection();
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
            final var fileLength = urlConnection.getContentLengthLong();
            final var fileLastModified = urlConnection.getLastModified();
            final var contentType = ContentType.forFilename(filename);
            final var contentDisposition = ContentDisposition.builder()
                    .type(contentType != null && contentType.isXssSafeHtmlTag() ? INLINE : ATTACHMENT);
            if (exposeFilename) {
                contentDisposition.filename(filename);
            }
            return builder()
                    .contentLength(fileLength)
                    .lastModified(Instant.ofEpochMilli(fileLastModified))
                    .etag(ETag.computeStrong(requireNonNull(clazz.getResourceAsStream(resourceName))))
                    .contentType(contentType)
                    .contentDisposition(contentDisposition.build())
                    .content(() -> requireNonNull(clazz.getResourceAsStream(resourceName)))
                    .build();
        });
        if (range == null) {
            return resource;
        }
        if (!range.getUnit().equals(BYTES_UNIT)) {
            throw new StatusException(RANGE_NOT_SATISFIABLE_416, "`%s` unit must be: %s".formatted(RANGE, BYTES_UNIT));
        }
        final ContentRange contentRange;
        try {
            contentRange = forRange(range, requireNonNull(resource.getContentLength()));
        } catch (final Exception exception) {
            throw new StatusException(RANGE_NOT_SATISFIABLE_416, exception);
        }
        return resource.toBuilder()
                .contentLength(contentRange.getContentLength())
                .contentRange(contentRange)
                .content(() -> {
                    final var resourceAsStream = requireNonNull(clazz.getResourceAsStream(resourceName));
                    try {
                        return contentRange.forInputStream(resourceAsStream);
                    } catch (final Throwable throwable) {
                        try {
                            resourceAsStream.close();
                        } catch (final Throwable closeThrowable) {
                            throwable.addSuppressed(closeThrowable);
                        }
                        throw new StatusException(RANGE_NOT_SATISFIABLE_416, throwable);
                    }
                }).build();
    }

    @Immutable
    @Value
    private static class OfFileCacheKey {

        String filePath;
        boolean exposeFilename;
    }

    private static final Cache<OfFileCacheKey, Resource> OF_FILE_CACHE = newCache();

    /**
     * If a {@link File} that was previously given to {@link #ofFile(File, boolean, boolean, Range)} with
     * <code>immutable</code> set to <code>true</code> has been deleted, it should be removed from the internal cache
     * by calling this method. The cache entry will eventually expire automatically, but it's a good idea to invalidate
     * it as soon as possible so that the entry doesn't needlessly consume memory.
     *
     * @param file the {@link File}
     */
    public static void ofFileImmutableCacheInvalidate(final File file) {
        final var filePath = file.getPath();
        // Invalidate all cache entry variants.
        OF_FILE_CACHE.invalidate(new OfFileCacheKey(filePath, true));
        OF_FILE_CACHE.invalidate(new OfFileCacheKey(filePath, false));
    }

    /**
     * @return {@link #ofFile(File, boolean, boolean, Range)} with <code>exposeFilename</code> set to <code>true</code>
     */
    public static Resource ofFile(final File file, final boolean immutable, final @Nullable Range range) {
        return ofFile(file, immutable, true, range);
    }

    /**
     * Creates a {@link Resource} instance from the given {@link File}, with various {@link Resource} headers set using
     * the metadata of the given {@link File}, and {@link #getContent()} set using {@link FileInputStream} with
     * {@link ContentRange#forInputStream(InputStream)} as needed.
     *
     * @param file           the {@link File}
     * @param immutable      <code>true</code> if the given {@link File} is immutable (the path, name, metadata, and
     *                       content will never change while the JVM is running) and the computed {@link Resource}
     *                       should be internally cached and use {@link ETag#computeStrong(File)}, <code>false</code>
     *                       otherwise. Call {@link #ofFileImmutableCacheInvalidate(File)} when the immutable
     *                       {@link File} is deleted.
     * @param exposeFilename <code>true</code> to set {@link ContentDisposition#getFilename()} to
     *                       {@link File#getName()}, <code>false</code> to not set
     *                       {@link ContentDisposition#getFilename()}
     * @param range          the {@link Range}, or <code>null</code> to not set {@link #getContentRange()}
     *
     * @return the {@link Resource} instance
     */
    public static Resource ofFile(final File file, final boolean immutable, final boolean exposeFilename,
            final @Nullable Range range) throws StatusException {
        if (!file.exists()) {
            if (immutable) {
                OF_FILE_CACHE.invalidate(new OfFileCacheKey(file.getPath(), exposeFilename));
            }
            throw new StatusException(NOT_FOUND_404, "Nonexistent file: " + file);
        }
        final var resource = !immutable ? ofFileNoRange(file, true, exposeFilename) :
                OF_FILE_CACHE.get(new OfFileCacheKey(file.getPath(), exposeFilename), _ ->
                        ofFileNoRange(file, false, exposeFilename));
        if (range == null) {
            return resource;
        }
        if (!range.getUnit().equals(BYTES_UNIT)) {
            throw new StatusException(RANGE_NOT_SATISFIABLE_416, "`%s` unit must be: %s".formatted(RANGE, BYTES_UNIT));
        }
        final ContentRange contentRange;
        try {
            contentRange = forRange(range, requireNonNull(resource.getContentLength()));
        } catch (final Exception exception) {
            throw new StatusException(RANGE_NOT_SATISFIABLE_416, exception);
        }
        return resource.toBuilder()
                .contentLength(contentRange.getContentLength())
                .contentRange(contentRange)
                .content(() -> {
                    try {
                        return contentRange.forInputStream(new FileInputStream(file));
                    } catch (final FileNotFoundException fileNotFoundException) {
                        throw new StatusException(NOT_FOUND_404, fileNotFoundException);
                    } catch (final IOException ioException) {
                        throw new StatusException(RANGE_NOT_SATISFIABLE_416, ioException);
                    }
                }).build();
    }

    private static Resource ofFileNoRange(final File file, final boolean weakETag, final boolean exposeFilename) {
        if (!file.canRead()) {
            throw new StatusException(NOT_FOUND_404, "Unreadable file: " + file);
        }
        final var filename = file.getName();
        final var fileLength = file.length();
        final var fileLastModified = file.lastModified();
        final var contentType = ContentType.forFilename(filename);
        final var contentDisposition = ContentDisposition.builder()
                .type(contentType != null && contentType.isXssSafeHtmlTag() ? INLINE : ATTACHMENT);
        if (exposeFilename) {
            contentDisposition.filename(filename);
        }
        return builder()
                .contentLength(fileLength)
                .lastModified(Instant.ofEpochMilli(fileLastModified))
                .etag(weakETag ? ETag.computeWeak(filename, fileLength, fileLastModified) : ETag.computeStrong(file))
                .contentType(contentType)
                .contentDisposition(contentDisposition.build())
                .content(() -> {
                    try {
                        return new FileInputStream(file);
                    } catch (final FileNotFoundException fileNotFoundException) {
                        throw new StatusException(NOT_FOUND_404, fileNotFoundException);
                    }
                }).build();
    }

    /** The {@link Header#CONTENT_LENGTH} {@link Long}, or <code>null</code> if unknown. */
    private final @Nullable Long contentLength;

    /** The {@link Header#LAST_MODIFIED} {@link Instant}, or <code>null</code> if unknown. */
    private final @Nullable Instant lastModified;

    /** The {@link ETag}, or <code>null</code>. */
    private final @Nullable ETag etag;

    /** The {@link ContentType}, or <code>null</code>. */
    private final @Nullable ContentType contentType;

    /** The {@link ContentEncoding}, or <code>null</code>. */
    private final @Nullable ContentEncoding contentEncoding;

    /** The {@link ContentDisposition}, or <code>null</code>. */
    private final @Nullable ContentDisposition contentDisposition;

    /** The {@link ContentRange}, or <code>null</code>. */
    private final @Nullable ContentRange contentRange;

    /**
     * The content {@link InputStream} {@link Supplier}.
     * <p>
     * Note: this {@link Supplier#get()} is not guaranteed to be called, but {@link InputStream#close()} is guaranteed
     * to be called if this {@link Supplier#get()} is called.
     */
    private final @SuppressWarnings("Immutable") @ToString.Exclude Supplier<InputStream> content;
}
