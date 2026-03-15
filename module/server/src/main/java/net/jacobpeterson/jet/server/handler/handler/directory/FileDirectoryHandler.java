package net.jacobpeterson.jet.server.handler.handler.directory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handle.response.resource.Resource;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.concurrent.TimeUnit.DAYS;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.MAX_AGE_1_YEAR_IMMUTABLE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.NO_CACHE;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static net.jacobpeterson.jet.server.handle.response.resource.Resource.DEFAULT_PEEK_LENGTH;

/**
 * {@link FileDirectoryHandler} is a {@link Handler} for serving
 * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}
 * from files in a given directory {@link Path} using {@link Response#responseResource(Resource)}, with support for
 * relativizing request paths, caching {@link Resource} instances of immutable files, and applying a
 * {@link ResponseCacheControl}.
 */
@NullMarked
public class FileDirectoryHandler implements Handler {

    /**
     * The default {@link #getDefaultFilename()}: <code>"index.html"</code>
     */
    public static final String DEFAULT_DEFAULT_FILENAME = "index.html";

    public static FileDirectoryHandler forMutableFiles(final Path directory,
            final @Nullable String requestPathStartsWith, final boolean trustedContentType) {
        return forMutableFiles(directory, requestPathStartsWith, DEFAULT_DEFAULT_FILENAME, NO_CACHE, trustedContentType,
                null, null);
    }

    /**
     * Creates a new {@link FileDirectoryHandler} instance for mutable files (see {@link #isImmutableFiles()}).
     *
     * @param directory             the {@link #getDirectory()}
     * @param requestPathStartsWith the {@link #getRequestPathStartsWith()}
     * @param defaultFilename       the {@link #getDefaultFilename()}
     * @param cacheControl          the {@link #getCacheControl()}
     * @param trustedContentType    see {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer,
     *                              ContentEncoding, boolean)}
     * @param peekLength            see {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer,
     *                              ContentEncoding, boolean)}
     * @param contentEncoding       see {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer,
     *                              ContentEncoding, boolean)}
     *
     * @return the new {@link FileDirectoryHandler} instance
     */
    public static FileDirectoryHandler forMutableFiles(final Path directory,
            final @Nullable String requestPathStartsWith, final @Nullable String defaultFilename,
            final @Nullable ResponseCacheControl cacheControl, final boolean trustedContentType,
            final @Nullable Integer peekLength, final @Nullable ContentEncoding contentEncoding) {
        return new FileDirectoryHandler(directory, requestPathStartsWith, defaultFilename, cacheControl, false, null,
                trustedContentType, peekLength, contentEncoding);
    }

    public static FileDirectoryHandler forImmutableFiles(final Path directory,
            final @Nullable String requestPathStartsWith, final boolean trustedContentType) {
        return forImmutableFiles(directory, requestPathStartsWith, DEFAULT_DEFAULT_FILENAME, MAX_AGE_1_YEAR_IMMUTABLE,
                Caffeine.newBuilder().expireAfterAccess(7, DAYS).softValues().build(), trustedContentType,
                DEFAULT_PEEK_LENGTH, null);
    }

    /**
     * Creates a new {@link FileDirectoryHandler} instance for immutable files (see {@link #isImmutableFiles()}).
     *
     * @param directory              the {@link #getDirectory()}
     * @param requestPathStartsWith  the {@link #getRequestPathStartsWith()}
     * @param defaultFilename        the {@link #getDefaultFilename()}
     * @param cacheControl           the {@link #getCacheControl()}
     * @param cacheForImmutableFiles the {@link Resource} {@link Cache}
     * @param trustedContentType     see {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer,
     *                               ContentEncoding, boolean)}
     * @param peekLength             see {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer,
     *                               ContentEncoding, boolean)}
     * @param contentEncoding        see {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer,
     *                               ContentEncoding, boolean)}
     *
     * @return the new {@link FileDirectoryHandler} instance
     */
    public static FileDirectoryHandler forImmutableFiles(final Path directory,
            final @Nullable String requestPathStartsWith, final @Nullable String defaultFilename,
            final @Nullable ResponseCacheControl cacheControl,
            final @Nullable Cache<String, Resource> cacheForImmutableFiles, final boolean trustedContentType,
            final @Nullable Integer peekLength, final @Nullable ContentEncoding contentEncoding) {
        return new FileDirectoryHandler(directory, requestPathStartsWith, defaultFilename, cacheControl, true,
                cacheForImmutableFiles, trustedContentType, peekLength, contentEncoding);
    }

    /**
     * The directory {@link Path} to serve files from.
     */
    private final @Getter Path directory;

    /**
     * Serve files in {@link #getDirectory()} relative to {@link Request#getUrl()} {@link Url#getNormalizedPath()} with
     * this {@link String} removed from the beginning, or <code>null</code> to not substring {@link Request#getUrl()}
     * {@link Url#getNormalizedPath()}.
     */
    private final @Getter @Nullable String requestPathStartsWith;

    /**
     * If the request path represents a directory, attempt to serve a file in that directory with this filename.
     */
    private final @Getter @Nullable String defaultFilename;

    /**
     * The {@link ResponseCacheControl} to set for files served from {@link #getDirectory()}.
     */
    private final @Getter @Nullable ResponseCacheControl cacheControl;

    /**
     * <code>true</code> if all files in {@link #getDirectory()} are immutable (the path, name, metadata, and content
     * will never change while the JVM is running) and computed {@link Resource}s should be internally cached and use
     * a strong {@link ETag}, <code>false</code> otherwise.
     * <p>
     * Note: Call {@link #immutableCacheInvalidate(Path)} when the immutable {@link File} is deleted.
     */
    private final @Getter boolean immutableFiles;

    private final @Nullable Cache<String, Resource> cacheForImmutableFiles;
    private final boolean trustedContentType;
    private final @Nullable Integer peekLength;
    private final @Nullable ContentEncoding contentEncoding;

    private FileDirectoryHandler(final Path directory, final @Nullable String requestPathStartsWith,
            final @Nullable String defaultFilename, final @Nullable ResponseCacheControl cacheControl,
            final boolean immutableFiles, final @Nullable Cache<String, Resource> cacheForImmutableFiles,
            final boolean trustedContentType, final @Nullable Integer peekLength,
            final @Nullable ContentEncoding contentEncoding) {
        checkArgument(Files.isDirectory(directory), "Not a directory: %s", directory);
        this.directory = directory.toAbsolutePath();
        this.requestPathStartsWith = requestPathStartsWith;
        this.defaultFilename = defaultFilename;
        this.cacheControl = cacheControl;
        this.immutableFiles = immutableFiles;
        this.cacheForImmutableFiles = cacheForImmutableFiles;
        checkArgument(immutableFiles == (cacheForImmutableFiles != null),
                "A cache must be provided if `immutableFiles` is `true`");
        this.trustedContentType = trustedContentType;
        this.peekLength = peekLength;
        this.contentEncoding = contentEncoding;
    }

    @Override
    public void handle(final Handle handle) {
        final var requestPath = handle.getRequest().getUrl().getNormalizedPath();
        final String requestPathNoStartsWith;
        if (requestPathStartsWith != null) {
            if (!requestPath.startsWith(requestPathStartsWith)) {
                throw new StatusException(NOT_FOUND_404,
                        "Request path does not start with \"%s\": %s".formatted(requestPathStartsWith, requestPath));
            }
            requestPathNoStartsWith = requestPath.substring(requestPathStartsWith.length() + 1);
        } else {
            requestPathNoStartsWith = requestPath;
        }
        final var requestFile = directory.resolve(requestPathNoStartsWith).normalize();
        if (!requestFile.startsWith(directory)) {
            throw new StatusException(NOT_FOUND_404,
                    "Request file does not start with \"%s\": %s".formatted(directory, requestFile));
        }
        final Path requestFileOrDefault;
        if (defaultFilename != null && Files.isDirectory(requestFile)) {
            requestFileOrDefault = directory.resolve(defaultFilename);
        } else {
            requestFileOrDefault = requestFile;
        }
        if (!Files.isRegularFile(requestFileOrDefault)) {
            throw new StatusException(NOT_FOUND_404, "Invalid file: " + requestFileOrDefault);
        }
        final var response = handle.getResponse();
        final Resource resource;
        if (cacheForImmutableFiles != null) {
            resource = cacheForImmutableFiles.get(requestFileOrDefault.toString(), _ -> Resource.ofFile(
                    requestFileOrDefault, true, trustedContentType, null, peekLength, contentEncoding, true));
        } else {
            resource = Resource.ofFile(
                    requestFileOrDefault, false, trustedContentType, null, peekLength, contentEncoding, true);
        }
        response.responseResource(resource);
        if (cacheControl != null) {
            response.setCacheControl(cacheControl);
        }
    }

    /**
     * If this {@link FileDirectoryHandler} is for immutable files, this method will invalidate the {@link Resource}
     * cache entry for the given file {@link Path}. This should be called when an immutable file previously served by
     * this {@link FileDirectoryHandler} is deleted.
     *
     * @param file the file {@link Path}
     */
    public void immutableCacheInvalidate(final Path file) {
        checkArgument(cacheForImmutableFiles != null, "This `FileDirectoryHandler` doesn't cache immutable files");
        cacheForImmutableFiles.invalidate(file.toString());
    }
}
