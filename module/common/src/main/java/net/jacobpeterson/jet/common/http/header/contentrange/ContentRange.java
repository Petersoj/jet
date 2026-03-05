package net.jacobpeterson.jet.common.http.header.contentrange;

import com.google.common.base.Splitter;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.range.Range;
import net.jacobpeterson.jet.common.http.status.Status;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Long.parseLong;
import static java.lang.Math.max;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

/**
 * {@link ContentRange} is an immutable class that represents a standardized HTTP {@link Header#CONTENT_RANGE}.
 * <p>
 * The HTTP <strong><code>Content-Range</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Response_header">response header</a> is used in
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Range_requests">range requests</a> to indicate
 * where the content of a response body belongs in relation to a complete resource.
 * <p>
 * It should only be included in
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/206"><code>206 Partial Content</code></a>
 * or <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/416"><code>416 Range Not
 * Satisfiable</code></a> responses.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range">
 * developer.mozilla.org</a>
 * @see Header#CONTENT_RANGE
 * @see Status#PARTIAL_CONTENT_206
 * @see Status#RANGE_NOT_SATISFIABLE_416
 * @see Range
 */
@NullMarked
@Immutable
@EqualsAndHashCode(cacheStrategy = LAZY)
@SuppressWarnings("NullAway") // TODO remove once NullAway false positives are fixed
public final class ContentRange {

    /**
     * The bytes unit: <code>"bytes"</code>
     */
    public static final String BYTES_UNIT = "bytes";

    /**
     * The unknown token: <code>"*"</code>
     */
    public static final String UNKNOWN_TOKEN = "*";

    /**
     * The unit-value delimiter: <code>" "</code>
     */
    public static final String UNIT_VALUE_DELIMITER = " ";

    /**
     * The range-{@link #getSize()} delimiter: <code>"/"</code>
     */
    public static final String RANGE_SIZE_DELIMITER = "/";

    /**
     * The {@link #getStart()}-{@link #getEnd()} delimiter: <code>"-"</code>
     */
    public static final String RANGE_START_END_DELIMITER = "-";

    private static final Splitter PARSE_UNIT_VALUE_SPLITTER =
            Splitter.on(UNIT_VALUE_DELIMITER).limit(2).trimResults();
    private static final Splitter PARSE_RANGE_SIZE_SPLITTER =
            Splitter.on(RANGE_SIZE_DELIMITER).limit(2).trimResults();
    private static final Splitter PARSE_RANGE_START_END_SPLITTER =
            Splitter.on(RANGE_START_END_DELIMITER).limit(2).trimResults();

    /**
     * Gets the {@link ContentRange} for the given {@link Range} and known content <code>size</code>.
     *
     * @param range the {@link Range}, or <code>null</code> for {@link ContentRange#getStart()} of <code>0</code> and
     *              {@link ContentRange#getEnd()} of <code>size - 1</code>
     * @param size  the {@link ContentRange#getSize()}
     *
     * @return the {@link ContentRange} ({@link ContentRange#getStart()} and {@link ContentRange#getEnd()} are never
     * <code>null</code>)
     */
    public static ContentRange forRange(final @Nullable Range range, final long size) {
        long start = 0;
        var end = max(0, size - 1);
        if (range != null) {
            final var rangeStart = range.getStart();
            final var rangeEnd = range.getEnd();
            if (rangeStart != null && rangeEnd != null) {
                start = rangeStart;
                end = rangeEnd;
            } else if (rangeStart != null) {
                start = rangeStart;
            } else if (rangeEnd != null) {
                start = size - rangeEnd;
            } else {
                throw new IllegalStateException();
            }
        }
        return builder()
                .unit(range != null ? range.getUnit() : null)
                .start(start)
                .end(end)
                .size(size)
                .build();
    }

    /**
     * Parses the given {@link Header#CONTENT_RANGE} value {@link String} into a {@link ContentRange}.
     *
     * @param contentRange the {@link Header#CONTENT_RANGE} value {@link String}
     *
     * @return the {@link ContentRange}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static ContentRange parse(final String contentRange) throws IllegalArgumentException {
        final var builder = builder();
        final var unitRangeSplit = PARSE_UNIT_VALUE_SPLITTER.splitToList(contentRange);
        checkArgument(unitRangeSplit.size() == 2, "Invalid content range: %s", contentRange);
        builder.unit(unitRangeSplit.getFirst());
        final var rangeSizeSplit = PARSE_RANGE_SIZE_SPLITTER.splitToList(unitRangeSplit.get(1));
        checkArgument(rangeSizeSplit.size() == 2, "Invalid content range: %s", contentRange);
        final var range = rangeSizeSplit.getFirst();
        if (!range.equals(UNKNOWN_TOKEN)) {
            final var startEndSplit = PARSE_RANGE_START_END_SPLITTER.splitToList(range);
            checkArgument(startEndSplit.size() == 2, "Invalid content range: %s", contentRange);
            try {
                builder.start(parseLong(startEndSplit.getFirst()));
                builder.end(parseLong(startEndSplit.get(1)));
            } catch (final NumberFormatException numberFormatException) {
                throw new IllegalArgumentException(numberFormatException);
            }
        }
        final var size = rangeSizeSplit.get(1);
        if (!size.equals(UNKNOWN_TOKEN)) {
            try {
                builder.size(parseLong(size));
            } catch (final NumberFormatException numberFormatException) {
                throw new IllegalArgumentException(numberFormatException);
            }
        }
        return builder.build();
    }

    /**
     * {@link Builder} is a builder class for {@link ContentRange}.
     *
     * @see #builder()
     */
    public static final class Builder {}

    /**
     * The unit for specifying ranges. Currently, only <code>bytes</code> is supported.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range#unit">
     * developer.mozilla.org</a>
     * @see #BYTES_UNIT
     */
    private final @Getter String unit;

    /**
     * A range with the format <code>&lt;range-start&gt;-&lt;range-end&gt;</code>, where
     * <code>&lt;range-start&gt;</code> and <code>&lt;range-end&gt;</code> are integers for the start and end position
     * (zero-indexed &amp; inclusive) of the range in the given <code>&lt;unit&gt;</code>, respectively. <code>*</code>
     * is used in a <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/416"><code>416 Range Not
     * Satisfiable</code></a> response to indicate that the value is not a range.
     * <p>
     * A <code>null</code> value represents a <code>*</code> (the {@link #UNKNOWN_TOKEN}).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range#range">
     * developer.mozilla.org</a>
     */
    private final @Getter @Nullable Long start;

    /**
     * A range with the format <code>&lt;range-start&gt;-&lt;range-end&gt;</code>, where
     * <code>&lt;range-start&gt;</code> and <code>&lt;range-end&gt;</code> are integers for the start and end position
     * (zero-indexed &amp; inclusive) of the range in the given <code>&lt;unit&gt;</code>, respectively. <code>*</code>
     * is used in a <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/416"><code>416 Range Not
     * Satisfiable</code></a> response to indicate that the value is not a range.
     * <p>
     * A <code>null</code> value represents a <code>*</code> (the {@link #UNKNOWN_TOKEN}).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range#range">
     * developer.mozilla.org</a>
     */
    private final @Getter @Nullable Long end;

    /**
     * The total length of the document (or <code>*</code> if unknown).
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Range#size">
     * developer.mozilla.org</a>
     */
    private final @Getter @Nullable Long size;

    private @LazyInit @EqualsAndHashCode.Exclude @Nullable String string;

    /**
     * @param unit  {@link #getUnit()}, or <code>null</code> for {@link #BYTES_UNIT}
     * @param start the {@link #getStart()}, or possibly <code>null</code> if the given <code>end</code> is also
     *              <code>null</code>
     * @param end   the {@link #getEnd()}, or possibly <code>null</code> if the given <code>start</code> is also
     *              <code>null</code>
     * @param size  the {@link #getSize()}
     *
     * @throws IllegalArgumentException thrown for invalid arguments
     */
    @lombok.Builder(toBuilder = true)
    private ContentRange(final @Nullable String unit, @Nullable final Long start, @Nullable final Long end,
            @Nullable final Long size) throws IllegalArgumentException {
        checkArgument((start == null) == (end == null), "Both `start` and `end` must be `null` or non-`null`");
        final var rangeSet = start != null;
        if (rangeSet) {
            checkArgument(start >= 0, "`start` must be zero or positive");
            checkArgument(end >= 0, "`end` must be zero or positive");
            checkArgument(start <= end, "`start` must be less than or equal to `end`");
        }
        if (size != null) {
            checkArgument(size >= 0, "`size` must be zero or positive");
            if (rangeSet) {
                checkArgument(size == 0 ? end <= size : end < size, "`end` must be less than `size`");
            }
        }
        this.unit = unit == null ? BYTES_UNIT : unit;
        this.start = start;
        this.end = end;
        this.size = size;
    }

    /**
     * @return <code>true</code> if {@link #getStart()} equals <code>0</code> and {@link #getEnd()} equals
     * {@link #getSize()} minus one.
     */
    public boolean isRedundant() {
        return start != null && end != null && size != null && start == 0 && end == max(0, size - 1);
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#CONTENT_RANGE}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            final var string = new StringBuilder();
            string.append(unit).append(' ');
            if (start == null || end == null) {
                string.append(UNKNOWN_TOKEN);
            } else {
                string.append(start).append(RANGE_START_END_DELIMITER).append(end);
            }
            string.append(RANGE_SIZE_DELIMITER);
            string.append(size == null ? UNKNOWN_TOKEN : size);
            this.string = string.toString();
        }
        return string;
    }
}
