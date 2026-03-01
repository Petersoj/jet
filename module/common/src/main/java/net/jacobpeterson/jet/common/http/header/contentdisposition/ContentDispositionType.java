package net.jacobpeterson.jet.common.http.header.contentdisposition;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link ContentDispositionType} is an enum that represents the type of {@link ContentDisposition}.
 *
 * @see ContentDisposition
 */
@NullMarked
@RequiredArgsConstructor
public enum ContentDispositionType {

    /**
     * Default value, indicating it can be displayed inside the Web page, or as the Web page.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition#as_a_response_header_for_the_main_body">
     * developer.mozilla.org</a>
     */
    INLINE("inline"),

    /**
     * Indicating it should be downloaded; most browsers presenting a 'Save as' dialog, prefilled with the value of the
     * <code>filename</code> parameters if present.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition#as_a_response_header_for_the_main_body">
     * developer.mozilla.org</a>
     */
    ATTACHMENT("attachment"),

    /**
     * A <code>multipart/form-data</code> body requires a <code>Content-Disposition</code> header to provide information
     * about each subpart of the form (e.g., for every form field and any files that are part of field data). The first
     * directive is always <code>form-data</code>, and the header must also include a <code>name</code> parameter to
     * identify the relevant field. Additional directives are case-insensitive. The value of any arguments (after the
     * <code>=</code> sign) may be either a token or a quoted string. Quoted strings are recommended, and many server
     * implementations require the values to be quoted. This is because a token must be US-ASCII for MIME type headers
     * like <code>Content-Disposition</code>, and US-ASCII does not allow some characters that are common in filenames
     * and other values. Multiple parameters are separated by a semicolon (<code>;</code>).
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition#as_a_header_for_a_multipart_body">
     * developer.mozilla.org</a>
     */
    FORM_DATA("form-data");

    private final String string;

    @Override
    public String toString() {
        return string;
    }

    /**
     * An {@link ImmutableMap} of lowercased {@link #toString()} mapped to {@link ContentDispositionType}.
     */
    public static final ImmutableMap<String, ContentDispositionType> VALUES_OF_LOWERCASED_STRINGS = stream(values())
            .collect(toImmutableMap(value -> value.toString().toLowerCase(ROOT), identity()));

    /**
     * Gets the {@link ContentDispositionType} for the given <code>string</code>.
     *
     * @param string the case-insensitive {@link #toString()}
     *
     * @return the {@link ContentDispositionType}, or <code>null</code> if no mapping exists
     */
    public static @Nullable ContentDispositionType forString(final String string) {
        return VALUES_OF_LOWERCASED_STRINGS.get(string.toLowerCase(ROOT));
    }
}
