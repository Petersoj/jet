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
 * {@link OpenApiSchema} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">OpenAPI Schema Object</a>.
 * <p>
 * The Schema Object allows the definition of input and output data types. These types can be objects, but also
 * primitives and arrays. This object is a superset of the
 * <a href="https://www.ietf.org/archive/id/draft-bhutton-json-schema-01.html">JSON Schema Specification Draft
 * 2020-12</a>. The empty schema (which allows any instance to validate) <em>MAY</em> be represented by the boolean
 * value <code>true</code> and a schema which allows no instance to validate <em>MAY</em> be represented by the boolean
 * value <code>false</code>.
 * <p>
 * For more information about the keywords, see
 * <a href="https://www.ietf.org/archive/id/draft-bhutton-json-schema-01.html">JSON Schema Core</a> and
 * <a href="https://www.ietf.org/archive/id/draft-bhutton-json-schema-validation-01.html">JSON Schema Validation</a>.
 * <p>
 * Unless stated otherwise, the keyword definitions follow those of JSON Schema and do not add any additional semantics;
 * this includes keywords such as <code>$schema</code>, <code>$id</code>, <code>$ref</code>, and
 * <code>$dynamicRef</code> being URIs rather than URLs. Where JSON Schema indicates that behavior is defined by the
 * application (e.g. for annotations), OAS also defers the definition of semantics to the application consuming the
 * OpenAPI document.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiSchema {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiSchema} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiSchema[] value() default {};
    }

    /**
     * The {@link OpenApiSchema} value.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String value() default "";

    /**
     * The {@link Class} to generate a JSON schema from.
     * <p>
     * If {@link #value()} is set, the generated JSON schema is combined with it.
     */
    @AnnotationJsonIgnore
    @AnnotationArrayIsNullableValue
    Class<?>[] from() default {};
}
