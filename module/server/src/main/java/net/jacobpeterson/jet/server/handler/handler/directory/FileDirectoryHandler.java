package net.jacobpeterson.jet.server.handler.handler.directory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

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
import static java.util.concurrent.TimeUnit.DAYS;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.MAX_AGE_1_YEAR_IMMUTABLE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.NO_CACHE;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static net.jacobpeterson.jet.common.http.url.Url.pathTrimLeading;
import static net.jacobpeterson.jet.server.handle.response.resource.Resource.DEFAULT_PEEK_LENGTH;

/**
 * {@link FileDirectoryHandler} is a {@link Handler} for serving files in a given directory {@link Path} using
 * {@link Response#responseResource(Resource)} with
 * {@link Resource#ofFile(Path, boolean, boolean, ContentType, Integer, ContentEncoding, boolean)}, with support for
 * relativizing request paths, serving a default file (like <code>/index.html</code>) for request paths representing a
 * directory, serving a default file for requests paths representing a file without an extension (like
 * <code>/index</code>), redirecting to default files or default extensions, applying a {@link ResponseCacheControl},
 * and caching {@link Resource} instances from with automatic cache invalidation using {@link WatchService}.
 * <p>
 * Note: this class implements {@link AutoCloseable}, but calling {@link #close()} is not necessary if
 * {@link #isWatchServiceEnabled()} is <code>false</code>.
 */
@NullMarked
@Slf4j
public class FileDirectoryHandler implements Handler, AutoCloseable {

    /**
     * @return {@link #simple(Path, String, ResponseCacheControl, boolean)} with <code>cacheControl</code> set to
     * {@link ResponseCacheControl#NO_CACHE}
     */
    public static FileDirectoryHandler simpleMutable(final Path directory, final @Nullable String requestPathStartsWith,
            final boolean trustedContentType) {
        return simple(directory, requestPathStartsWith, NO_CACHE, trustedContentType);
    }

    /**
     * Note: only use this method if a given request path will <strong>always</strong> return the same file
     * {@link Resource}. This should typically only be used for directories that contain public immutable files with a
     * tokenized filename e.g. a random {@link UUID} or a cache-busting version number, such as from a Vite build.
     * {@link ResponseCacheControl#MAX_AGE_1_YEAR_IMMUTABLE} requires that both the request path is static
     * <strong>and</strong> the file is immutable.
     *
     * @return {@link #simple(Path, String, ResponseCacheControl, boolean)} with <code>cacheControl</code> set to
     * {@link ResponseCacheControl#MAX_AGE_1_YEAR_IMMUTABLE}
     */
    public static FileDirectoryHandler simpleImmutable(final Path directory,
            final @Nullable String requestPathStartsWith, final boolean trustedContentType) {
        return simple(directory, requestPathStartsWith, MAX_AGE_1_YEAR_IMMUTABLE, trustedContentType);
    }

    /**
     * @return a new {@link FileDirectoryHandler} with
     * <code>requestPathRelativizer</code> set to a {@link String} {@link UnaryOperator} that removes the given
     * <code>requestPathAlwaysStartsWith</code> from the start of the given request path,
     * <code>defaultFilename</code> set to <code>"index.html"</code>,
     * <code>defaultExtension</code> set to <code>".html"</code>,
     * <code>redirectToDefault</code> set to <code>true</code>,
     * <code>strongETag</code> set to <code>true</code>,
     * <code>peekLength</code> set to {@link Resource#DEFAULT_PEEK_LENGTH},
     * <code>contentEncoding</code> set to <code>null</code>,
     * <code>resourcesOfPathsCache</code> set to {@link Caffeine#expireAfterAccess(long, TimeUnit)} with 7 days and
     * {@link Caffeine#softValues()}, and
     * <code>enableWatchService</code> set to <code>true</code>
     */
    public static FileDirectoryHandler simple(final Path directory, final @Nullable String requestPathAlwaysStartsWith,
            final @Nullable ResponseCacheControl cacheControl, final boolean trustedContentType) {
        return builder()
                .directory(directory)
                .requestPathRelativizer(requestPathAlwaysStartsWith == null ? null : requestPath ->
                        requestPath.substring(requestPathAlwaysStartsWith.length()))
                .defaultFilename("index.html")
                .defaultExtension(".html")
                .redirectToDefault(true)
                .cacheControl(cacheControl)
                .strongETag(true)
                .trustedContentType(trustedContentType)
                .peekLength(DEFAULT_PEEK_LENGTH)
                .contentEncoding(null)
                .resourcesOfPathsCache(Caffeine.newBuilder()
                        .expireAfterAccess(7, DAYS)
                        .softValues()
                        .build())
                .enableWatchService(true)
                .build();
    }

    /**
     * The directory {@link Path} to serve files from.
     */
    private final @Getter Path directory;

    /**
     * A {@link String} {@link UnaryOperator} that transforms {@link Request#getUrl()} {@link Url#getNormalizedPath()}
     * into a path relative to {@link #getDirectory()}, or <code>null</code> to not transform {@link Request#getUrl()}
     * {@link Url#getNormalizedPath()}.
     */
    private final @Getter @Nullable UnaryOperator<String> requestPathRelativizer;

    /**
     * If a request path represents a directory, attempt to serve a file in that directory with this filename e.g.
     * <code>/blog</code> serves <code>/blog/index.html</code>.
     */
    private final @Getter @Nullable String defaultFilename;

    /**
     * If a request path represents a non-existent file, attempt to serve a file with this extension appended (include
     * the dot) e.g. <code>/blog/post</code> serves <code>/blog/post.html</code>.
     */
    private final @Getter @Nullable String defaultExtension;

    /**
     * Whether to call {@link Response#redirectTemporarily(Url)} to the request path with {@link #getDefaultFilename()}
     * or {@link #getDefaultExtension()}. This prevents multiple requests paths from serving the same content e.g.
     * helps establish the canonical request path of a file.
     */
    private final @Getter boolean redirectToDefault;

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

    private final @Nullable String defaultFilenameWithoutDefaultExtension;
    private final @Getter @Nullable WatchService watchService;
    private @Nullable Cache<String, Resource> resourcesOfPathsCache;

    /**
     * Instantiates a new {@link FileDirectoryHandler}.
     *
     * @param directory              the {@link #getDirectory()}
     * @param requestPathRelativizer the {@link #getRequestPathRelativizer()}
     * @param defaultFilename        the {@link #getDefaultFilename()}
     * @param defaultExtension       the {@link #getDefaultExtension()}
     * @param redirectToDefault      the {@link #isRedirectToDefault()}
     * @param cacheControl           the {@link #getCacheControl()}
     * @param strongETag             the {@link #isStrongETag()}
     * @param trustedContentType     the {@link #isTrustedContentType()}
     * @param peekLength             the {@link #getPeekLength()}
     * @param contentEncoding        the {@link #getContentEncoding()}
     * @param resourcesOfPathsCache  the {@link Resource} {@link Cache}, or <code>null</code> to disable caching
     * @param enableWatchService     the {@link #isWatchServiceEnabled()}
     */
    @lombok.Builder
    private FileDirectoryHandler(final Path directory, final @Nullable UnaryOperator<String> requestPathRelativizer,
            final @Nullable String defaultFilename, final @Nullable String defaultExtension,
            final boolean redirectToDefault, final @Nullable ResponseCacheControl cacheControl,
            final boolean strongETag, final boolean trustedContentType, final @Nullable Integer peekLength,
            final @Nullable ContentEncoding contentEncoding,
            final @Nullable Cache<String, Resource> resourcesOfPathsCache, final boolean enableWatchService) {
        checkArgument(Files.isDirectory(directory), "Not a directory: %s", directory);
        this.directory = directory.toAbsolutePath();
        this.requestPathRelativizer = requestPathRelativizer;
        this.defaultFilename = defaultFilename;
        this.defaultExtension = defaultExtension;
        defaultFilenameWithoutDefaultExtension = defaultFilename != null && defaultExtension != null &&
                defaultFilename.endsWith(defaultExtension) ?
                defaultFilename.substring(0, defaultFilename.length() - defaultExtension.length()) : null;
        this.redirectToDefault = redirectToDefault;
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
                            final var directoryOfWatchKey = directoriesOfWatchKeys.get(watchKey);
                            if (directoryOfWatchKey == null) {
                                continue;
                            }
                            for (final var pollEvent : watchKey.pollEvents()) {
                                final var eventKind = pollEvent.kind();
                                if (eventKind == OVERFLOW) {
                                    LOGGER.warn("`WatchService` `OVERFLOW` event occurred: {}", directory);
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
                        FileDirectoryHandler.this.resourcesOfPathsCache = null;
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
        final var requestUrl = handle.getRequest().getUrl();
        final var requestPath = requestUrl.getNormalizedPath();
        if (redirectToDefault) {
            final Integer redirectRemoveLength;
            if (defaultFilename != null && requestPath.endsWith(defaultFilename)) {
                redirectRemoveLength = defaultFilename.length();
            } else if (defaultExtension != null && requestPath.endsWith(defaultExtension)) {
                redirectRemoveLength = defaultExtension.length();
            } else if (defaultFilenameWithoutDefaultExtension != null &&
                    requestPath.endsWith(defaultFilenameWithoutDefaultExtension)) {
                redirectRemoveLength = defaultFilenameWithoutDefaultExtension.length();
            } else {
                redirectRemoveLength = null;
            }
            if (redirectRemoveLength != null) {
                handle.getResponse().redirectTemporarily(requestUrl.toBuilder()
                        .path(requestPath.substring(0, requestPath.length() - redirectRemoveLength))
                        .build());
                return;
            }
        }
        var requestFile = directory.resolve(pathTrimLeading(requestPathRelativizer == null ? requestPath :
                requestPathRelativizer.apply(requestPath))).normalize();
        if (!Files.isRegularFile(requestFile)) {
            if (defaultFilename != null && Files.isDirectory(requestFile)) {
                requestFile = requestFile.resolve(defaultFilename);
            } else if (defaultExtension != null) {
                requestFile = requestFile.resolveSibling(requestFile.getFileName().toString() + defaultExtension);
            } else {
                throw new StatusException(NOT_FOUND_404, "Invalid file: " + requestFile);
            }
            // No need to call `Files.isRegularFile()` here since `Resource.ofFile()` will check for file existence.
        }
        if (!requestFile.startsWith(directory)) { // Protect against path traversals
            throw new StatusException(NOT_FOUND_404,
                    "Request file does not start with \"%s\": %s".formatted(directory, requestFile));
        }
        final var response = handle.getResponse();
        final var fRequestFile = requestFile;
        // `resourcesOfPathsCache` null-check is racy, but NPE is unlikely and is better than using invalid `Resource`.
        response.responseResource(resourcesOfPathsCache != null ?
                resourcesOfPathsCache.get(fRequestFile.toString(), _ -> Resource.ofFile(
                        fRequestFile, strongETag, trustedContentType, null, peekLength, contentEncoding, true)) :
                Resource.ofFile(fRequestFile, strongETag, trustedContentType, null, peekLength, contentEncoding, true));
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
            resourcesOfPathsCache = null;
        }
    }
}
