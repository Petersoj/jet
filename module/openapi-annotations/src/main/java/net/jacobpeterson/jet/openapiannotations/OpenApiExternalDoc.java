package net.jacobpeterson.jet.openapiannotations;

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
 * {@link OpenApiExternalDoc} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#external-documentation-object">OpenAPI External Documentation
 * Object</a>.
 * <p>
 * Allows referencing an external resource for extended documentation.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#external-documentation-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiExternalDoc {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiExternalDoc} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiExternalDoc[] value() default {};
    }

    /**
     * A description of the target documentation.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#external-doc-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * <strong><em>REQUIRED</em></strong>. The URI for the target documentation. This <em>MUST</em> be in the form of a
     * URI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#external-doc-url">spec.openapis.org</a>
     */
    String url() default "";

    /**
     * {@link OpenApiExternalDoc} raw JSON object {@link String}, merged with the existing JSON object created from
     * the serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
