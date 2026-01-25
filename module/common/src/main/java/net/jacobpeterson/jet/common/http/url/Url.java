package net.jacobpeterson.jet.common.http.url;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.google.errorprone.annotations.Immutable;
import lombok.EqualsAndHashCode;
import org.eclipse.jetty.util.URIUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ROOT;
import static java.util.Map.entry;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link Url} is an immutable class that represents a standardized Uniform Resource Locator (URL), the most common
 * subtype of a Uniform Resource Identifier (URI), requiring a scheme, host, and path that starts with <code>/</code>.
 * <p>
 * <strong>Uniform Resource Identifiers (URI)</strong> are used to identify "resources" on the web. URIs are commonly
 * used as targets of <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP">HTTP</a> requests, in which case the
 * URI represents a location for a resource, such as a document, a photo, binary data. The most common type of URI is a
 * Uniform Resource Locator (<a href="https://developer.mozilla.org/en-US/docs/Glossary/URL">URL</a>), which is known as
 * the <em>web address</em>.
 * <p>
 * URIs can be used to trigger behaviors other than fetching a resource, including opening an email client, sending text
 * messages, or executing JavaScript, when used in other places such as the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Elements/a#href"><code>href</code></a> of an
 * HTML <code>&lt;a&gt;</code> link.
 * <p>
 * The <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference">URI reference</a> provides details about the
 * components that make up a URI:
 * <ul>
 * <li>
 *     <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes">Schemes</a> -
 *     The first part of the URI, before the <code>:</code> character, which indicates the protocol the browser must
 *     use to fetch the resource.
 * </li>
 * <li>
 *     <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Authority">Authority</a> -
 *     The section that comes after the scheme and before the path. It may have up to three parts: <code>user</code>
 *     information, <code>host</code>, and <code>port</code>.
 * </li>
 * <li>
 *     <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Path">Path</a> -
 *     The section after the authority. Contains data, usually organized in hierarchical form, to identify a resource
 *     within the scope of the URI's scheme and authority.
 * </li>
 * <li>
 *     <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Query">Query</a> -
 *     The section after the path. Contains non-hierarchical data to identify a resource within the scope of the
 *     URI's scheme and naming authority along with data in the path component.
 * </li>
 * <li>
 *     <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Fragment">Fragment</a> -
 *     An optional part at the end of a URI starting with a <code>#</code> character. It is used to identify a
 *     specific part of the resource, such as a section of a document or a position in a video.
 * </li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI">developer.mozilla.org</a>
 */
@NullMarked
@Immutable
@EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
@SuppressWarnings({"LombokGetterMayBeUsed", "Immutable",
        "OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class Url {

    /**
     * The delimiter for the scheme of a {@link Url}.
     */
    public static final String SCHEME_DELIMITER = ":";

    /**
     * The delimiter for the authority of a {@link Url}.
     */
    public static final String AUTHORITY_DELIMITER = "//";

    /**
     * The delimiter for the user of a {@link Url}.
     */
    public static final String USER_INFO_DELIMITER = "@";

    /**
     * The delimiter for the port of a {@link Url}.
     */
    public static final String PORT_DELIMITER = ":";

    /**
     * The delimiter for a path segment of a {@link Url}.
     */
    public static final String PATH_SEGMENT_DELIMITER = "/";
    private static final char PATH_SEGMENT_DELIMITER_CHAR = PATH_SEGMENT_DELIMITER.charAt(0);

    /**
     * The delimiter for the query of a {@link Url}.
     */
    public static final String QUERY_DELIMITER = "?";

    /**
     * The delimiter for a query parameter of a {@link Url}.
     */
    public static final String QUERY_PARAMETER_DELIMITER = "&";

    /**
     * The delimiter for a query key-value of a {@link Url}.
     */
    public static final String QUERY_KEY_VALUE_DELIMITER = "=";

    /**
     * The delimiter for the fragment of a {@link Url}.
     */
    public static final String FRAGMENT_DELIMITER = "#";

    /**
     * The inclusive minimum bound for visible ASCII characters.
     *
     * @see #requireVisibleAsciiChars(String)
     */
    public static final char VISIBLE_ASCII_MINIMUM = 0x21;

    /**
     * The inclusive maximum bound for visible ASCII characters.
     *
     * @see #requireVisibleAsciiChars(String)
     */
    public static final char VISIBLE_ASCII_MAXIMUM = 0x7E;

    /**
     * @return {@link URLEncoder#encode(String, Charset)} with <code>decoded</code> and {@link StandardCharsets#UTF_8}
     * (<code>+</code> is replaced with <code>%20</code> to conform with URL percent encoding standards)
     */
    public static String encode(final String decoded) {
        return URLEncoder.encode(decoded, UTF_8).replace("+", "%20");
    }

    /**
     * @return {@link #encode(String)} and <code>%2F</code> replaced with {@link #PATH_SEGMENT_DELIMITER} using
     * {@link String#replace(CharSequence, CharSequence)}
     */
    public static String encodePath(final String decodedPath) {
        return encode(decodedPath).replace("%2F", PATH_SEGMENT_DELIMITER);
    }

    /**
     * @return {@link URLDecoder#decode(String, Charset)} with <code>encoded</code> and {@link StandardCharsets#UTF_8}
     */
    public static String decode(final String encoded) {
        return URLDecoder.decode(encoded, UTF_8);
    }

    /**
     * Validates that each <code>char</code> in the given <code>string</code> is greater than or equal to
     * {@link #VISIBLE_ASCII_MINIMUM} and less than or equal to {@link #VISIBLE_ASCII_MAXIMUM}.
     *
     * @param string the {@link String} to validate
     *
     * @throws IllegalArgumentException thrown if the given <code>string</code> is invalid
     */
    public static void requireVisibleAsciiChars(final String string) throws IllegalArgumentException {
        for (var index = 0; index < string.length(); index++) {
            final var charAt = string.charAt(index);
            if (charAt < VISIBLE_ASCII_MINIMUM || charAt > VISIBLE_ASCII_MAXIMUM) {
                throw new IllegalArgumentException("Invalid URL character found at index %d: 0x%02X"
                        .formatted(index, (int) charAt));
            }
        }
    }

    /**
     * Removes all prepended {@link #PATH_SEGMENT_DELIMITER}s from the given <code>encodedPath</code>.
     *
     * @param encodedPath the encoded path
     *
     * @return the trimmed encoded path
     */
    public static String encodedPathTrimLeading(final String encodedPath) {
        var startIndex = 0;
        final var endIndex = encodedPath.length() - 1;
        while (startIndex <= endIndex && encodedPath.charAt(startIndex) == PATH_SEGMENT_DELIMITER_CHAR) {
            startIndex++;
        }
        return encodedPath.substring(startIndex, endIndex + 1);
    }

    /**
     * Removes all appended {@link #PATH_SEGMENT_DELIMITER}s from the given <code>encodedPath</code>.
     *
     * @param encodedPath the encoded path
     *
     * @return the trimmed encoded path
     */
    public static String encodedPathTrimTrailing(final String encodedPath) {
        final var startIndex = 0;
        var endIndex = encodedPath.length() - 1;
        while (endIndex >= startIndex && encodedPath.charAt(endIndex) == PATH_SEGMENT_DELIMITER_CHAR) {
            endIndex--;
        }
        return encodedPath.substring(startIndex, endIndex + 1);
    }

    /**
     * @return {@link #encodedPathTrimLeading(String)} {@link #encodedPathTrimTrailing(String)}
     */
    public static String encodedPathTrim(final String encodedPath) {
        return encodedPathTrimLeading(encodedPathTrimTrailing(encodedPath));
    }

    /**
     * @param encodedPath the encoded path
     *
     * @return {@link Splitter#on(String)} {@link #PATH_SEGMENT_DELIMITER} {@link Splitter#omitEmptyStrings()}
     * {@link Splitter#splitToStream(CharSequence)}
     */
    public static Stream<String> encodedPathSegmentsToStream(final String encodedPath) {
        return Splitter.on(PATH_SEGMENT_DELIMITER).omitEmptyStrings().splitToStream(encodedPath);
    }

    /**
     * @return {@link #encodedPathSegmentsToStream(String)} {@link Stream#toList()}
     */
    public static List<String> encodedPathSegmentsToList(final String encodedPath) {
        return encodedPathSegmentsToStream(encodedPath).toList();
    }

    /**
     * @return {@link #encodedPathSegmentsToStream(String)} {@link #decode(String)} {@link Stream#toList()}
     */
    public static List<String> encodedPathSegmentsToDecodedList(final String encodedPath) {
        return encodedPathSegmentsToStream(encodedPath).map(Url::decode).toList();
    }

    /**
     * Normalizes the given <code>encodedPath</code> with the following process:
     * <ol>
     * <li>Collapse sequential {@link #PATH_SEGMENT_DELIMITER}s (e.g. <code>/a/b///</code> to <code>/a/b/</code>)</li>
     * <li>Resolve relative paths (e.g. <code>/a/b/..</code> to <code>/a</code>)</li>
     * <li>{@link #encodedPathTrimTrailing(String)} (unless the previous steps result in
     * {@link #PATH_SEGMENT_DELIMITER})</li>
     * </ol>
     *
     * @param encodedPath the encoded path
     *
     * @return the normalized encoded path
     */
    public static String normalizeEncodedPath(final String encodedPath) {
        final var normalized = URIUtil.normalizePathQuery(URIUtil.compactPath(encodedPath));
        if (normalized == null) {
            return encodedPath.startsWith(PATH_SEGMENT_DELIMITER) ? PATH_SEGMENT_DELIMITER : "";
        }
        return normalized.equals(PATH_SEGMENT_DELIMITER) ? PATH_SEGMENT_DELIMITER : encodedPathTrimTrailing(normalized);
    }

    /**
     * Parses the given <code>encodedQuery</code> into a {@link ListMultimap} of query parameters. Query parameters with
     * an empty key are omitted.
     *
     * @param encodedQuery the encoded query (without the leading {@link #QUERY_DELIMITER})
     *
     * @return the query parameters {@link ListMultimap}
     *
     * @see #QUERY_PARAMETER_DELIMITER
     * @see #QUERY_KEY_VALUE_DELIMITER
     */
    public static ListMultimap<String, String> parseEncodedQueryParameters(final @Nullable String encodedQuery) {
        return encodedQuery == null ? ImmutableListMultimap.of() :
                Splitter.on(QUERY_PARAMETER_DELIMITER).splitToStream(encodedQuery)
                        .map(parameter -> Splitter.on(QUERY_KEY_VALUE_DELIMITER).limit(2).splitToList(parameter))
                        .filter(keyValue -> !keyValue.getFirst().isEmpty())
                        .collect(toImmutableListMultimap(List::getFirst,
                                keyValue -> keyValue.size() == 1 ? "" : keyValue.get(1)));
    }

    /**
     * @param encodedQueryParameters {@link #parseEncodedQueryParameters(String)}
     *
     * @return {@link ListMultimap#entries()} {@link #decode(String)}
     */
    public static ListMultimap<String, String> decodeParsedEncodedQueryParameters(
            final ListMultimap<String, String> encodedQueryParameters) {
        return ImmutableListMultimap.copyOf(encodedQueryParameters.entries().stream()
                .map(parameter -> entry(decode(parameter.getKey()), decode(parameter.getValue())))
                .toList());
    }

    /**
     * @return {@link #decodeParsedEncodedQueryParameters(ListMultimap)} {@link #parseEncodedQueryParameters(String)}
     */
    public static ListMultimap<String, String> parseDecodeEncodedQueryParameters(final @Nullable String encodedQuery) {
        return decodeParsedEncodedQueryParameters(parseEncodedQueryParameters(encodedQuery));
    }

    /**
     * Concatenates the given URL components using the appropriate delimiters: {@link #SCHEME_DELIMITER},
     * {@link #AUTHORITY_DELIMITER}, {@link #USER_INFO_DELIMITER}, {@link #PORT_DELIMITER},
     * {@link #PATH_SEGMENT_DELIMITER}, {@link #QUERY_DELIMITER}, and {@link #FRAGMENT_DELIMITER}.
     *
     * @param scheme   the scheme
     * @param userInfo the user info
     * @param host     the host
     * @param port     the port
     * @param path     the path
     * @param query    the query
     * @param fragment the fragment
     *
     * @return the concatenated {@link String}
     */
    public static String concatComponents(final String scheme, final @Nullable String userInfo, final String host,
            final @Nullable Integer port, final String path, final @Nullable String query,
            final @Nullable String fragment) {
        final var string = new StringBuilder();
        string.append(scheme).append(SCHEME_DELIMITER).append(AUTHORITY_DELIMITER);
        if (userInfo != null) {
            string.append(userInfo).append(USER_INFO_DELIMITER);
        }
        string.append(host);
        if (port != null) {
            string.append(PORT_DELIMITER).append(port);
        }
        if ((!path.equals(PATH_SEGMENT_DELIMITER) && !path.isEmpty()) || query != null || fragment != null) {
            if (path.startsWith(PATH_SEGMENT_DELIMITER)) {
                string.append(path);
            } else {
                string.append(PATH_SEGMENT_DELIMITER_CHAR).append(path);
            }
        }
        if (query != null) {
            string.append(QUERY_DELIMITER).append(query);
        }
        if (fragment != null) {
            string.append(FRAGMENT_DELIMITER).append(fragment);
        }
        return string.toString();
    }

    /**
     * Parses the given <code>encodedUrl</code> into a {@link Url}.
     *
     * @param encodedUrl the encoded URL {@link String}. Note: {@link #requireVisibleAsciiChars(String)} is called
     *                   for each
     *                   URL component after parsing.
     *
     * @return the {@link Url}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     * @see #toString()
     */
    public static Url parse(final String encodedUrl) throws IllegalArgumentException {
        return new Url(URI.create(encodedUrl));
    }

    /**
     * Creates a {@link Url} from the given Java {@link URI}.
     *
     * @param javaUri the Java {@link URI}
     *
     * @return the {@link Url}
     *
     * @throws IllegalArgumentException thrown upon invalid {@link URI} values during the conversion process
     */
    public static Url fromJava(final URI javaUri) throws IllegalArgumentException {
        return new Url(javaUri);
    }

    /**
     * Creates a {@link Url} {@link Builder}.
     *
     * @return the {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link Builder} is a builder class for {@link Url}.
     *
     * @see #builder()
     */
    public static final class Builder {

        private String scheme;
        private @Nullable String encodedUserInfo;
        private String host;
        private @Nullable Integer port;
        private StringBuilder encodedPath;
        private @Nullable StringBuilder encodedQuery;
        private @Nullable String encodedFragment;

        private Builder() {
            scheme = "";
            host = "";
            encodedPath = new StringBuilder(PATH_SEGMENT_DELIMITER);
        }

        private Builder(final Url url) {
            scheme = url.getScheme();
            encodedUserInfo = url.getEncodedUserInfo();
            host = url.getHost();
            port = url.getPort();
            encodedPath = new StringBuilder(url.getEncodedPath());
            final var urlEncodedQuery = url.getEncodedQuery();
            encodedQuery = urlEncodedQuery == null ? null : new StringBuilder(urlEncodedQuery);
            encodedFragment = url.getEncodedFragment();
        }

        /**
         * @see #getScheme()
         */
        public Builder scheme(final String scheme) {
            this.scheme = scheme;
            return this;
        }

        /**
         * @return {@link #scheme(String)} {@link Scheme#toString()}
         *
         * @see #getSchemeEnum()
         */
        public Builder scheme(final Scheme scheme) {
            return scheme(scheme.toString());
        }

        /**
         * @see #getEncodedUserInfo()
         */
        public Builder encodedUserInfo(final @Nullable String encodedUserInfo) {
            this.encodedUserInfo = encodedUserInfo;
            return this;
        }

        /**
         * @return {@link #encodedUserInfo(String)} {@link #encode(String)}
         */
        public Builder userInfo(final @Nullable String userInfo) {
            return encodedUserInfo(userInfo == null ? null : encode(userInfo));
        }

        /**
         * @see #getHost()
         */
        public Builder host(final String host) {
            this.host = host;
            return this;
        }

        /**
         * @see #getPort()
         */
        public Builder port(final @Nullable Integer port) {
            this.port = port;
            return this;
        }

        /**
         * @see #getEncodedPath()
         */
        public Builder encodedPath(final @Nullable String encodedPath) {
            this.encodedPath = new StringBuilder(encodedPath == null ? PATH_SEGMENT_DELIMITER : encodedPath);
            return this;
        }

        /**
         * @return {@link #encodedPath(String)} {@link #encodePath(String)}
         */
        public Builder path(final @Nullable String path) {
            return encodedPath(path == null ? null : encodePath(path));
        }

        /**
         * Appends the given <code>encodedPathSegment</code> to the existing {@link #encodedPath(String)}. A
         * {@link #PATH_SEGMENT_DELIMITER} is prefixed if the existing {@link #encodedPath(String)} doesn't end with
         * {@link #PATH_SEGMENT_DELIMITER}.
         */
        public Builder addEncodedPathSegment(final String encodedPathSegment) {
            if (!encodedPathSegment.startsWith(PATH_SEGMENT_DELIMITER) &&
                    encodedPath.charAt(encodedPath.length() - 1) != PATH_SEGMENT_DELIMITER_CHAR) {
                encodedPath.append(PATH_SEGMENT_DELIMITER_CHAR);
            }
            encodedPath.append(encodedPathSegment);
            return this;
        }

        /**
         * @return {@link #addEncodedPathSegment(String)} {@link #encode(String)}
         */
        public Builder addPathSegment(final String pathSegment) {
            return addEncodedPathSegment(encode(pathSegment));
        }

        /**
         * @see #addEncodedPathSegment(String)
         */
        public Builder addEncodedPathSegments(final String encodedPathSegments) {
            return addEncodedPathSegment(encodedPathSegments);
        }

        /**
         * @param pathSegments the path segments (leading and trailing {@link #PATH_SEGMENT_DELIMITER}s are unnecessary)
         *
         * @return {@link #addEncodedPathSegments(String)} {@link #encodePath(String)}
         */
        public Builder addPathSegments(final String pathSegments) {
            return addEncodedPathSegments(encodePath(pathSegments));
        }

        /**
         * @see #getEncodedQuery()
         */
        public Builder encodedQuery(final @Nullable String encodedQuery) {
            this.encodedQuery = encodedQuery == null ? null : new StringBuilder(encodedQuery);
            return this;
        }

        /**
         * @return {@link #encodedQuery(String)} {@link #encode(String)}
         */
        public Builder query(final @Nullable String query) {
            return encodedQuery(query == null ? null : encode(query));
        }

        /**
         * Appends the concatenation of the given <code>encodedKey</code>, {@link #QUERY_KEY_VALUE_DELIMITER}, and the
         * given <code>encodedValue</code> to the existing {@link #query(String)}. A
         * {@link #QUERY_PARAMETER_DELIMITER} is prefixed if there is an existing {@link #query(String)}.
         */
        public Builder addEncodedQueryParameter(final String encodedKey, final String encodedValue) {
            if (encodedQuery == null) {
                encodedQuery = new StringBuilder();
            }
            if (!encodedQuery.isEmpty()) {
                encodedQuery.append(QUERY_PARAMETER_DELIMITER);
            }
            encodedQuery.append(encodedKey).append(QUERY_KEY_VALUE_DELIMITER).append(encodedValue);
            return this;
        }

        /**
         * @return {@link #addEncodedQueryParameter(String, String)} {@link #encode(String)}
         */
        public Builder addQueryParameter(final String key, final String value) {
            return addEncodedQueryParameter(encode(key), encode(value));
        }

        /**
         * @return {@link #addEncodedQueryParameters(Multimap)} {@link Multimaps#forMap(Map)}
         */
        public Builder addEncodedQueryParameters(final Map<String, String> encodedQueryParameters) {
            return addEncodedQueryParameters(Multimaps.forMap(encodedQueryParameters));
        }

        /**
         * {@link Multimap#forEach(BiConsumer)} with {@link #addEncodedQueryParameter(String, String)}.
         */
        public Builder addEncodedQueryParameters(final Multimap<String, String> encodedQueryParameters) {
            encodedQueryParameters.forEach(this::addEncodedQueryParameter);
            return this;
        }

        /**
         * @return {@link #addQueryParameters(Multimap)} {@link Multimaps#forMap(Map)}
         */
        public Builder addQueryParameters(final Map<String, String> queryParameters) {
            return addQueryParameters(Multimaps.forMap(queryParameters));
        }

        /**
         * @return {@link #addEncodedQueryParameters(Multimap)} {@link Multimap#entries()} {@link #encode(String)}
         */
        public Builder addQueryParameters(final Multimap<String, String> queryParameters) {
            return addEncodedQueryParameters(ImmutableListMultimap.copyOf(queryParameters.entries().stream()
                    .map(parameter -> entry(encode(parameter.getKey()), encode(parameter.getValue())))
                    .toList()));
        }

        /**
         * @see #getEncodedFragment()
         */
        public Builder encodedFragment(final @Nullable String encodedFragment) {
            this.encodedFragment = encodedFragment;
            return this;
        }

        /**
         * @return {@link #encodedFragment(String)} {@link #encode(String)}
         */
        public Builder fragment(final @Nullable String fragment) {
            return encodedFragment(fragment == null ? null : encode(fragment));
        }

        /**
         * @return the built {@link Url}
         *
         * @throws IllegalArgumentException thrown upon building failure
         */
        public Url build() throws IllegalArgumentException {
            return parse(concatComponents(scheme, encodedUserInfo, host, port,
                    encodedPath.toString(), encodedQuery == null ? null : encodedQuery.toString(), encodedFragment));
        }
    }

    private final String scheme;
    private final @Nullable String encodedUserInfo;
    private final String host;
    private final @Nullable Integer port;
    private final String encodedPath;
    private final @Nullable String encodedQuery;
    private final @Nullable String encodedFragment;
    private @Nullable Optional<Scheme> schemeEnum;
    private @Nullable String decodedUserInfo;
    private @Nullable Optional<InetAddress> hostAsInetAddress;
    private @Nullable Optional<InternetDomainName> hostAsDomainName;
    private @Nullable Optional<Integer> portOrDefault;
    private @Nullable Optional<Integer> customPort;
    private @Nullable String encodedAuthority;
    private @Nullable String decodedAuthority;
    private @Nullable String decodedPath;
    private @Nullable List<String> encodedPathSegments;
    private @Nullable List<String> pathSegments;
    private @Nullable String encodedNormalizedPath;
    private @Nullable String normalizedPath;
    private @Nullable List<String> encodedNormalizedPathSegments;
    private @Nullable List<String> normalizedPathSegments;
    private @Nullable String decodedQuery;
    private @Nullable ListMultimap<String, String> encodedQueryParameters;
    private @Nullable ListMultimap<String, String> decodedQueryParameters;
    private @Nullable String decodedFragment;
    private @Nullable String encodedPathQuery;
    private @Nullable String decodedPathQuery;
    private @Nullable String encodedPathQueryFragment;
    private @Nullable String decodedPathQueryFragment;
    private @Nullable String toEncodedString;
    private @Nullable String toDecodedString;

    // Use Java's `URI` instead of Jetty's `HttpURI` as it's more standardized.
    // Java's `URI` allows all components to be `null` and allows decoded UTF-8 characters in the path.
    private Url(final URI uri) throws IllegalArgumentException {
        final var uriScheme = uri.getScheme();
        checkArgument(uriScheme != null && !uriScheme.isEmpty(), "Invalid scheme");
        // `requireVisibleAsciiChars()` unnecessary since URI requires visible ASCII scheme
        scheme = uriScheme.toLowerCase(ROOT);

        final var uriUserInfo = uri.getRawUserInfo();
        if (uriUserInfo != null) {
            requireVisibleAsciiChars(uriUserInfo);
        }
        encodedUserInfo = uriUserInfo;

        final var uriHost = uri.getHost();
        checkArgument(uriHost != null && !uriHost.isEmpty(), "Invalid host");
        // `requireVisibleAsciiChars()` unnecessary since URI requires visible ASCII host
        host = uriHost.toLowerCase(ROOT);

        final var uriPort = uri.getPort();
        port = uriPort > 0 ? uriPort : null;

        final var uriPath = uri.getRawPath();
        if (uriPath != null) {
            requireVisibleAsciiChars(uriPath);
        }
        encodedPath = uriPath == null ? PATH_SEGMENT_DELIMITER :
                !uriPath.startsWith(PATH_SEGMENT_DELIMITER) ? PATH_SEGMENT_DELIMITER + uriPath : uriPath;

        final var uriQuery = uri.getRawQuery();
        if (uriQuery != null) {
            requireVisibleAsciiChars(uriQuery);
        }
        encodedQuery = uriQuery;

        final var uriFragment = uri.getRawFragment();
        if (uriFragment != null) {
            requireVisibleAsciiChars(uriFragment);
        }
        encodedFragment = uriFragment;
    }

    /**
     * @return the scheme
     *
     * @see #SCHEME_DELIMITER
     */
    @EqualsAndHashCode.Include
    public String getScheme() {
        return scheme;
    }

    /**
     * @return internally-cached {@link Scheme#forString(String)} {@link #getScheme()}
     */
    public @Nullable Scheme getSchemeEnum() {
        if (schemeEnum == null) {
            schemeEnum = Optional.ofNullable(Scheme.forString(getScheme()));
        }
        return schemeEnum.orElse(null);
    }

    /**
     * @return the encoded user info
     *
     * @see #USER_INFO_DELIMITER
     */
    public @Nullable String getEncodedUserInfo() {
        return encodedUserInfo;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedUserInfo()}
     */
    @EqualsAndHashCode.Include
    public @Nullable String getUserInfo() {
        if (decodedUserInfo == null) {
            final var encodedUserInfo = getEncodedUserInfo();
            if (encodedUserInfo != null) {
                decodedUserInfo = decode(encodedUserInfo);
            }
        }
        return decodedUserInfo;
    }

    /**
     * @return the host
     */
    @EqualsAndHashCode.Include
    public String getHost() {
        return host;
    }

    /**
     * @return internally-cached {@link InetAddresses#forUriString(String)} {@link #getHost()}, or <code>null</code>
     * upon {@link IllegalArgumentException}
     */
    public @Nullable InetAddress getHostAsInetAddress() {
        if (hostAsInetAddress == null) {
            try {
                hostAsInetAddress = Optional.of(InetAddresses.forUriString(getHost()));
            } catch (final IllegalArgumentException illegalArgumentException) {
                hostAsInetAddress = Optional.empty();
            }
        }
        return hostAsInetAddress.orElse(null);
    }

    /**
     * @return internally-cached {@link InternetDomainName#from(String)} {@link #getHost()}, or <code>null</code> upon
     * {@link IllegalArgumentException}
     */
    public @Nullable InternetDomainName getHostAsDomainName() {
        if (hostAsDomainName == null) {
            try {
                hostAsDomainName = Optional.of(InternetDomainName.from(getHost()));
            } catch (final IllegalArgumentException illegalArgumentException) {
                hostAsDomainName = Optional.empty();
            }
        }
        return hostAsDomainName.orElse(null);
    }

    /**
     * @return the port
     *
     * @see #PORT_DELIMITER
     */
    public @Nullable Integer getPort() {
        return port;
    }

    /**
     * @return internally-cached {@link #getPort()} if non-<code>null</code>, or {@link #getSchemeEnum()}
     * {@link Scheme#getDefaultPort()}
     */
    public @Nullable Integer getPortOrDefault() {
        if (portOrDefault == null) {
            final var port = getPort();
            if (port != null) {
                portOrDefault = Optional.of(port);
            } else {
                final var schemeEnum = getSchemeEnum();
                portOrDefault = Optional.ofNullable(schemeEnum == null ? null : schemeEnum.getDefaultPort());
            }
        }
        return portOrDefault.orElse(null);
    }

    /**
     * @return internally-cached <code>null</code> if <code>{@link #getPort()} == null</code> or if
     * <code>{@link #getSchemeEnum()} != null</code> and
     * <code>{@link #getPort()} == {@link Scheme#getDefaultPort()}</code>,
     * {@link #getPort()} otherwise
     */
    @EqualsAndHashCode.Include
    public @Nullable Integer getCustomPort() {
        if (customPort == null) {
            final var port = getPort();
            if (port == null) {
                customPort = Optional.empty();
            } else {
                final var schemeEnum = getSchemeEnum();
                customPort = Optional.ofNullable(
                        schemeEnum != null && port.equals(schemeEnum.getDefaultPort()) ? null : port);
            }
        }
        return customPort.orElse(null);
    }

    /**
     * @return internally-cached concatenation of {@link #getEncodedUserInfo()}, {@link #USER_INFO_DELIMITER},
     * {@link #getHost()}, {@link #PORT_DELIMITER}, {@link #getPort()}
     *
     * @see #AUTHORITY_DELIMITER
     */
    public String getEncodedAuthority() {
        if (encodedAuthority == null) {
            final var encodedAuthority = new StringBuilder();
            final var encodedUserInfo = getEncodedUserInfo();
            if (encodedUserInfo != null) {
                encodedAuthority.append(encodedUserInfo).append(USER_INFO_DELIMITER);
            }
            encodedAuthority.append(getHost());
            final var port = getPort();
            if (port != null) {
                encodedAuthority.append(PORT_DELIMITER).append(port);
            }
            this.encodedAuthority = encodedAuthority.toString();
        }
        return encodedAuthority;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedAuthority()}
     */
    public String getAuthority() {
        if (decodedAuthority == null) {
            decodedAuthority = decode(getEncodedAuthority());
        }
        return decodedAuthority;
    }

    /**
     * @return the encoded path
     *
     * @see #PATH_SEGMENT_DELIMITER
     */
    public String getEncodedPath() {
        return encodedPath;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedPath()}
     */
    public String getPath() {
        if (decodedPath == null) {
            decodedPath = decode(getEncodedPath());
        }
        return decodedPath;
    }

    /**
     * @return internally-cached {@link #encodedPathSegmentsToList(String)} {@link #getEncodedPath()}
     */
    public List<String> getEncodedPathSegments() {
        if (encodedPathSegments == null) {
            encodedPathSegments = encodedPathSegmentsToList(getEncodedPath());
        }
        return encodedPathSegments;
    }

    /**
     * @return internally-cached {@link #encodedPathSegmentsToDecodedList(String)} {@link #getEncodedPath()}
     */
    public List<String> getPathSegments() {
        if (pathSegments == null) {
            pathSegments = encodedPathSegmentsToDecodedList(getEncodedPath());
        }
        return pathSegments;
    }

    /**
     * @return internally-cached {@link #normalizeEncodedPath(String)} {@link #getEncodedPath()}
     */
    public String getEncodedNormalizedPath() {
        if (encodedNormalizedPath == null) {
            encodedNormalizedPath = normalizeEncodedPath(getEncodedPath());
        }
        return encodedNormalizedPath;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedNormalizedPath()}
     */
    public String getNormalizedPath() {
        if (normalizedPath == null) {
            normalizedPath = decode(getEncodedNormalizedPath());
        }
        return normalizedPath;
    }

    /**
     * @return internally-cached {@link #encodedPathSegmentsToList(String)} {@link #getEncodedNormalizedPath()}
     */
    public List<String> getEncodedNormalizedPathSegments() {
        if (encodedNormalizedPathSegments == null) {
            encodedNormalizedPathSegments = encodedPathSegmentsToList(getEncodedNormalizedPath());
        }
        return encodedNormalizedPathSegments;
    }

    /**
     * @return internally-cached {@link #encodedPathSegmentsToDecodedList(String)} {@link #getEncodedNormalizedPath()}
     */
    @EqualsAndHashCode.Include
    public List<String> getNormalizedPathSegments() {
        if (normalizedPathSegments == null) {
            normalizedPathSegments = encodedPathSegmentsToDecodedList(getEncodedNormalizedPath());
        }
        return normalizedPathSegments;
    }

    /**
     * @return the encoded query
     *
     * @see #QUERY_DELIMITER
     */
    public @Nullable String getEncodedQuery() {
        return encodedQuery;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedQuery()}
     */
    public @Nullable String getQuery() {
        if (decodedQuery == null) {
            final var encodedQuery = getEncodedQuery();
            if (encodedQuery != null) {
                decodedQuery = decode(encodedQuery);
            }
        }
        return decodedQuery;
    }

    /**
     * @return internally-cached {@link #parseEncodedQueryParameters(String)} {@link #getEncodedQuery()}
     *
     * @see #QUERY_PARAMETER_DELIMITER
     * @see #QUERY_KEY_VALUE_DELIMITER
     */
    public ListMultimap<String, String> getEncodedQueryParameters() {
        if (encodedQueryParameters == null) {
            encodedQueryParameters = parseEncodedQueryParameters(getEncodedQuery());
        }
        return encodedQueryParameters;
    }

    /**
     * @return internally-cached {@link #decodeParsedEncodedQueryParameters(ListMultimap)}
     * {@link #getEncodedQueryParameters()}
     */
    @EqualsAndHashCode.Include
    public ListMultimap<String, String> getQueryParameters() {
        if (decodedQueryParameters == null) {
            decodedQueryParameters = decodeParsedEncodedQueryParameters(getEncodedQueryParameters());
        }
        return decodedQueryParameters;
    }

    /**
     * @return {@link #getEncodedQueryParameters()} {@link ListMultimap#keys()}
     */
    public Multiset<String> getEncodedQueryKeys() {
        return getEncodedQueryParameters().keys();
    }

    /**
     * @return {@link #getQueryParameters()} {@link ListMultimap#keys()}
     */
    public Multiset<String> getQueryKeys() {
        return getQueryParameters().keys();
    }

    /**
     * @param key the key
     *
     * @return {@link #getEncodedQueryParameters()} {@link ListMultimap#get(Object)}
     */
    public List<String> getEncodedQueryValues(final String key) {
        return getEncodedQueryParameters().get(key);
    }

    /**
     * @param key the key
     *
     * @return {@link #getQueryParameters()} {@link ListMultimap#get(Object)}
     */
    public List<String> getQueryValues(final String key) {
        return getQueryParameters().get(key);
    }

    /**
     * @param key the key
     *
     * @return {@link #getEncodedQueryValues(String)} {@link List#getFirst()}, or <code>null</code>
     */
    public @Nullable String getEncodedQueryValue(final String key) {
        final var encodedValues = getEncodedQueryValues(key);
        return encodedValues.isEmpty() ? null : encodedValues.getFirst();
    }

    /**
     * @param key the key
     *
     * @return {@link #getQueryValues(String)} {@link List#getFirst()}, or <code>null</code>
     */
    public @Nullable String getQueryValue(final String key) {
        final var values = getQueryValues(key);
        return values.isEmpty() ? null : values.getFirst();
    }

    /**
     * @return the encoded fragment
     *
     * @see #FRAGMENT_DELIMITER
     */
    public @Nullable String getEncodedFragment() {
        return encodedFragment;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedFragment()}
     */
    @EqualsAndHashCode.Include
    public @Nullable String getFragment() {
        if (decodedFragment == null) {
            final var encodedFragment = getEncodedFragment();
            if (encodedFragment != null) {
                decodedFragment = decode(encodedFragment);
            }
        }
        return decodedFragment;
    }

    /**
     * @return internally-cached concatenation of {@link #getEncodedPath()}, {@link #QUERY_DELIMITER}, and
     * {@link #getEncodedQuery()}
     */
    public String getEncodedPathQuery() {
        if (encodedPathQuery == null) {
            final var encodedPathQuery = new StringBuilder(getEncodedPath());
            final var encodedQuery = getEncodedQuery();
            if (encodedQuery != null) {
                encodedPathQuery.append(QUERY_DELIMITER).append(encodedQuery);
            }
            this.encodedPathQuery = encodedPathQuery.toString();
        }
        return encodedPathQuery;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedPathQuery()}
     */
    public String getPathQuery() {
        if (decodedPathQuery == null) {
            decodedPathQuery = decode(getEncodedPathQuery());
        }
        return decodedPathQuery;
    }

    /**
     * @return internally-cached concatenation of {@link #getEncodedPath()}, {@link #QUERY_DELIMITER},
     * {@link #getEncodedQuery()}, {@link #FRAGMENT_DELIMITER}, and {@link #getEncodedFragment()}
     */
    public String getEncodedPathQueryFragment() {
        if (encodedPathQueryFragment == null) {
            final var encodedPathQueryFragment = new StringBuilder(getEncodedPath());
            final var encodedQuery = getEncodedQuery();
            if (encodedQuery != null) {
                encodedPathQueryFragment.append(QUERY_DELIMITER).append(encodedQuery);
            }
            final var encodedFragment = getEncodedFragment();
            if (encodedFragment != null) {
                encodedPathQueryFragment.append(FRAGMENT_DELIMITER).append(encodedFragment);
            }
            this.encodedPathQueryFragment = encodedPathQueryFragment.toString();
        }
        return encodedPathQueryFragment;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #getEncodedPathQueryFragment()}
     */
    public String getPathQueryFragment() {
        if (decodedPathQueryFragment == null) {
            decodedPathQueryFragment = decode(getEncodedPathQueryFragment());
        }
        return decodedPathQueryFragment;
    }

    /**
     * @return {@link URI#create(String)} {@link #toString()}
     *
     * @see #fromJava(URI)
     */
    public URI toJava() {
        return URI.create(toString());
    }

    /**
     * @return copies this {@link Url} into a {@link Builder}
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * @return internally-cached
     * {@link #concatComponents(String, String, String, Integer, String, String, String)} with
     * {@link #getScheme()}, {@link #getEncodedUserInfo()}, {@link #getHost()}, {@link #getCustomPort()},
     * {@link #getEncodedNormalizedPath()}, {@link #getEncodedQuery()}, and {@link #getEncodedFragment()}
     */
    public String toEncodedString() {
        if (toEncodedString == null) {
            toEncodedString = concatComponents(getScheme(), getEncodedUserInfo(), getHost(), getCustomPort(),
                    getEncodedNormalizedPath(), getEncodedQuery(), getEncodedFragment());
        }
        return toEncodedString;
    }

    /**
     * @return internally-cached {@link #decode(String)} {@link #toEncodedString()}
     */
    public String toDecodedString() {
        if (toDecodedString == null) {
            toDecodedString = decode(toEncodedString());
        }
        return toDecodedString;
    }

    /**
     * @see #toEncodedString()
     */
    @Override
    public String toString() {
        return toEncodedString();
    }
}
