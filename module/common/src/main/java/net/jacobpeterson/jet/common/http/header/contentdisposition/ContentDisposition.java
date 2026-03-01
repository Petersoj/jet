package net.jacobpeterson.jet.common.http.header.contentdisposition;

import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contentrange.ContentRange;
import net.jacobpeterson.jet.common.http.header.range.Range;
import net.jacobpeterson.jet.common.http.url.Url;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.http.url.Url.decode;
import static net.jacobpeterson.jet.common.http.url.Url.encode;

/**
 * {@link ContentRange} is an immutable class that represents a standardized HTTP {@link Header#CONTENT_DISPOSITION}.
 * <p>
 * The HTTP <strong><code>Content-Disposition</code></strong> header indicates whether content should be displayed
 * <em>inline</em> in the browser as a web page or part of a web page or downloaded as an <em>attachment</em> locally.
 * <p>
 * In a multipart body, the header must be used on each subpart to provide information about its corresponding field.
 * The subpart is delimited by the <em>boundary</em> defined in the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Type">
 * <code>Content-Type</code></a> header. When used on the body itself, <code>Content-Disposition</code> has no effect.
 * <p>
 * The <code>Content-Disposition</code> header is defined in the larger context of MIME messages for email, but only a
 * subset of the possible parameters apply to HTTP forms and
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/POST"><code>POST</code></a> requests.
 * Only the value <code>form-data</code>, as well as the optional directive <code>name</code> and <code>filename</code>,
 * can be used in the HTTP context.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition">
 * developer.mozilla.org</a>
 * @see Header#CONTENT_DISPOSITION
 * @see Range
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
public final class ContentDisposition {

    /**
     * The directives delimiter: <code>";"</code>
     */
    public static final String DIRECTIVE_DELIMITER = ";";

    /**
     * The directive key-value delimiter: <code>"="</code>
     */
    public static final String DIRECTIVE_KEY_VALUE_DELIMITER = "=";

    /**
     * The <code>name</code> directive key: <code>"name"</code>
     */
    public static final String NAME_DIRECTIVE_KEY = "name";

    /**
     * The <code>filename</code> directive key: <code>"filename"</code>
     */
    public static final String FILENAME_DIRECTIVE_KEY = "filename";

    /**
     * The UTF-8 encoded <code>filename</code> directive key: <code>"filename*"</code>
     */
    public static final String UTF_8_ENCODED_FILENAME_DIRECTIVE_KEY = "filename*";

    /**
     * The {@link #UTF_8_ENCODED_FILENAME_DIRECTIVE_KEY} value prefix: <code>"UTF-8''"</code>
     */
    public static final String UTF_8_ENCODED_FILENAME_DIRECTIVE_VALUE_PREFIX = "UTF-8''";

    /**
     * For {@link #toString()}, replace characters matching this {@link Pattern} with <code>_</code>.
     */
    public static final Pattern NON_FILENAME_PATTERN = Pattern.compile("[^ a-zA-Z0-9-_.',(){}\\[\\]]+");

    private static final Splitter PARSE_DIRECTIVE_SPLITTER =
            Splitter.on(DIRECTIVE_DELIMITER).trimResults().omitEmptyStrings();
    private static final Splitter PARSE_DIRECTIVE_KEY_VALUE_SPLITTER =
            Splitter.on(DIRECTIVE_KEY_VALUE_DELIMITER).limit(2).trimResults();

    public static ContentDisposition parse(final String contentDisposition) throws IllegalArgumentException {
        final var builder = builder();
        for (final var directive : PARSE_DIRECTIVE_SPLITTER.split(contentDisposition)) {
            final var keyValue = PARSE_DIRECTIVE_KEY_VALUE_SPLITTER.splitToList(directive);
            final var key = keyValue.getFirst();
            if (keyValue.size() == 1) {
                final var type = ContentDispositionType.forString(key);
                checkArgument(type != null, "Unknown type: %s", key);
                builder.type(type);
                continue;
            }
            final var value = keyValue.get(1);
            final var isName = key.equalsIgnoreCase(NAME_DIRECTIVE_KEY);
            final var isFilename = key.equalsIgnoreCase(FILENAME_DIRECTIVE_KEY);
            if (isName || isFilename) {
                final var unquotedValue = value.startsWith("\"") && value.endsWith("\"") ?
                        value.substring(1, value.length() - 1) : value;
                if (isName) {
                    builder.name(unquotedValue);
                } else {
                    builder.filename(unquotedValue);
                }
            } else if (key.equalsIgnoreCase(UTF_8_ENCODED_FILENAME_DIRECTIVE_KEY)) {
                checkArgument(value.toUpperCase(ROOT).startsWith(UTF_8_ENCODED_FILENAME_DIRECTIVE_VALUE_PREFIX),
                        "`%s` must start with: %s", UTF_8_ENCODED_FILENAME_DIRECTIVE_KEY,
                        UTF_8_ENCODED_FILENAME_DIRECTIVE_VALUE_PREFIX);
                builder.utf8EncodedFilename(value
                        .substring(UTF_8_ENCODED_FILENAME_DIRECTIVE_VALUE_PREFIX.length()));
            } else {
                throw new IllegalArgumentException("Unknown directive key: " + key);
            }
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
     * {@link Builder} is a builder class for {@link ContentDisposition}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private @Nullable ContentDispositionType type;
        private @Nullable String name;
        private @Nullable String utf8EncodedFilename;

        /**
         * @see #getType()
         */
        public Builder type(final ContentDispositionType type) {
            this.type = type;
            return this;
        }

        /**
         * @see #getName()
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @see #getUtf8EncodedFilename()
         */
        public Builder utf8EncodedFilename(final String utf8EncodedFilename) {
            this.utf8EncodedFilename = utf8EncodedFilename;
            return this;
        }

        /**
         * @return {@link #utf8EncodedFilename(String)} {@link Url#encode(String)}
         */
        public Builder filename(final String filename) {
            return utf8EncodedFilename(encode(filename));
        }

        /**
         * Builds this {@link Builder} into a new {@link ContentDisposition} instance.
         *
         * @return the built {@link ContentDisposition}
         */
        public ContentDisposition build() {
            checkArgument(type != null, "`type` must be set");
            return new ContentDisposition(type, name, utf8EncodedFilename);
        }
    }

    /**
     * The {@link ContentDispositionType}.
     */
    private final @Getter @EqualsAndHashCode.Include ContentDispositionType type;

    /**
     * The name.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition#name">
     * developer.mozilla.org</a>
     */
    private final @Getter @EqualsAndHashCode.Include @Nullable String name;

    /**
     * The {@link StandardCharsets#UTF_8}-encoded filename.
     */
    private final @Getter @Nullable String utf8EncodedFilename;

    private @LazyInit @Nullable String decodedFilename;
    private @LazyInit @Nullable String string;

    /**
     * @return internally-cached {@link Url#decode(String)} {@link #getUtf8EncodedFilename()}
     */
    @EqualsAndHashCode.Include
    public @Nullable String getFilename() {
        if (decodedFilename == null && utf8EncodedFilename != null) {
            decodedFilename = decode(utf8EncodedFilename);
        }
        return decodedFilename;
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#CONTENT_DISPOSITION}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            final var string = new StringBuilder();
            string.append(type);
            if (name != null) {
                string.append(DIRECTIVE_DELIMITER).append(' ')
                        .append(NAME_DIRECTIVE_KEY).append(DIRECTIVE_KEY_VALUE_DELIMITER)
                        .append('"')
                        .append(name)
                        .append('"');
            }
            if (utf8EncodedFilename != null) {
                string.append(DIRECTIVE_DELIMITER).append(' ')
                        .append(FILENAME_DIRECTIVE_KEY).append(DIRECTIVE_KEY_VALUE_DELIMITER)
                        .append('"')
                        .append(NON_FILENAME_PATTERN.matcher(requireNonNull(getFilename())).replaceAll("_"))
                        .append('"');
                string.append(DIRECTIVE_DELIMITER).append(' ')
                        .append(UTF_8_ENCODED_FILENAME_DIRECTIVE_KEY).append(DIRECTIVE_KEY_VALUE_DELIMITER)
                        .append(UTF_8_ENCODED_FILENAME_DIRECTIVE_VALUE_PREFIX)
                        .append(utf8EncodedFilename);
            }
            this.string = string.toString();
        }
        return string;
    }
}
