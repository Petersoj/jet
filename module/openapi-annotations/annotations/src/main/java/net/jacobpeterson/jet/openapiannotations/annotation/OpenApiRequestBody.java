package net.jacobpeterson.jet.openapiannotations.annotation;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonRawString;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiRequestBody} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#request-body-object">OpenAPI Request Body Object</a>.
 * <p>
 * Describes a single request body.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#request-body-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiRequestBody {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiRequestBody} entry in an {@link AnnotationArrayIsMap}
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
         * The map entry value.
         */
        @AnnotationArrayIsNullableValue
        @AnnotationJsonObjectInline
        OpenApiRequestBody[] value() default {};
    }

    /**
     * A brief description of the request body. This could contain examples of use.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#request-body-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * <strong><em>REQUIRED</em></strong>. The content of the request body. The key is a media type or
     * <a href="https://tools.ietf.org/html/rfc9110#appendix-A">media type range</a> and the value describes it. The map
     * <em>SHOULD</em> have at least one entry; if it does not, the behavior is implementation-defined. For requests
     * that match multiple keys, only the most specific key is applicable. e.g. <code>"text/plain"</code> overrides
     * <code>"text/*"</code>
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#request-body-content">spec.openapis.org</a>
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
     * Determines if the request body is required in the request. Defaults to <code>false</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#request-body-required">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] required() default {};

    /**
     * {@link OpenApiRequestBody} raw JSON.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
