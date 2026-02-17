package net.jacobpeterson.jet.openapiannotations.annotation;

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
 * {@link OpenApiReference} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-object">OpenAPI Reference Object</a>.
 * <p>
 * A simple object to allow referencing other components in the OpenAPI Description, internally and externally.
 * <p>
 * The <code>$ref</code> string value contains a URI
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc3986">RFC3986</a>, which identifies the value being
 * referenced. See the rules for resolving
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#relative-references-in-api-description-uris">Relative
 * References</a>.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiReference {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiReference} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiReference[] value() default {};
    }

    /**
     * <strong><em>REQUIRED</em></strong>. The reference identifier. This <em>MUST</em> be in the form of a URI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-ref">spec.openapis.org</a>
     */
    String $ref() default "";

    /**
     * A short summary which by default <em>SHOULD</em> override that of the referenced component. If the referenced
     * object-type does not allow a <code>summary</code> field, then this field has no effect.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-summary">spec.openapis.org</a>
     */
    String summary() default "";

    /**
     * A description which by default <em>SHOULD</em> override that of the referenced component.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation. If the referenced object-type does not allow a <code>description</code> field, then
     * this field has no effect.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * {@link OpenApiReference} raw JSON.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
