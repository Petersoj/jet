package net.jacobpeterson.jet.common.http.header.contentencoding;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link CompressionType} is an enum that represents a {@link ContentEncoding} compression type.
 *
 * @see ContentEncoding
 */
@NullMarked
@RequiredArgsConstructor
public enum CompressionType {

    /**
     * A format using the <a href="https://en.wikipedia.org/wiki/LZ77_and_LZ78#LZ77">Lempel-Ziv coding</a> (LZ77), with
     * a 32-bit CRC. This is the original format of the UNIX <em>gzip</em> program. The HTTP/1.1 standard also
     * recommends that the servers supporting this content-encoding should recognize <code>x-gzip</code> as an alias,
     * for compatibility purposes.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#gzip">
     * developer.mozilla.org</a>
     */
    GZIP("gzip"),

    /**
     * A format using the <a href="https://en.wikipedia.org/wiki/LZW">Lempel-Ziv-Welch</a> (LZW) algorithm. The value
     * name was taken from the UNIX <em>compress</em> program, which implemented this algorithm. Like the compress
     * program, which has disappeared from most UNIX distributions, this content-encoding is not used by many browsers
     * today, partly because of a patent issue (it expired in 2003).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#compress">
     * developer.mozilla.org</a>
     */
    COMPRESS("compress"),

    /**
     * Using the <a href="https://en.wikipedia.org/wiki/Zlib">zlib</a> structure (defined in
     * <a href="https://datatracker.ietf.org/doc/html/rfc1950">RFC 1950</a>) with the
     * <a href="https://en.wikipedia.org/wiki/Deflate">deflate</a> compression algorithm (defined in
     * <a href="https://datatracker.ietf.org/doc/html/rfc1951">RFC 1951</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#deflate">
     * developer.mozilla.org</a>
     */
    DEFLATE("deflate"),

    /**
     * A format using the <a href="https://developer.mozilla.org/en-US/docs/Glossary/Brotli_compression">Brotli</a>
     * algorithm structure (defined in <a href="https://datatracker.ietf.org/doc/html/rfc7932">RFC 7932</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#br">
     * developer.mozilla.org</a>
     */
    BROTLI("br"),

    /**
     * A format using the <a href="https://developer.mozilla.org/en-US/docs/Glossary/Zstandard_compression">
     * Zstandard</a> algorithm structure (defined in <a href="https://datatracker.ietf.org/doc/html/rfc8878">RFC
     * 8878</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#zstd">
     * developer.mozilla.org</a>
     */
    ZSTANDARD("zstd"),

    /**
     * A format that uses the
     * <a href="https://datatracker.ietf.org/doc/html/draft-ietf-httpbis-compression-dictionary#name-dictionary-compressed-brotl">
     * Dictionary-Compressed Brotli algorithm</a>. See
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Compression_dictionary_transport">Compression
     * Dictionary Transport</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#dcb">
     * developer.mozilla.org</a>
     */
    DICTIONARY_COMPRESSED_BROTLI("dcb"),

    /**
     * A format that uses the
     * <a href="https://datatracker.ietf.org/doc/html/draft-ietf-httpbis-compression-dictionary#name-dictionary-compressed-zstan">
     * Dictionary-Compressed Zstandard algorithm</a>. See
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Compression_dictionary_transport">Compression
     * Dictionary Transport</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#dcz">
     * developer.mozilla.org</a>
     */
    DICTIONARY_COMPRESSED_ZSTANDARD("dcz");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link CompressionType}.
     */
    public static final ImmutableMap<String, CompressionType> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link CompressionType} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link CompressionType}, or <code>null</code> if no mapping exists
     */
    public static @Nullable CompressionType forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
