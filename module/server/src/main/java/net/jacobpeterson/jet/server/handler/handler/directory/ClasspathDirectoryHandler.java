package net.jacobpeterson.jet.server.handler.handler.directory;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
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
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.nio.file.FileSystems.newFileSystem;
import static java.nio.file.Files.walk;
import static java.util.Objects.requireNonNull;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.MAX_AGE_1_YEAR_IMMUTABLE;
import static net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl.NO_CACHE;
import static net.jacobpeterson.jet.common.http.status.Status.NOT_FOUND_404;
import static net.jacobpeterson.jet.common.http.url.Url.pathTrimLeading;
import static net.jacobpeterson.jet.common.http.url.Url.pathTrimTrailing;
import static net.jacobpeterson.jet.server.handle.response.resource.Resource.DEFAULT_PEEK_LENGTH;

/**
 * {@link ClasspathDirectoryHandler} is a {@link Handler} for serving files in a given classpath directory using
 * {@link Response#responseResource(Resource)} with
 * {@link Resource#ofClasspath(Class, String, boolean, ContentType, Integer, ContentEncoding, boolean)}, with support
 * for relativizing request paths, serving a default file (like <code>/index.html</code>) for request paths representing
 * a directory, serving a default file for requests paths representing a file without an extension (like
 * <code>/index</code>), redirecting to default files or default extensions, and applying a
 * {@link ResponseCacheControl}.
 */
@NullMarked
public class ClasspathDirectoryHandler implements Handler {

    /**
     * @return {@link #simple(Class, String, String, ResponseCacheControl, boolean)} with <code>cacheControl</code> set
     * to {@link ResponseCacheControl#NO_CACHE}
     */
    public static ClasspathDirectoryHandler simpleMutable(final Class<?> clazz, final String directory,
            final @Nullable String requestPathStartsWith, final boolean trustedContentType) {
        return simple(clazz, directory, requestPathStartsWith, NO_CACHE, trustedContentType);
    }

    /**
     * Note: only use this method if a given request path will <strong>always</strong> return the same file
     * {@link Resource}. This should typically only be used for directories that contain public immutable files with a
     * tokenized filename e.g. a random {@link UUID} or a cache-busting version number, such as from a Vite build.
     * {@link ResponseCacheControl#MAX_AGE_1_YEAR_IMMUTABLE} requires that both the request path is static
     * <strong>and</strong> the file is immutable.
     *
     * @return {@link #simple(Class, String, String, ResponseCacheControl, boolean)} with <code>cacheControl</code> set
     * to {@link ResponseCacheControl#MAX_AGE_1_YEAR_IMMUTABLE}
     */
    public static ClasspathDirectoryHandler simpleImmutable(final Class<?> clazz, final String directory,
            final @Nullable String requestPathStartsWith, final boolean trustedContentType) {
        return simple(clazz, directory, requestPathStartsWith, MAX_AGE_1_YEAR_IMMUTABLE, trustedContentType);
    }

    /**
     * @return a new {@link FileDirectoryHandler} with
     * <code>requestPathRelativizer</code> set to a {@link String} {@link UnaryOperator} that removes the given
     * <code>requestPathAlwaysStartsWith</code> from the start of the given request path,
     * <code>defaultFilename</code> set to <code>"index.html"</code>,
     * <code>defaultExtension</code> set to <code>".html"</code>,
     * <code>redirectToDefault</code> set to <code>true</code>,
     * <code>strongETag</code> set to <code>true</code>,
     * <code>peekLength</code> set to {@link Resource#DEFAULT_PEEK_LENGTH}, and
     * <code>contentEncoding</code> set to <code>null</code>.
     */
    public static ClasspathDirectoryHandler simple(final Class<?> clazz, final String directory,
            final @Nullable String requestPathAlwaysStartsWith, final @Nullable ResponseCacheControl cacheControl,
            final boolean trustedContentType) {
        return builder()
                .clazz(clazz)
                .directory(directory)
                .requestPathRelativizer(requestPathAlwaysStartsWith == null ? null : requestPath ->
                        requestPath.substring(requestPathAlwaysStartsWith.length()))
                .defaultFilename("index.html")
                .defaultExtension(".html")
                .redirectToDefault(true)
                .cacheControl(cacheControl)
                .trustedContentType(trustedContentType)
                .peekLength(DEFAULT_PEEK_LENGTH)
                .contentEncoding(null)
                .build();
    }

    /**
     * The {@link Class} to call {@link Class#getResource(String)} on.
     */
    private final @Getter Class<?> clazz;

    /**
     * The directory path relative to {@link #getClazz()}.
     */
    private final @Getter String directory;

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
     * The <code>trustedContentType</code> argument for
     * {@link Resource#ofClasspath(Class, String, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    private final @Getter boolean trustedContentType;

    /**
     * The <code>peekLength</code> argument for
     * {@link Resource#ofClasspath(Class, String, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    private final @Getter @Nullable Integer peekLength;

    /**
     * The <code>contentEncoding</code> argument for
     * {@link Resource#ofClasspath(Class, String, boolean, ContentType, Integer, ContentEncoding, boolean)}.
     */
    private final @Getter @Nullable ContentEncoding contentEncoding;

    private final @Nullable String defaultFilenameWithoutDefaultExtension;
    private final ImmutableSet<String> filePaths;

    /**
     * Instantiates a new {@link FileDirectoryHandler}.
     *
     * @param clazz                  the {@link #getClazz()}
     * @param directory              the {@link #getDirectory()}
     * @param requestPathRelativizer the {@link #getRequestPathRelativizer()}
     * @param defaultFilename        the {@link #getDefaultFilename()}
     * @param defaultExtension       the {@link #getDefaultExtension()}
     * @param redirectToDefault      the {@link #isRedirectToDefault()}
     * @param cacheControl           the {@link #getCacheControl()}
     * @param trustedContentType     the {@link #isTrustedContentType()}
     * @param peekLength             the {@link #getPeekLength()}
     * @param contentEncoding        the {@link #getContentEncoding()}
     */
    @lombok.Builder
    private ClasspathDirectoryHandler(final Class<?> clazz, final String directory,
            final @Nullable UnaryOperator<String> requestPathRelativizer,
            final @Nullable String defaultFilename, final @Nullable String defaultExtension,
            final boolean redirectToDefault, final @Nullable ResponseCacheControl cacheControl,
            final boolean trustedContentType, final @Nullable Integer peekLength,
            final @Nullable ContentEncoding contentEncoding) {
        this.clazz = clazz;
        this.directory = pathTrimTrailing(directory);
        this.requestPathRelativizer = requestPathRelativizer;
        this.defaultFilename = defaultFilename;
        this.defaultExtension = defaultExtension;
        defaultFilenameWithoutDefaultExtension = defaultFilename != null && defaultExtension != null &&
                defaultFilename.endsWith(defaultExtension) ?
                defaultFilename.substring(0, defaultFilename.length() - defaultExtension.length()) : null;
        this.redirectToDefault = redirectToDefault;
        this.cacheControl = cacheControl;
        this.trustedContentType = trustedContentType;
        this.peekLength = peekLength;
        this.contentEncoding = contentEncoding;
        try {
            final var directoryUri = requireNonNull(clazz.getResource(directory), directory).toURI();
            final var scheme = directoryUri.getScheme();
            if (scheme.equals("jar")) {
                try (final var jarFileSystem = newFileSystem(directoryUri, Map.of())) {
                    filePaths = getClasspathResourceFiles(jarFileSystem.getPath(directory));
                }
            } else if (scheme.equals("file")) {
                filePaths = getClasspathResourceFiles(Path.of(directoryUri));
            } else {
                throw new UnsupportedOperationException("Scheme: " + scheme);
            }
        } catch (final URISyntaxException exception) {
            throw new RuntimeException(exception);
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private ImmutableSet<String> getClasspathResourceFiles(final Path directoryPath) throws IOException {
        try (final var walk = walk(directoryPath)) {
            return walk.filter(Files::isRegularFile)
                    .filter(path -> !path.endsWith(".class"))
                    .map(path -> directory + "/" + directoryPath.relativize(path))
                    .collect(toImmutableSet());
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
        var requestFile = directory + "/" + pathTrimLeading(requestPathRelativizer == null ? requestPath :
                requestPathRelativizer.apply(requestPath));
        if (!filePaths.contains(requestFile)) {
            var defaultFound = false;
            if (defaultFilename != null) {
                final var requestFileDefaultFilename = pathTrimTrailing(requestFile) + "/" + defaultFilename;
                if (filePaths.contains(requestFileDefaultFilename)) {
                    defaultFound = true;
                    requestFile = requestFileDefaultFilename;
                }
            }
            if (!defaultFound && defaultExtension != null) {
                final var requestFileDefaultExtension = requestFile + defaultExtension;
                if (filePaths.contains(requestFileDefaultExtension)) {
                    defaultFound = true;
                    requestFile = requestFileDefaultExtension;
                }
            }
            if (!defaultFound) {
                throw new StatusException(NOT_FOUND_404, "Invalid file: " + requestFile);
            }
        }
        final var response = handle.getResponse();
        response.responseResource(Resource.ofClasspath(clazz, requestFile, trustedContentType, null, peekLength,
                contentEncoding, true));
        if (cacheControl != null) {
            response.setCacheControl(cacheControl);
        }
    }
}
