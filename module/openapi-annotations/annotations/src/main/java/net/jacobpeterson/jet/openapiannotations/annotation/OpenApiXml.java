package net.jacobpeterson.jet.openapiannotations.annotation;

import com.google.gson.annotations.SerializedName;
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
 * {@link OpenApiXml} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-object">OpenAPI XML Object</a>.
 * <p>
 * A metadata object that allows for more fine-tuned XML model definitions. When using a Schema Object with XML, if no
 * XML Object is present, the behavior is determined by the XML Object’s default field values.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiXml {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiXml} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiXml[] value() default {};
    }

    /**
     * One of <code>element</code>, <code>attribute</code>, <code>text</code>, <code>cdata</code>, or <code>none</code>,
     * as explained under <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-node-types">XML Node Types</a>. The
     * default value is <code>none</code> if <code>$ref</code>, <code>$dynamicRef</code>, or <code>type: "array"</code>
     * is present in the <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">Schema Object</a> containing
     * the XML Object, and <code>element</code> otherwise.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-node-type">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    NodeType[] nodeType() default {};

    /**
     * {@link NodeType} is an enum for {@link #nodeType()}.
     */
    enum NodeType {

        @SerializedName("element")
        ELEMENT,

        @SerializedName("attribute")
        ATTRIBUTE,

        @SerializedName("text")
        TEXT,

        @SerializedName("cdata")
        CDATA,

        @SerializedName("none")
        NONE
    }

    /**
     * Sets the name of the element/attribute corresponding to the schema, replacing the name that was inferred as
     * described under <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-node-names">XML Node Names</a>. This field
     * <em>SHALL</em> be ignored if the <code>nodeType</code> is <code>text</code>, <code>cdata</code>, or
     * <code>none</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * The IRI (<a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc3987">RFC3987</a>) of the namespace
     * definition. Value <em>MUST</em> be in the form of a non-relative IRI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-namespace">spec.openapis.org</a>
     */
    String namespace() default "";

    /**
     * The prefix to be used for the <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-name">name</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-prefix">spec.openapis.org</a>
     */
    String prefix() default "";

    /**
     * Declares whether the property definition translates to an attribute instead of an element. Default value is
     * <code>false</code>. If <code>nodeType</code> is present, this field <em>MUST NOT</em> be present.
     * <p>
     * <strong>Deprecated:</strong> Use <code>nodeType: "attribute"</code> instead of <code>attribute: true</code>
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-attribute">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] attribute() default {};

    /**
     * <em>MAY</em> be used only for an array definition. Signifies whether the array is wrapped (for example,
     * <code>&lt;books&gt;&lt;book/&gt;&lt;book/&gt;&lt;/books&gt;</code>) or unwrapped
     * (<code>&lt;book/&gt;&lt;book/&gt;</code>). Default value is <code>false</code>. The definition takes effect only
     * when defined alongside <code>type</code> being <code>"array"</code> (outside the <code>items</code>). If
     * <code>nodeType</code> is present, this field <em>MUST NOT</em> be present.
     * <p>
     * <strong>Deprecated:</strong> Use <code>nodeType: "element"</code> instead of <code>wrapped: true</code>
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#xml-wrapped">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] wrapped() default {};

    /**
     * {@link OpenApiXml} raw JSON object {@link String}, merged with the existing JSON object created from the
     * serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
