package net.jacobpeterson.jet.server.http.header.contenttype;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.net.MediaType;
import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.Value;
import net.jacobpeterson.jet.server.http.header.HttpHeader;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Multimaps.flatteningToMultimap;
import static com.google.common.collect.Multimaps.forMap;
import static com.google.common.collect.Multimaps.unmodifiableSetMultimap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link ContentType} is an immutable class that wraps {@link MediaType} from
 * <a href="https://github.com/google/guava">Google Guava</a> and adds some extra functionality.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
 * developer.mozilla.org</a>
 * @see HttpHeader#CONTENT_TYPE
 * @see MediaType
 */
@NullMarked
@Immutable
@Value @EqualsAndHashCode(cacheStrategy = LAZY)
public class ContentType {

    public static final String WILDCARD_TYPE = "*";
    public static final ContentType WILDCARD_WILDCARD = new ContentType(MediaType.ANY_TYPE);

    public static final String TEXT_TYPE = "text";
    public static final ContentType TEXT_WILDCARD = new ContentType(MediaType.ANY_TEXT_TYPE);
    public static final ContentType TEXT_CSS_UTF_8 = new ContentType(MediaType.CSS_UTF_8);
    public static final ContentType TEXT_CSS =
            new ContentType(TEXT_CSS_UTF_8.getMediaType().withoutParameters());
    public static final ContentType TEXT_CSV_UTF_8 = new ContentType(MediaType.CSV_UTF_8);
    public static final ContentType TEXT_CSV =
            new ContentType(TEXT_CSV_UTF_8.getMediaType().withoutParameters());
    public static final ContentType TEXT_HTML_UTF_8 = new ContentType(MediaType.HTML_UTF_8);
    public static final ContentType TEXT_HTML =
            new ContentType(TEXT_HTML_UTF_8.getMediaType().withoutParameters());
    public static final ContentType TEXT_MARKDOWN_UTF_8 = new ContentType(MediaType.MD_UTF_8);
    public static final ContentType TEXT_MARKDOWN =
            new ContentType(TEXT_MARKDOWN_UTF_8.getMediaType().withoutParameters());
    public static final ContentType TEXT_PLAIN_UTF_8 = new ContentType(MediaType.PLAIN_TEXT_UTF_8);
    public static final ContentType TEXT_PLAIN =
            new ContentType(TEXT_PLAIN_UTF_8.getMediaType().withoutParameters());
    public static final ContentType TEXT_TSV_UTF_8 = new ContentType(MediaType.TSV_UTF_8);
    public static final ContentType TEXT_TSV =
            new ContentType(TEXT_TSV_UTF_8.getMediaType().withoutParameters());
    public static final ContentType TEXT_XML_UTF_8 = new ContentType(MediaType.XML_UTF_8);
    public static final ContentType TEXT_XML =
            new ContentType(TEXT_XML_UTF_8.getMediaType().withoutParameters());

    public static final String IMAGE_TYPE = "image";
    public static final ContentType IMAGE_WILDCARD = new ContentType(MediaType.ANY_IMAGE_TYPE);
    public static final ContentType IMAGE_BMP = new ContentType(MediaType.BMP);
    public static final ContentType IMAGE_GIF = new ContentType(MediaType.GIF);
    public static final ContentType IMAGE_ICO = new ContentType(MediaType.ICO);
    public static final ContentType IMAGE_JPEG = new ContentType(MediaType.JPEG);
    public static final ContentType IMAGE_PNG = new ContentType(MediaType.PNG);
    public static final ContentType IMAGE_SVG_UTF_8 = new ContentType(MediaType.SVG_UTF_8);
    public static final ContentType IMAGE_SVG = new ContentType(IMAGE_SVG_UTF_8.getMediaType().withoutParameters());
    public static final ContentType IMAGE_TIFF = new ContentType(MediaType.TIFF);
    public static final ContentType IMAGE_AVIF = new ContentType(MediaType.AVIF);
    public static final ContentType IMAGE_WEBP = new ContentType(MediaType.WEBP);
    public static final ContentType IMAGE_HEIF = new ContentType(MediaType.HEIF);
    public static final ContentType IMAGE_HEIC = new ContentType(IMAGE_TYPE, "heic");

    public static final String AUDIO_TYPE = "audio";
    public static final ContentType AUDIO_WILDCARD = new ContentType(MediaType.ANY_AUDIO_TYPE);
    public static final ContentType AUDIO_MP4 = new ContentType(MediaType.MP4_AUDIO);
    public static final ContentType AUDIO_MPEG = new ContentType(MediaType.MPEG_AUDIO);
    public static final ContentType AUDIO_OGG = new ContentType(MediaType.OGG_AUDIO);
    public static final ContentType AUDIO_WEBM = new ContentType(MediaType.WEBM_AUDIO);
    public static final ContentType AUDIO_BASIC = new ContentType(MediaType.BASIC_AUDIO);
    public static final ContentType AUDIO_AAC = new ContentType(MediaType.AAC_AUDIO);
    public static final ContentType AUDIO_VORBIS = new ContentType(MediaType.VORBIS_AUDIO);
    public static final ContentType AUDIO_VND_WAVE = new ContentType(MediaType.VND_WAVE_AUDIO);

    public static final String VIDEO_TYPE = "video";
    public static final ContentType VIDEO_WILDCARD = new ContentType(MediaType.ANY_VIDEO_TYPE);
    public static final ContentType VIDEO_MP4 = new ContentType(MediaType.MP4_VIDEO);
    public static final ContentType VIDEO_MPEG = new ContentType(MediaType.MPEG_VIDEO);
    public static final ContentType VIDEO_OGG = new ContentType(MediaType.OGG_VIDEO);
    public static final ContentType VIDEO_QUICKTIME = new ContentType(MediaType.QUICKTIME);
    public static final ContentType VIDEO_WEBM = new ContentType(MediaType.WEBM_VIDEO);
    public static final ContentType VIDEO_FLV = new ContentType(MediaType.FLV_VIDEO);

    public static final String APPLICATION_TYPE = "application";
    public static final ContentType APPLICATION_WILDCARD = new ContentType(MediaType.ANY_APPLICATION_TYPE);
    public static final ContentType APPLICATION_XML_UTF_8 = new ContentType(MediaType.APPLICATION_XML_UTF_8);
    public static final ContentType APPLICATION_XML =
            new ContentType(APPLICATION_XML_UTF_8.getMediaType().withoutParameters());
    public static final ContentType APPLICATION_BZIP2 = new ContentType(MediaType.BZIP2);
    public static final ContentType APPLICATION_FORM_URL_ENCODED = new ContentType(MediaType.FORM_DATA);
    public static final ContentType APPLICATION_GZIP = new ContentType(MediaType.GZIP);
    public static final ContentType APPLICATION_JAVASCRIPT_UTF_8 = new ContentType(MediaType.JAVASCRIPT_UTF_8);
    public static final ContentType APPLICATION_JAVASCRIPT =
            new ContentType(APPLICATION_JAVASCRIPT_UTF_8.getMediaType().withoutParameters());
    public static final ContentType APPLICATION_JSON_UTF_8 = new ContentType(MediaType.JSON_UTF_8);
    public static final ContentType APPLICATION_JSON =
            new ContentType(APPLICATION_JSON_UTF_8.getMediaType().withoutParameters());
    public static final ContentType APPLICATION_JWT = new ContentType(MediaType.JWT);
    public static final ContentType APPLICATION_MANIFEST_JSON_UTF_8 = new ContentType(MediaType.MANIFEST_JSON_UTF_8);
    public static final ContentType APPLICATION_MANIFEST_JSON =
            new ContentType(APPLICATION_MANIFEST_JSON_UTF_8.getMediaType().withoutParameters());
    public static final ContentType APPLICATION_OCTET_STREAM = new ContentType(MediaType.OCTET_STREAM);
    public static final ContentType APPLICATION_PDF = new ContentType(MediaType.PDF);
    public static final ContentType APPLICATION_RTF_UTF_8 = new ContentType(MediaType.RTF_UTF_8);
    public static final ContentType APPLICATION_RTF =
            new ContentType(APPLICATION_RTF_UTF_8.getMediaType().withoutParameters());
    public static final ContentType APPLICATION_XHTML_UTF_8 = new ContentType(MediaType.XHTML_UTF_8);
    public static final ContentType APPLICATION_XHTML =
            new ContentType(APPLICATION_XHTML_UTF_8.getMediaType().withoutParameters());
    public static final ContentType APPLICATION_ZIP = new ContentType(MediaType.ZIP);

    public static final String FONT_TYPE = "font";
    public static final ContentType FONT_WILDCARD = new ContentType(MediaType.ANY_FONT_TYPE);
    public static final ContentType FONT_COLLECTION = new ContentType(MediaType.FONT_COLLECTION);
    public static final ContentType FONT_OTF = new ContentType(MediaType.FONT_OTF);
    public static final ContentType FONT_TTF = new ContentType(MediaType.FONT_TTF);
    public static final ContentType FONT_WOFF = new ContentType(MediaType.FONT_WOFF);
    public static final ContentType FONT_WOFF2 = new ContentType(MediaType.FONT_WOFF2);

    public static final String MULTIPART_TYPE = "multipart";
    public static final ContentType MULTIPART_WILDCARD = new ContentType(MULTIPART_TYPE, WILDCARD_TYPE);
    public static final ContentType MULTIPART_FORM_DATA = new ContentType(MULTIPART_TYPE, "form-data");

    /**
     * A non-exhaustive {@link Set} of common {@link ContentType}s that modern browsers can typically render through an
     * HTML tag without the need for any extra plugins and has a very low likelihood of being vulnerable to XSS attacks
     * if the content is used with the proper HTML tag or directly rendered in a browser tab. The content type of
     * untrusted content (e.g. user-submitted files) should be checked against this {@link Set} before setting the
     * {@link HttpHeader#CONTENT_TYPE} response header to the value from {@link #forFileExtension(String)}.
     */
    public static final Set<ContentType> LIKELY_XSS_SAFE_HTML_TAG_CONTENT_TYPES = Set.of(
            IMAGE_BMP, IMAGE_GIF, IMAGE_ICO, IMAGE_JPEG, IMAGE_PNG, IMAGE_TIFF, IMAGE_AVIF, IMAGE_WEBP, IMAGE_HEIF,
            IMAGE_HEIC,
            AUDIO_MP4, AUDIO_MPEG, AUDIO_OGG, AUDIO_WEBM, AUDIO_BASIC, AUDIO_AAC, AUDIO_VORBIS, AUDIO_VND_WAVE,
            VIDEO_MP4, VIDEO_MPEG, VIDEO_OGG, VIDEO_QUICKTIME, VIDEO_WEBM, VIDEO_FLV,
            APPLICATION_PDF);

    /**
     * A non-exhaustive {@link Set} of common {@link ContentType}s for typically intrinsically compressed file types.
     */
    public static final Set<ContentType> COMPRESSED_CONTENT_TYPES = Set.of(
            IMAGE_GIF, IMAGE_ICO, IMAGE_JPEG, IMAGE_PNG, IMAGE_TIFF, IMAGE_AVIF, IMAGE_WEBP, IMAGE_HEIF, IMAGE_HEIC,
            AUDIO_MP4, AUDIO_MPEG, AUDIO_OGG, AUDIO_WEBM, AUDIO_AAC, AUDIO_VORBIS,
            VIDEO_MP4, VIDEO_MPEG, VIDEO_OGG, VIDEO_QUICKTIME, VIDEO_WEBM, VIDEO_FLV,
            FONT_WOFF, FONT_WOFF2,
            new ContentType(APPLICATION_TYPE, "zip"),
            new ContentType(APPLICATION_TYPE, "x-zip"),
            new ContentType(APPLICATION_TYPE, "bzip"),
            new ContentType(APPLICATION_TYPE, "x-bzip"),
            new ContentType(APPLICATION_TYPE, "bzip2"),
            new ContentType(APPLICATION_TYPE, "x-bzip2"),
            new ContentType(APPLICATION_TYPE, "gzip"),
            new ContentType(APPLICATION_TYPE, "x-gzip"),
            new ContentType(APPLICATION_TYPE, "brotli"),
            new ContentType(APPLICATION_TYPE, "x-br"),
            new ContentType(APPLICATION_TYPE, "zstd"),
            new ContentType(APPLICATION_TYPE, "x-zstd"),
            new ContentType(APPLICATION_TYPE, "zstandard"),
            new ContentType(APPLICATION_TYPE, "x-zstandard"),
            new ContentType(APPLICATION_TYPE, "x-xz"),
            new ContentType(APPLICATION_TYPE, "x-rar-compressed"),
            new ContentType(APPLICATION_TYPE, "x-zip-compressed"),
            new ContentType(APPLICATION_TYPE, "x-7z-compressed"));

    /**
     * A {@link SetMultimap} of {@link ContentType}s mapped to their dot-less file extensions (e.g.
     * <code>text/plain</code> -> <code>txt</code>).
     */
    public static final SetMultimap<ContentType, String> FILE_EXTENSIONS_OF_CONTENT_TYPES;

    /**
     * The inverse of {@link #FILE_EXTENSIONS_OF_CONTENT_TYPES}.
     */
    public static final Map<String, ContentType> CONTENT_TYPE_OF_FILE_EXTENSIONS;

    static {
        final byte[] tsvBytes;
        try (final var inputStream = ContentType.class.getResourceAsStream("file-extensions-of-mime-types.tsv")) {
            tsvBytes = checkNotNull(inputStream).readAllBytes();
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
        FILE_EXTENSIONS_OF_CONTENT_TYPES = unmodifiableSetMultimap(stream(new String(tsvBytes, UTF_8).split("\n"))
                .filter(line -> !line.isBlank() && !line.startsWith("#"))
                .map(line -> line.split("\t"))
                .collect(flatteningToMultimap(tsv -> parse(tsv[0]), tsv -> stream(tsv[1].split(" ")),
                        () -> SetMultimapBuilder.linkedHashKeys().hashSetValues().build())));
        CONTENT_TYPE_OF_FILE_EXTENSIONS = FILE_EXTENSIONS_OF_CONTENT_TYPES.entries().stream()
                .collect(toUnmodifiableMap(Entry::getValue, Entry::getKey, (first, _) -> first));
    }

    /**
     * Parses the given <code>contentType</code> into a {@link ContentType} using {@link MediaType#parse(String)}.
     *
     * @param contentType the content type {@link String}
     *
     * @return the {@link ContentType}
     *
     * @throws IllegalArgumentException thrown if <code>contentType</code> is invalid
     */
    public static ContentType parse(final String contentType) throws IllegalArgumentException {
        return new ContentType(MediaType.parse(contentType));
    }

    /**
     * Gets the {@link ContentType} for the given <code>fileExtension</code> using
     * {@link #CONTENT_TYPE_OF_FILE_EXTENSIONS}.
     *
     * @param fileExtension the case-insensitive, dot-less file extension {@link String}
     *
     * @return the {@link ContentType}, or <code>null</code> if no mapping exists
     */
    public static @Nullable ContentType forFileExtension(final String fileExtension) {
        return CONTENT_TYPE_OF_FILE_EXTENSIONS.get(fileExtension.toLowerCase(ROOT));
    }

    MediaType mediaType;

    /**
     * Instantiates a new {@link ContentType}.
     *
     * @param type    the type
     * @param subtype the subtype
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    public ContentType(final String type, final String subtype) throws IllegalArgumentException {
        this(type, subtype, (Map<String, String>) null);
    }

    /**
     * Instantiates a new {@link ContentType}.
     *
     * @param type       the type
     * @param subtype    the subtype
     * @param parameters the parameters {@link Map}, or <code>null</code> for no parameters
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    public ContentType(final String type, final String subtype, final @Nullable Map<String, String> parameters)
            throws IllegalArgumentException {
        this(type, subtype, parameters == null ? null : forMap(parameters));
    }

    /**
     * Instantiates a new {@link ContentType}.
     *
     * @param type       the type
     * @param subtype    the subtype
     * @param parameters the parameters {@link Multimap}, or <code>null</code> for no parameters
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    public ContentType(final String type, final String subtype, final @Nullable Multimap<String, String> parameters)
            throws IllegalArgumentException {
        final var mediaType = MediaType.create(type, subtype);
        this.mediaType = parameters == null ? mediaType : mediaType.withParameters(parameters);
    }

    /**
     * Instantiates a new {@link ContentType}.
     *
     * @param type    the type
     * @param subtype the subtype
     * @param charset the {@link Charset}
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    public ContentType(final String type, final String subtype, final Charset charset) throws IllegalArgumentException {
        mediaType = MediaType.create(type, subtype).withCharset(charset);
    }

    /**
     * Instantiates a new {@link ContentType}.
     *
     * @param mediaType the {@link MediaType}
     */
    public ContentType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Checks if this {@link ContentType} is in {@link #LIKELY_XSS_SAFE_HTML_TAG_CONTENT_TYPES}.
     */
    public boolean isLikelyXssSafeHtmlTag() {
        return LIKELY_XSS_SAFE_HTML_TAG_CONTENT_TYPES.contains(new ContentType(mediaType.withoutParameters()));
    }

    /**
     * Gets the dot-less file extensions for this {@link ContentType} from {@link #FILE_EXTENSIONS_OF_CONTENT_TYPES}.
     *
     * @return the {@link String} {@link Set}
     */
    public Set<String> getFileExtensions() {
        return FILE_EXTENSIONS_OF_CONTENT_TYPES.get(this);
    }

    /**
     * @see MediaType#toString()
     */
    @Override
    public String toString() {
        return mediaType.toString();
    }
}
