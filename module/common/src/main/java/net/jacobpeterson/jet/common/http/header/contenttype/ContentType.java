package net.jacobpeterson.jet.common.http.header.contenttype;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.MultimapBuilder.SetMultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.net.MediaType;
import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.Value;
import net.jacobpeterson.jet.common.http.header.Header;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.collect.Multimaps.flatteningToMultimap;
import static com.google.common.collect.Multimaps.unmodifiableSetMultimap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllLines;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link ContentType} is an immutable class that represents a standardized HTTP content type (MIME type). This class
 * wraps {@link MediaType} from <a href="https://github.com/google/guava">Google Guava</a> and adds some extra
 * functionality.
 * <p>
 * The HTTP <strong><code>Content-Type</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Representation_header">representation header</a> is used
 * to indicate the original
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/MIME_type">media type</a> of a resource before any
 * content encoding is applied. In responses, the <code>Content-Type</code> header informs the client about the media
 * type of the returned data. In requests such as
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a> or
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT"><code>PUT</code></a>, the client
 * uses the <code>Content-Type</code> header to specify the type of content being sent to the server. If a server
 * implementation or configuration is strict about content type handling, a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/415"><code>415</code></a> client error
 * response may be returned.</p>
 * <p>
 * The <code>Content-Type</code> header differs from
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">
 * <code>Content-Encoding</code></a> in that <code>Content-Encoding</code> helps the recipient understand how to decode
 * data to its original form.
 * <p>
 * This value may be ignored if browsers perform
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/MIME_types#mime_sniffing">MIME sniffing</a> (or
 * content sniffing) on responses. To prevent browsers from using MIME sniffing, set the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/X-Content-Type-Options">
 * <code>X-Content-Type-Options</code></a> header value to <code>nosniff</code>. See
 * <a href="https://developer.mozilla.org/en-US/docs/Web/Security/Practical_implementation_guides/MIME_types">MIME type
 * verification</a> for more details.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
 * developer.mozilla.org</a>
 * @see Header#CONTENT_TYPE
 * @see MediaType
 */
@NullMarked
@Immutable
@Value @EqualsAndHashCode(cacheStrategy = LAZY)
public class ContentType {

    // The below public constants are commonly used content types. The constants are defined as `String` and
    // `ContentType` types. The `String` types are provided so they can be used within annotations.

    public static final String SEPARATOR_TOKEN = "/";
    public static final String WILDCARD_TOKEN = "*";
    public static final String WILDCARD_WILDCARD_STRING = WILDCARD_TOKEN + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType WILDCARD_WILDCARD = new ContentType(WILDCARD_TOKEN, WILDCARD_TOKEN);

    // BEGIN text types

    public static final String TEXT_TYPE_STRING = "text";
    public static final String TEXT_WILDCARD_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType TEXT_WILDCARD = new ContentType(TEXT_TYPE_STRING, WILDCARD_TOKEN);

    public static final String TEXT_CSS_SUBTYPE_STRING = "css";
    public static final String TEXT_CSS_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + TEXT_CSS_SUBTYPE_STRING;
    public static final ContentType TEXT_CSS = new ContentType(TEXT_TYPE_STRING, TEXT_CSS_SUBTYPE_STRING);
    public static final ContentType TEXT_CSS_UTF_8 = TEXT_CSS.withCharset(UTF_8);

    public static final String TEXT_CSV_SUBTYPE_STRING = "csv";
    public static final String TEXT_CSV_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + TEXT_CSV_SUBTYPE_STRING;
    public static final ContentType TEXT_CSV = new ContentType(TEXT_TYPE_STRING, TEXT_CSV_SUBTYPE_STRING);
    public static final ContentType TEXT_CSV_UTF_8 = TEXT_CSV.withCharset(UTF_8);

    public static final String TEXT_HTML_SUBTYPE_STRING = "html";
    public static final String TEXT_HTML_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + TEXT_HTML_SUBTYPE_STRING;
    public static final ContentType TEXT_HTML = new ContentType(TEXT_TYPE_STRING, TEXT_HTML_SUBTYPE_STRING);
    public static final ContentType TEXT_HTML_UTF_8 = TEXT_HTML.withCharset(UTF_8);

    public static final String TEXT_MARKDOWN_SUBTYPE_STRING = "markdown";
    public static final String TEXT_MARKDOWN_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + TEXT_MARKDOWN_SUBTYPE_STRING;
    public static final ContentType TEXT_MARKDOWN = new ContentType(TEXT_TYPE_STRING, TEXT_MARKDOWN_SUBTYPE_STRING);
    public static final ContentType TEXT_MARKDOWN_UTF_8 = TEXT_MARKDOWN.withCharset(UTF_8);

    public static final String TEXT_PLAIN_SUBTYPE_STRING = "plain";
    public static final String TEXT_PLAIN_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + TEXT_PLAIN_SUBTYPE_STRING;
    public static final ContentType TEXT_PLAIN = new ContentType(TEXT_TYPE_STRING, TEXT_PLAIN_SUBTYPE_STRING);
    public static final ContentType TEXT_PLAIN_UTF_8 = TEXT_PLAIN.withCharset(UTF_8);

    public static final String TEXT_TSV_SUBTYPE_STRING = "tab-separated-values";
    public static final String TEXT_TSV_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + TEXT_TSV_SUBTYPE_STRING;
    public static final ContentType TEXT_TSV = new ContentType(TEXT_TYPE_STRING, TEXT_TSV_SUBTYPE_STRING);
    public static final ContentType TEXT_TSV_UTF_8 = TEXT_TSV.withCharset(UTF_8);

    public static final String TEXT_XML_SUBTYPE_STRING = "xml";
    public static final String TEXT_XML_STRING = TEXT_TYPE_STRING + SEPARATOR_TOKEN + TEXT_XML_SUBTYPE_STRING;
    public static final ContentType TEXT_XML = new ContentType(TEXT_TYPE_STRING, TEXT_XML_SUBTYPE_STRING);
    public static final ContentType TEXT_XML_UTF_8 = TEXT_XML.withCharset(UTF_8);

    // END text types

    // BEGIN image types

    public static final String IMAGE_TYPE_STRING = "image";
    public static final String IMAGE_WILDCARD_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType IMAGE_WILDCARD = new ContentType(IMAGE_TYPE_STRING, WILDCARD_TOKEN);

    public static final String IMAGE_BMP_SUBTYPE_STRING = "bmp";
    public static final String IMAGE_BMP_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_BMP_SUBTYPE_STRING;
    public static final ContentType IMAGE_BMP = new ContentType(IMAGE_TYPE_STRING, IMAGE_BMP_SUBTYPE_STRING);

    public static final String IMAGE_GIF_SUBTYPE_STRING = "gif";
    public static final String IMAGE_GIF_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_GIF_SUBTYPE_STRING;
    public static final ContentType IMAGE_GIF = new ContentType(IMAGE_TYPE_STRING, IMAGE_GIF_SUBTYPE_STRING);

    public static final String IMAGE_ICO_SUBTYPE_STRING = "vnd.microsoft.icon";
    public static final String IMAGE_ICO_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_ICO_SUBTYPE_STRING;
    public static final ContentType IMAGE_ICO = new ContentType(IMAGE_TYPE_STRING, IMAGE_ICO_SUBTYPE_STRING);

    public static final String IMAGE_JPEG_SUBTYPE_STRING = "jpeg";
    public static final String IMAGE_JPEG_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_JPEG_SUBTYPE_STRING;
    public static final ContentType IMAGE_JPEG = new ContentType(IMAGE_TYPE_STRING, IMAGE_JPEG_SUBTYPE_STRING);

    public static final String IMAGE_PNG_SUBTYPE_STRING = "png";
    public static final String IMAGE_PNG_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_PNG_SUBTYPE_STRING;
    public static final ContentType IMAGE_PNG = new ContentType(IMAGE_TYPE_STRING, IMAGE_PNG_SUBTYPE_STRING);

    public static final String IMAGE_SVG_SUBTYPE_STRING = "svg+xml";
    public static final String IMAGE_SVG_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_SVG_SUBTYPE_STRING;
    public static final ContentType IMAGE_SVG = new ContentType(IMAGE_TYPE_STRING, IMAGE_SVG_SUBTYPE_STRING);
    public static final ContentType IMAGE_SVG_UTF_8 = IMAGE_SVG.withCharset(UTF_8);

    public static final String IMAGE_TIFF_SUBTYPE_STRING = "tiff";
    public static final String IMAGE_TIFF_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_TIFF_SUBTYPE_STRING;
    public static final ContentType IMAGE_TIFF = new ContentType(IMAGE_TYPE_STRING, IMAGE_TIFF_SUBTYPE_STRING);

    public static final String IMAGE_AVIF_SUBTYPE_STRING = "avif";
    public static final String IMAGE_AVIF_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_AVIF_SUBTYPE_STRING;
    public static final ContentType IMAGE_AVIF = new ContentType(IMAGE_TYPE_STRING, IMAGE_AVIF_SUBTYPE_STRING);

    public static final String IMAGE_WEBP_SUBTYPE_STRING = "webp";
    public static final String IMAGE_WEBP_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_WEBP_SUBTYPE_STRING;
    public static final ContentType IMAGE_WEBP = new ContentType(IMAGE_TYPE_STRING, IMAGE_WEBP_SUBTYPE_STRING);

    public static final String IMAGE_HEIC_SUBTYPE_STRING = "heic";
    public static final String IMAGE_HEIC_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_HEIC_SUBTYPE_STRING;
    public static final ContentType IMAGE_HEIC = new ContentType(IMAGE_TYPE_STRING, IMAGE_HEIC_SUBTYPE_STRING);

    public static final String IMAGE_HEIF_SUBTYPE_STRING = "heif";
    public static final String IMAGE_HEIF_STRING = IMAGE_TYPE_STRING + SEPARATOR_TOKEN + IMAGE_HEIF_SUBTYPE_STRING;
    public static final ContentType IMAGE_HEIF = new ContentType(IMAGE_TYPE_STRING, IMAGE_HEIF_SUBTYPE_STRING);

    // END image types

    // BEGIN audio types

    public static final String AUDIO_TYPE_STRING = "audio";
    public static final String AUDIO_WILDCARD_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType AUDIO_WILDCARD = new ContentType(AUDIO_TYPE_STRING, WILDCARD_TOKEN);

    public static final String AUDIO_MP4_SUBTYPE_STRING = "mp4";
    public static final String AUDIO_MP4_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN +
            AUDIO_MP4_SUBTYPE_STRING;
    public static final ContentType AUDIO_MP4 = new ContentType(AUDIO_TYPE_STRING, AUDIO_MP4_SUBTYPE_STRING);

    public static final String AUDIO_MPEG_SUBTYPE_STRING = "mpeg";
    public static final String AUDIO_MPEG_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN +
            AUDIO_MPEG_SUBTYPE_STRING;
    public static final ContentType AUDIO_MPEG = new ContentType(AUDIO_TYPE_STRING, AUDIO_MPEG_SUBTYPE_STRING);

    public static final String AUDIO_OGG_SUBTYPE_STRING = "ogg";
    public static final String AUDIO_OGG_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN +
            AUDIO_OGG_SUBTYPE_STRING;
    public static final ContentType AUDIO_OGG = new ContentType(AUDIO_TYPE_STRING, AUDIO_OGG_SUBTYPE_STRING);

    public static final String AUDIO_WEBM_SUBTYPE_STRING = "webm";
    public static final String AUDIO_WEBM_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN +
            AUDIO_WEBM_SUBTYPE_STRING;
    public static final ContentType AUDIO_WEBM = new ContentType(AUDIO_TYPE_STRING, AUDIO_WEBM_SUBTYPE_STRING);

    public static final String AUDIO_AAC_SUBTYPE_STRING = "aac";
    public static final String AUDIO_AAC_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN +
            AUDIO_AAC_SUBTYPE_STRING;
    public static final ContentType AUDIO_AAC = new ContentType(AUDIO_TYPE_STRING, AUDIO_AAC_SUBTYPE_STRING);

    public static final String AUDIO_VORBIS_SUBTYPE_STRING = "vorbis";
    public static final String AUDIO_VORBIS_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN +
            AUDIO_VORBIS_SUBTYPE_STRING;
    public static final ContentType AUDIO_VORBIS = new ContentType(AUDIO_TYPE_STRING, AUDIO_VORBIS_SUBTYPE_STRING);

    public static final String AUDIO_VND_WAVE_SUBTYPE_STRING = "vnd.wave";
    public static final String AUDIO_VND_WAVE_STRING = AUDIO_TYPE_STRING + SEPARATOR_TOKEN +
            AUDIO_VND_WAVE_SUBTYPE_STRING;
    public static final ContentType AUDIO_VND_WAVE = new ContentType(AUDIO_TYPE_STRING, AUDIO_VND_WAVE_SUBTYPE_STRING);

    // END audio types

    // BEGIN video types

    public static final String VIDEO_TYPE_STRING = "video";
    public static final String VIDEO_WILDCARD_STRING = VIDEO_TYPE_STRING + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType VIDEO_WILDCARD = new ContentType(VIDEO_TYPE_STRING, WILDCARD_TOKEN);

    public static final String VIDEO_MP4_SUBTYPE_STRING = "mp4";
    public static final String VIDEO_MP4_STRING = VIDEO_TYPE_STRING + SEPARATOR_TOKEN +
            VIDEO_MP4_SUBTYPE_STRING;
    public static final ContentType VIDEO_MP4 = new ContentType(VIDEO_TYPE_STRING,
            VIDEO_MP4_SUBTYPE_STRING);

    public static final String VIDEO_MPEG_SUBTYPE_STRING = "mpeg";
    public static final String VIDEO_MPEG_STRING = VIDEO_TYPE_STRING + SEPARATOR_TOKEN +
            VIDEO_MPEG_SUBTYPE_STRING;
    public static final ContentType VIDEO_MPEG = new ContentType(VIDEO_TYPE_STRING,
            VIDEO_MPEG_SUBTYPE_STRING);

    public static final String VIDEO_OGG_SUBTYPE_STRING = "ogg";
    public static final String VIDEO_OGG_STRING = VIDEO_TYPE_STRING + SEPARATOR_TOKEN +
            VIDEO_OGG_SUBTYPE_STRING;
    public static final ContentType VIDEO_OGG = new ContentType(VIDEO_TYPE_STRING,
            VIDEO_OGG_SUBTYPE_STRING);

    public static final String VIDEO_QUICKTIME_SUBTYPE_STRING = "quicktime";
    public static final String VIDEO_QUICKTIME_STRING = VIDEO_TYPE_STRING + SEPARATOR_TOKEN +
            VIDEO_QUICKTIME_SUBTYPE_STRING;
    public static final ContentType VIDEO_QUICKTIME = new ContentType(VIDEO_TYPE_STRING,
            VIDEO_QUICKTIME_SUBTYPE_STRING);

    public static final String VIDEO_WEBM_SUBTYPE_STRING = "webm";
    public static final String VIDEO_WEBM_STRING = VIDEO_TYPE_STRING + SEPARATOR_TOKEN +
            VIDEO_WEBM_SUBTYPE_STRING;
    public static final ContentType VIDEO_WEBM = new ContentType(VIDEO_TYPE_STRING,
            VIDEO_WEBM_SUBTYPE_STRING);

    // END video types

    // BEGIN application types

    public static final String APPLICATION_TYPE_STRING = "application";
    public static final String APPLICATION_WILD_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType APPLICATION_WILDCARD = new ContentType(APPLICATION_TYPE_STRING, WILDCARD_TOKEN);

    public static final String APPLICATION_GZIP_SUBTYPE_STRING = "gzip";
    public static final String APPLICATION_GZIP_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_GZIP_SUBTYPE_STRING;
    public static final ContentType APPLICATION_GZIP = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_GZIP_SUBTYPE_STRING);

    public static final String APPLICATION_JAVASCRIPT_SUBTYPE_STRING = "javascript";
    public static final String APPLICATION_JAVASCRIPT_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_JAVASCRIPT_SUBTYPE_STRING;
    public static final ContentType APPLICATION_JAVASCRIPT = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_JAVASCRIPT_SUBTYPE_STRING);
    public static final ContentType APPLICATION_JAVASCRIPT_UTF_8 = APPLICATION_JAVASCRIPT.withCharset(UTF_8);

    public static final String APPLICATION_JSON_SUBTYPE_STRING = "json";
    public static final String APPLICATION_JSON_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_JSON_SUBTYPE_STRING;
    public static final ContentType APPLICATION_JSON = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_JSON_SUBTYPE_STRING);
    public static final ContentType APPLICATION_JSON_UTF_8 = APPLICATION_JSON.withCharset(UTF_8);

    public static final String APPLICATION_JWT_SUBTYPE_STRING = "jwt";
    public static final String APPLICATION_JWT_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_JWT_SUBTYPE_STRING;
    public static final ContentType APPLICATION_JWT = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_JWT_SUBTYPE_STRING);

    public static final String APPLICATION_MANIFEST_JSON_SUBTYPE_STRING = "manifest+json";
    public static final String APPLICATION_MANIFEST_JSON_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_MANIFEST_JSON_SUBTYPE_STRING;
    public static final ContentType APPLICATION_MANIFEST_JSON = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_MANIFEST_JSON_SUBTYPE_STRING);
    public static final ContentType APPLICATION_MANIFEST_JSON_UTF_8 = APPLICATION_MANIFEST_JSON.withCharset(UTF_8);

    public static final String APPLICATION_OCTET_STREAM_SUBTYPE_STRING = "octet-stream";
    public static final String APPLICATION_OCTET_STREAM_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_OCTET_STREAM_SUBTYPE_STRING;
    public static final ContentType APPLICATION_OCTET_STREAM = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_OCTET_STREAM_SUBTYPE_STRING);

    public static final String APPLICATION_PDF_SUBTYPE_STRING = "pdf";
    public static final String APPLICATION_PDF_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_PDF_SUBTYPE_STRING;
    public static final ContentType APPLICATION_PDF = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_PDF_SUBTYPE_STRING);

    public static final String APPLICATION_RTF_SUBTYPE_STRING = "rtf";
    public static final String APPLICATION_RTF_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_RTF_SUBTYPE_STRING;
    public static final ContentType APPLICATION_RTF = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_RTF_SUBTYPE_STRING);
    public static final ContentType APPLICATION_RTF_UTF_8 = APPLICATION_RTF.withCharset(UTF_8);

    public static final String APPLICATION_WASM_SUBTYPE_STRING = "wasm";
    public static final String APPLICATION_WASM_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_WASM_SUBTYPE_STRING;
    public static final ContentType APPLICATION_WASM = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_WASM_SUBTYPE_STRING);

    public static final String APPLICATION_FORM_URL_ENCODED_SUBTYPE_STRING = "x-www-form-urlencoded";
    public static final String APPLICATION_FORM_URL_ENCODED_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_FORM_URL_ENCODED_SUBTYPE_STRING;
    public static final ContentType APPLICATION_FORM_URL_ENCODED = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_FORM_URL_ENCODED_SUBTYPE_STRING);

    public static final String APPLICATION_XHTML_SUBTYPE_STRING = "xhtml+xml";
    public static final String APPLICATION_XHTML_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_XHTML_SUBTYPE_STRING;
    public static final ContentType APPLICATION_XHTML = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_XHTML_SUBTYPE_STRING);
    public static final ContentType APPLICATION_XHTML_UTF_8 = APPLICATION_XHTML.withCharset(UTF_8);

    public static final String APPLICATION_XML_SUBTYPE_STRING = "xml";
    public static final String APPLICATION_XML_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_XML_SUBTYPE_STRING;
    public static final ContentType APPLICATION_XML = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_XML_SUBTYPE_STRING);
    public static final ContentType APPLICATION_XML_UTF_8 = APPLICATION_XML.withCharset(UTF_8);

    public static final String APPLICATION_ZIP_SUBTYPE_STRING = "zip";
    public static final String APPLICATION_ZIP_STRING = APPLICATION_TYPE_STRING + SEPARATOR_TOKEN +
            APPLICATION_ZIP_SUBTYPE_STRING;
    public static final ContentType APPLICATION_ZIP = new ContentType(APPLICATION_TYPE_STRING,
            APPLICATION_ZIP_SUBTYPE_STRING);

    // END application types

    // BEGIN font types

    public static final String FONT_TYPE_STRING = "font";
    public static final String FONT_WILDCARD_STRING = FONT_TYPE_STRING + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType FONT_WILDCARD = new ContentType(FONT_TYPE_STRING, WILDCARD_TOKEN);

    public static final String FONT_COLLECTION_SUBTYPE_STRING = "collection";
    public static final String FONT_COLLECTION_STRING = FONT_TYPE_STRING + SEPARATOR_TOKEN +
            FONT_COLLECTION_SUBTYPE_STRING;
    public static final ContentType FONT_COLLECTION = new ContentType(FONT_TYPE_STRING,
            FONT_COLLECTION_SUBTYPE_STRING);

    public static final String FONT_OTF_SUBTYPE_STRING = "otf";
    public static final String FONT_OTF_STRING = FONT_TYPE_STRING + SEPARATOR_TOKEN +
            FONT_OTF_SUBTYPE_STRING;
    public static final ContentType FONT_OTF = new ContentType(FONT_TYPE_STRING,
            FONT_OTF_SUBTYPE_STRING);

    public static final String FONT_TTF_SUBTYPE_STRING = "ttf";
    public static final String FONT_TTF_STRING = FONT_TYPE_STRING + SEPARATOR_TOKEN +
            FONT_TTF_SUBTYPE_STRING;
    public static final ContentType FONT_TTF = new ContentType(FONT_TYPE_STRING,
            FONT_TTF_SUBTYPE_STRING);

    public static final String FONT_WOFF_SUBTYPE_STRING = "woff";
    public static final String FONT_WOFF_STRING = FONT_TYPE_STRING + SEPARATOR_TOKEN +
            FONT_WOFF_SUBTYPE_STRING;
    public static final ContentType FONT_WOFF = new ContentType(FONT_TYPE_STRING,
            FONT_WOFF_SUBTYPE_STRING);

    public static final String FONT_WOFF2_SUBTYPE_STRING = "woff2";
    public static final String FONT_WOFF2_STRING = FONT_TYPE_STRING + SEPARATOR_TOKEN +
            FONT_WOFF2_SUBTYPE_STRING;
    public static final ContentType FONT_WOFF2 = new ContentType(FONT_TYPE_STRING,
            FONT_WOFF2_SUBTYPE_STRING);

    // END font types

    // BEGIN multipart types

    public static final String MULTIPART_TYPE_STRING = "multipart";
    public static final String MULTIPART_WILDCARD_STRING = MULTIPART_TYPE_STRING + SEPARATOR_TOKEN + WILDCARD_TOKEN;
    public static final ContentType MULTIPART_WILDCARD = new ContentType(MULTIPART_TYPE_STRING, WILDCARD_TOKEN);

    public static final String MULTIPART_FORM_DATA_SUBTYPE_STRING = "form-data";
    public static final String MULTIPART_FORM_DATA_STRING = MULTIPART_TYPE_STRING + SEPARATOR_TOKEN +
            MULTIPART_FORM_DATA_SUBTYPE_STRING;
    public static final ContentType MULTIPART_FORM_DATA = new ContentType(MULTIPART_TYPE_STRING,
            MULTIPART_FORM_DATA_SUBTYPE_STRING);

    // END multipart types

    /**
     * A non-exhaustive, unmodifiable {@link Set} of common parameter-less {@link ContentType}s that modern browsers can
     * typically render through an HTML tag without the need for any extra plugins and have a very low likelihood of
     * being vulnerable to XSS attacks if the content is used with the proper HTML tag or directly rendered in a browser
     * tab. The content type of untrusted content (e.g. user-submitted files) should be checked against this
     * {@link Set} before setting the {@link Header#CONTENT_TYPE} response header to the value from
     * {@link #forFileExtension(String)}.
     */
    public static final Set<ContentType> XSS_SAFE_HTML_TAG_CONTENT_TYPES = Set.of(
            IMAGE_BMP, IMAGE_GIF, IMAGE_ICO, IMAGE_JPEG, IMAGE_PNG, IMAGE_TIFF, IMAGE_AVIF, IMAGE_WEBP, IMAGE_HEIC,
            IMAGE_HEIF,
            AUDIO_MP4, AUDIO_MPEG, AUDIO_OGG, AUDIO_WEBM, AUDIO_AAC, AUDIO_VORBIS, AUDIO_VND_WAVE,
            VIDEO_MP4, VIDEO_MPEG, VIDEO_OGG, VIDEO_QUICKTIME, VIDEO_WEBM,
            APPLICATION_PDF);

    /**
     * A non-exhaustive, unmodifiable {@link Set} of common {@link ContentType}s for typically intrinsically
     * compressed file types.
     */
    public static final Set<ContentType> COMPRESSED_CONTENT_TYPES = Set.of(
            IMAGE_GIF, IMAGE_ICO, IMAGE_JPEG, IMAGE_PNG, IMAGE_TIFF, IMAGE_AVIF, IMAGE_WEBP, IMAGE_HEIF, IMAGE_HEIC,
            AUDIO_MP4, AUDIO_MPEG, AUDIO_OGG, AUDIO_WEBM, AUDIO_AAC, AUDIO_VORBIS,
            VIDEO_MP4, VIDEO_MPEG, VIDEO_OGG, VIDEO_QUICKTIME, VIDEO_WEBM,
            FONT_WOFF, FONT_WOFF2,
            new ContentType(APPLICATION_TYPE_STRING, "zip"),
            new ContentType(APPLICATION_TYPE_STRING, "x-zip"),
            new ContentType(APPLICATION_TYPE_STRING, "bzip"),
            new ContentType(APPLICATION_TYPE_STRING, "x-bzip"),
            new ContentType(APPLICATION_TYPE_STRING, "bzip2"),
            new ContentType(APPLICATION_TYPE_STRING, "x-bzip2"),
            new ContentType(APPLICATION_TYPE_STRING, "gzip"),
            new ContentType(APPLICATION_TYPE_STRING, "x-gzip"),
            new ContentType(APPLICATION_TYPE_STRING, "brotli"),
            new ContentType(APPLICATION_TYPE_STRING, "x-br"),
            new ContentType(APPLICATION_TYPE_STRING, "zstd"),
            new ContentType(APPLICATION_TYPE_STRING, "x-zstd"),
            new ContentType(APPLICATION_TYPE_STRING, "zstandard"),
            new ContentType(APPLICATION_TYPE_STRING, "x-zstandard"),
            new ContentType(APPLICATION_TYPE_STRING, "x-xz"),
            new ContentType(APPLICATION_TYPE_STRING, "x-rar-compressed"),
            new ContentType(APPLICATION_TYPE_STRING, "x-zip-compressed"),
            new ContentType(APPLICATION_TYPE_STRING, "x-7z-compressed"));

    /**
     * A non-exhaustive, unmodifiable {@link SetMultimap} of {@link ContentType}s mapped to their dot-less file
     * extensions (e.g. <code>text/plain</code> -> <code>txt</code>).
     */
    public static final SetMultimap<ContentType, String> FILE_EXTENSIONS_OF_CONTENT_TYPES;

    /**
     * The inverse of {@link #FILE_EXTENSIONS_OF_CONTENT_TYPES}.
     */
    public static final Map<String, ContentType> CONTENT_TYPE_OF_FILE_EXTENSIONS;

    static {
        final List<String> tsvLines;
        try {
            tsvLines = readAllLines(Path.of(requireNonNull(
                    ContentType.class.getResource("file-extensions-of-mime-types.tsv")).toURI()), UTF_8);
        } catch (final IOException | URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
        FILE_EXTENSIONS_OF_CONTENT_TYPES = unmodifiableSetMultimap(tsvLines.stream()
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
     * Calls {@link #ContentType(String, String, Map)} with <code>parameters</code> set to <code>null</code>.
     */
    public ContentType(final String type, final String subtype) throws IllegalArgumentException {
        this(type, subtype, (Map<String, String>) null);
    }

    /**
     * Calls {@link #ContentType(String, String, Multimap)} with <code>parameters</code> set to the given
     * <code>parameters</code> wrapped with {@link Multimaps#forMap(Map)} if non-<code>null</code>.
     */
    public ContentType(final String type, final String subtype, final @Nullable Map<String, String> parameters) {
        this(type, subtype, parameters == null ? null : Multimaps.forMap(parameters));
    }

    /**
     * Instantiates a new {@link ContentType}.
     *
     * @param type       the type
     * @param subtype    the subtype
     * @param parameters the parameters {@link Multimap}, or <code>null</code> for no parameters
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     *
     * @see MediaType#create(String, String)
     */
    public ContentType(final String type, final String subtype, final @Nullable Multimap<String, String> parameters)
            throws IllegalArgumentException {
        final var mediaType = MediaType.create(type, subtype);
        this(parameters == null ? mediaType : mediaType.withParameters(parameters));
    }

    /**
     * Instantiates a new {@link ContentType}.
     *
     * @param type    the type
     * @param subtype the subtype
     * @param charset the {@link Charset}
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     *
     * @see MediaType#create(String, String)
     * @see MediaType#withCharset(Charset)
     */
    public ContentType(final String type, final String subtype, final Charset charset) throws IllegalArgumentException {
        this(MediaType.create(type, subtype).withCharset(charset));
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
     * @return {@link MediaType#type()}
     */
    public String getType() {
        return mediaType.type();
    }

    /**
     * @return {@link MediaType#subtype()}
     */
    public String getSubtype() {
        return mediaType.subtype();
    }

    /**
     * @return {@link MediaType#parameters()}
     */
    public ImmutableListMultimap<String, String> getParameters() {
        return mediaType.parameters();
    }

    /**
     * @return {@link MediaType#withoutParameters()}
     */
    @SuppressWarnings("ReferenceEquality")
    public ContentType withoutParameters() {
        final var withoutParameters = mediaType.withoutParameters();
        return withoutParameters == mediaType ? this : new ContentType(withoutParameters);
    }

    /**
     * Calls {@link #withParameters(Map)} with <code>parameters</code> set to {@link Map#of(Object, Object)} with the
     * given arguments respectively.
     */
    public ContentType withParameter(final String attribute, final String value) {
        return withParameters(Map.of(attribute, value));
    }

    /**
     * Calls {@link #withParameters(Multimap)} with <code>parameters</code> wrapped with {@link Multimaps#forMap(Map)}.
     */
    public ContentType withParameters(final Map<String, String> parameters) {
        return withParameters(Multimaps.forMap(parameters));
    }

    /**
     * @return {@link MediaType#withParameters(Multimap)}
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    public ContentType withParameters(final Multimap<String, String> parameters) throws IllegalArgumentException {
        return new ContentType(mediaType.withParameters(parameters));
    }

    /**
     * Calls {@link #addParameters(Map)} with <code>parameters</code> set to {@link Map#of(Object, Object)} with the
     * given arguments respectively.
     */
    public ContentType addParameter(final String attribute, final String value) {
        return addParameters(Map.of(attribute, value));
    }

    /**
     * Calls {@link #addParameters(Multimap)} with <code>parameters</code> wrapped with {@link Multimaps#forMap(Map)}.
     */
    public ContentType addParameters(final Map<String, String> parameters) {
        return addParameters(Multimaps.forMap(parameters));
    }

    /**
     * @return {@link MediaType#withParameters(Multimap)} with a {@link Multimap} of contents combined from
     * {@link #getParameters()} and the given <code>parameters</code>
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    public ContentType addParameters(final Multimap<String, String> parameters) throws IllegalArgumentException {
        final var existingParameters = getParameters();
        final var combinedParameters = MultimapBuilder
                .hashKeys(existingParameters.keySet().size() + parameters.keySet().size())
                .arrayListValues(1)
                .build(existingParameters);
        combinedParameters.putAll(parameters);
        return new ContentType(mediaType.withParameters(combinedParameters));
    }

    /**
     * @return {@link MediaType#charset()} {@link Optional#orNull()}
     */
    public @Nullable Charset getCharset() {
        return mediaType.charset().orNull();
    }

    /**
     * @return {@link MediaType#withCharset(Charset)}
     */
    public ContentType withCharset(final Charset charset) {
        return new ContentType(mediaType.withCharset(charset));
    }

    /**
     * Calls {@link #is(MediaType)} with the given {@link ContentType#getMediaType()}.
     */
    public boolean is(final ContentType contentType) {
        return is(contentType.getMediaType());
    }

    /**
     * @return {@link MediaType#is(MediaType)}
     */
    public boolean is(final MediaType mediaType) {
        return this.mediaType.is(mediaType);
    }

    /**
     * Checks if this {@link #withoutParameters()} is in {@link #XSS_SAFE_HTML_TAG_CONTENT_TYPES}.
     */
    public boolean isXssSafeHtmlTag() {
        return XSS_SAFE_HTML_TAG_CONTENT_TYPES.contains(withoutParameters());
    }

    /**
     * Gets the dot-less file extensions for this {@link ContentType} from {@link #FILE_EXTENSIONS_OF_CONTENT_TYPES}.
     *
     * @return the unmodifiable {@link String} {@link Set}
     */
    public Set<String> getFileExtensions() {
        return FILE_EXTENSIONS_OF_CONTENT_TYPES.get(this);
    }

    /**
     * @return {@link MediaType#toString()}
     */
    @Override
    public String toString() {
        return mediaType.toString();
    }
}
