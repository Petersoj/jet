package net.jacobpeterson.jet.openapiannotations.annotation;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonRawString;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiHeader} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#header-object">OpenAPI Header Object</a>.
 * <p>
 * Describes a single header for <a href="https://spec.openapis.org/oas/v3.2.0.html#response-headers">HTTP responses</a>
 * and for <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-headers">individual parts in
 * <code>multipart</code> representations</a>; see the relevant
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#response-object">Response Object</a> and
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-object">Encoding Object</a> documentation for
 * restrictions on which headers can be described.
 * <p>
 * The Header Object follows the structure of the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-object">Parameter Object</a>, including determining its
 * serialization strategy based on whether <code>schema</code> or <code>content</code> is present, with the following
 * changes:
 * <ol>
 * <li><code>name</code> <em>MUST NOT</em> be specified, it is given in the corresponding <code>headers</code> map.</li>
 * <li><code>in</code> <em>MUST NOT</em> be specified, it is implicitly in <code>header</code>.</li>
 * <li>All traits that are affected by the location <em>MUST</em> be applicable to a location of <code>header</code>
 * (for example, <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-style"><code>style</code></a>). This means
 * that <code>allowEmptyValue</code> <em>MUST NOT</em> be used, and <code>style</code>, if used, <em>MUST</em> be
 * limited to <code>"simple"</code>.</li>
 * </ol>
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiHeader {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiHeader} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiHeader[] value() default {};
    }

    /**
     * A brief description of the header. This could contain examples of use.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * Determines whether this header is mandatory. The default value is <code>false</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-required">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] required() default {};

    /**
     * Specifies that the header is deprecated and <em>SHOULD</em> be transitioned out of usage. Default value is
     * <code>false</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-deprecated">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] deprecated() default {};

    /**
     * Example of the parameter’s potential value; see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working With Examples</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-example">spec.openapis.org</a>
     */
    @AnnotationJsonRawString
    String example() default "";

    /**
     * Examples of the parameter’s potential value; see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working With Examples</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-examples">spec.openapis.org</a>
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
     * For simpler scenarios, a
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#header-schema"><code>schema</code></a> and
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#header-style"><code>style</code></a> can describe the
     * structure and syntax of the header.
     * <p>
     * When serializing headers with <code>schema</code>, URI percent-encoding <em>MUST NOT</em> be applied; if using
     * an RFC6570 implementation that automatically applies it, it <em>MUST</em> be removed before use. Implementations
     * <em>MUST</em> pass header values through unchanged rather than attempting to automatically quote header values,
     * as the quoting rules vary too widely among different headers; see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#appendix-d-serializing-headers-and-cookies">Appendix D</a> for
     * guidance on quoting and escaping.</p>
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#fixed-fields-for-use-with-schema-0">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    @AnnotationJsonObjectInline
    Schema[] schema() default {};

    /**
     * {@link Schema} is an annotation for {@link OpenApiHeader#schema()}.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface Schema { //@formatter:on

        /**
         * Describes how the header value will be serialized. The default (and only legal value for headers) is
         * <code>"simple"</code>.
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-style">spec.openapis.org</a>
         */
        @AnnotationArrayIsNullableValue
        String style() default "";

        /**
         * When this is true, header values of type <code>array</code> or <code>object</code> generate a single header
         * whose value is a comma-separated list of the array items or key-value pairs of the map, see
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#style-examples">Style Examples</a>. For other data types
         * this field has no effect. The default value is <code>false</code>.
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-explode">spec.openapis.org</a>
         */
        @AnnotationArrayIsNullableValue
        boolean[] explode() default {};

        /**
         * The schema defining the type used for the header.
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#header-schema">spec.openapis.org</a>
         */
        @AnnotationArrayIsNullableValue
        OpenApiSchema[] schema() default {};
    }

    /**
     * For more complex scenarios, the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#header-content"><code>content</code></a> field can define
     * the media type and schema of the parameter, as well as give examples of its use.
     *
     * @see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#fixed-fields-for-use-with-content-0">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiMediaType.MapEntry[] content() default {};

    /**
     * @see #content()
     */
    @AnnotationArrayIsMap
    @SerializedName("content")
    OpenApiReference.MapEntry[] contentReferences() default {};
}
