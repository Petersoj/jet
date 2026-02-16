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
 * {@link OpenApiParameter} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-object">OpenAPI Parameter Object</a>.
 * <p>
 * Describes a single operation parameter.
 * <p>
 * A unique parameter is defined by a combination of a
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-name">name</a> and
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in">location</a>.
 * <p>
 * See <a href="https://spec.openapis.org/oas/v3.2.0.html#appendix-e-percent-encoding-and-form-media-types">Appendix
 * E</a> for a detailed examination of percent-encoding concerns, including interactions with the
 * <code>application/x-www-form-urlencoded</code> query string format.
 * <p>
 * The rules for serialization of the parameter are specified in one of two ways. Parameter Objects <em>MUST</em>
 * include either a <code>content</code> field or a <code>schema</code> field, but not both. See
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#appendix-b-data-type-conversion">Appendix B</a> for a discussion
 * of converting values of various types to string representations.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiParameter {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiParameter} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiParameter[] value() default {};
    }

    /**
     * <strong><em>REQUIRED</em></strong>. The name of the parameter. Parameter names are <em>case-sensitive</em>.
     * <ul>
     * <li>If <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in"><code>in</code></a> is
     * <code>"path"</code>, the <code>name</code> field <em>MUST</em> correspond to a single template expression
     * occurring within the <a href="https://spec.openapis.org/oas/v3.2.0.html#paths-path">path</a> field in the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#paths-object">Paths Object</a>. See
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-templating">Path Templating</a> for further
     * information.</li>
     * <li>If <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in"><code>in</code></a> is
     * <code>"header"</code> and the <code>name</code> field is <code>"Accept"</code>, <code>"Content-Type"</code> or
     * <code>"Authorization"</code>, the parameter definition <em>SHALL</em> be ignored.</li>
     * <li>If <code>in</code> is <code>"querystring"</code>, or for
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#style-examples">certain combinations</a> of
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-style"><code>style</code></a> and
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-explode"><code>explode</code></a>, the value of
     * <code>name</code> is not used in the parameter serialization.</li>
     * <li>For all other cases, the <code>name</code> corresponds to the parameter name used by the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in"><code>in</code></a> field.</li>
     * </ul>
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * <strong><em>REQUIRED</em></strong>. The location of the parameter. Possible values are <code>"query"</code>,
     * <code>"querystring"</code>, <code>"header"</code>, <code>"path"</code> or <code>"cookie"</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    ParameterLocation[] in() default {};

    /**
     * {@link ParameterLocation} is an enum for the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-object">OpenAPI Parameter Locations</a>.
     * <p>
     * There are five possible parameter locations specified by the <code>in</code> field.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-locations">spec.openapis.org</a>
     */
    enum ParameterLocation {

        /**
         * Used together with <a href="https://spec.openapis.org/oas/v3.2.0.html#path-templating">Path Templating</a>,
         * where the parameter value is actually part of the operation’s URL. This does not include the host or base
         * path of the API. For example, in <code>/items/{itemId}</code>, the path parameter is <code>itemId</code>.
         */
        @SerializedName("path")
        PATH,

        /**
         * Parameters that are appended to the URL. For example, in <code>/items?id=###</code>, the query parameter is
         * <code>id</code>; <em>MUST NOT</em> appear in the same operation (or in the operation’s path-item) as an
         * <code>in: "querystring"</code> parameter.
         */
        @SerializedName("query")
        QUERY,

        /**
         * A parameter that treats the entire URL query string as a value which <em>MUST</em> be specified using the
         * <code>content</code> field, most often with media type <code>application/x-www-form-urlencoded</code> using
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-object">Encoding Objects</a> in the same way as
         * with request bodies of that media type; <em>MUST NOT</em> appear more than once, and <em>MUST NOT</em> appear
         * in the same operation (or in the operation’s path-item) as any <code>in: "query"</code> parameters.
         */
        @SerializedName("querystring")
        QUERYSTRING,

        /**
         * Custom headers that are expected as part of the request. Note that
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc9110">RFC9110</a>
         * <a href="https://datatracker.ietf.org/doc/html/rfc9110#section-5.1">Section 5.1</a> states header names are
         * case-insensitive.
         */
        @SerializedName("header")
        HEADER,

        /**
         * Used to pass a specific cookie value to the API.
         */
        @SerializedName("cookie")
        COOKIE
    }

    /**
     * A brief description of the parameter. This could contain examples of use.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * Determines whether this parameter is mandatory. If the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in">parameter location</a> is <code>"path"</code>,
     * this field is <strong><em>REQUIRED</em></strong> and its value <em>MUST</em> be <code>true</code>. Otherwise, the
     * field <em>MAY</em> be included and its default value is <code>false</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-required">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] required() default {};

    /**
     * Specifies that a parameter is deprecated and <em>SHOULD</em> be transitioned out of usage. Default value is
     * <code>false</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-deprecated">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] deprecated() default {};

    /**
     * If <code>true</code>, clients <em>MAY</em> pass a zero-length string value in place of parameters that would
     * otherwise be omitted entirely, which the server <em>SHOULD</em> interpret as the parameter being unused. Default
     * value is <code>false</code>. If
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-style"><code>style</code></a> is used, and if
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#style-examples">behavior is <em>n/a</em> (cannot be
     * serialized)</a>, the value of <code>allowEmptyValue</code> <em>SHALL</em> be ignored. Interactions between this
     * field and the parameter’s <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">Schema Object</a> are
     * implementation-defined. This field is valid only for <code>query</code> parameters.
     * <p>
     * <strong>Deprecated:</strong> Use of this field is <em>NOT RECOMMENDED</em>, and it is likely to be removed in a
     * later revision.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-allow-empty-value">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] allowEmptyValue() default {};

    /**
     * Example of the parameter’s potential value; see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working With Examples</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-example">spec.openapis.org</a>
     */
    @AnnotationJsonRawString
    String example() default "";

    /**
     * Examples of the parameter’s potential value; see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working With Examples</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-examples">spec.openapis.org</a>
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
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-schema"><code>schema</code></a> and
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-style"><code>style</code></a> can describe the
     * structure and syntax of the parameter.
     * <p>
     * These fields <em>MUST NOT</em> be used with <code>in: "querystring"</code>.
     * <p>
     * Care is needed for parameters with <code>schema</code> that have <code>in: "header"</code> or <code>in: "cookie",
     * style: "cookie"</code>:
     * <ul>
     * <li>When serializing these values, URI percent-encoding <em>MUST NOT</em> be applied.</li>
     * <li>When parsing these parameters, any apparent percent-encoding <em>MUST NOT</em> be decoded.</li>
     * <li>If using an RFC6570 implementation that automatically performs encoding or decoding steps, the steps
     * <em>MUST</em> be undone before use.</li>
     * </ul>
     * In these cases, implementations <em>MUST</em> pass values through unchanged rather than attempting to quote or
     * escape them, as the quoting rules for headers and escaping conventions for cookies vary too widely to be
     * performed automatically; see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#appendix-d-serializing-headers-and-cookies">Appendix D</a> for
     * guidance on quoting and escaping.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#fixed-fields-for-use-with-schema">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    @AnnotationJsonObjectInline
    Schema[] schema() default {};

    /**
     * {@link Schema} is an annotation for {@link OpenApiParameter#schema()}.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface Schema { //@formatter:on

        /**
         * Describes how the parameter value will be serialized depending on the type of the parameter value. Default
         * values (based on value of <code>in</code>): for <code>"query"</code> - <code>"form"</code>; for
         * <code>"path"</code> - <code>"simple"</code>; for <code>"header"</code> - <code>"simple"</code>; for
         * <code>"cookie"</code> - <code>"form"</code> (for compatibility reasons; note that
         * <code>style: "cookie"</code> <em>SHOULD</em> be used with <code>in: "cookie"</code>; see
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#appendix-d-serializing-headers-and-cookies">Appendix D</a>
         * for details).
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-style">spec.openapis.org</a>
         */
        @AnnotationArrayIsNullableValue
        Style[] style() default {};

        /**
         * {@link Style} is an enum for the
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#style-values">OpenAPI Style Values</a>.
         * <p>
         * In order to support common ways of serializing simple parameters, a set of <code>style</code> values are
         * defined. Combinations not represented in this table are not permitted.
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#style-values">spec.openapis.org</a>
         */
        enum Style {

            /**
             * Type: primitive, <code>array</code>, <code>object</code>
             * <p>
             * In: <code>path</code>
             * <p>
             * Path-style parameters defined by
             * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6570">RFC6570</a>
             * <a href="https://datatracker.ietf.org/doc/html/rfc6570#section-3.2.7">Section 3.2.7</a>
             */
            @SerializedName("matrix")
            MATRIX,

            /**
             * Type: primitive, <code>array</code>, <code>object</code>
             * <p>
             * In: <code>path</code>
             * <p>
             * Label style parameters defined by
             * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6570">RFC6570</a>
             * <a href="https://datatracker.ietf.org/doc/html/rfc6570#section-3.2.5">Section 3.2.5</a>
             */
            @SerializedName("label")
            LABEL,

            /**
             * Type: primitive, <code>array</code>, <code>object</code>
             * <p>
             * In: <code>path</code>, <code>header</code>
             * <p>
             * Simple style parameters defined by
             * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6570">RFC6570</a>
             * <a href="https://datatracker.ietf.org/doc/html/rfc6570#section-3.2.2">Section 3.2.2</a>. This option
             * replaces <code>collectionFormat</code> with a <code>csv</code> value from OpenAPI 2.0.
             */
            @SerializedName("simple")
            SIMPLE,

            /**
             * Type: primitive, <code>array</code>, <code>object</code>
             * <p>
             * In: <code>query</code>, <code>cookie</code>
             * <p>
             * Form style parameters defined by
             * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6570">RFC6570</a>
             * <a href="https://datatracker.ietf.org/doc/html/rfc6570#section-3.2.8">Section 3.2.8</a>. This option
             * replaces <code>collectionFormat</code> with a <code>csv</code> (when <code>explode</code> is false) or
             * <code>multi</code> (when <code>explode</code> is true) value from OpenAPI 2.0.
             */
            @SerializedName("form")
            FORM,

            /**
             * Type: <code>array</code>, <code>object</code>
             * <p>
             * In: <code>query</code>
             * <p>
             * Space separated array values or object properties and values. This option replaces
             * <code>collectionFormat</code> equal to <code>ssv</code> from OpenAPI 2.0.
             */
            @SerializedName("spaceDelimited")
            SPACE_DELIMITED,

            /**
             * Type: <code>array</code>, <code>object</code>
             * <p>
             * In: <code>query</code>
             * <p>
             * Pipe separated array values or object properties and values. This option replaces
             * <code>collectionFormat</code> equal to <code>pipes</code> from OpenAPI 2.0.
             */
            @SerializedName("pipeDelimited")
            PIPE_DELIMITED,

            /**
             * Type: <code>object</code>
             * <p>
             * In: <code>query</code>
             * <p>
             * Allows objects with scalar properties to be represented using form parameters. The representation of
             * array or object properties is not defined (but see
             * <a href="https://spec.openapis.org/oas/v3.2.0.html#extending-support-for-querystring-formats">Extending
             * Support for Querystring Formats</a> for alternatives).
             */
            @SerializedName("deepObject")
            DEEP_OBJECT,

            /**
             * Type: primitive, <code>array</code>, <code>object</code>
             * <p>
             * In: <code>cookie</code>
             * <p>
             * Analogous to <code>form</code>, but following
             * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6265">RFC6265</a> <code>Cookie</code> syntax
             * rules, meaning that name-value pairs are separated by a semicolon followed by a single space (e.g.
             * <code>n1=v1; n2=v2</code>), and no percent-encoding or other escaping is applied; data values that
             * require any sort of escaping <em>MUST</em> be provided in escaped form.
             */
            @SerializedName("cookie")
            COOKIE
        }

        /**
         * When this is true, parameter values of type <code>array</code> or <code>object</code> generate separate
         * parameters for each value of the array or key-value pair of the map. For other types of parameters, or when
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-style"><code>style</code></a> is
         * <code>"deepObject"</code>, this field has no effect. When <code>style</code> is <code>"form"</code> or
         * <code>"cookie"</code>, the default value is <code>true</code>. For all other styles, the default value is
         * <code>false</code>.
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-explode">spec.openapis.org</a>
         */
        @AnnotationArrayIsNullableValue
        boolean[] explode() default {};

        /**
         * When this is true, parameter values are serialized using reserved expansion, as defined by
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6570">RFC6570</a>
         * <a href="https://datatracker.ietf.org/doc/html/rfc6570#section-3.2.3">Section 3.2.3</a>, which allows
         * <a href="https://tools.ietf.org/html/rfc3986#section-2.2">RFC3986’s reserved character set</a>, as well as
         * percent-encoded triples, to pass through unchanged, while still percent-encoding all other disallowed
         * characters (including <code>%</code> outside of percent-encoded triples). Applications are still responsible
         * for percent-encoding reserved characters that are not allowed by the rules of the <code>in</code> destination
         * or media type, or are <a href="https://spec.openapis.org/oas/v3.2.0.html#path-templating">not allowed in the
         * path by this specification</a>; see
         * <a href="https://spec.openapis.org/oas/v3.2.0.html#url-percent-encoding">URL Percent-Encoding</a> for
         * details. The default value is <code>false</code>. This field only applies to <code>in</code> and
         * <code>style</code> values that automatically percent-encode.
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-allow-reserved">spec.openapis.org</a>
         */
        @AnnotationArrayIsNullableValue
        boolean[] allowReserved() default {};

        /**
         * The schema defining the type used for the parameter.
         *
         * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-schema">spec.openapis.org</a>
         */
        @AnnotationArrayIsNullableValue
        OpenApiSchema[] schema() default {};
    }

    /**
     * For more complex scenarios, the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-content"><code>content</code></a> field can define
     * the media type and schema of the parameter, as well as give examples of its use.
     * <p>
     * For use with <code>in: "querystring"</code> and <code>application/x-www-form-urlencoded</code>, see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#encoding-the-x-www-form-urlencoded-media-type">Encoding the
     * <code>x-www-form-urlencoded</code> Media Type</a>.
     * <p>
     * A map containing the representations for the parameter. The key is the media type and the value describes it. The
     * map <em>MUST</em> only contain one entry.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#fixed-fields-for-use-with-content">spec.openapis.org</a>
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
