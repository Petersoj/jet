package net.jacobpeterson.jet.server.handler.handler.directory;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import net.jacobpeterson.jet.common.http.header.contentencoding.ContentEncoding;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.url.Url;
import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import net.jacobpeterson.jet.server.handle.response.resource.Resource;
import net.jacobpeterson.jet.server.handler.handler.Handler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.util.Objects.requireNonNull;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static net.jacobpeterson.jet.common.http.url.Url.pathTrimLeading;

/**
 * {@link FileDirectoryHandler} is a {@link Handler} for serving files in a given directory {@link Path} using
 * {@link Response#responseResource(Resource)} with
 * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}, with support for
 * relativizing request paths, serving a default file (like <code>index.html</code>) for request paths representing a
 * directory, applying a {@link ResponseCacheControl}, caching {@link Resource} instances from with automatic cache
 * invalidation using {@link WatchService}, and applying a {@link ResponseCacheControl}.
 * <p>
 * Note: this class implements {@link AutoCloseable}, but calling {@link #close()} is not necessary if
 * {@link #isWatchServiceEnabled()} is <code>false</code>.
 */
@NullMarked
@Slf4j
public class FileDirectoryHandler implements Handler, AutoCloseable {

    /**
     * The default {@link #getDefaultFilename()}: <code>"index.html"</code>
     */
    public static final String INDEX_HTML_FILENAME = "index.html";

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
     * The {@link ResponseCacheControl} for {@link Response#setCacheControl(ResponseCacheControl)}, or <code>null</code>
     * to not call {@link Response#setCacheControl(ResponseCacheControl)}.
     */
    private final @Getter @Nullable ResponseCacheControl cacheControl;

    /**
     * The <code>strongETag</code> argument for
     * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    private final @Getter boolean strongETag;

    /**
     * The <code>trustedContentType</code> argument for
     * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    private final @Getter boolean trustedContentType;

    /**
     * The <code>peekLength</code> argument for
     * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    private final @Getter @Nullable Integer peekLength;

    /**
     * The <code>contentEncoding</code> argument for
     * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    private final @Getter @Nullable ContentEncoding contentEncoding;

    private final @Nullable Cache<String, Resource> resourcesOfPathsCache;
    private final @Getter @Nullable WatchService watchService;

    /**
     * Instantiates a new {@link FileDirectoryHandler}.
     *
     * @param directory             the {@link #getDirectory()}
     * @param requestPathStartsWith the {@link #getRequestPathStartsWith()}
     * @param defaultFilename       the {@link #getDefaultFilename()}
     * @param cacheControl          the {@link #getCacheControl()}
     * @param strongETag            the {@link #isStrongETag()}
     * @param trustedContentType    the {@link #isTrustedContentType()}
     * @param peekLength            the {@link #getPeekLength()}
     * @param contentEncoding       the {@link #getContentEncoding()}
     * @param resourcesOfPathsCache the {@link Resource} {@link Cache}
     * @param enableWatchService    the {@link #isWatchServiceEnabled()}
     */
    public FileDirectoryHandler(final Path directory, final @Nullable String requestPathStartsWith,
            final @Nullable String defaultFilename, final @Nullable ResponseCacheControl cacheControl,
            final boolean strongETag, final boolean trustedContentType, final @Nullable Integer peekLength,
            final @Nullable ContentEncoding contentEncoding,
            final @Nullable Cache<String, Resource> resourcesOfPathsCache, final boolean enableWatchService) {
        checkArgument(Files.isDirectory(directory), "Not a directory: %s", directory);
        this.directory = directory.toAbsolutePath();
        this.requestPathStartsWith = requestPathStartsWith;
        this.defaultFilename = defaultFilename != null ? pathTrimLeading(defaultFilename) : null;
        this.cacheControl = cacheControl;
        this.strongETag = strongETag;
        this.trustedContentType = trustedContentType;
        this.peekLength = peekLength;
        this.contentEncoding = contentEncoding;
        this.resourcesOfPathsCache = resourcesOfPathsCache;
        if (resourcesOfPathsCache != null && enableWatchService) {
            try {
                watchService = directory.getFileSystem().newWatchService();
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
            Thread.ofVirtual().start(new Runnable() {

                private final Map<WatchKey, Path> directoriesOfWatchKeys = new HashMap<>();

                @Override
                public void run() {
                    try (watchService) {
                        registerRecursively(requireNonNull(watchService), directory);
                        while (true) {
                            final WatchKey watchKey;
                            try {
                                watchKey = watchService.take();
                            } catch (final ClosedWatchServiceException | InterruptedException exception) {
                                return;
                            }
                            for (final var pollEvent : watchKey.pollEvents()) {
                                final var eventKind = pollEvent.kind();
                                if (eventKind == OVERFLOW) {
                                    LOGGER.warn("`WatchService` `OVERFLOW` event occurred: {}", directory);
                                    continue;
                                }
                                final var directoryOfWatchKey = directoriesOfWatchKeys.get(watchKey);
                                if (directoryOfWatchKey == null) {
                                    continue;
                                }
                                final var eventPath = directoryOfWatchKey.resolve((Path) pollEvent.context());
                                final var isDirectory = Files.isDirectory(eventPath, NOFOLLOW_LINKS);
                                if (isDirectory && eventKind == ENTRY_CREATE) {
                                    registerRecursively(watchService, eventPath);
                                } else if (!isDirectory && (eventKind == ENTRY_MODIFY || eventKind == ENTRY_DELETE)) {
                                    resourcesOfPathsCache.invalidate(eventPath.toString());
                                }
                            }
                            if (!watchKey.reset()) {
                                directoriesOfWatchKeys.remove(watchKey);
                                checkState(!directoriesOfWatchKeys.isEmpty());
                            }
                        }
                    } catch (final Throwable throwable) {
                        LOGGER.error("`FileDirectoryHandler` `WatchService` threw", throwable);
                    }
                }

                private void registerRecursively(final WatchService watchService, final Path startDirectory)
                        throws IOException {
                    walkFileTree(startDirectory, new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult preVisitDirectory(final Path directory, final BasicFileAttributes attrs)
                                throws IOException {
                            directoriesOfWatchKeys.put(directory.register(watchService,
                                    ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE), directory);
                            return CONTINUE;
                        }
                    });
                }
            });
        } else {
            watchService = null;
        }
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
        final var requestFile = directory.resolve(pathTrimLeading(requestPathNoStartsWith)).normalize();
        if (!requestFile.startsWith(directory)) {
            throw new StatusException(NOT_FOUND_404,
                    "Request file does not start with \"%s\": %s".formatted(directory, requestFile));
        }
        final Path requestFileOrDefault;
        if (defaultFilename != null && Files.isDirectory(requestFile)) {
            requestFileOrDefault = requestFile.resolve(defaultFilename);
        } else {
            requestFileOrDefault = requestFile;
        }
        if (!Files.isRegularFile(requestFileOrDefault)) {
            throw new StatusException(NOT_FOUND_404, "Invalid file: " + requestFileOrDefault);
        }
        final var response = handle.getResponse();
        response.responseResource(resourcesOfPathsCache != null ?
                resourcesOfPathsCache.get(requestFileOrDefault.toString(), _ ->
                        Resource.ofFile(requestFileOrDefault, strongETag, trustedContentType, null, peekLength,
                                contentEncoding, true)) :
                Resource.ofFile(requestFileOrDefault, strongETag, trustedContentType, null, peekLength,
                        contentEncoding, true));
        if (cacheControl != null) {
            response.setCacheControl(cacheControl);
        }
    }

    /**
     * If this {@link FileDirectoryHandler} has a {@link Resource} cache, this method will invalidate the
     * {@link Resource} cache entry for the given file {@link Path}. This should be called when a file previously served
     * by this {@link FileDirectoryHandler} is modified or deleted, although this may already be done automatically if
     * {@link #isWatchServiceEnabled()}.
     *
     * @param file the file {@link Path}
     */
    public void cacheInvalidate(final Path file) {
        checkArgument(resourcesOfPathsCache != null, "This `FileDirectoryHandler` does not have a `Resource` cache");
        resourcesOfPathsCache.invalidate(file.toString());
    }

    /**
     * @return <code>true</code> if {@link WatchService} is used to automatically call {@link #cacheInvalidate(Path)}
     * on file modification or deletion (and {@link #close()} must be called to release {@link WatchService} resources
     * when no longer using this {@link FileDirectoryHandler} instance), <code>false</code> otherwise
     */
    public boolean isWatchServiceEnabled() {
        return watchService != null;
    }

    @Override
    public void close() throws Exception {
        if (watchService != null) {
            watchService.close();
        }
    }
}
