package net.jacobpeterson.jet.common.http.header.contentencoding;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link ContentEncoding} is an immutable class that represents a standardized HTTP {@link Header#CONTENT_ENCODING}.
 * <p>
 * The HTTP <strong><code>Content-Encoding</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Representation_header">representation header</a> lists the
 * encodings and the order in which they have been applied to a resource. This lets the recipient know how to decode the
 * data in order to obtain the original content format described in the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
 * <code>Content-Type</code></a> header. Content encoding is mainly used to compress content without losing information
 * about the original media type.
 * <p>
 * Servers should compress data as much as possible, and should use content encoding where appropriate. Compressing
 * already compressed media types, such as .zip or .jpeg, is usually not appropriate because it can increase the file
 * size. If the original media is already encoded (e.g., as a .zip file), this information is not included in the
 * <code>Content-Encoding</code> header.
 * <p>
 * When the <code>Content-Encoding</code> header is present, other metadata (e.g.,
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Length">
 * <code>Content-Length</code></a>) refer to the encoded form of the data, not the original resource, unless explicitly
 * stated. Content encoding differs to
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Transfer-Encoding">
 * <code>Transfer-Encoding</code></a> in that <code>Transfer-Encoding</code> handles how HTTP messages themselves are
 * delivered across the network on a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers#hop-by-hop_headers">
 * hop-by-hop basis</a>.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">
 * developer.mozilla.org</a>
 * @see Header#CONTENT_ENCODING
 * @see CompressionType
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
public final class ContentEncoding {

    /**
     * The types delimiter: <code>","</code>
     */
    public static final String TYPES_DELIMITER = ",";

    /**
     * Constant with {@link #getType()} as {@link CompressionType#GZIP}.
     */
    public static final ContentEncoding GZIP = ContentEncoding.builder()
            .type(CompressionType.GZIP)
            .build();

    /**
     * Constant with {@link #getType()} as {@link CompressionType#COMPRESS}.
     */
    public static final ContentEncoding COMPRESS = ContentEncoding.builder()
            .type(CompressionType.COMPRESS)
            .build();

    /**
     * Constant with {@link #getType()} as {@link CompressionType#DEFLATE}.
     */
    public static final ContentEncoding DEFLATE = ContentEncoding.builder()
            .type(CompressionType.DEFLATE)
            .build();

    /**
     * Constant with {@link #getType()} as {@link CompressionType#BROTLI}.
     */
    public static final ContentEncoding BROTLI = ContentEncoding.builder()
            .type(CompressionType.BROTLI)
            .build();

    /**
     * Constant with {@link #getType()} as {@link CompressionType#ZSTANDARD}.
     */
    public static final ContentEncoding ZSTANDARD = ContentEncoding.builder()
            .type(CompressionType.ZSTANDARD)
            .build();

    /**
     * Constant with {@link #getType()} as {@link CompressionType#DICTIONARY_COMPRESSED_BROTLI}.
     */
    public static final ContentEncoding DICTIONARY_COMPRESSED_BROTLI = ContentEncoding.builder()
            .type(CompressionType.DICTIONARY_COMPRESSED_BROTLI)
            .build();

    /**
     * Constant with {@link #getType()} as {@link CompressionType#DICTIONARY_COMPRESSED_ZSTANDARD}.
     */
    public static final ContentEncoding DICTIONARY_COMPRESSED_ZSTANDARD = ContentEncoding.builder()
            .type(CompressionType.DICTIONARY_COMPRESSED_ZSTANDARD)
            .build();

    /**
     * An {@link ImmutableMap} of all the public static {@link ContentEncoding}s declared in this class with
     * {@link ContentEncoding#toString()} as the key.
     */
    public static final ImmutableMap<String, ContentEncoding> COMMON_CONTENT_ENCODINGS = ImmutableSet.of(
                    GZIP,
                    COMPRESS,
                    DEFLATE,
                    BROTLI,
                    ZSTANDARD,
                    DICTIONARY_COMPRESSED_BROTLI,
                    DICTIONARY_COMPRESSED_ZSTANDARD)
            .stream().collect(toImmutableMap(ContentEncoding::toString, identity()));

    private static final Splitter PARSE_TYPES_SPLITTER =
            Splitter.on(TYPES_DELIMITER).trimResults().omitEmptyStrings();

    /**
     * Parses the given {@link Header#CONTENT_ENCODING} value {@link String} into a {@link ContentEncoding}, firstly
     * checking {@link #COMMON_CONTENT_ENCODINGS} {@link ImmutableMap#get(Object)}.
     *
     * @param contentEncoding the {@link Header#CONTENT_ENCODING} value {@link String}
     *
     * @return the {@link ContentEncoding}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static ContentEncoding parse(final String contentEncoding) throws IllegalArgumentException {
        final var commonContentEncoding = COMMON_CONTENT_ENCODINGS.get(contentEncoding.trim().toLowerCase(ROOT));
        if (commonContentEncoding != null) {
            return commonContentEncoding;
        }
        final var builder = builder();
        for (final var type : PARSE_TYPES_SPLITTER.split(contentEncoding)) {
            final var typeEnum = CompressionType.forString(type);
            checkArgument(typeEnum != null, "Unknown type: %s", type);
            builder.type(typeEnum);
        }
        return builder.build();
    }

    /**
     * Creates a {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder class for {@link ContentEncoding}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private final ImmutableList.Builder<CompressionType> types = ImmutableList.builderWithExpectedSize(1);

        /**
         * @see #getTypes()
         */
        public Builder type(final CompressionType compressionType) {
            types.add(compressionType);
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link ContentEncoding} instance.
         *
         * @return the built {@link ContentEncoding}
         *
         * @throws IllegalArgumentException thrown if {@link #type(CompressionType)} was never called
         */
        public ContentEncoding build() throws IllegalArgumentException {
            final var types = this.types.build();
            checkArgument(!types.isEmpty(), "`type()` was never called");
            return new ContentEncoding(types);
        }
    }

    /**
     * The {@link ImmutableList} of {@link CompressionType}s.
     */
    private final @Getter @EqualsAndHashCode.Include ImmutableList<CompressionType> types;

    private @LazyInit @Nullable String string;

    /**
     * @return {@link #getTypes()} {@link ImmutableList#getFirst()}
     */
    public CompressionType getType() {
        return types.getFirst();
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#CONTENT_ENCODING}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            string = types.stream()
                    .map(CompressionType::toString)
                    .collect(joining(TYPES_DELIMITER + " "));
        }
        return string;
    }
}
