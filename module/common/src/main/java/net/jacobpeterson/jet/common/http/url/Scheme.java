package net.jacobpeterson.jet.common.http.url;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableMap;

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
    BLOB("blob", null),

    /**
     * Data directly embedded in the URL.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/data"><code>data</code></a>
     */
    DATA("data", null),

    /**
     * Host-specific file names.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#file"><code>file</code></a>
     */
    FILE("file", null),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Glossary/FTP">File Transfer Protocol</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ftp"><code>ftp</code></a>
     */
    FTP("ftp", 21),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP">Hypertext Transfer Protocol</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#http"><code>http</code></a>
     */
    HTTP("http", 80),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP">Hypertext Transfer Protocol</a>
     * for secure connections.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#http"><code>https</code></a>
     */
    HTTPS("https", 443),

    /**
     * URL-embedded JavaScript code.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/javascript">
     * <code>javascript</code></a>
     */
    JAVASCRIPT("javascript", null),

    /**
     * Electronic mail address.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#mailto"><code>mailto</code></a>
     */
    MAILTO("mailto", null),

    /**
     * Secure shell.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ssh"><code>ssh</code></a>
     */
    SSH("ssh", 22),

    /**
     * Telephone.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#tel"><code>tel</code></a>
     */
    TEL("tel", null),

    /**
     * Uniform Resource Names.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes/urn"><code>urn</code></a>
     */
    URN("urn", null),

    /**
     * Source code of the resource.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#view-source">
     * <code>view-source</code></a>
     */
    VIEW_SOURCE("view-source", null),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API">WebSocket connections</a>.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ws"><code>ws</code></a>
     */
    WS("ws", 80),

    /**
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API">WebSocket connections</a>
     * (for secure connections).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/URI/Reference/Schemes#ws"><code>wss</code></a>
     */
    WSS("wss", 443);

    private final String string;
    /** The default port, or <code>null</code> if there is no well-known default port. */
    private final @Getter @Nullable Integer defaultPort;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An unmodifiable {@link Map} of uppercased {@link #toString()} mapped to {@link Scheme}.
     */
    public static final Map<String, Scheme> VALUES_OF_UPPERCASED_STRINGS = stream(values())
            .collect(toUnmodifiableMap(value -> value.toString().toUpperCase(ROOT), identity()));

    /**
     * An unmodifiable {@link Map} of {@link #getDefaultPort()} mapped to {@link Scheme}.
     */
    public static final Map<Integer, Scheme> VALUES_OF_DEFAULT_PORTS = stream(values())
            .filter(value -> value.getDefaultPort() != null)
            .collect(toUnmodifiableMap(Scheme::getDefaultPort, identity(), (first, _) -> first));

    /**
     * Gets the {@link Scheme} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link Scheme}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Scheme forString(final String string) {
        return VALUES_OF_UPPERCASED_STRINGS.get(string.toUpperCase(ROOT));
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
