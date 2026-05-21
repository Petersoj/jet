package net.jacobpeterson.jet.server.handle.response.compression;

import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.header.headers.Headers;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.BROTLI;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.DEFLATE;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.GZIP;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.ZSTANDARD;

/**
 * {@link CompressionConfig} is an immutable class that represents the configuration for response compression.
 */
@NullMarked
@Immutable
@Value @Builder(toBuilder = true)
public class CompressionConfig {

    /**
     * An {@link ImmutableList} of {@link Level}s with {@link Level#getLevel()} set to <code>null</code> and in the
     * following order: {@link CompressionType#ZSTANDARD}, {@link CompressionType#BROTLI}, {@link CompressionType#GZIP},
     * {@link CompressionType#DEFLATE}.
     */
    public static final ImmutableList<Level> DEFAULT_LEVELS = ImmutableList.of(
            new Level(ZSTANDARD, null),
            new Level(BROTLI, null),
            new Level(GZIP, null),
            new Level(DEFLATE, null));

    /**
     * {@link Level} is an immutable class that represents a {@link CompressionType} mapped to a compression level.
     */
    @Immutable
    @Value @lombok.Builder(toBuilder = true)
    public static class Level {

        /**
         * The {@link CompressionType}.
         */
        CompressionType type;

        /**
         * The compression level, or <code>null</code> for {@link CompressionType#getDefaultLevel()}.
         */
        @Nullable Integer level;
    }

    /**
     * The {@link ImmutableList} of {@link Level}s, in order of preference.
     * <p>
     * Defaults to {@link #DEFAULT_LEVELS}.
     */
    @Default ImmutableList<Level> levels = DEFAULT_LEVELS;

    /**
     * Whether to call {@link Headers#ensureEntryContainingIgnoreCase(String, String)} with {@link Header#VARY} and
     * {@link Header#ACCEPT_ENCODING}.
     * <p>
     * Defaults to <code>true</code>.
     */
    @Default boolean ensureVaryHeader = true;

    /**
     * Whether to check the response {@link Header#CONTENT_ENCODING} to prevent double compression.
     * <p>
     * Defaults to <code>true</code>.
     */
    @Default boolean checkContentEncoding = true;

    /**
     * Whether to check the response {@link Header#CONTENT_RANGE} to prevent partial content compression.
     * <p>
     * Defaults to <code>true</code>.
     */
    @Default boolean checkContentRange = true;

    /**
     * The minimum value of the response {@link Header#CONTENT_LENGTH} to enable response compression, or
     * <code>null</code> to not check.
     * <p>
     * Defaults to <code>1 KiB</code>.
     */
    @Default @Nullable Long minimumContentLength = 1024L;

    /**
     * Whether to check the response {@link Header#CONTENT_TYPE} for {@link ContentType#isCompressed()} to prevent
     * double compression.
     * <p>
     * Defaults to <code>true</code>.
     */
    @Default boolean checkContentType = true;

    /**
     * Whether to use {@link ETag.Builder#value(String, CompressionType)} for an existing response {@link Header#ETAG}.
     * <p>
     * Defaults to <code>true</code>.
     */
    @Default boolean modifyETag = true;
}
