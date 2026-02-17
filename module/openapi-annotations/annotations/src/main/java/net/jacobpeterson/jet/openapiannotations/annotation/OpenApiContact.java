package net.jacobpeterson.jet.openapiannotations.annotation;

import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonRawString;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiContact} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-object">OpenAPI Contact Object</a>.
 * <p>
 * Contact information for the exposed API.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiContact {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiContact} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiContact[] value() default {};
    }

    /**
     * The identifying name of the contact person/organization.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * The URI for the contact information. This <em>MUST</em> be in the form of a URI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-url">spec.openapis.org</a>
     */
    String url() default "";

    /**
     * The email address of the contact person/organization. This <em>MUST</em> be in the form of an email address.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-email">spec.openapis.org</a>
     */
    String email() default "";

    /**
     * {@link OpenApiContact} raw JSON object {@link String}, merged with the existing JSON object created from the
     * serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
