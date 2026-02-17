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
 * {@link OpenApiExample} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#example-object">OpenAPI Example Object</a>.
 * <p>
 * An object grouping an internal or external example value with basic <code>summary</code> and <code>description</code>
 * metadata. The examples can show either data suitable for schema validation, or serialized data as required by the
 * containing <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-object">Media Type Object</a>,
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-object">Parameter Object</a>, or
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#header-object">Header Object</a>. This object is typically used in
 * fields named <code>examples</code> (plural), and is a
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-object">referenceable</a> alternative to older
 * <code>example</code> (singular) fields that do not support referencing or metadata. The various fields and types of
 * examples are explained in more detail under
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#working-with-examples">Working With Examples</a>.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#example-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiExample {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiExample} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiExample[] value() default {};
    }

    /**
     * Short description for the example.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#example-summary">spec.openapis.org</a>
     */
    String summary() default "";

    /**
     * Long description for the example.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#example-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * An example of the data structure that <em>MUST</em> be valid according to the relevant
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">Schema Object</a>. If this field is present,
     * <code>value</code> <em>MUST</em> be absent.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#example-data-value">spec.openapis.org</a>
     */
    @AnnotationJsonRawString
    String dataValue() default "";

    /**
     * An example of the serialized form of the value, including encoding and escaping as described under
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#validating-examples">Validating Examples</a>. If
     * <code>dataValue</code> is present, then this field <em>SHOULD</em> contain the serialization of the given data.
     * Otherwise, it <em>SHOULD</em> be the valid serialization of a data value that itself <em>MUST</em> be valid as
     * described for <code>dataValue</code>.  This field <em>SHOULD NOT</em> be used if the serialization format is
     * JSON, as the data form is easier to work with. If this field is present, <code>value</code>, and
     * <code>externalValue</code> <em>MUST</em> be absent.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#example-serialized-value">spec.openapis.org</a>
     */
    String serializedValue() default "";

    /**
     * A URI that identifies the serialized example in a separate document, allowing for values not easily or readably
     * expressed as a Unicode string.  If <code>dataValue</code> is present, then this field <em>SHOULD</em> identify a
     * serialization of the given data.  Otherwise, the value <em>SHOULD</em> be the valid serialization of a data value
     * that itself <em>MUST</em> be valid as described for <code>dataValue</code>. If this field is present,
     * <code>serializedValue</code> and <code>value</code> <em>MUST</em> be absent. See also the rules for resolving
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#relative-references-in-api-description-uris">Relative
     * References</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#example-external-value">spec.openapis.org</a>
     */
    String externalValue() default "";

    /**
     * Embedded literal example. The <code>value</code> field and <code>externalValue</code> field are mutually
     * exclusive. To represent examples of media types that cannot naturally be represented in JSON or YAML, use a
     * string value to contain the example, escaping where necessary.
     * <p>
     * <strong>Deprecated for non-JSON serialization targets:</strong> Use <code>dataValue</code> and/or
     * <code>serializedValue</code>, which both have unambiguous syntax and semantics, instead.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#example-value">spec.openapis.org</a>
     */
    @AnnotationJsonRawString
    String value() default "";

    /**
     * {@link OpenApiExample} raw JSON.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
