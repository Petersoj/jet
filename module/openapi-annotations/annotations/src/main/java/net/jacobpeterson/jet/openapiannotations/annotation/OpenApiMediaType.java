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
 * {@link OpenApiMediaType} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-object">OpenAPI Media Type Object</a>.
 * <p>
 * Each Media Type Object describes content structured in accordance with the media type identified by its key.
 * Multiple Media Type Objects can be used to describe content that can appear in any of several different media types.
 * <p>
 * When <code>example</code> or <code>examples</code> are provided, the example <em>SHOULD</em> match the specified
 * schema and be in the correct format as specified by the media type and its encoding. The <code>example</code> and
 * <code>examples</code> fields are mutually exclusive. See
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working With Examples</a> for further
 * guidance regarding the different ways of specifying examples, including non-JSON/YAML values.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiMediaType {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiMediaType} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiMediaType[] value() default {};
    }

    /**
     * A schema describing the complete content of the request, response, parameter, or header.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-schema">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiSchema[] schema() default {};

    /**
     * A schema describing each item within a
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#sequential-media-types">sequential media type</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-item-schema">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiSchema[] itemSchema() default {};

    /**
     * Example of the media type; see <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working
     * With Examples</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-example">spec.openapis.org</a>
     */
    @AnnotationJsonRawString
    String example() default "";

    /**
     * Examples of the media type; see <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working
     * With Examples</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-examples">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiExample.MapEntry[] examples() default {};

    /**
     * @see #examples()
     */
    @AnnotationArrayIsMap
    @SerializedName("examples")
    OpenApiReference.MapEntry[] exampleReferences() default {};

    /**
     * A map between a property name and its encoding information, as defined under
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-by-name">Encoding By Name</a>. The
     * <code>encoding</code> field <em>SHALL</em> only apply when the media type is <code>multipart</code> or
     * <code>application/x-www-form-urlencoded</code>. If no Encoding Object is provided for a property, the behavior is
     * determined by the default values documented for the Encoding Object. This field <em>MUST NOT</em> be present if
     * <code>prefixEncoding</code> or <code>itemEncoding</code> are present.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-encoding">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiEncoding.MapEntry[] encoding() default {};

    /**
     * An array of positional encoding information, as defined under
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-by-position">Encoding By Position</a>. The
     * <code>prefixEncoding</code> field <em>SHALL</em> only apply when the media type is <code>multipart</code>. If no
     * Encoding Object is provided for a property, the behavior is determined by the default values documented for the
     * Encoding Object. This field <em>MUST NOT</em> be present if <code>encoding</code> is present.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-prefix-encoding">spec.openapis.org</a>
     */
    OpenApiEncoding[] prefixEncoding() default {};

    /**
     * A single Encoding Object that provides encoding information for multiple array items, as defined under
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-by-position">Encoding By Position</a>. The
     * <code>itemEncoding</code> field <em>SHALL</em> only apply when the media type is <code>multipart</code>. If no
     * Encoding Object is provided for a property, the behavior is determined by the default values documented for the
     * Encoding Object. This field <em>MUST NOT</em> be present if <code>encoding</code> is present.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-item-encoding">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiEncoding[] itemEncoding() default {};
}
