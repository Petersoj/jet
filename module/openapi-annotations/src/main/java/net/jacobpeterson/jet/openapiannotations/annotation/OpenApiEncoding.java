package net.jacobpeterson.jet.openapiannotations.annotation;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonObjectInline;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiEncoding} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#components-object">OpenAPI Components Object</a>.
 * <p>
 * Holds a set of reusable objects for different aspects of the OAS. All objects defined within the Components Object
 * will have no effect on the API unless they are explicitly referenced from outside the Components Object.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiEncoding {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiEncoding} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiEncoding[] value() default {};
    }

    /**
     * The <code>Content-Type</code> for encoding a specific property. The value is a comma-separated list, each element
     * of which is either a specific media type (e.g. <code>image/png</code>) or a wildcard media type (e.g.
     * <code>image/*</code>). The default value depends on the type as shown in the table below.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-content-type">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    String[] contentType() default {};

    /**
     * A map allowing additional information to be provided as headers. <code>Content-Type</code> is described
     * separately and <em>SHALL</em> be ignored in this section. This field <em>SHALL</em> be ignored if the media type
     * is not a <code>multipart</code>.
     * <p>
     * Note: {@link OpenApiEncoding} has no <code>headers()</code> method because using {@link OpenApiHeader} directly
     * in {@link OpenApiEncoding} causes a cyclic annotation compiler error.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-headers">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    @SerializedName("headers")
    OpenApiReference.MapEntry[] headerReferences() default {};

    /**
     * Applies nested Encoding Objects in the same manner as the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-object">Media Type Object</a>’s
     * <code>encoding</code> field.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-encoding">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    Nested.MapEntry[] encoding() default {};

    /**
     * Applies nested Encoding Objects in the same manner as the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-object">Media Type Object</a>’s
     * <code>prefixEncoding</code> field.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-prefix-encoding">spec.openapis.org</a>
     */
    Nested[] prefixEncoding() default {};

    /**
     * Applies nested Encoding Objects in the same manner as the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-object">Media Type Object</a>’s
     * <code>itemEncoding</code> field.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-item-encoding">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    Nested[] itemEncoding() default {};

    /**
     * Describes how a specific property value will be serialized depending on its type. See
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-object">Parameter Object</a> for details on the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-style"><code>style</code></a> field. The behavior
     * follows the same values as <code>query</code> parameters, including the default value of <code>"form"</code>
     * which applies only when <code>contentType</code> is <em>not</em> being used due to one or both of
     * <code>explode</code> or <code>allowReserved</code> being explicitly specified. Note that the initial
     * <code>?</code> used in query strings is not used in <code>application/x-www-form-urlencoded</code> message
     * bodies, and <em>MUST</em> be removed (if using an RFC6570 implementation) or simply not added (if constructing
     * the string manually). This field <em>SHALL</em> be ignored if the media type is not
     * <code>application/x-www-form-urlencoded</code> or <code>multipart/form-data</code>. If a value is explicitly
     * defined, then the value of
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-content-type"><code>contentType</code></a> (implicit
     * or explicit) <em>SHALL</em> be ignored.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-style">spec.openapis.org</a>
     */
    String style() default "";

    /**
     * When this is true, property values of type <code>array</code> or <code>object</code> generate separate
     * parameters for each value of the array, or key-value-pair of the map. For other types of properties, or when
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-style"><code>style</code></a> is
     * <code>"deepObject"</code>, this field has no effect. When <code>style</code> is <code>"form"</code>, the default
     * value is <code>true</code>. For all other styles, the default value is <code>false</code>. This field
     * <em>SHALL</em> be ignored if the media type is not <code>application/x-www-form-urlencoded</code> or
     * <code>multipart/form-data</code>. If a value is explicitly defined, then the value of
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-content-type"><code>contentType</code></a> (implicit
     * or explicit) <em>SHALL</em> be ignored.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-explode">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] explode() default {};

    /**
     * When this is true, parameter values are serialized using reserved expansion, as defined by
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6570">RFC6570</a>
     * <a href="https://datatracker.ietf.org/doc/html/rfc6570#section-3.2.3">Section 3.2.3</a>, which allows
     * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC3986’s reserved character set</a>, as well as
     * percent-encoded triples, to pass through unchanged, while still percent-encoding all other disallowed characters
     * (including <code>%</code> outside of percent-encoded triples). Applications are still responsible for
     * percent-encoding reserved characters that are not allowed in the target media type; see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#url-percent-encoding">URL Percent-Encoding</a> for details.
     * The default value is <code>false</code>. This field <em>SHALL</em> be ignored if the media type is not
     * <code>application/x-www-form-urlencoded</code> or <code>multipart/form-data</code>. If a value is explicitly
     * defined, then the value of
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-content-type"><code>contentType</code></a> (implicit
     * or explicit) <em>SHALL</em> be ignored.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-allow-reserved">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] allowReserved() default {};

    /**
     * {@link Nested} is an annotation for a one-level nested {@link OpenApiEncoding} annotation.
     * <p>
     * Note: {@link OpenApiEncoding} uses this one-level {@link Nested} annotation because using {@link OpenApiEncoding}
     * as a method return type in {@link OpenApiEncoding} causes a cyclic annotation compiler error.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface Nested { //@formatter:on

        /**
         * {@link MapEntry} is an annotation for a {@link Nested} entry in an {@link AnnotationArrayIsMap}
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
            Nested[] value() default {};
        }

        /**
         * @see OpenApiEncoding#contentType()
         */
        @AnnotationArrayIsNullableValue
        String[] contentType() default {};

        /**
         * @see OpenApiEncoding#headerReferences()
         */
        @AnnotationArrayIsMap
        @SerializedName("headers")
        OpenApiReference.MapEntry[] headerReferences() default {};

        /**
         * @see OpenApiEncoding#style()
         */
        String style() default "";

        /**
         * @see OpenApiEncoding#explode()
         */
        @AnnotationArrayIsNullableValue
        boolean[] explode() default {};

        /**
         * @see OpenApiEncoding#allowReserved()
         */
        @AnnotationArrayIsNullableValue
        boolean[] allowReserved() default {};
    }
}
