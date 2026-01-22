package net.jacobpeterson.jet.common.http.version;

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
 * {@link Version} is an enum that represents a standardized HTTP protocol version.
 * <p>
 * Some cookie names contain prefixes that impose specific restrictions on the cookie's attributes in supporting
 * user-agents. All cookie prefixes start with a double-underscore (<code>__</code>) and end in a dash (<code>-</code>).
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Evolution_of_HTTP">developer.mozilla.org</a>
 */
@NullMarked
@RequiredArgsConstructor
public enum Version {

    /**
     * HTTP/0.9 – The one-line protocol
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Evolution_of_HTTP#http0.9_%E2%80%93_the_one-line_protocol">
     * developer.mozilla.org</a>
     */
    HTTP_0_9(Version.PREFIX + "0.9", 9),

    /**
     * HTTP/1.0 – Building extensibility
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Evolution_of_HTTP#http1.0_%E2%80%93_building_extensibility">
     * developer.mozilla.org</a>
     */
    HTTP_1_0(Version.PREFIX + "1.0", 10),

    /**
     * HTTP/1.1 – The standardized protocol
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Evolution_of_HTTP#http1.1_%E2%80%93_the_standardized_protocol">
     * developer.mozilla.org</a>
     */
    HTTP_1_1(Version.PREFIX + "1.1", 11),

    /**
     * HTTP/2 – A protocol for greater performance
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Evolution_of_HTTP#http2_%E2%80%93_a_protocol_for_greater_performance">
     * developer.mozilla.org</a>
     */
    HTTP_2(Version.PREFIX + "2.0", 20),

    /**
     * HTTP/3 - HTTP over QUIC
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Evolution_of_HTTP#http3_-_http_over_quic">
     * developer.mozilla.org</a>
     */
    HTTP_3(Version.PREFIX + "3.0", 30);

    private final String string;
    /** The version as an <code>int</code>. */
    private final @Getter int integer;

    @Override
    public String toString() {
        return string;
    }

    /**
     * @return {@link #toString()} {@link String#substring(int)} with {@link String#length()} of {@link #PREFIX}
     */
    public String toStringNoPrefix() {
        return string.substring(PREFIX.length());
    }

    /**
     * @return <code>{@link #getInteger()} / 10</code>
     */
    public double toDecimal() {
        return integer / 10.0;
    }

    /**
     * The <code>HTTP/</code> version prefix.
     */
    public static final String PREFIX = "HTTP/";

    /**
     * An unmodifiable {@link Map} of uppercased {@link #toString()} mapped to {@link Version}.
     */
    public static final Map<String, Version> VALUES_OF_UPPERCASED_STRINGS = stream(values())
            .collect(toUnmodifiableMap(value -> value.toString().toUpperCase(ROOT), identity()));

    /**
     * An unmodifiable {@link Map} of uppercased {@link #toStringNoPrefix()} mapped to {@link Version}.
     */
    public static final Map<String, Version> VALUES_OF_UPPERCASED_STRING_NO_PREFIX = stream(values())
            .collect(toUnmodifiableMap(value -> value.toStringNoPrefix().toUpperCase(ROOT), identity()));

    /**
     * An unmodifiable {@link Map} of {@link #getInteger()} mapped to {@link Version}.
     */
    public static final Map<Integer, Version> VALUES_OF_INTEGERS = stream(values())
            .collect(toUnmodifiableMap(Version::getInteger, identity()));

    /**
     * Gets the {@link Version} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link Version}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Version forString(final String string) {
        return VALUES_OF_UPPERCASED_STRINGS.get(string.toUpperCase(ROOT));
    }

    /**
     * Gets the {@link Version} for the given <code>stringNoPrefix</code>.
     *
     * @param stringNoPrefix the case-insensitive {@link #toStringNoPrefix()}
     *
     * @return the {@link Version}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Version forStringNoPrefix(final String stringNoPrefix) {
        return VALUES_OF_UPPERCASED_STRING_NO_PREFIX.get(stringNoPrefix.toUpperCase(ROOT));
    }

    /**
     * Gets the {@link Version} for the given <code>integer</code>.
     *
     * @param integer the {@link #getInteger()}
     *
     * @return the {@link Version}, or <code>null</code> if no mapping exists
     */
    public static @Nullable Version forInteger(final int integer) {
        return VALUES_OF_INTEGERS.get(integer);
    }

    /**
     * @return {@link #forInteger(int)} with the given <code>decimal * 10</code>
     */
    public static @Nullable Version forDecimal(final double decimal) {
        return forInteger((int) (decimal * 10));
    }
}
