package net.jacobpeterson.jet.server.handle.request.multipart;

import com.google.errorprone.annotations.Immutable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;

/**
 * {@link MultipartConfig} is an immutable class that represents the configuration for handling a multipart web
 * server request.
 */
@NullMarked
@Immutable
@Value @Builder(toBuilder = true)
public class MultipartConfig {

    /**
     * The default for {@link #getTemporaryDirectory()}: <code>System.getProperty("java.io.tmpdir")</code>
     */
    public static final Path DEFAULT_TEMPORARY_DIRECTORY = Path.of(System.getProperty("java.io.tmpdir"));

    /**
     * The maximum total size (in bytes) of the multipart request.
     * <p>
     * Defaults to <code>1 GiB</code>.
     */
    @Default long maxTotalSize = 1024 * 1024 * 1024;

    /**
     * The maximum number of {@link MultiPart}s in the multipart request.
     * <p>
     * Defaults to <code>100</code>.
     */
    @Default int maxPartCount = 100;

    /**
     * The maximum size (in bytes) of a {@link MultiPart}.
     * <p>
     * Defaults to <code>1 GiB</code>.
     */
    @Default long maxPartSize = 1024 * 1024 * 1024;

    /**
     * The size (in bytes) at which a {@link MultiPart} buffer is moved from memory to disk (stored in
     * {@link #getTemporaryDirectory()}).
     * <p>
     * Defaults to <code>8 MiB</code>.
     */
    @Default long partSizeMemoryToDiskThreshold = 8 * 1024 * 1024;

    /**
     * The directory {@link Path} where {@link MultiPart}s are temporarily buffered to if {@link MultiPart#getSize()}
     * exceeds {@link #getPartSizeMemoryToDiskThreshold()}.
     * <p>
     * Defaults to {@link #DEFAULT_TEMPORARY_DIRECTORY}.
     */
    @Default Path temporaryDirectory = DEFAULT_TEMPORARY_DIRECTORY;
}
