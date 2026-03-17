package net.jacobpeterson.jet.common.http.header.ifrange;

import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.etag.ETag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static com.google.common.base.Preconditions.checkArgument;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static net.jacobpeterson.jet.common.http.header.etag.ETag.WEAK_PREFIX;

/**
 * {@link IfRange} is an immutable class that represents a standardized HTTP {@link Header#IF_RANGE}.
 * <p>
 * The HTTP <strong><code>If-Range</code></strong>
 * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Request_header">request header</a> makes a range request
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Conditional_requests">conditional</a>. If the
 * condition is fulfilled, a <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Range_requests">range
 * request</a> is issued, and the server sends back a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/206"><code>206 Partial Content</code></a>
 * response with part (or parts) of the resource in the body. If the condition is not fulfilled, the full resource is
 * sent back with a <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/200"><code>200
 * OK</code></a> status.
 * <p>
 * This header can be used either with the
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Last-Modified">
 * <code>Last-Modified</code></a> validator or with
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/ETag"><code>ETag</code></a>, but not
 * with both.
 * <p>
 * The most common use case is to resume a download with guarantees that the resource on the server has not been
 * modified since the last part has been received by the client.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/If-Range">developer.mozilla.org</a>
 * @see Header#IF_RANGE
 */
@NullMarked
@Immutable
@EqualsAndHashCode(onlyExplicitlyIncluded = true, cacheStrategy = LAZY)
public final class IfRange {

    /**
     * Parses the given {@link Header#IF_RANGE} value {@link String} into an {@link IfRange}.
     *
     * @param ifRange the {@link Header#IF_RANGE} value {@link String}
     *
     * @return the {@link IfRange}
     *
     * @throws IllegalArgumentException thrown upon parsing failure
     */
    public static IfRange parse(final String ifRange) throws IllegalArgumentException {
        final var trimmed = ifRange.trim();
        if (trimmed.startsWith(WEAK_PREFIX) || trimmed.startsWith("\"")) {
            return new IfRange(null, ETag.parse(trimmed));
        } else {
            try {
                return new IfRange(ZonedDateTime.parse(trimmed, RFC_1123_DATE_TIME), null);
            } catch (final DateTimeParseException dateTimeParseException) {
                throw new IllegalArgumentException(dateTimeParseException);
            }
        }
    }

    /**
     * See {@link Header#LAST_MODIFIED}.
     * <p>
     * Note: if this is non-<code>null</code>, then {@link #getETag()} is <code>null</code>.
     */
    private final @Getter @EqualsAndHashCode.Include @Nullable ZonedDateTime dateTime;

    /**
     * An entity tag uniquely representing the requested resource. It is a string of ASCII characters placed between
     * double quotes (Like <code>"675af34563dc-tr34"</code>). A weak entity tag (one prefixed by <code>W/</code>) must
     * not be used in this header.
     * <p>
     * Note: if this is non-<code>null</code>, then {@link #getDateTime()} is <code>null</code>.
     */
    private final @Getter @EqualsAndHashCode.Include @Nullable ETag eTag;

    private @LazyInit @Nullable String string;

    /**
     * @param dateTime the {@link #getDateTime()}
     * @param eTag     the {@link #getETag()}
     */
    @lombok.Builder(toBuilder = true)
    private IfRange(final @Nullable ZonedDateTime dateTime, final @Nullable ETag eTag) {
        checkArgument((dateTime == null) != (eTag == null), "`dateTime` and `eTag` are mutually exclusive");
        this.dateTime = dateTime;
        this.eTag = eTag;
    }

    /**
     * @return internally-cached {@link String} value for {@link Header#IF_RANGE}
     *
     * @see #parse(String)
     */
    @Override
    public String toString() {
        if (string == null) {
            if (dateTime != null) {
                string = RFC_1123_DATE_TIME.format(dateTime.withZoneSameInstant(UTC));
            } else if (eTag != null) {
                string = eTag.toString();
            } else {
                throw new IllegalStateException();
            }
        }
        return string;
    }
}
