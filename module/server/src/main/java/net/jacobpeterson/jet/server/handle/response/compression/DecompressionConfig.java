package net.jacobpeterson.jet.server.handle.response.compression;

import com.google.errorprone.annotations.Immutable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import net.jacobpeterson.jet.common.http.header.headers.Headers;
import org.jspecify.annotations.NullMarked;

/**
 * {@link DecompressionConfig} is an immutable class that represents the configuration for transparent response
 * decompression.
 */
@NullMarked
@Immutable
@Value @Builder(toBuilder = true)
public class DecompressionConfig {

    /**
     * A static {@link DecompressionConfig} instance with all default values.
     */
    public static final DecompressionConfig DEFAULT = builder().build();

    /**
     * Whether to call {@link Headers#ensureEntryContainingIgnoreCase(String, String)} with {@link Header#VARY} and
     * {@link Header#ACCEPT_ENCODING}.
     * <p>
     * Defaults to <code>true</code>.
     */
    @Default boolean ensureVaryHeader = true;

    /**
     * Whether to modify an existing response {@link Header#ETAG} by using
     * {@link ETag#getValueWithoutCompressedSuffix()} or adding a suffix denoting decompression was applied.
     * <p>
     * Defaults to <code>true</code>.
     */
    @Default boolean modifyETag = true;
}
