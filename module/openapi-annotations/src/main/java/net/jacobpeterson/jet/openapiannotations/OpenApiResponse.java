package net.jacobpeterson.jet.openapiannotations;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationJsonRawString;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiResponse} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#response-object">OpenAPI Response Object</a>.
 * <p>
 * Describes a single response from an API operation, including design-time, static <code>links</code> to operations
 * based on the response.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#response-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiResponse {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiResponse} entry in an {@link AnnotationArrayIsMap}
     * annotation method.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface MapEntry { //@formatter:on

        /**
         * The map entry key.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        String key() default "";

        /**
         * The map entry key <code>int</code>.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        @AnnotationArrayIsNullableValue
        int[] keyInt() default {};

        /**
         * The map entry key enum.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        @AnnotationArrayIsNullableValue
        Status[] keyEnum() default {};

        /**
         * The map entry value.
         */
        @AnnotationArrayIsNullableValue
        @AnnotationJsonObjectInline
        OpenApiResponse[] value() default {};
    }

    /**
     * A short summary of the meaning of the response.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#response-summary">spec.openapis.org</a>
     */
    String summary() default "";

    /**
     * A description of the response. <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a>
     * syntax <em>MAY</em> be used for rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#response-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * Maps a header name to its definition. <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc9110">RFC9110</a>
     * <a href="https://datatracker.ietf.org/doc/html/rfc9110#section-5.1">Section 5.1</a> states header names are
     * case-insensitive. If a response header is defined with the name <code>"Content-Type"</code>, it <em>SHALL</em> be
     * ignored.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#response-headers">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiHeader.MapEntry[] headers() default {};

    /**
     * @see #headers()
     */
    @AnnotationArrayIsMap
    @SerializedName("headers")
    OpenApiReference.MapEntry[] headerReferences() default {};

    /**
     * A map containing descriptions of potential response payloads. The key is a media type or
     * <a href="https://tools.ietf.org/html/rfc9110#appendix-A">media type range</a> and the value describes it. For
     * responses that match multiple keys, only the most specific key is applicable. e.g. <code>"text/plain"</code>
     * overrides <code>"text/*"</code>
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#response-content">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiMediaType.MapEntry[] content() default {};

    /**
     * @see #content()
     */
    @AnnotationArrayIsMap
    @SerializedName("content")
    OpenApiReference.MapEntry[] contentReferences() default {};

    /**
     * A map of operations links that can be followed from the response. The key of the map is a short name for the
     * link, following the naming constraints of the names for
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#components-object">Component Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#response-links">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiLink.MapEntry[] links() default {};

    /**
     * @see #links()
     */
    @AnnotationArrayIsMap
    @SerializedName("links")
    OpenApiReference.MapEntry[] linkReferences() default {};

    /**
     * {@link OpenApiResponse} raw JSON object {@link String}, merged with the existing JSON object created from the
     * serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
