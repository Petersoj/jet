package net.jacobpeterson.jet.common.http.header.acceptencoding;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.lang.Double.parseDouble;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.joining;
import static lombok.AccessLevel.PRIVATE;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link AcceptEncoding} is an immutable class that represents a standardized HTTP {@link Header#ACCEPT_ENCODING}.
 * <p>
 * The HTTP <strong><code>Accept-Encoding</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Request_header">request</a> and
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Response_header">response header</a> indicates the content
 * encoding (usually a compression algorithm) that the sender can understand. In requests, the server uses
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Content_negotiation">content negotiation</a> to
 * select one of the encoding proposals from the client and informs the client of that choice with the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">
 * <code>Content-Encoding</code></a> response header. In responses, it provides information about which content
 * encodings the server can understand in messages to the requested resource, so that the encoding can be used in
 * subsequent requests to the resource. For example, <code>Accept-Encoding</code> is included in a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/415">
 * <code>415 Unsupported Media Type</code></a> response if a request to a resource (e.g.,
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods/PUT"><code>PUT</code></a>) used an
 * unsupported encoding.
 * <p>
 * Even if both the client and the server support the same compression algorithms, the server may choose not to compress
 * the body of a response if the <code>identity</code> value is also acceptable. This happens in two common cases:
 * <ol>
 * <li>The data is already compressed, meaning a second round of compression will not reduce the transmitted data size,
 * and may actually increase the size of the content in some cases. This is true for pre-compressed image formats (JPEG,
 * for instance).</li>
 * <li>The server is overloaded and cannot allocate computing resources to perform the compression. For example,
 * Microsoft recommends not to compress if a server uses more than 80% of its computational power.</li>
 * </ol>
 * <p>
 * As long as the <code>identity;q=0</code> or <code>*;q=0</code> directives do not explicitly forbid the
 * <code>identity</code> value that means no encoding, the server must never return a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/406"><code>406 Not Acceptable</code></a>
 * error.
 * <p>
 * <strong>Note:</strong> IANA maintains
 * <a href="https://www.iana.org/assignments/http-parameters/http-parameters.xhtml#content-coding">a list of official
 * content encodings</a>. The <code>bzip</code> and <code>bzip2</code> encodings are non-standard, but may be used in
 * some cases, particularly for legacy support.
 *
 * @see
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Encoding">developer.mozilla.org</a>
 * @see Header#ACCEPT_ENCODING
 */
@NullMarked
@Immutable
@RequiredArgsConstructor(access = PRIVATE) @EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
public final class AcceptEncoding {

    /**
     * The entry delimiter: <code>","</code>
     */
    public static final String ENTRY_DELIMITER = ",";

    /**
     * The weight parameter delimiter: <code>"q"</code>
     * <p>
     * Any value is placed in an order of preference expressed using a relative
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Quality_values">quality value</a> called
     * <em>weight</em>.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Encoding#q">developer.mozilla.org</a>
     */
    public static final String WEIGHT_PARAMETER_DELIMITER = ";q=";

    /**
     * The wildcard value: <code>"*"</code>
     * <p>
     * Matches any content encoding not already listed in the header. This is the default value if the header is not
     * present. This directive does not suggest that any algorithm is supported but indicates that no preference is
     * expressed.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Encoding#sect">developer.mozilla.org</a>
     */
    public static final String WILDCARD_VALUE = "*";

    /**
     * An {@link AcceptEncoding} constant for {@link #WILDCARD_VALUE}.
     */
    public static final AcceptEncoding WILDCARD = AcceptEncoding.builder()
            .add(new Entry(WILDCARD_VALUE, null))
            .build();

    /**
     * The identity function value: <code>"identity"</code>
     * <p>
     * Indicates the identity function (that is, without modification or compression). This value is always considered
     * as acceptable, even if omitted.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Encoding#identity">developer.mozilla.org</a>
     */
    public static final String IDENTITY_VALUE = "identity";

    /**
     * An {@link AcceptEncoding} constant for {@link #IDENTITY_VALUE}.
     */
    public static final AcceptEncoding IDENTITY = AcceptEncoding.builder()
            .add(new Entry(IDENTITY_VALUE, null))
            .build();

    /**
     * {@link Entry} represents an entry in the list of {@link AcceptEncoding} values.
     */
    @Value @lombok.Builder(toBuilder = true)
    @Immutable
    public static class Entry {

        /**
         * Parses the given {@link #ENTRY_DELIMITER} entry into an {@link Entry}.
         *
         * @param entry the {@link #toString()}
         *
         * @return the {@link Entry}
         *
         * @throws IllegalArgumentException thrown upon parsing failure
         */
        public static Entry parse(final String entry) throws IllegalArgumentException {
            final var normalized = entry.replace(" ", "").toLowerCase(ROOT);
            final var indexOfWeight = normalized.indexOf(WEIGHT_PARAMETER_DELIMITER);
            return indexOfWeight == -1 ? new Entry(normalized, null) : new Entry(normalized.substring(0, indexOfWeight),
                    parseDouble(normalized.substring(indexOfWeight + WEIGHT_PARAMETER_DELIMITER.length())));
        }

        /**
         * The content encoding (compression type) value {@link String}.
         */
        String value;

        /**
         * The weight parameter {@link Double} value of the {@link #WEIGHT_PARAMETER_DELIMITER}.
         */
        @Nullable Double weight;

        /**
         * @return <code>true</code> if {@link #getValue()} {@link String#equalsIgnoreCase(String)}
         * {@link #WILDCARD_VALUE}, <code>false</code> otherwise
         */
        public boolean isValueWildcard() {
            return value.equalsIgnoreCase(WILDCARD_VALUE);
        }

        /**
         * @return <code>true</code> if {@link #getValue()} {@link String#equalsIgnoreCase(String)}
         * {@link #IDENTITY_VALUE}, <code>false</code> otherwise
         */
        public boolean isValueIdentity() {
            return value.equalsIgnoreCase(IDENTITY_VALUE);
        }

        /**
         * @return {@link CompressionType#forString(String)} {@link #getValue()}
         */
        public @Nullable CompressionType getValueCompressionType() {
            return CompressionType.forString(value);
        }

        @Override
        public String toString() {
            if (weight == null) {
                return value;
            }
            return value + WEIGHT_PARAMETER_DELIMITER + weight;
        }
    }

    private static final Splitter PARSE_ENTRY_SPLITTER = Splitter.on(ENTRY_DELIMITER).trimResults().omitEmptyStrings();

    /**
     * Parses the given {@link Header#ACCEPT_ENCODING} value {@link String} into an {@link AcceptEncoding}.
     *
     * @param accept the {@link Header#ACCEPT_ENCODING} value {@link String}
     *
     * @return the {@link AcceptEncoding}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static AcceptEncoding parse(final String accept) throws IllegalArgumentException {
        return builder()
                .addAll(PARSE_ENTRY_SPLITTER.splitToStream(accept)
                        .map(Entry::parse)
                        .collect(toImmutableList()))
                .build();
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
     * {@link Builder} is a builder class for {@link AcceptEncoding}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private final ImmutableList.Builder<Entry> entries = ImmutableList.builder();

        /**
         * @see #getEntries()
         */
        public Builder add(final Entry entry) {
            entries.add(entry);
            return this;
        }

        /**
         * @see #getEntries()
         */
        public Builder addAll(final Entry... entries) {
            this.entries.add(entries);
            return this;
        }

        /**
         * @see #getEntries()
         */
        public Builder addAll(final Iterable<Entry> entries) {
            this.entries.addAll(entries);
            return this;
        }

        /**
         * Builds this {@link Builder} into a new {@link AcceptEncoding} instance.
         *
         * @return the built {@link AcceptEncoding}
         *
         * @throws IllegalArgumentException thrown if {@link #add(Entry)} one of the {@link #addAll(Entry...)}
         *                                  overloads was never called
         */
        public AcceptEncoding build() throws IllegalArgumentException {
            final var entries = this.entries.build();
            checkArgument(!entries.isEmpty(), "`add()` or `addAll()` was never called");
            return new AcceptEncoding(entries);
        }
    }

    /**
     * The {@link ImmutableList} of {@link Entry}s.
     */
    private final @Getter @EqualsAndHashCode.Include ImmutableList<Entry> entries;

    private @LazyInit @Nullable ImmutableSet<CompressionType> entryTypes;
    private @LazyInit @Nullable String string;

    /**
     * @return internally-cached {@link ImmutableSet} {@link CompressionType}s from {@link #getEntries()}
     * {@link Entry#getValueCompressionType()}
     */
    public ImmutableSet<CompressionType> getEntryTypes() {
        if (entryTypes == null) {
            entryTypes = entries.stream()
                    .map(Entry::getValueCompressionType)
                    .filter(Objects::nonNull)
                    .collect(toImmutableSet());
        }
        return entryTypes;
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#ACCEPT}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            string = entries.stream()
                    .map(Entry::toString)
                    .collect(joining(ENTRY_DELIMITER + " "));
        }
        return string;
    }
}
