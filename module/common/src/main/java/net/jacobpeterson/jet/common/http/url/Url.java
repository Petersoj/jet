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
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.UriCompliance.Violation;
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
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static org.eclipse.jetty.http.UriCompliance.Violation.BAD_PERCENT_ENCODING;
import static org.eclipse.jetty.http.UriCompliance.Violation.BAD_UTF8_ENCODING;
import static org.eclipse.jetty.http.UriCompliance.Violation.ILLEGAL_PATH_CHARACTERS;
import static org.eclipse.jetty.http.UriCompliance.Violation.SUSPICIOUS_PATH_CHARACTERS;
import static org.eclipse.jetty.http.UriCompliance.Violation.TRUNCATED_UTF8_ENCODING;

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
@SuppressWarnings("LombokGetterMayBeUsed")
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

    private static final Set<Violation> ERROR_VIOLATIONS = Set.of(
            BAD_UTF8_ENCODING,
            TRUNCATED_UTF8_ENCODING,
            BAD_PERCENT_ENCODING,
            SUSPICIOUS_PATH_CHARACTERS,
            ILLEGAL_PATH_CHARACTERS);

    /**
     * Calls {@link URLEncoder#encode(String, Charset)} with the given <code>decoded</code> and
     * {@link StandardCharsets#UTF_8}.
     */
    public static String encode(final String decoded) {
        return URLEncoder.encode(decoded, UTF_8);
    }

    /**
     * @return same as {@link #encode(String)}, but excludes encoding the {@link #PATH_SEGMENT_DELIMITER}
     */
    public static String encodePath(final String decodedPath) {
        return URIUtil.encodePath(decodedPath);
    }

    /**
     * Calls {@link URLDecoder#decode(String, Charset)} with the given <code>encoded</code> and
     * {@link StandardCharsets#UTF_8}.
     */
    public static String decode(final String encoded) {
        return URLDecoder.decode(encoded, UTF_8);
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
     * @return {@link Splitter#on(String)} {@link #PATH_SEGMENT_DELIMITER} {@link Splitter#omitEmptyStrings()}
     * {@link Splitter#splitToStream(CharSequence)}
     */
    public static Stream<String> pathSegmentsToStream(final String path) {
        return Splitter.on(PATH_SEGMENT_DELIMITER).omitEmptyStrings().splitToStream(path);
    }

    /**
     * @return {@link #pathSegmentsToStream(String)} {@link Stream#toList()}
     */
    public static List<String> pathSegmentsToList(final String path) {
        return pathSegmentsToStream(path).toList();
    }

    /**
     * @return {@link #pathSegmentsToStream(String)} {@link #decode(String)} {@link Stream#toList()}
     */
    public static List<String> decodePathSegmentsToList(final String encodedPath) {
        return pathSegmentsToStream(encodedPath).map(Url::decode).toList();
    }

    /**
     * @return {@link #pathSegmentsToStream(String)} {@link #encode(String)} {@link Stream#toList()}
     */
    public static List<String> encodePathSegmentsToList(final String decodedPath) {
        return pathSegmentsToStream(decodedPath).map(Url::encode).toList();
    }

    /**
     * Normalizes the given <code>encodedPath</code> by resolving relative paths (e.g. <code>/a/b/..</code> to
     * <code>/a</code>), collapsing sequential {@link #PATH_SEGMENT_DELIMITER}s to one
     * {@link #PATH_SEGMENT_DELIMITER} (e.g. <code>/a//</code> to <code>/a</code>), and
     * {@link #pathTrimTrailing(String)}.
     *
     * @param encodedPath the encoded path
     *
     * @return the normalized path
     */
    public static String normalizePath(final String encodedPath) {
        final var normalized = URIUtil.normalizePathQuery(encodedPath);
        return normalized != null ? pathTrimTrailing(URIUtil.compactPath(normalized)) :
                encodedPath.startsWith(PATH_SEGMENT_DELIMITER) ? PATH_SEGMENT_DELIMITER : "";
    }

    /**
     * Parses the given <code>query</code> into a {@link ListMultimap} of query parameters.
     *
     * @param query the query (without the leading {@link #QUERY_DELIMITER})
     *
     * @return the query parameters {@link ListMultimap}
     *
     * @see #QUERY_PARAMETER_DELIMITER
     * @see #QUERY_KEY_VALUE_DELIMITER
     */
    public static ListMultimap<String, String> parseQueryParameters(final @Nullable String query) {
        return query == null ? ImmutableListMultimap.of() : Splitter.on(QUERY_PARAMETER_DELIMITER).splitToStream(query)
                .map(parameter -> Splitter.on(QUERY_KEY_VALUE_DELIMITER).limit(2).splitToList(parameter))
                .collect(toImmutableListMultimap(List::getFirst,
                        keyValue -> keyValue.size() == 1 ? "" : keyValue.get(1)));
    }

    /**
     * @param queryParameters {@link #parseQueryParameters(String)}
     *
     * @return {@link ListMultimap#entries()} {@link #decode(String)}
     */
    public static ListMultimap<String, String> decodeParsedQueryParameters(
            final ListMultimap<String, String> queryParameters) {
        return ImmutableListMultimap.copyOf(queryParameters.entries().stream()
                .map(parameter -> entry(decode(parameter.getKey()), decode(parameter.getValue())))
                .toList());
    }

    /**
     * @return {@link #decodeParsedQueryParameters(ListMultimap)} {@link #parseQueryParameters(String)}
     */
    public static ListMultimap<String, String> parseQueryParametersDecode(final @Nullable String query) {
        return decodeParsedQueryParameters(parseQueryParameters(query));
    }

    /**
     * Concatenates the given arguments using the appropriate delimiters: {@link #SCHEME_DELIMITER},
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
    public static String concatStringComponents(final String scheme, final @Nullable String userInfo, final String host,
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
        if (!path.equals(PATH_SEGMENT_DELIMITER) || query != null || fragment != null) {
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
     * @param encodedUrl the encoded URL {@link String}
     *
     * @return the {@link Url}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     * @see #toString()
     */
    public static Url parse(final String encodedUrl) throws IllegalArgumentException {
        return new Url(encodedUrl);
    }

    /**
     * @return {@link #parse(String)} {@link URI#toASCIIString()}
     */
    public static Url fromJava(final URI javaUri) {
        return parse(javaUri.toASCIIString());
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
     * {@link Builder} is a reusable builder class for {@link Url}.
     */
    public static final class Builder {

        private String scheme;
        private @Nullable String userInfo;
        private String host;
        private @Nullable Integer port;
        private StringBuilder path;
        private @Nullable StringBuilder query;
        private @Nullable String fragment;

        private Builder() {
            scheme = "";
            host = "";
            path = new StringBuilder(PATH_SEGMENT_DELIMITER);
        }

        private Builder(final Url url) {
            scheme = url.getScheme();
            userInfo = url.getUserInfo();
            host = url.getHost();
            port = url.getPort();
            path = new StringBuilder(url.getPath());
            final var urlQuery = url.getQuery();
            query = urlQuery == null ? null : new StringBuilder(urlQuery);
            fragment = url.getFragment();
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
         * @return {@link #encodedUserInfo(String)} {@link #encode(String)}
         */
        public Builder userInfo(final @Nullable String userInfo) {
            return encodedUserInfo(userInfo == null ? null : encode(userInfo));
        }

        /**
         * @see #getUserInfo()
         */
        public Builder encodedUserInfo(final @Nullable String encodedUserInfo) {
            userInfo = encodedUserInfo;
            return this;
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
         * @return {@link #encodedPath(String)} {@link #encode(String)}
         */
        public Builder path(final String path) {
            return encodedPath(encode(path));
        }

        /**
         * @see #getPath()
         */
        public Builder encodedPath(final String encodedPath) {
            path = new StringBuilder(encodedPath);
            return this;
        }

        /**
         * @return {@link #addEncodedPathSegment(String)} {@link #encode(String)}
         */
        public Builder addPathSegment(final String pathSegment) {
            return addEncodedPathSegment(encode(pathSegment));
        }

        /**
         * Appends the given <code>encodedPathSegment</code> to the existing {@link #path(String)}. A
         * {@link #PATH_SEGMENT_DELIMITER} is prefixed if the existing {@link #path(String)} doesn't end with
         * {@link #PATH_SEGMENT_DELIMITER}.
         */
        public Builder addEncodedPathSegment(final String encodedPathSegment) {
            if (path.charAt(path.length() - 1) != PATH_SEGMENT_DELIMITER_CHAR) {
                path.append(PATH_SEGMENT_DELIMITER_CHAR);
            }
            path.append(encodedPathSegment);
            return this;
        }

        /**
         * @param pathSegments the path segments (without a leading or trailing {@link #PATH_SEGMENT_DELIMITER})
         *
         * @return {@link #addEncodedPathSegments(String)} {@link Url#encodePath(String)}
         */
        public Builder addPathSegments(final String pathSegments) {
            return addEncodedPathSegments(Url.encodePath(pathSegments));
        }

        /**
         * @see #addEncodedPathSegment(String)
         */
        public Builder addEncodedPathSegments(final String encodedPathSegments) {
            return addEncodedPathSegment(encodedPathSegments);
        }

        /**
         * @see #normalizePath(String)
         */
        public Builder normalizePath() {
            path = new StringBuilder(Url.normalizePath(path.toString()));
            return this;
        }

        /**
         * @return {@link #encodedQuery(String)} {@link #encode(String)}
         */
        public Builder query(final @Nullable String query) {
            return encodedQuery(query == null ? null : encode(query));
        }

        /**
         * @see #getQuery()
         */
        public Builder encodedQuery(final @Nullable String encodedQuery) {
            query = encodedQuery == null ? null : new StringBuilder(encodedQuery);
            return this;
        }

        /**
         * @return {@link #addEncodedQueryParameter(String, String)} {@link #encode(String)}
         */
        public Builder addQueryParameter(final String key, final String value) {
            return addEncodedQueryParameter(encode(key), encode(value));
        }

        /**
         * Appends the concatenation of the given <code>encodedKey</code>, {@link #QUERY_KEY_VALUE_DELIMITER}, and the
         * given <code>encodedValue</code> to the existing {@link #query(String)}. A
         * {@link #QUERY_PARAMETER_DELIMITER} is prefixed if there is an existing {@link #query(String)}.
         */
        public Builder addEncodedQueryParameter(final String encodedKey, final String encodedValue) {
            if (query == null) {
                query = new StringBuilder();
            }
            if (!query.isEmpty()) {
                query.append(QUERY_PARAMETER_DELIMITER);
            }
            query.append(encodedKey).append(QUERY_KEY_VALUE_DELIMITER).append(encodedValue);
            return this;
        }

        /**
         * @return {@link #addQueryParameters(Multimap)} {@link Multimaps#forMap(Map)}
         */
        public Builder addQueryParameters(final Map<String, String> queryParameters) {
            return addQueryParameters(Multimaps.forMap(queryParameters));
        }

        /**
         * @return {@link #addEncodedQueryParameters(Multimap)} {@link Multimaps#forMap(Map)}
         */
        public Builder addEncodedQueryParameters(final Map<String, String> encodedQueryParameters) {
            return addEncodedQueryParameters(Multimaps.forMap(encodedQueryParameters));
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
         * Calls {@link Multimap#forEach(BiConsumer)} with {@link #addEncodedQueryParameter(String, String)}.
         */
        public Builder addEncodedQueryParameters(final Multimap<String, String> encodedQueryParameters) {
            encodedQueryParameters.forEach(this::addEncodedQueryParameter);
            return this;
        }

        /**
         * @return {@link #encodedFragment(String)} {@link #encode(String)}
         */
        public Builder fragment(final @Nullable String fragment) {
            return encodedFragment(fragment == null ? null : encode(fragment));
        }

        /**
         * @see #getFragment()
         */
        public Builder encodedFragment(final @Nullable String encodedFragment) {
            this.fragment = encodedFragment;
            return this;
        }

        /**
         * @return the built {@link Url}
         *
         * @throws IllegalArgumentException thrown upon parsing failure
         */
        public Url build() throws IllegalArgumentException {
            return parse(concatStringComponents(scheme, userInfo, host, port,
                    path.toString(), query == null ? null : query.toString(), fragment));
        }
    }

    private final String scheme;
    private final @Nullable String userInfo;
    private final String host;
    private final @Nullable Integer port;
    private final String path;
    private final @Nullable String query;
    private final @Nullable String fragment;
    private final boolean ambiguous;
    private @SuppressWarnings("Immutable") @Nullable ListMultimap<String, String> queryParameters;
    private @SuppressWarnings("Immutable") @Nullable ListMultimap<String, String> decodedQueryParameters;
    private @SuppressWarnings("Immutable") @Nullable String toString;

    private Url(final String encodedUrl) throws IllegalArgumentException {
        final var httpUri = HttpURI.build(encodedUrl);
        for (final var violation : ERROR_VIOLATIONS) {
            if (httpUri.hasViolation(violation)) {
                throw new IllegalArgumentException(violation.getDescription());
            }
        }
        final var httpUriScheme = httpUri.getScheme();
        checkArgument(httpUriScheme != null, "Invalid scheme");
        scheme = httpUriScheme;
        userInfo = httpUri.getUser();
        final var httpUriHost = httpUri.getHost();
        checkArgument(httpUriHost != null, "Invalid host");
        host = httpUriHost;
        final var httpUriPort = httpUri.getPort();
        port = httpUriPort > 0 ? httpUriPort : null;
        final var httpUriPath = httpUri.getPath();
        path = httpUriPath == null ? PATH_SEGMENT_DELIMITER :
                !httpUriPath.startsWith(PATH_SEGMENT_DELIMITER) ? PATH_SEGMENT_DELIMITER + httpUriPath : httpUriPath;
        query = httpUri.getQuery();
        fragment = httpUri.getFragment();
        ambiguous = httpUri.isAmbiguous();
    }

    /**
     * @return the scheme
     *
     * @see #SCHEME_DELIMITER
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * @return {@link Scheme#forString(String)} {@link #getScheme()}
     */
    public @Nullable Scheme getSchemeEnum() {
        return Scheme.forString(getScheme());
    }

    /**
     * @return the user info
     *
     * @see #USER_INFO_DELIMITER
     */
    public @Nullable String getUserInfo() {
        return userInfo;
    }

    /**
     * @return {@link #decode(String)} {@link #getUserInfo()}
     */
    public @Nullable String getDecodedUserInfo() {
        final var userInfo = getUserInfo();
        return userInfo == null ? null : decode(userInfo);
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return {@link InetAddresses#forUriString(String)} {@link #getHost()}, or <code>null</code> upon
     * {@link IllegalArgumentException}
     */
    public @Nullable InetAddress getHostAsInetAddress() {
        try {
            return InetAddresses.forUriString(getHost());
        } catch (final IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    /**
     * @return {@link InternetDomainName#from(String)} {@link #getHost()}, or <code>null</code> upon
     * {@link IllegalArgumentException}
     */
    public @Nullable InternetDomainName getHostAsDomainName() {
        try {
            return InternetDomainName.from(getHost());
        } catch (final IllegalArgumentException illegalArgumentException) {
            return null;
        }
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
     * @return {@link #getPort()} if non-<code>null</code>, or {@link #getSchemeEnum()} {@link Scheme#getDefaultPort()}
     */
    public @Nullable Integer getPortOrDefault() {
        final var port = getPort();
        if (port != null) {
            return port;
        }
        final var scheme = getSchemeEnum();
        return scheme == null ? null : scheme.getDefaultPort();
    }

    /**
     * @return <code>null</code> if <code>{@link #getPort()} == null</code> or if {@link Scheme#forDefaultPort(int)} is
     * non-<code>null</code>, {@link #getPort()} otherwise
     */
    public @Nullable Integer getCustomPort() {
        final var port = getPort();
        if (port == null) {
            return null;
        }
        return Scheme.forDefaultPort(port) != null ? null : port;
    }

    /**
     * @return the authority (the concatenation of {@link #getUserInfo()}, {@link #USER_INFO_DELIMITER},
     * {@link #getHost()}, {@link #PORT_DELIMITER}, {@link #getPort()})
     *
     * @see #AUTHORITY_DELIMITER
     */
    public String getAuthority() {
        final var authority = new StringBuilder();
        final var userInfo = getUserInfo();
        if (userInfo != null) {
            authority.append(userInfo).append(USER_INFO_DELIMITER);
        }
        authority.append(getHost());
        final var port = getPort();
        if (port != null) {
            authority.append(PORT_DELIMITER).append(port);
        }
        return authority.toString();
    }

    /**
     * @return {@link #decode(String)} {@link #getAuthority()}
     */
    public String getDecodedAuthority() {
        return decode(getAuthority());
    }

    /**
     * @return the path
     *
     * @see #PATH_SEGMENT_DELIMITER
     */
    public String getPath() {
        return path;
    }

    /**
     * @return {@link #decode(String)} {@link #getPath()}
     */
    public String getDecodedPath() {
        return decode(getPath());
    }

    /**
     * @return {@link #pathSegmentsToList(String)} {@link #getPath()}
     */
    public List<String> getPathSegments() {
        return pathSegmentsToList(getPath());
    }

    /**
     * @return {@link #decodePathSegmentsToList(String)} {@link #getPath()}
     */
    public List<String> getDecodedPathSegments() {
        return decodePathSegmentsToList(getPath());
    }

    /**
     * @return {@link #normalizePath(String)} {@link #getPath()}
     */
    public String getNormalizedPath() {
        return normalizePath(getPath());
    }

    /**
     * @return {@link #decode(String)} {@link #getNormalizedPath()}
     */
    public String getDecodedNormalizedPath() {
        return decode(getNormalizedPath());
    }

    /**
     * @return {@link #pathSegmentsToList(String)} {@link #getNormalizedPath()}
     */
    public List<String> getNormalizedPathSegments() {
        return pathSegmentsToList(getNormalizedPath());
    }

    /**
     * @return {@link #decodePathSegmentsToList(String)} {@link #getNormalizedPath()}
     */
    public List<String> getDecodedNormalizedPathSegments() {
        return decodePathSegmentsToList(getNormalizedPath());
    }

    /**
     * @return the query
     *
     * @see #QUERY_DELIMITER
     */
    public @Nullable String getQuery() {
        return query;
    }

    /**
     * @return {@link #decode(String)} {@link #getQuery()}
     */
    public @Nullable String getDecodedQuery() {
        final var query = getQuery();
        return query == null ? null : decode(query);
    }

    /**
     * @return internally-cached {@link #parseQueryParameters(String)} {@link #getQuery()}
     *
     * @see #QUERY_PARAMETER_DELIMITER
     * @see #QUERY_KEY_VALUE_DELIMITER
     */
    public ListMultimap<String, String> getQueryParameters() {
        if (queryParameters == null) {
            queryParameters = parseQueryParameters(getQuery());
        }
        return queryParameters;
    }

    /**
     * @return internally-cached {@link #decodeParsedQueryParameters(ListMultimap)} {@link #getQueryParameters()}
     */
    public ListMultimap<String, String> getDecodedQueryParameters() {
        if (decodedQueryParameters == null) {
            decodedQueryParameters = decodeParsedQueryParameters(getQueryParameters());
        }
        return decodedQueryParameters;
    }

    /**
     * @return {@link #getQueryParameters()} {@link ListMultimap#keys()}
     */
    public Multiset<String> getQueryKeys() {
        return getQueryParameters().keys();
    }

    /**
     * @return {@link #getDecodedQueryParameters()} {@link ListMultimap#keys()}
     */
    public Multiset<String> getDecodedQueryKeys() {
        return getDecodedQueryParameters().keys();
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
     * @return {@link #getDecodedQueryParameters()} {@link ListMultimap#get(Object)}
     */
    public List<String> getDecodedQueryValues(final String key) {
        return getDecodedQueryParameters().get(key);
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
     * @param key the key
     *
     * @return {@link #getDecodedQueryValues(String)} {@link List#getFirst()}, or <code>null</code>
     */
    public @Nullable String getDecodedQueryValue(final String key) {
        final var values = getDecodedQueryValues(key);
        return values.isEmpty() ? null : values.getFirst();
    }

    /**
     * @return the fragment
     *
     * @see #FRAGMENT_DELIMITER
     */
    public @Nullable String getFragment() {
        return fragment;
    }

    /**
     * @return {@link #decode(String)} {@link #getFragment()}
     */
    public @Nullable String getDecodedFragment() {
        final var fragment = getFragment();
        return fragment == null ? null : decode(fragment);
    }

    /**
     * @return the concatenation of {@link #getPath()}, {@link #QUERY_DELIMITER}, and {@link #getQuery()}
     */
    public String getPathQuery() {
        final var pathQuery = new StringBuilder(getPath());
        final var query = getQuery();
        if (query != null) {
            pathQuery.append(QUERY_DELIMITER).append(query);
        }
        return pathQuery.toString();
    }

    /**
     * @return {@link #decode(String)} {@link #getPathQuery()}
     */
    public String getDecodedPathQuery() {
        return decode(getPathQuery());
    }

    /**
     * @return the concatenation of {@link #getPath()}, {@link #QUERY_DELIMITER}, {@link #getQuery()},
     * {@link #FRAGMENT_DELIMITER}, and {@link #getFragment()}
     */
    public String getPathQueryFragment() {
        final var pathQueryFragment = new StringBuilder(getPath());
        final var query = getQuery();
        if (query != null) {
            pathQueryFragment.append(QUERY_DELIMITER).append(query);
        }
        final var fragment = getFragment();
        if (fragment != null) {
            pathQueryFragment.append(FRAGMENT_DELIMITER).append(fragment);
        }
        return pathQueryFragment.toString();
    }

    /**
     * @return {@link #decode(String)} {@link #getPathQueryFragment()}
     */
    public String getDecodedPathQueryFragment() {
        return decode(getPathQueryFragment());
    }

    /**
     * @return <code>true</code> if this {@link Url} contains ambiguity upon decoding (e.g.
     * <code>http://example.com/%2F/%25</code> is ambiguous because <code>%2F</code> decodes to
     * {@link #PATH_SEGMENT_DELIMITER} and <code>%25</code> decodes to <code>%</code>, and
     * {@link #PATH_SEGMENT_DELIMITER} and <code>%</code> are special URI characters), <code>false</code> otherwise
     */
    @SuppressWarnings("JavadocLinkAsPlainText")
    public boolean isAmbiguous() {
        return ambiguous;
    }

    /**
     * @return {@link #parse(String)} {@link #toString()}
     */
    public Url normalize() {
        return parse(toString());
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
     * {@link #concatStringComponents(String, String, String, Integer, String, String, String)} with
     * {@link #getScheme()}, {@link #getUserInfo()}, {@link #getHost()}, {@link #getCustomPort()},
     * {@link #getNormalizedPath()}, {@link #getQuery()}, and {@link #getFragment()}
     */
    @Override
    public String toString() {
        if (toString == null) {
            toString = concatStringComponents(getScheme(), getUserInfo(), getHost(), getCustomPort(),
                    getNormalizedPath(), getQuery(), getFragment());
        }
        return toString;
    }

    /**
     * @return {@link #decode(String)} {@link #toString()}
     */
    public String toDecodedString() {
        return decode(toString());
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof final Url url)) {
            return false;
        }
        return toString().equals(url.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
