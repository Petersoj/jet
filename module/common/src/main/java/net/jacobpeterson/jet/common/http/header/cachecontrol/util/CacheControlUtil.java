package net.jacobpeterson.jet.common.http.header.cachecontrol.util;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.cachecontrol.request.RequestCacheControl;
import net.jacobpeterson.jet.common.http.header.cachecontrol.response.ResponseCacheControl;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.lang.Long.parseLong;
import static java.util.Locale.ROOT;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

/**
 * {@link CacheControlUtil} is a utility class for {@link RequestCacheControl} and {@link ResponseCacheControl}.
 */
@NullMarked
public final class CacheControlUtil {

    /**
     * The directive delimiter: <code>","</code>
     */
    public static final String DIRECTIVE_DELIMITER = ",";

    /**
     * The directive key-value delimiter: <code>"="</code>
     */
    public static final String DIRECTIVE_KEY_VALUE_DELIMITER = "=";

    private static final Splitter PARSE_DIRECTIVE_SPLITTER =
            Splitter.on(DIRECTIVE_DELIMITER).trimResults().omitEmptyStrings();
    private static final Splitter PARSE_DIRECTIVE_KEY_VALUE_SPLITTER =
            Splitter.on(DIRECTIVE_KEY_VALUE_DELIMITER).limit(2).trimResults().omitEmptyStrings();

    /**
     * Parses the given {@link Header#CACHE_CONTROL} value {@link String} into a {@link String} {@link ImmutableMap}. If
     * a directive is present, but has no value the given {@link Header#CACHE_CONTROL}, then it maps to an empty
     * {@link String} in the returned {@link ImmutableMap}.
     *
     * @param cacheControl the {@link Header#CACHE_CONTROL} value {@link String}
     *
     * @return the {@link String} {@link ImmutableMap}
     *
     * @see CacheControlUtil#parse(String)
     * @see #toString()
     */
    public static ImmutableMap<String, String> parse(final String cacheControl) {
        return PARSE_DIRECTIVE_SPLITTER.splitToStream(cacheControl)
                .map(PARSE_DIRECTIVE_KEY_VALUE_SPLITTER::splitToList)
                .filter(not(List::isEmpty))
                .collect(toImmutableMap(keyValue -> keyValue.getFirst().toLowerCase(ROOT),
                        keyValue -> keyValue.size() == 1 ? "" : keyValue.get(1)));
    }

    /**
     * @return {@link Map#get(Object)} {@link Long#parseLong(String)}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static Optional<Long> parseValueLong(final Map<String, String> directives, final String responseDirectiveKey)
            throws IllegalArgumentException {
        final var directiveValue = directives.get(responseDirectiveKey);
        if (directiveValue == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(parseLong(directiveValue));
        } catch (final NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(numberFormatException);
        }
    }

    /**
     * Creates a {@link String} value for {@link Header#CACHE_CONTROL} from the given <code>directives</code>.
     *
     * @param directives the directives {@link String} {@link Map}
     *
     * @return the {@link Header#CACHE_CONTROL} value {@link String}
     */
    public static String toString(final Map<String, String> directives) {
        return directives.entrySet().stream()
                .map(entry -> entry.getKey() +
                        (!entry.getValue().isEmpty() ? DIRECTIVE_KEY_VALUE_DELIMITER + entry.getValue() : ""))
                .collect(joining(DIRECTIVE_DELIMITER + " "));
    }

    private CacheControlUtil() {}
}
