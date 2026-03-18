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
 * {@link OpenApiDiscriminator} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#discriminator-object">OpenAPI Discriminator Object</a>.
 * <p>
 * When request bodies or response payloads may be one of a number of different schemas, these should use the JSON
 * Schema <code>anyOf</code> or <code>oneOf</code> keywords to describe the possible schemas
 * (see <a href="https://spec.openapis.org/oas/v3.2.0.html#composition-and-inheritance-polymorphism">Composition and
 * Inheritance</a>).
 * <p>
 * A polymorphic schema <em>MAY</em> include a Discriminator Object, which defines the name of the property that may be
 * used as a hint for which schema of the <code>anyOf</code> or <code>oneOf</code>, or which schema that references the
 * current schema in an <code>allOf</code>, is expected to validate the structure of the model. This hint can be used to
 * aid in serialization, deserialization, and validation. The Discriminator Object does this by implicitly or explicitly
 * associating the possible values of a named property with alternative schemas.
 * <p>
 * Note that <code>discriminator</code> <em>MUST NOT</em> change the validation outcome of the schema.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#discriminator-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiDiscriminator {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiDiscriminator} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiDiscriminator[] value() default {};
    }

    /**
     * <strong><em>REQUIRED</em></strong>. The name of the discriminating property in the payload that will hold the
     * discriminating value. The discriminating property <em>MAY</em> be defined as required or optional, but when
     * defined as optional the Discriminator Object <em>MUST</em> include a <code>defaultMapping</code> field that
     * specifies which schema is expected to validate the structure of the model when the discriminating property is not
     * present.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#property-name">spec.openapis.org</a>
     */
    String propertyName() default "";

    /**
     * An object to hold mappings between payload values and schema names or URI references.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#discriminator-mapping">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    Mapping[] mapping() default {};

    /**
     * {@link Mapping} is an annotation an entry in the {@link #mapping()} map.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface Mapping { //@formatter:on

        /**
         * The {@link #mapping()} map entry key.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        String key() default "";

        /**
         * The {@link #mapping()} map entry value.
         */
        @AnnotationJsonObjectInline
        String value() default "";
    }

    /**
     * The schema name or URI reference to a schema that is expected to validate the structure of the model when the
     * discriminating property is not present in the payload or contains a value for which there is no explicit or
     * implicit mapping.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#discriminator-default-mapping">spec.openapis.org</a>
     */
    String defaultMapping() default "";

    /**
     * {@link OpenApiDiscriminator} raw JSON object {@link String}, merged with the existing JSON object created from
     * the serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
