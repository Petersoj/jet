package net.jacobpeterson.jet.common.http.header.contenttype;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.Files;
import com.google.common.net.MediaType;
import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
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

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSetMultimap.flatteningToImmutableSetMultimap;
import static com.google.common.io.Files.getFileExtension;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllLines;
import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link ContentType} is an immutable class that represents a standardized HTTP {@link Header#CONTENT_TYPE} (aka MIME
 * type). Internally, this class wraps {@link MediaType} from <a href="https://github.com/google/guava">Google Guava</a>
 * and adds some extra functionality. The public constants are commonly used content types. The constants are defined as
 * both {@link ContentType} and as {@link String} types. The {@link String} types are provided so they can be used
 * within annotations.
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
 * response may be returned.
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
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(cacheStrategy = LAZY)
public final class ContentType {

    /** The {@link #getType()}-{@link #getSubtype()} delimiter: <code>"/"</code> */
    public static final String TYPE_DELIMITER = "/";
    /** The wildcard token: <code>"*"</code> */
    public static final String WILDCARD_TOKEN = "*";
    public static final String WILDCARD_WILDCARD_STRING = WILDCARD_TOKEN + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType WILDCARD_WILDCARD = create(WILDCARD_TOKEN, WILDCARD_TOKEN);

    // BEGIN text types

    public static final String TEXT_TYPE_STRING = "text";
    public static final String TEXT_WILDCARD_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType TEXT_WILDCARD = create(TEXT_TYPE_STRING, WILDCARD_TOKEN);

    public static final String TEXT_CSS_SUBTYPE_STRING = "css";
    public static final String TEXT_CSS_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + TEXT_CSS_SUBTYPE_STRING;
    public static final ContentType TEXT_CSS = create(TEXT_TYPE_STRING, TEXT_CSS_SUBTYPE_STRING);
    public static final ContentType TEXT_CSS_UTF_8 = TEXT_CSS.withCharset(UTF_8);

    public static final String TEXT_CSV_SUBTYPE_STRING = "csv";
    public static final String TEXT_CSV_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + TEXT_CSV_SUBTYPE_STRING;
    public static final ContentType TEXT_CSV = create(TEXT_TYPE_STRING, TEXT_CSV_SUBTYPE_STRING);
    public static final ContentType TEXT_CSV_UTF_8 = TEXT_CSV.withCharset(UTF_8);

    public static final String TEXT_HTML_SUBTYPE_STRING = "html";
    public static final String TEXT_HTML_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + TEXT_HTML_SUBTYPE_STRING;
    public static final ContentType TEXT_HTML = create(TEXT_TYPE_STRING, TEXT_HTML_SUBTYPE_STRING);
    public static final ContentType TEXT_HTML_UTF_8 = TEXT_HTML.withCharset(UTF_8);

    public static final String TEXT_MARKDOWN_SUBTYPE_STRING = "markdown";
    public static final String TEXT_MARKDOWN_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + TEXT_MARKDOWN_SUBTYPE_STRING;
    public static final ContentType TEXT_MARKDOWN = create(TEXT_TYPE_STRING, TEXT_MARKDOWN_SUBTYPE_STRING);
    public static final ContentType TEXT_MARKDOWN_UTF_8 = TEXT_MARKDOWN.withCharset(UTF_8);

    public static final String TEXT_PLAIN_SUBTYPE_STRING = "plain";
    public static final String TEXT_PLAIN_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + TEXT_PLAIN_SUBTYPE_STRING;
    public static final ContentType TEXT_PLAIN = create(TEXT_TYPE_STRING, TEXT_PLAIN_SUBTYPE_STRING);
    public static final ContentType TEXT_PLAIN_UTF_8 = TEXT_PLAIN.withCharset(UTF_8);

    public static final String TEXT_TSV_SUBTYPE_STRING = "tab-separated-values";
    public static final String TEXT_TSV_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + TEXT_TSV_SUBTYPE_STRING;
    public static final ContentType TEXT_TSV = create(TEXT_TYPE_STRING, TEXT_TSV_SUBTYPE_STRING);
    public static final ContentType TEXT_TSV_UTF_8 = TEXT_TSV.withCharset(UTF_8);

    public static final String TEXT_XML_SUBTYPE_STRING = "xml";
    public static final String TEXT_XML_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER + TEXT_XML_SUBTYPE_STRING;
    public static final ContentType TEXT_XML = create(TEXT_TYPE_STRING, TEXT_XML_SUBTYPE_STRING);
    public static final ContentType TEXT_XML_UTF_8 = TEXT_XML.withCharset(UTF_8);

    public static final String TEXT_EVENT_STREAM_SUBTYPE_STRING = "event-stream";
    public static final String TEXT_EVENT_STREAM_STRING = TEXT_TYPE_STRING + TYPE_DELIMITER +
            TEXT_EVENT_STREAM_SUBTYPE_STRING;
    public static final ContentType TEXT_EVENT_STREAM = create(TEXT_TYPE_STRING, TEXT_EVENT_STREAM_SUBTYPE_STRING);

    // END text types

    // BEGIN image types

    public static final String IMAGE_TYPE_STRING = "image";
    public static final String IMAGE_WILDCARD_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType IMAGE_WILDCARD = create(IMAGE_TYPE_STRING, WILDCARD_TOKEN);

    public static final String IMAGE_BMP_SUBTYPE_STRING = "bmp";
    public static final String IMAGE_BMP_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_BMP_SUBTYPE_STRING;
    public static final ContentType IMAGE_BMP = create(IMAGE_TYPE_STRING, IMAGE_BMP_SUBTYPE_STRING);

    public static final String IMAGE_GIF_SUBTYPE_STRING = "gif";
    public static final String IMAGE_GIF_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_GIF_SUBTYPE_STRING;
    public static final ContentType IMAGE_GIF = create(IMAGE_TYPE_STRING, IMAGE_GIF_SUBTYPE_STRING);

    public static final String IMAGE_ICO_SUBTYPE_STRING = "vnd.microsoft.icon";
    public static final String IMAGE_ICO_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_ICO_SUBTYPE_STRING;
    public static final ContentType IMAGE_ICO = create(IMAGE_TYPE_STRING, IMAGE_ICO_SUBTYPE_STRING);

    public static final String IMAGE_JPEG_SUBTYPE_STRING = "jpeg";
    public static final String IMAGE_JPEG_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_JPEG_SUBTYPE_STRING;
    public static final ContentType IMAGE_JPEG = create(IMAGE_TYPE_STRING, IMAGE_JPEG_SUBTYPE_STRING);

    public static final String IMAGE_PNG_SUBTYPE_STRING = "png";
    public static final String IMAGE_PNG_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_PNG_SUBTYPE_STRING;
    public static final ContentType IMAGE_PNG = create(IMAGE_TYPE_STRING, IMAGE_PNG_SUBTYPE_STRING);

    public static final String IMAGE_SVG_SUBTYPE_STRING = "svg+xml";
    public static final String IMAGE_SVG_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_SVG_SUBTYPE_STRING;
    public static final ContentType IMAGE_SVG = create(IMAGE_TYPE_STRING, IMAGE_SVG_SUBTYPE_STRING);
    public static final ContentType IMAGE_SVG_UTF_8 = IMAGE_SVG.withCharset(UTF_8);

    public static final String IMAGE_TIFF_SUBTYPE_STRING = "tiff";
    public static final String IMAGE_TIFF_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_TIFF_SUBTYPE_STRING;
    public static final ContentType IMAGE_TIFF = create(IMAGE_TYPE_STRING, IMAGE_TIFF_SUBTYPE_STRING);

    public static final String IMAGE_AVIF_SUBTYPE_STRING = "avif";
    public static final String IMAGE_AVIF_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_AVIF_SUBTYPE_STRING;
    public static final ContentType IMAGE_AVIF = create(IMAGE_TYPE_STRING, IMAGE_AVIF_SUBTYPE_STRING);

    public static final String IMAGE_WEBP_SUBTYPE_STRING = "webp";
    public static final String IMAGE_WEBP_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_WEBP_SUBTYPE_STRING;
    public static final ContentType IMAGE_WEBP = create(IMAGE_TYPE_STRING, IMAGE_WEBP_SUBTYPE_STRING);

    public static final String IMAGE_HEIC_SUBTYPE_STRING = "heic";
    public static final String IMAGE_HEIC_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_HEIC_SUBTYPE_STRING;
    public static final ContentType IMAGE_HEIC = create(IMAGE_TYPE_STRING, IMAGE_HEIC_SUBTYPE_STRING);

    public static final String IMAGE_HEIF_SUBTYPE_STRING = "heif";
    public static final String IMAGE_HEIF_STRING = IMAGE_TYPE_STRING + TYPE_DELIMITER + IMAGE_HEIF_SUBTYPE_STRING;
    public static final ContentType IMAGE_HEIF = create(IMAGE_TYPE_STRING, IMAGE_HEIF_SUBTYPE_STRING);

    // END image types

    // BEGIN audio types

    public static final String AUDIO_TYPE_STRING = "audio";
    public static final String AUDIO_WILDCARD_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType AUDIO_WILDCARD = create(AUDIO_TYPE_STRING, WILDCARD_TOKEN);

    public static final String AUDIO_MP4_SUBTYPE_STRING = "mp4";
    public static final String AUDIO_MP4_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER +
            AUDIO_MP4_SUBTYPE_STRING;
    public static final ContentType AUDIO_MP4 = create(AUDIO_TYPE_STRING, AUDIO_MP4_SUBTYPE_STRING);

    public static final String AUDIO_MPEG_SUBTYPE_STRING = "mpeg";
    public static final String AUDIO_MPEG_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER +
            AUDIO_MPEG_SUBTYPE_STRING;
    public static final ContentType AUDIO_MPEG = create(AUDIO_TYPE_STRING, AUDIO_MPEG_SUBTYPE_STRING);

    public static final String AUDIO_OGG_SUBTYPE_STRING = "ogg";
    public static final String AUDIO_OGG_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER +
            AUDIO_OGG_SUBTYPE_STRING;
    public static final ContentType AUDIO_OGG = create(AUDIO_TYPE_STRING, AUDIO_OGG_SUBTYPE_STRING);

    public static final String AUDIO_WEBM_SUBTYPE_STRING = "webm";
    public static final String AUDIO_WEBM_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER +
            AUDIO_WEBM_SUBTYPE_STRING;
    public static final ContentType AUDIO_WEBM = create(AUDIO_TYPE_STRING, AUDIO_WEBM_SUBTYPE_STRING);

    public static final String AUDIO_AAC_SUBTYPE_STRING = "aac";
    public static final String AUDIO_AAC_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER +
            AUDIO_AAC_SUBTYPE_STRING;
    public static final ContentType AUDIO_AAC = create(AUDIO_TYPE_STRING, AUDIO_AAC_SUBTYPE_STRING);

    public static final String AUDIO_VORBIS_SUBTYPE_STRING = "vorbis";
    public static final String AUDIO_VORBIS_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER +
            AUDIO_VORBIS_SUBTYPE_STRING;
    public static final ContentType AUDIO_VORBIS = create(AUDIO_TYPE_STRING, AUDIO_VORBIS_SUBTYPE_STRING);

    public static final String AUDIO_VND_WAVE_SUBTYPE_STRING = "vnd.wave";
    public static final String AUDIO_VND_WAVE_STRING = AUDIO_TYPE_STRING + TYPE_DELIMITER +
            AUDIO_VND_WAVE_SUBTYPE_STRING;
    public static final ContentType AUDIO_VND_WAVE = create(AUDIO_TYPE_STRING, AUDIO_VND_WAVE_SUBTYPE_STRING);

    // END audio types

    // BEGIN video types

    public static final String VIDEO_TYPE_STRING = "video";
    public static final String VIDEO_WILDCARD_STRING = VIDEO_TYPE_STRING + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType VIDEO_WILDCARD = create(VIDEO_TYPE_STRING, WILDCARD_TOKEN);

    public static final String VIDEO_MP4_SUBTYPE_STRING = "mp4";
    public static final String VIDEO_MP4_STRING = VIDEO_TYPE_STRING + TYPE_DELIMITER +
            VIDEO_MP4_SUBTYPE_STRING;
    public static final ContentType VIDEO_MP4 = create(VIDEO_TYPE_STRING, VIDEO_MP4_SUBTYPE_STRING);

    public static final String VIDEO_MPEG_SUBTYPE_STRING = "mpeg";
    public static final String VIDEO_MPEG_STRING = VIDEO_TYPE_STRING + TYPE_DELIMITER +
            VIDEO_MPEG_SUBTYPE_STRING;
    public static final ContentType VIDEO_MPEG = create(VIDEO_TYPE_STRING, VIDEO_MPEG_SUBTYPE_STRING);

    public static final String VIDEO_OGG_SUBTYPE_STRING = "ogg";
    public static final String VIDEO_OGG_STRING = VIDEO_TYPE_STRING + TYPE_DELIMITER +
            VIDEO_OGG_SUBTYPE_STRING;
    public static final ContentType VIDEO_OGG = create(VIDEO_TYPE_STRING, VIDEO_OGG_SUBTYPE_STRING);

    public static final String VIDEO_QUICKTIME_SUBTYPE_STRING = "quicktime";
    public static final String VIDEO_QUICKTIME_STRING = VIDEO_TYPE_STRING + TYPE_DELIMITER +
            VIDEO_QUICKTIME_SUBTYPE_STRING;
    public static final ContentType VIDEO_QUICKTIME = create(VIDEO_TYPE_STRING, VIDEO_QUICKTIME_SUBTYPE_STRING);

    public static final String VIDEO_WEBM_SUBTYPE_STRING = "webm";
    public static final String VIDEO_WEBM_STRING = VIDEO_TYPE_STRING + TYPE_DELIMITER +
            VIDEO_WEBM_SUBTYPE_STRING;
    public static final ContentType VIDEO_WEBM = create(VIDEO_TYPE_STRING, VIDEO_WEBM_SUBTYPE_STRING);

    // END video types

    // BEGIN application types

    public static final String APPLICATION_TYPE_STRING = "application";
    public static final String APPLICATION_WILD_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType APPLICATION_WILDCARD = create(APPLICATION_TYPE_STRING, WILDCARD_TOKEN);

    public static final String APPLICATION_GZIP_SUBTYPE_STRING = "gzip";
    public static final String APPLICATION_GZIP_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_GZIP_SUBTYPE_STRING;
    public static final ContentType APPLICATION_GZIP = create(APPLICATION_TYPE_STRING,
            APPLICATION_GZIP_SUBTYPE_STRING);

    public static final String APPLICATION_JAVASCRIPT_SUBTYPE_STRING = "javascript";
    public static final String APPLICATION_JAVASCRIPT_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_JAVASCRIPT_SUBTYPE_STRING;
    public static final ContentType APPLICATION_JAVASCRIPT = create(APPLICATION_TYPE_STRING,
            APPLICATION_JAVASCRIPT_SUBTYPE_STRING);
    public static final ContentType APPLICATION_JAVASCRIPT_UTF_8 = APPLICATION_JAVASCRIPT.withCharset(UTF_8);

    public static final String APPLICATION_JSON_SUBTYPE_STRING = "json";
    public static final String APPLICATION_JSON_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_JSON_SUBTYPE_STRING;
    public static final ContentType APPLICATION_JSON = create(APPLICATION_TYPE_STRING,
            APPLICATION_JSON_SUBTYPE_STRING);
    public static final ContentType APPLICATION_JSON_UTF_8 = APPLICATION_JSON.withCharset(UTF_8);

    public static final String APPLICATION_JWT_SUBTYPE_STRING = "jwt";
    public static final String APPLICATION_JWT_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_JWT_SUBTYPE_STRING;
    public static final ContentType APPLICATION_JWT = create(APPLICATION_TYPE_STRING,
            APPLICATION_JWT_SUBTYPE_STRING);

    public static final String APPLICATION_MANIFEST_JSON_SUBTYPE_STRING = "manifest+json";
    public static final String APPLICATION_MANIFEST_JSON_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_MANIFEST_JSON_SUBTYPE_STRING;
    public static final ContentType APPLICATION_MANIFEST_JSON = create(APPLICATION_TYPE_STRING,
            APPLICATION_MANIFEST_JSON_SUBTYPE_STRING);
    public static final ContentType APPLICATION_MANIFEST_JSON_UTF_8 = APPLICATION_MANIFEST_JSON.withCharset(UTF_8);

    public static final String APPLICATION_OCTET_STREAM_SUBTYPE_STRING = "octet-stream";
    public static final String APPLICATION_OCTET_STREAM_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_OCTET_STREAM_SUBTYPE_STRING;
    public static final ContentType APPLICATION_OCTET_STREAM = create(APPLICATION_TYPE_STRING,
            APPLICATION_OCTET_STREAM_SUBTYPE_STRING);

    public static final String APPLICATION_PDF_SUBTYPE_STRING = "pdf";
    public static final String APPLICATION_PDF_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_PDF_SUBTYPE_STRING;
    public static final ContentType APPLICATION_PDF = create(APPLICATION_TYPE_STRING,
            APPLICATION_PDF_SUBTYPE_STRING);

    public static final String APPLICATION_RTF_SUBTYPE_STRING = "rtf";
    public static final String APPLICATION_RTF_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_RTF_SUBTYPE_STRING;
    public static final ContentType APPLICATION_RTF = create(APPLICATION_TYPE_STRING,
            APPLICATION_RTF_SUBTYPE_STRING);
    public static final ContentType APPLICATION_RTF_UTF_8 = APPLICATION_RTF.withCharset(UTF_8);

    public static final String APPLICATION_WASM_SUBTYPE_STRING = "wasm";
    public static final String APPLICATION_WASM_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_WASM_SUBTYPE_STRING;
    public static final ContentType APPLICATION_WASM = create(APPLICATION_TYPE_STRING,
            APPLICATION_WASM_SUBTYPE_STRING);

    public static final String APPLICATION_FORM_URL_ENCODED_SUBTYPE_STRING = "x-www-form-urlencoded";
    public static final String APPLICATION_FORM_URL_ENCODED_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_FORM_URL_ENCODED_SUBTYPE_STRING;
    public static final ContentType APPLICATION_FORM_URL_ENCODED = create(APPLICATION_TYPE_STRING,
            APPLICATION_FORM_URL_ENCODED_SUBTYPE_STRING);

    public static final String APPLICATION_XHTML_SUBTYPE_STRING = "xhtml+xml";
    public static final String APPLICATION_XHTML_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_XHTML_SUBTYPE_STRING;
    public static final ContentType APPLICATION_XHTML = create(APPLICATION_TYPE_STRING,
            APPLICATION_XHTML_SUBTYPE_STRING);
    public static final ContentType APPLICATION_XHTML_UTF_8 = APPLICATION_XHTML.withCharset(UTF_8);

    public static final String APPLICATION_XML_SUBTYPE_STRING = "xml";
    public static final String APPLICATION_XML_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_XML_SUBTYPE_STRING;
    public static final ContentType APPLICATION_XML = create(APPLICATION_TYPE_STRING,
            APPLICATION_XML_SUBTYPE_STRING);
    public static final ContentType APPLICATION_XML_UTF_8 = APPLICATION_XML.withCharset(UTF_8);

    public static final String APPLICATION_ZIP_SUBTYPE_STRING = "zip";
    public static final String APPLICATION_ZIP_STRING = APPLICATION_TYPE_STRING + TYPE_DELIMITER +
            APPLICATION_ZIP_SUBTYPE_STRING;
    public static final ContentType APPLICATION_ZIP = create(APPLICATION_TYPE_STRING,
            APPLICATION_ZIP_SUBTYPE_STRING);

    // END application types

    // BEGIN font types

    public static final String FONT_TYPE_STRING = "font";
    public static final String FONT_WILDCARD_STRING = FONT_TYPE_STRING + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType FONT_WILDCARD = create(FONT_TYPE_STRING, WILDCARD_TOKEN);

    public static final String FONT_COLLECTION_SUBTYPE_STRING = "collection";
    public static final String FONT_COLLECTION_STRING = FONT_TYPE_STRING + TYPE_DELIMITER +
            FONT_COLLECTION_SUBTYPE_STRING;
    public static final ContentType FONT_COLLECTION = create(FONT_TYPE_STRING, FONT_COLLECTION_SUBTYPE_STRING);

    public static final String FONT_OTF_SUBTYPE_STRING = "otf";
    public static final String FONT_OTF_STRING = FONT_TYPE_STRING + TYPE_DELIMITER +
            FONT_OTF_SUBTYPE_STRING;
    public static final ContentType FONT_OTF = create(FONT_TYPE_STRING, FONT_OTF_SUBTYPE_STRING);

    public static final String FONT_TTF_SUBTYPE_STRING = "ttf";
    public static final String FONT_TTF_STRING = FONT_TYPE_STRING + TYPE_DELIMITER +
            FONT_TTF_SUBTYPE_STRING;
    public static final ContentType FONT_TTF = create(FONT_TYPE_STRING, FONT_TTF_SUBTYPE_STRING);

    public static final String FONT_WOFF_SUBTYPE_STRING = "woff";
    public static final String FONT_WOFF_STRING = FONT_TYPE_STRING + TYPE_DELIMITER +
            FONT_WOFF_SUBTYPE_STRING;
    public static final ContentType FONT_WOFF = create(FONT_TYPE_STRING, FONT_WOFF_SUBTYPE_STRING);

    public static final String FONT_WOFF2_SUBTYPE_STRING = "woff2";
    public static final String FONT_WOFF2_STRING = FONT_TYPE_STRING + TYPE_DELIMITER +
            FONT_WOFF2_SUBTYPE_STRING;
    public static final ContentType FONT_WOFF2 = create(FONT_TYPE_STRING, FONT_WOFF2_SUBTYPE_STRING);

    // END font types

    // BEGIN multipart types

    public static final String MULTIPART_TYPE_STRING = "multipart";
    public static final String MULTIPART_WILDCARD_STRING = MULTIPART_TYPE_STRING + TYPE_DELIMITER + WILDCARD_TOKEN;
    public static final ContentType MULTIPART_WILDCARD = create(MULTIPART_TYPE_STRING, WILDCARD_TOKEN);

    public static final String MULTIPART_FORM_DATA_SUBTYPE_STRING = "form-data";
    public static final String MULTIPART_FORM_DATA_STRING = MULTIPART_TYPE_STRING + TYPE_DELIMITER +
            MULTIPART_FORM_DATA_SUBTYPE_STRING;
    public static final ContentType MULTIPART_FORM_DATA = create(MULTIPART_TYPE_STRING,
            MULTIPART_FORM_DATA_SUBTYPE_STRING);

    // END multipart types

    /**
     * A non-exhaustive {@link ImmutableSet} of common parameter-less {@link ContentType}s that modern browsers can
     * typically render through an HTML tag without the need for any extra plugins and have a very low likelihood of
     * being vulnerable to XSS attacks if the content is used with the proper HTML tag or directly rendered in a browser
     * tab. The content type of untrusted content (e.g. user-submitted files) should be checked against this
     * {@link Set} before setting the {@link Header#CONTENT_TYPE} response header to the value from
     * {@link #forFileExtension(String)}.
     */
    public static final ImmutableSet<ContentType> XSS_SAFE_HTML_TAG_CONTENT_TYPES = ImmutableSet.of(
            IMAGE_BMP, IMAGE_GIF, IMAGE_ICO, IMAGE_JPEG, IMAGE_PNG, IMAGE_TIFF, IMAGE_AVIF, IMAGE_WEBP, IMAGE_HEIC,
            IMAGE_HEIF,
            AUDIO_MP4, AUDIO_MPEG, AUDIO_OGG, AUDIO_WEBM, AUDIO_AAC, AUDIO_VORBIS, AUDIO_VND_WAVE,
            VIDEO_MP4, VIDEO_MPEG, VIDEO_OGG, VIDEO_QUICKTIME, VIDEO_WEBM,
            APPLICATION_PDF);

    /**
     * A non-exhaustive {@link ImmutableSet} of common {@link ContentType}s for typically intrinsically compressed file
     * types.
     */
    public static final ImmutableSet<ContentType> COMPRESSED_CONTENT_TYPES = ImmutableSet.of(
            IMAGE_GIF, IMAGE_ICO, IMAGE_JPEG, IMAGE_PNG, IMAGE_TIFF, IMAGE_AVIF, IMAGE_WEBP, IMAGE_HEIF, IMAGE_HEIC,
            AUDIO_MP4, AUDIO_MPEG, AUDIO_OGG, AUDIO_WEBM, AUDIO_AAC, AUDIO_VORBIS,
            VIDEO_MP4, VIDEO_MPEG, VIDEO_OGG, VIDEO_QUICKTIME, VIDEO_WEBM,
            FONT_WOFF, FONT_WOFF2,
            create(APPLICATION_TYPE_STRING, "zip"),
            create(APPLICATION_TYPE_STRING, "x-zip"),
            create(APPLICATION_TYPE_STRING, "bzip"),
            create(APPLICATION_TYPE_STRING, "x-bzip"),
            create(APPLICATION_TYPE_STRING, "bzip2"),
            create(APPLICATION_TYPE_STRING, "x-bzip2"),
            create(APPLICATION_TYPE_STRING, "gzip"),
            create(APPLICATION_TYPE_STRING, "x-gzip"),
            create(APPLICATION_TYPE_STRING, "brotli"),
            create(APPLICATION_TYPE_STRING, "x-br"),
            create(APPLICATION_TYPE_STRING, "zstd"),
            create(APPLICATION_TYPE_STRING, "x-zstd"),
            create(APPLICATION_TYPE_STRING, "zstandard"),
            create(APPLICATION_TYPE_STRING, "x-zstandard"),
            create(APPLICATION_TYPE_STRING, "x-xz"),
            create(APPLICATION_TYPE_STRING, "x-rar-compressed"),
            create(APPLICATION_TYPE_STRING, "x-zip-compressed"),
            create(APPLICATION_TYPE_STRING, "x-7z-compressed"));

    /**
     * A non-exhaustive {@link ImmutableSetMultimap} of {@link ContentType}s mapped to their dot-less file
     * extensions (e.g. <code>text/plain</code> -> <code>txt</code>).
     */
    public static final ImmutableSetMultimap<ContentType, String> FILE_EXTENSIONS_OF_CONTENT_TYPES;

    /**
     * The inverse of {@link #FILE_EXTENSIONS_OF_CONTENT_TYPES}.
     */
    public static final ImmutableMap<String, ContentType> CONTENT_TYPE_OF_FILE_EXTENSIONS;

    static {
        final List<String> tsvLines;
        try {
            tsvLines = readAllLines(Path.of(requireNonNull(
                    ContentType.class.getResource("file-extensions-of-mime-types.tsv")).toURI()), UTF_8);
        } catch (final IOException | URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
        FILE_EXTENSIONS_OF_CONTENT_TYPES = tsvLines.stream()
                .filter(line -> !line.isBlank() && !line.startsWith("#"))
                .map(line -> Splitter.on('\t').limit(2).splitToList(line))
                .collect(flatteningToImmutableSetMultimap(tsv -> parse(tsv.getFirst()),
                        tsv -> Splitter.on(' ').splitToStream(tsv.get(1))));
        CONTENT_TYPE_OF_FILE_EXTENSIONS = FILE_EXTENSIONS_OF_CONTENT_TYPES.entries().stream()
                .collect(toImmutableMap(Entry::getValue, Entry::getKey, (first, _) -> first));
    }

    /**
     * @return {@link #wrap(MediaType)} {@link MediaType#parse(String)}
     *
     * @see #toString()
     */
    public static ContentType parse(final String contentType) {
        return wrap(MediaType.parse(contentType));
    }

    /**
     * @return {@link #forFileExtension(String)} with {@link Files#getFileExtension(String)}
     */
    public static @Nullable ContentType forFilename(final String filename) {
        return forFileExtension(getFileExtension(filename));
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

    /**
     * @return {@link #create(String, String, Map)} with <code>parameters</code> set to <code>null</code>
     */
    public static ContentType create(final String type, final String subtype) {
        return create(type, subtype, (Map<String, String>) null);
    }

    /**
     * @return {@link #create(String, String, Multimap)} with <code>parameters</code> set to the given
     * <code>parameters</code> wrapped with {@link Multimaps#forMap(Map)} if non-<code>null</code>
     */
    public static ContentType create(final String type, final String subtype,
            final @Nullable Map<String, String> parameters) {
        return create(type, subtype, parameters == null ? null : Multimaps.forMap(parameters));
    }

    /**
     * @return {@link #wrap(MediaType)} with {@link MediaType#create(String, String)} and
     * {@link MediaType#withParameters(Multimap)}
     */
    public static ContentType create(final String type, final String subtype,
            final @Nullable Multimap<String, String> parameters) {
        final var mediaType = MediaType.create(type, subtype);
        return wrap(parameters == null ? mediaType : mediaType.withParameters(parameters));
    }

    /**
     * @return {@link #wrap(MediaType)} with {@link MediaType#create(String, String)} and
     * {@link MediaType#withCharset(Charset)}
     */
    public static ContentType create(final String type, final String subtype, final Charset charset) {
        return wrap(MediaType.create(type, subtype).withCharset(charset));
    }

    /**
     * Wraps the given {@link MediaType} in a {@link ContentType}.
     *
     * @param mediaType the {@link MediaType}
     */
    public static ContentType wrap(final MediaType mediaType) {
        return new ContentType(mediaType);
    }

    private final MediaType mediaType;

    /**
     * @return the wrapped {@link MediaType}
     */
    public MediaType unwrap() {
        return mediaType;
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
        return withoutParameters == mediaType ? this : wrap(withoutParameters);
    }

    /**
     * @return {@link #withParameters(Map)} {@link Map#of(Object, Object)}
     */
    public ContentType withParameter(final String attribute, final String value) {
        return withParameters(Map.of(attribute, value));
    }

    /**
     * @return {@link #withParameters(Multimap)} {@link Multimaps#forMap(Map)}
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
        return wrap(mediaType.withParameters(parameters));
    }

    /**
     * @return {@link #addParameters(Map)} {@link Map#of(Object, Object)}
     */
    public ContentType addParameter(final String attribute, final String value) {
        return addParameters(Map.of(attribute, value));
    }

    /**
     * @return {@link #addParameters(Multimap)} {@link Multimaps#forMap(Map)}
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
        return wrap(mediaType.withParameters(ImmutableSetMultimap.<String, String>builderWithExpectedKeys(
                        existingParameters.keySet().size() + parameters.keySet().size())
                .expectedValuesPerKey(1)
                .putAll(existingParameters)
                .putAll(parameters)
                .build()));
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
        return wrap(mediaType.withCharset(charset));
    }

    /**
     * @return {@link #is(MediaType)} {@link ContentType#unwrap()}
     */
    public boolean is(final ContentType contentType) {
        return is(contentType.unwrap());
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
     * @return the {@link String} {@link ImmutableSet}
     */
    public ImmutableSet<String> getFileExtensions() {
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
