package net.jacobpeterson.jet.common.http.url;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ROOT;
import static java.util.Map.entry;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link Url} is an immutable class that represents a standardized Uniform Resource Locator (URL), the most common
 * subtype of a Uniform Resource Identifier (URI), requiring a scheme, host, and path that always starts with
 * <code>/</code>.
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
 * <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes">Schemes</a> -
 * The first part of the URI, before the <code>:</code> character, which indicates the protocol the browser must use to
 * fetch the resource.
 * </li>
 * <li>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Authority">Authority</a> -
 * The section that comes after the scheme and before the path. It may have up to three parts: <code>user</code>
 * information, <code>host</code>, and <code>port</code>.
 * </li>
 * <li>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Path">Path</a> -
 * The section after the authority. Contains data, usually organized in hierarchical form, to identify a resource within
 * the scope of the URI's scheme and authority.
 * </li>
 * <li>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Query">Query</a> -
 * The section after the path. Contains non-hierarchical data to identify a resource within the scope of the URI's
 * scheme and naming authority along with data in the path component.
 * </li>
 * <li>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Fragment">Fragment</a> -
 * An optional part at the end of a URI starting with a <code>#</code> character. It is used to identify a specific part
 * of the resource, such as a section of a document or a position in a video.
 * </li>
 * </ul>
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI">developer.mozilla.org</a>
 */
@NullMarked
@Immutable
@EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
@SuppressWarnings({"LombokGetterMayBeUsed", "OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class Url {

    /**
     * The {@link #getScheme()} delimiter: <code>":"</code>
     */
    public static final String SCHEME_DELIMITER = ":";

    /**
     * The {@link #getAuthority()} delimiter: <code>"//"</code>
     */
    public static final String AUTHORITY_DELIMITER = "//";

    /**
     * The {@link #getUserInfo()} delimiter: <code>"@"</code>
     */
    public static final String USER_INFO_DELIMITER = "@";

    /**
     * The {@link #getPort()} delimiter: <code>":"</code>
     */
    public static final String PORT_DELIMITER = ":";

    /**
     * The {@link #getPathSegments()} delimiter: <code>"/"</code>
     */
    public static final String PATH_SEGMENT_DELIMITER = "/";
    private static final char PATH_SEGMENT_DELIMITER_CHAR = PATH_SEGMENT_DELIMITER.charAt(0);

    /**
     * The {@link #getQuery()} delimiter: <code>"?"</code>
     */
    public static final String QUERY_DELIMITER = "?";

    /**
     * The {@link #getQueryParameters()} delimiter: <code>"&amp;"</code>
     */
    public static final String QUERY_PARAMETER_DELIMITER = "&";

    /**
     * The {@link #getQueryParameters()} key-value delimiter: <code>"="</code>
     */
    public static final String QUERY_KEY_VALUE_DELIMITER = "=";

    /**
     * The {@link #getFragment()} delimiter: <code>"#"</code>
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

    private static final Splitter ENCODED_PATH_SEGMENTS_TO_STREAM_SPLITTER =
            Splitter.on(PATH_SEGMENT_DELIMITER).omitEmptyStrings();
    private static final Splitter PARSE_ENCODED_QUERY_PARAMETERS_PARAMETER_SPLITTER =
            Splitter.on(QUERY_PARAMETER_DELIMITER);
    private static final Splitter PARSE_ENCODED_QUERY_PARAMETERS_KEY_VALUE_SPLITTER =
            Splitter.on(QUERY_KEY_VALUE_DELIMITER).limit(2);

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
            checkArgument(charAt >= VISIBLE_ASCII_MINIMUM && charAt <= VISIBLE_ASCII_MAXIMUM,
                    "Non-visible ASCII char found at index: %s", index);
        }
    }

    /**
     * Removes all prepended {@link #PATH_SEGMENT_DELIMITER}s from the given <code>path</code>.
     *
     * @param path the path
     *
     * @return the trimmed path
     */
    public static String pathTrimLeading(final String path) {
        var startIndex = 0;
        final var endIndex = path.length() - 1;
        while (startIndex <= endIndex && path.charAt(startIndex) == PATH_SEGMENT_DELIMITER_CHAR) {
            startIndex++;
        }
        return path.substring(startIndex, endIndex + 1);
    }

    /**
     * Removes all appended {@link #PATH_SEGMENT_DELIMITER}s from the given <code>path</code>.
     *
     * @param path the path
     *
     * @return the trimmed path
     */
    public static String pathTrimTrailing(final String path) {
        final var startIndex = 0;
        var endIndex = path.length() - 1;
        while (endIndex >= startIndex && path.charAt(endIndex) == PATH_SEGMENT_DELIMITER_CHAR) {
            endIndex--;
        }
        return path.substring(startIndex, endIndex + 1);
    }

    /**
     * @return {@link #pathTrimLeading(String)} {@link #pathTrimTrailing(String)}
     */
    public static String pathTrim(final String path) {
        return pathTrimLeading(pathTrimTrailing(path));
    }

    /**
     * @param encodedPath the encoded path
     *
     * @return {@link Splitter#on(String)} {@link #PATH_SEGMENT_DELIMITER} {@link Splitter#omitEmptyStrings()}
     */
    public static ImmutableList<String> encodedPathSegmentsToList(final String encodedPath) {
        return ENCODED_PATH_SEGMENTS_TO_STREAM_SPLITTER.splitToStream(encodedPath).collect(toImmutableList());
    }

    /**
     * @param encodedPath the encoded path
     *
     * @return {@link Splitter#on(String)} {@link #PATH_SEGMENT_DELIMITER} {@link Splitter#omitEmptyStrings()}
     * {@link #decode(String)}
     */
    public static ImmutableList<String> encodedPathSegmentsToDecodedList(final String encodedPath) {
        return ENCODED_PATH_SEGMENTS_TO_STREAM_SPLITTER.splitToStream(encodedPath)
                .map(Url::decode)
                .collect(toImmutableList());
    }

    /**
     * Normalizes the given <code>encodedPath</code> with the following process:
     * <ol>
     * <li>Collapse sequential {@link #PATH_SEGMENT_DELIMITER}s (e.g. <code>/a/b///</code> to <code>/a/b/</code>)</li>
     * <li>Resolve relative paths (e.g. <code>/a/b/..</code> to <code>/a</code>)</li>
     * <li>{@link #pathTrimTrailing(String)} (unless the previous steps result in
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
        return normalized.equals(PATH_SEGMENT_DELIMITER) ? PATH_SEGMENT_DELIMITER : pathTrimTrailing(normalized);
    }

    /**
     * Parses the given <code>encodedQuery</code> into an {@link ImmutableListMultimap} of query parameters. Query
     * parameters with an empty key are omitted.
     *
     * @param encodedQuery the encoded query (without the leading {@link #QUERY_DELIMITER})
     *
     * @return the query parameters {@link ImmutableListMultimap}
     *
     * @see #QUERY_PARAMETER_DELIMITER
     * @see #QUERY_KEY_VALUE_DELIMITER
     */
    public static ImmutableListMultimap<String, String> parseEncodedQueryParameters(
            final @Nullable String encodedQuery) {
        return encodedQuery == null ? ImmutableListMultimap.of() : PARSE_ENCODED_QUERY_PARAMETERS_PARAMETER_SPLITTER
                .splitToStream(encodedQuery)
                .map(PARSE_ENCODED_QUERY_PARAMETERS_KEY_VALUE_SPLITTER::splitToList)
                .filter(keyValue -> !keyValue.getFirst().isEmpty())
                .collect(toImmutableListMultimap(List::getFirst,
                        keyValue -> keyValue.size() == 1 ? "" : keyValue.get(1)));
    }

    /**
     * @param encodedQueryParameters {@link #parseEncodedQueryParameters(String)}
     *
     * @return {@link ListMultimap#entries()} {@link #decode(String)}
     */
    public static ImmutableListMultimap<String, String> decodeParsedEncodedQueryParameters(
            final ListMultimap<String, String> encodedQueryParameters) {
        return ImmutableListMultimap.copyOf(encodedQueryParameters.entries().stream()
                .map(parameter -> entry(decode(parameter.getKey()), decode(parameter.getValue())))
                .toList());
    }

    /**
     * @return {@link #decodeParsedEncodedQueryParameters(ListMultimap)} {@link #parseEncodedQueryParameters(String)}
     */
    public static ImmutableListMultimap<String, String> parseDecodeEncodedQueryParameters(
            final @Nullable String encodedQuery) {
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
     *                   for each URL component after parsing.
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
     * Creates a {@link Builder}.
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
        public Builder encodedUserInfo(final String encodedUserInfo) {
            this.encodedUserInfo = encodedUserInfo;
            return this;
        }

        /**
         * @return {@link #encodedUserInfo(String)} {@link #encode(String)}
         */
        public Builder userInfo(final String userInfo) {
            return encodedUserInfo(encode(userInfo));
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
        public Builder port(final int port) {
            this.port = port;
            return this;
        }

        /**
         * @see #getEncodedPath()
         */
        public Builder encodedPath(final String encodedPath) {
            this.encodedPath = new StringBuilder(encodedPath);
            return this;
        }

        /**
         * @return {@link #encodedPath(String)} {@link #encodePath(String)}
         */
        public Builder path(final String path) {
            return encodedPath(encodePath(path));
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
        public Builder encodedQuery(final String encodedQuery) {
            this.encodedQuery = new StringBuilder(encodedQuery);
            return this;
        }

        /**
         * @return {@link #encodedQuery(String)} {@link #encode(String)}
         */
        public Builder query(final String query) {
            return encodedQuery(encode(query));
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
        public Builder encodedFragment(final String encodedFragment) {
            this.encodedFragment = encodedFragment;
            return this;
        }

        /**
         * @return {@link #encodedFragment(String)} {@link #encode(String)}
         */
        public Builder fragment(final String fragment) {
            return encodedFragment(encode(fragment));
        }

        /**
         * Builds this {@link Builder} into a new {@link Url} instance.
         *
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
    private @LazyInit @Nullable Optional<Scheme> schemeEnum;
    private @LazyInit @Nullable String decodedUserInfo;
    private @LazyInit @Nullable Optional<InetAddress> hostAsInetAddress;
    private @LazyInit @Nullable Optional<InternetDomainName> hostAsDomainName;
    private @LazyInit @Nullable Optional<Integer> portOrDefault;
    private @LazyInit @Nullable Optional<Integer> customPort;
    private @LazyInit @Nullable String encodedAuthority;
    private @LazyInit @Nullable String decodedAuthority;
    private @LazyInit @Nullable String decodedPath;
    private @LazyInit @Nullable ImmutableList<String> encodedPathSegments;
    private @LazyInit @Nullable ImmutableList<String> pathSegments;
    private @LazyInit @Nullable String encodedNormalizedPath;
    private @LazyInit @Nullable String normalizedPath;
    private @LazyInit @Nullable ImmutableList<String> encodedNormalizedPathSegments;
    private @LazyInit @Nullable ImmutableList<String> normalizedPathSegments;
    private @LazyInit @Nullable String decodedQuery;
    private @LazyInit @Nullable ImmutableListMultimap<String, String> encodedQueryParameters;
    private @LazyInit @Nullable ImmutableListMultimap<String, String> decodedQueryParameters;
    private @LazyInit @Nullable String decodedFragment;
    private @LazyInit @Nullable String encodedPathQuery;
    private @LazyInit @Nullable String decodedPathQuery;
    private @LazyInit @Nullable String encodedPathQueryFragment;
    private @LazyInit @Nullable String decodedPathQueryFragment;
    private @LazyInit @Nullable String toEncodedString;
    private @LazyInit @Nullable String toDecodedString;

    // Use Java's `URI` instead of Jetty's `HttpURI` as it's more standardized.
    // Java's `URI` allows all components to be `null` and allows decoded UTF-8 characters in the path.
    private Url(final URI uri) throws IllegalArgumentException {
        final var uriScheme = uri.getScheme();
        checkArgument(uriScheme != null && !uriScheme.isEmpty(), "Invalid scheme: %s", uriScheme);
        // `requireVisibleAsciiChars()` unnecessary since URI requires visible ASCII scheme
        scheme = uriScheme.toLowerCase(ROOT);

        final var uriUserInfo = uri.getRawUserInfo();
        if (uriUserInfo != null) {
            requireVisibleAsciiChars(uriUserInfo);
        }
        encodedUserInfo = uriUserInfo;

        final var uriHost = uri.getHost();
        checkArgument(uriHost != null && !uriHost.isEmpty(), "Invalid host: %s", uriHost);
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
     * @return internally-cached <code>null</code> if {@link #getPort()} is <code>null</code> or if
     * <code>{@link #getSchemeEnum()}</code> is non-<code>null</code> and {@link #getPort()} equals
     * {@link Scheme#getDefaultPort()}, {@link #getPort()} otherwise
     */
    @EqualsAndHashCode.Include
    public @Nullable Integer getCustomPort() {
        if (customPort == null) {
            final var port = getPort();
            if (port == null) {
                customPort = Optional.empty();
            } else {
                final var schemeEnum = getSchemeEnum();
                customPort = Optional.ofNullable(schemeEnum != null &&
                        port.equals(schemeEnum.getDefaultPort()) ? null : port);
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
    public ImmutableList<String> getEncodedPathSegments() {
        if (encodedPathSegments == null) {
            encodedPathSegments = encodedPathSegmentsToList(getEncodedPath());
        }
        return encodedPathSegments;
    }

    /**
     * @return internally-cached {@link #encodedPathSegmentsToDecodedList(String)} {@link #getEncodedPath()}
     */
    public ImmutableList<String> getPathSegments() {
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
    @EqualsAndHashCode.Include
    public String getNormalizedPath() {
        if (normalizedPath == null) {
            normalizedPath = decode(getEncodedNormalizedPath());
        }
        return normalizedPath;
    }

    /**
     * @return internally-cached {@link #encodedPathSegmentsToList(String)} {@link #getEncodedNormalizedPath()}
     */
    public ImmutableList<String> getEncodedNormalizedPathSegments() {
        if (encodedNormalizedPathSegments == null) {
            encodedNormalizedPathSegments = encodedPathSegmentsToList(getEncodedNormalizedPath());
        }
        return encodedNormalizedPathSegments;
    }

    /**
     * @return internally-cached {@link #encodedPathSegmentsToDecodedList(String)} {@link #getEncodedNormalizedPath()}
     */
    public ImmutableList<String> getNormalizedPathSegments() {
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
    public ImmutableListMultimap<String, String> getEncodedQueryParameters() {
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
    public ImmutableListMultimap<String, String> getQueryParameters() {
        if (decodedQueryParameters == null) {
            decodedQueryParameters = decodeParsedEncodedQueryParameters(getEncodedQueryParameters());
        }
        return decodedQueryParameters;
    }

    /**
     * @return {@link #getEncodedQueryParameters()} {@link ListMultimap#keys()}
     */
    public ImmutableMultiset<String> getEncodedQueryKeys() {
        return getEncodedQueryParameters().keys();
    }

    /**
     * @return {@link #getQueryParameters()} {@link ListMultimap#keys()}
     */
    public ImmutableMultiset<String> getQueryKeys() {
        return getQueryParameters().keys();
    }

    /**
     * @param key the key
     *
     * @return {@link #getEncodedQueryParameters()} {@link ListMultimap#get(Object)}
     */
    public ImmutableList<String> getEncodedQueryValues(final String key) {
        return getEncodedQueryParameters().get(key);
    }

    /**
     * @param key the key
     *
     * @return {@link #getQueryParameters()} {@link ListMultimap#get(Object)}
     */
    public ImmutableList<String> getQueryValues(final String key) {
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
     * @return this {@link Url} copied into a new {@link Builder} instance
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
     * @return {@link #toEncodedString()}
     */
    @Override
    public String toString() {
        return toEncodedString();
    }
}
