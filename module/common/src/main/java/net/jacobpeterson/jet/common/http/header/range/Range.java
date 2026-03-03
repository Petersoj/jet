package net.jacobpeterson.jet.common.http.header.range;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contentrange.ContentRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Long.parseLong;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link Range} is an immutable class that represents a standardized HTTP {@link Header#RANGE}.
 * <p>
 * The HTTP <strong><code>Range</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Request_header">request header</a> indicates the part of
 * a resource that the server should return. Several parts can be requested at the same time in one <code>Range</code>
 * header, and the server may send back these ranges in a multipart document. If the server sends back ranges, it uses
 * the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/206"><code>206 Partial Content</code></a>
 * status code for the response. If the ranges are invalid, the server returns the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/416"><code>416 Range Not
 * Satisfiable</code></a> error.
 * <p>
 * A server that doesn't support range requests may ignore the <code>Range</code> header and return the whole resource
 * with a <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/200"><code>200</code></a> status
 * code. Older browsers used a response header of
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Accept-Ranges"><code>Accept-Ranges:
 * none</code></a> to disable features like 'pause' or 'resume' in download managers, but since a server ignoring the
 * <code>Range</code> header has the same meaning as responding with <code>Accept-Ranges: none</code>, the header is
 * rarely used in this way.
 * <p>
 * Currently only
 * <a href="https://www.iana.org/assignments/http-parameters/http-parameters.xhtml#range-units"><code>bytes</code> units
 * are registered</a> which are <em>offsets</em> (zero-indexed &amp; inclusive). If the requested data has a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Encoding">content coding</a>
 * applied, each byte range represents the encoded sequence of bytes, not the bytes that would be obtained after
 * decoding.
 * <p>
 * The header is a
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/CORS-safelisted_request_header">CORS-safelisted request
 * header</a> when the directive specifies a single byte range.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range">developer.mozilla.org</a>
 * @see Header#RANGE
 * @see #ALL_BYTES
 * @see ContentRange
 */
@NullMarked
@Immutable
@EqualsAndHashCode(cacheStrategy = LAZY)
@SuppressWarnings("NullAway") // TODO remove once NullAway false positives are fixed
public final class Range {

    /**
     * The bytes {@link #getUnit()}: <code>"bytes"</code>
     */
    public static final String BYTES_UNIT = "bytes";

    /**
     * The {@link #getUnit()}-range delimiter: <code>"="</code>
     */
    public static final String UNIT_RANGE_DELIMITER = "=";

    /**
     * The {@link #getStart()}-{@link #getEnd()} delimiter: <code>"-"</code>
     */
    public static final String START_END_DELIMITER = "-";

    /**
     * The multiple ranges delimiter: <code>","</code>
     *
     * @see #multipleToString(Collection)
     */
    public static final String MULTIPLE_RANGES_DELIMITER = ",";

    /**
     * A {@link Range} with {@link #getUnit()} set to {@link #BYTES_UNIT}, {@link #getStart()} set to <code>0</code>,
     * and {@link #getEnd()} set to <code>null</code>.
     */
    public static final Range ALL_BYTES = builder().unit(BYTES_UNIT).start(0L).build();

    private static final Splitter PARSE_UNIT_RANGE_SPLITTER =
            Splitter.on(UNIT_RANGE_DELIMITER).limit(2).trimResults().omitEmptyStrings();
    private static final Splitter PARSE_RANGES_SPLITTER =
            Splitter.on(MULTIPLE_RANGES_DELIMITER).trimResults().omitEmptyStrings();
    private static final Splitter PARSE_START_END_SPLITTER =
            Splitter.on(START_END_DELIMITER).limit(2).trimResults();

    /**
     * Parses the given {@link Header#RANGE} value {@link String} into a {@link ImmutableList} of {@link Range}s.
     *
     * @param range the {@link Header#RANGE} value {@link String}
     *
     * @return the {@link Range} {@link ImmutableList}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     * @see #toString()
     */
    public static ImmutableList<Range> parse(final String range) throws IllegalArgumentException {
        final var ranges = ImmutableList.<Range>builder();
        final var unitRangeSplit = PARSE_UNIT_RANGE_SPLITTER.splitToList(range);
        checkArgument(unitRangeSplit.size() == 2, "Invalid range: %s", range);
        final var unit = unitRangeSplit.getFirst();
        for (final var rangeSplit : PARSE_RANGES_SPLITTER.split(unitRangeSplit.get(1))) {
            final var startEndSplit = PARSE_START_END_SPLITTER.splitToList(rangeSplit);
            checkArgument(startEndSplit.size() == 2, "Invalid range: %s", range);
            final var builder = builder().unit(unit);
            final var start = startEndSplit.getFirst();
            final var startIsEmpty = start.isEmpty();
            final var end = startEndSplit.get(1);
            final var endIsEmpty = end.isEmpty();
            checkArgument(!startIsEmpty || !endIsEmpty, "Invalid range: %s", range);
            try {
                if (!startIsEmpty) {
                    builder.start(parseLong(start));
                }
                if (!endIsEmpty) {
                    builder.end(parseLong(end));
                }
            } catch (final NumberFormatException numberFormatException) {
                throw new IllegalArgumentException(numberFormatException);
            }
            ranges.add(builder.build());
        }
        return ranges.build();
    }

    /**
     * Creates a {@link Header#RANGE} value {@link String} from the given {@link Range} {@link Collection}.
     *
     * @param ranges the {@link Range} {@link Collection}
     *
     * @return the {@link Header#RANGE} value {@link String} ({@link Range#toString()} of {@link #ALL_BYTES} is returned
     * if the given <code>ranges</code> {@link Collection#isEmpty()}
     */
    public static String multipleToString(final Collection<Range> ranges) {
        if (ranges.isEmpty()) {
            return ALL_BYTES.toString();
        }
        final var string = new StringBuilder();
        var first = true;
        for (final var range : ranges) {
            if (first) {
                string.append(range);
                first = false;
            } else {
                string.append(MULTIPLE_RANGES_DELIMITER).append(' ').append(range.toStringNoUnit());
            }
        }
        return string.toString();
    }

    /**
     * {@link Builder} is a builder class for {@link Range}.
     *
     * @see #builder()
     */
    public static final class Builder {}

    /**
     * The unit in which ranges are defined. Currently only <code>bytes</code> are a registered unit.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range#unit">
     * developer.mozilla.org</a>
     * @see #BYTES_UNIT
     */
    private final @Getter String unit;

    /**
     * An integer in the given unit indicating the start position of the request range.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range#range-start">
     * developer.mozilla.org</a>
     */
    private final @Getter @Nullable Long start;

    /**
     * <dl>
     * <dt>If {@link #getStart()} is non-<code>null</code>:</dt>
     * <dd>
     * An integer in the given unit indicating the end position of the requested range. This value is optional and, if
     * omitted, the end of the resource is used as the end of the range.
     * </dd>
     * <dt>If {@link #getStart()} is <code>null</code>:</dt>
     * <dd>
     * An integer indicating the number of units at the end of the resource to return.
     * </dd>
     * </dl>
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range#range-end">
     * developer.mozilla.org</a>
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Range#suffix-length">
     * developer.mozilla.org</a>
     */
    private final @Getter @Nullable Long end;

    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String stringNoUnit;
    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String string;

    /**
     * @param unit  the {@link #getUnit()}, or <code>null</code> for {@link #BYTES_UNIT}
     * @param start the {@link #getStart()}, or <code>null</code>. If this value is <code>null</code> and the given
     *              <code>end</code> is also <code>null</code>, then this value is set to <code>0</code>.
     * @param end   the {@link #getEnd()}, or <code>null</code>
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    @lombok.Builder(toBuilder = true)
    private Range(final @Nullable String unit, @Nullable final Long start, @Nullable final Long end)
            throws IllegalArgumentException {
        if (start != null) {
            checkArgument(start >= 0, "`start` must be zero or positive");
        }
        if (end != null) {
            checkArgument(end >= 0, "`end` must be zero or positive");
        }
        if (start != null && end != null) {
            checkArgument(start <= end, "`start` must be less than or equal to `end`");
        }
        this.unit = unit == null ? BYTES_UNIT : unit;
        this.start = start == null && end == null ? (Long) 0L : start;
        this.end = end;
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#RANGE}, but without the prefixed
     * {@link #getUnit()} and {@link #UNIT_RANGE_DELIMITER}
     */
    public String toStringNoUnit() {
        if (stringNoUnit == null) {
            final var stringNoUnit = new StringBuilder();
            appendToStringNoUnit(stringNoUnit);
            this.stringNoUnit = stringNoUnit.toString();
        }
        return stringNoUnit;
    }

    private void appendToStringNoUnit(final StringBuilder string) {
        if (start != null) {
            string.append(start);
        }
        string.append(START_END_DELIMITER);
        if (end != null) {
            string.append(end);
        }
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#RANGE}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            final var string = new StringBuilder();
            string.append(unit).append(UNIT_RANGE_DELIMITER);
            appendToStringNoUnit(string);
            this.string = string.toString();
        }
        return string;
    }
}
