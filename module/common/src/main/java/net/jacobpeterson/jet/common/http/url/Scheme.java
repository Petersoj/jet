package net.jacobpeterson.jet.common.http.url;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link Scheme} is an enum that represents a standardized HTTP scheme.
 * <p>
 * The <strong>scheme</strong> of a URI is the first part of the URI, before the <code>:</code> character. It
 * indicates which protocol the browser must use to fetch the resource. The scheme may affect how the rest of the URI
 * is structured and interpreted.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes">developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum Scheme {

    /**
     * Binary Large Object; a pointer to a large in-memory object.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/blob"><code>blob</code></a>
     */
    BLOB(ToString.BLOB, null),

    /**
     * Data directly embedded in the URL.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/data"><code>data</code></a>
     */
    DATA(ToString.DATA, null),

    /**
     * Host-specific file names.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#file"><code>file</code></a>
     */
    FILE(ToString.FILE, null),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/FTP">File Transfer Protocol</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ftp"><code>ftp</code></a>
     */
    FTP(ToString.FTP, 21),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP">Hypertext Transfer Protocol</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#http"><code>http</code></a>
     */
    HTTP(ToString.HTTP, 80),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP">Hypertext Transfer Protocol</a>
     * for secure connections.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#http"><code>https</code></a>
     */
    HTTPS(ToString.HTTPS, 443),

    /**
     * URL-embedded JavaScript code.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/javascript">
     * <code>javascript</code></a>
     */
    JAVASCRIPT(ToString.JAVASCRIPT, null),

    /**
     * Electronic mail address.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#mailto"><code>mailto</code></a>
     */
    MAILTO(ToString.MAILTO, null),

    /**
     * Secure shell.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ssh"><code>ssh</code></a>
     */
    SSH(ToString.SSH, 22),

    /**
     * Telephone.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#tel"><code>tel</code></a>
     */
    TEL(ToString.TEL, null),

    /**
     * Uniform Resource Names.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/urn"><code>urn</code></a>
     */
    URN(ToString.URN, null),

    /**
     * Source code of the resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#view-source">
     * <code>view-source</code></a>
     */
    VIEW_SOURCE(ToString.VIEW_SOURCE, null),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API">WebSocket connections</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ws"><code>ws</code></a>
     */
    WS(ToString.WS, 80),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API">WebSocket connections</a>
     * (for secure connections).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ws"><code>wss</code></a>
     */
    WSS(ToString.WSS, 443);

    private final String string;

    /**
     * The default port, or <code>null</code> if there is no well-known default port.
     */
    private final @Getter @Nullable Integer defaultPort;

    @Override
    public String toString() {
        return string;
    }

    /**
     * {@link ToString} contains the {@link #toString()} constants for all {@link Scheme} enums so they can be used
     * within annotations.
     */
    public static final class ToString {

        /**
         * @see Scheme#BLOB
         */
        public static final String BLOB = "blob";

        /**
         * @see Scheme#DATA
         */
        public static final String DATA = "data";

        /**
         * @see Scheme#FILE
         */
        public static final String FILE = "file";

        /**
         * @see Scheme#FTP
         */
        public static final String FTP = "ftp";

        /**
         * @see Scheme#HTTP
         */
        public static final String HTTP = "http";

        /**
         * @see Scheme#HTTPS
         */
        public static final String HTTPS = "https";

        /**
         * @see Scheme#JAVASCRIPT
         */
        public static final String JAVASCRIPT = "javascript";

        /**
         * @see Scheme#MAILTO
         */
        public static final String MAILTO = "mailto";

        /**
         * @see Scheme#SSH
         */
        public static final String SSH = "ssh";

        /**
         * @see Scheme#TEL
         */
        public static final String TEL = "tel";

        /**
         * @see Scheme#URN
         */
        public static final String URN = "urn";

        /**
         * @see Scheme#VIEW_SOURCE
         */
        public static final String VIEW_SOURCE = "view-source";

        /**
         * @see Scheme#WS
         */
        public static final String WS = "ws";

        /**
         * @see Scheme#WSS
         */
        public static final String WSS = "wss";
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link Scheme}.
     */
    public static final ImmutableMap<String, Scheme> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * An {@link ImmutableMap} of {@link #getDefaultPort()} mapped to {@link Scheme}.
     */
    @SuppressWarnings({"NullAway", "DataFlowIssue"})
    public static final ImmutableMap<Integer, Scheme> VALUES_OF_DEFAULT_PORTS = stream(values())
            .filter(value -> value.getDefaultPort() != null)
            .collect(toImmutableMap(Scheme::getDefaultPort, identity(), (first, _) -> first));

    /**
     * Gets the {@link Scheme} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link Scheme}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Scheme forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }

    /**
     * Gets the {@link Scheme} for the given <code>defaultPort</code>.
     *
     * @param defaultPort the {@link #getDefaultPort()}
     *
     * @return the {@link Scheme}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Scheme forDefaultPort(final int defaultPort) {
        return VALUES_OF_DEFAULT_PORTS.get(defaultPort);
    }
}
