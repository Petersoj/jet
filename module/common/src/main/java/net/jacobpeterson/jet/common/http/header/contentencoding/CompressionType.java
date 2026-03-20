package net.jacobpeterson.jet.common.http.header.contentencoding;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import com.aayushatharva.brotli4j.encoder.PreparedDictionary;
import com.github.luben.zstd.RecyclingBufferPool;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.nio.ByteBuffer.allocateDirect;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

/**
 * {@link CompressionType} is an enum that represents a {@link ContentEncoding} compression type.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#directives">
 * developer.mozilla.org</a>
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
    GZIP("gzip", 0, 9, 5, false),

    /**
     * A format using the <a href="https://en.wikipedia.org/wiki/LZW">Lempel-Ziv-Welch</a> (LZW) algorithm. The value
     * name was taken from the UNIX <em>compress</em> program, which implemented this algorithm. Like the compress
     * program, which has disappeared from most UNIX distributions, this content-encoding is not used by many browsers
     * today, partly because of a patent issue (it expired in 2003).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#compress">
     * developer.mozilla.org</a>
     */
    COMPRESS("compress", 0, 0, 0, false),

    /**
     * Using the <a href="https://en.wikipedia.org/wiki/Zlib">zlib</a> structure (defined in
     * <a href="https://datatracker.ietf.org/doc/html/rfc1950">RFC 1950</a>) with the
     * <a href="https://en.wikipedia.org/wiki/Deflate">deflate</a> compression algorithm (defined in
     * <a href="https://datatracker.ietf.org/doc/html/rfc1951">RFC 1951</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#deflate">
     * developer.mozilla.org</a>
     */
    DEFLATE("deflate", 0, 9, 5, false),

    /**
     * A format using the <a href="https://developer.mozilla.org/en-US/docs/Glossary/Brotli_compression">Brotli</a>
     * algorithm structure (defined in <a href="https://datatracker.ietf.org/doc/html/rfc7932">RFC 7932</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#br">
     * developer.mozilla.org</a>
     */
    BROTLI("br", 0, 11, 4, false),

    /**
     * A format using the <a href="https://developer.mozilla.org/en-US/docs/Glossary/Zstandard_compression">
     * Zstandard</a> algorithm structure (defined in <a href="https://datatracker.ietf.org/doc/html/rfc8878">RFC
     * 8878</a>).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding#zstd">
     * developer.mozilla.org</a>
     */
    ZSTANDARD("zstd", 0, 22, 6, false),

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
    DICTIONARY_COMPRESSED_BROTLI("dcb", 0, 11, 4, true),

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
    DICTIONARY_COMPRESSED_ZSTANDARD("dcz", 0, 22, 6, true);

    private final String string;

    /**
     * The minimum compression level.
     */
    private final @Getter int minimumLevel;

    /**
     * The maximum compression level.
     */
    private final @Getter int maximumLevel;

    /**
     * The compression level to achieve roughly a 100 MB/s throughput according
     * <a href="https://github.com/inikep/lzbench">this</a> benchmark.
     */
    private final @Getter int defaultLevel;

    /**
     * Whether a compression dictionary is required for {@link #compress(OutputStream, Integer, byte[])} and
     * {@link #decompress(InputStream, byte[])}.
     */
    private final @Getter boolean dictionaryRequired;

    /**
     * @return {@link #compress(OutputStream, Integer)} with <code>level</code> set to <code>null</code>
     */
    public byte[] compress(final byte[] bytes) {
        return compress(bytes, (Integer) null);
    }

    /**
     * @return {@link #compress(OutputStream, Integer, byte[])} with <code>dictionary</code> set to <code>null</code>
     */
    public byte[] compress(final byte[] bytes, final @Nullable Integer level) {
        return compress(bytes, level, null);
    }

    /**
     * @return {@link #compress(OutputStream, Integer, byte[])} with <code>level</code> set to <code>null</code>
     */
    public byte[] compress(final byte[] bytes, final byte @Nullable [] dictionary) {
        return compress(bytes, null, dictionary);
    }

    /**
     * @return {@link #compress(OutputStream, Integer, byte[])} {@link ByteArrayOutputStream#toByteArray()}
     */
    public byte[] compress(final byte[] bytes, final @Nullable Integer level, final byte @Nullable [] dictionary) {
        final var compressed = new ByteArrayOutputStream();
        try (final var compress = compress(compressed, level, dictionary)) {
            compress.write(bytes);
        } catch (final IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
        return compressed.toByteArray();
    }

    /**
     * @return {@link #compress(OutputStream, Integer)} with <code>level</code> set to <code>null</code>
     */
    public OutputStream compress(final OutputStream outputStream) throws IOException {
        return compress(outputStream, (Integer) null);
    }

    /**
     * @return {@link #compress(OutputStream, Integer, byte[])} with <code>dictionary</code> set to <code>null</code>
     */
    public OutputStream compress(final OutputStream outputStream, final @Nullable Integer level) throws IOException {
        return compress(outputStream, level, null);
    }

    /**
     * @return {@link #compress(OutputStream, Integer, byte[])} with <code>level</code> set to <code>null</code>
     */
    public OutputStream compress(final OutputStream outputStream, final byte @Nullable [] dictionary)
            throws IOException {
        return compress(outputStream, null, dictionary);
    }

    /**
     * Compresses the given {@link OutputStream} using the compression algorithm represented by this
     * {@link CompressionType}.
     *
     * @param outputStream the {@link OutputStream} to compress
     * @param level        the compression level, or <code>null</code> for {@link #getDefaultLevel()}
     * @param dictionary   the dictionary bytes if {@link #isDictionaryRequired()}, <code>null</code> otherwise
     *
     * @return the compressing {@link OutputStream}
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public OutputStream compress(final OutputStream outputStream, final @Nullable Integer level,
            final byte @Nullable [] dictionary) throws IOException {
        checkArgument(!dictionaryRequired || dictionary != null, "`dictionary` must be set for `%s`", name());
        final var levelOrDefault = level != null ? level : getDefaultLevel();
        return switch (this) {
            case GZIP -> new GZIPOutputStream(outputStream, DEFAULT_BUFFER_SIZE) {{ def.setLevel(levelOrDefault); }};
            case COMPRESS -> throw new UnsupportedOperationException();
            case DEFLATE -> new DeflaterOutputStream(outputStream, new Deflater(levelOrDefault), DEFAULT_BUFFER_SIZE) {
                @Override
                public void close() throws IOException {
                    super.close();
                    def.close();
                }
            };
            case BROTLI -> new BrotliOutputStream(outputStream, Parameters.create(levelOrDefault), DEFAULT_BUFFER_SIZE);
            case ZSTANDARD -> new ZstdOutputStream(outputStream, RecyclingBufferPool.INSTANCE, levelOrDefault);
            case DICTIONARY_COMPRESSED_BROTLI -> {
                final var compress = new BrotliOutputStream(outputStream, Parameters.create(levelOrDefault),
                        DEFAULT_BUFFER_SIZE);
                compress.attachDictionary((PreparedDictionary) () -> {
                    final var dictionaryDirect = allocateDirect(requireNonNull(dictionary).length);
                    dictionaryDirect.put(dictionary);
                    return dictionaryDirect;
                });
                yield compress;
            }
            case DICTIONARY_COMPRESSED_ZSTANDARD -> {
                final var compress = new ZstdOutputStream(outputStream, RecyclingBufferPool.INSTANCE, levelOrDefault);
                compress.setDict(requireNonNull(dictionary));
                yield compress;
            }
        };
    }

    /**
     * @return {@link #decompress(byte[], byte[])} with <code>dictionary</code> set to <code>null</code>
     */
    public byte[] decompress(final byte[] bytes) {
        return decompress(bytes, null);
    }

    /**
     * @return {@link #decompress(InputStream, byte[])} with {@link ByteArrayInputStream#ByteArrayInputStream(byte[])}
     * and {@link InputStream#readAllBytes()}
     */
    public byte[] decompress(final byte[] bytes, final byte @Nullable [] dictionary) {
        try (final var decompress = decompress(new ByteArrayInputStream(bytes), dictionary)) {
            return decompress.readAllBytes();
        } catch (final IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    /**
     * @return {@link #decompress(InputStream, byte[])} with <code>dictionary</code> set to <code>null</code>
     */
    public InputStream decompress(final InputStream inputStream) throws IOException {
        return decompress(inputStream, null);
    }

    /**
     * Decompresses the given {@link InputStream} using the compression algorithm represented by this
     * {@link CompressionType}.
     *
     * @param inputStream the {@link InputStream} to decompress
     * @param dictionary  the dictionary bytes if {@link #isDictionaryRequired()}, <code>null</code> otherwise
     *
     * @return the decompressing {@link InputStream}
     *
     * @throws IOException thrown for {@link IOException}s
     */
    public InputStream decompress(final InputStream inputStream, final byte @Nullable [] dictionary)
            throws IOException {
        checkArgument(!dictionaryRequired || dictionary != null, "`dictionary` must be set for `%s`", name());
        return switch (this) {
            case GZIP -> new GZIPInputStream(inputStream, DEFAULT_BUFFER_SIZE);
            case COMPRESS -> throw new UnsupportedOperationException();
            case DEFLATE -> new InflaterInputStream(inputStream, new Inflater(), DEFAULT_BUFFER_SIZE) {
                @Override
                public void close() throws IOException {
                    inf.close();
                    super.close();
                }
            };
            case BROTLI -> new BrotliInputStream(inputStream, DEFAULT_BUFFER_SIZE);
            case ZSTANDARD -> new ZstdInputStream(inputStream, RecyclingBufferPool.INSTANCE);
            case DICTIONARY_COMPRESSED_BROTLI -> {
                final var decompress = new BrotliInputStream(inputStream, DEFAULT_BUFFER_SIZE);
                final var dictionaryDirect = allocateDirect(requireNonNull(dictionary).length);
                dictionaryDirect.put(dictionary);
                decompress.attachDictionary(dictionaryDirect);
                yield decompress;
            }
            case DICTIONARY_COMPRESSED_ZSTANDARD -> {
                final var decompress = new ZstdInputStream(inputStream, RecyclingBufferPool.INSTANCE);
                decompress.setDict(requireNonNull(dictionary));
                yield decompress;
            }
        };
    }

    @Override
    public String toString() {
        return string;
    }

    static {
        Brotli4jLoader.ensureAvailability();
    }

    /**
     * The default buffer size for compression streams: 16 KiB
     *
     * @see <a href="https://bugs.openjdk.org/browse/JDK-8299336">bugs.openjdk.org</a>
     */
    public static final int DEFAULT_BUFFER_SIZE = 16 * 1024;

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
