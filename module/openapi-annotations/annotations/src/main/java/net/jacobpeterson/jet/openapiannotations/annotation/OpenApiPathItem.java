package net.jacobpeterson.jet.openapiannotations.annotation;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonObjectInline;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiPathItem} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object">OpenAPI Path Item Object</a>.
 * <p>
 * Describes the operations available on a single path. A Path Item <em>MAY</em> be empty, due to
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-filtering">ACL constraints</a>. The path itself is still
 * exposed to the documentation viewer but they will not know which operations and parameters are available.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiPathItem {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiPathItem} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiPathItem[] value() default {};
    }

    /**
     * Allows for a referenced definition of this path item. The value <em>MUST</em> be in the form of a URI, and the
     * referenced structure <em>MUST</em> be in the form of a
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object">Path Item Object</a>. In case a Path Item
     * Object field appears both in the defined object and the referenced object, the behavior is undefined. See the
     * rules for resolving
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#relative-references-in-api-description-uris">Relative
     * References</a>.
     * <p>
     * <em><strong>Note:</strong> The behavior of <code>$ref</code> with adjacent properties is likely to change in
     * future versions of this specification to bring it into closer alignment with the behavior of the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-object">Reference Object</a>.</em>
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-ref">spec.openapis.org</a>
     */
    String $ref() default "";

    /**
     * An optional string summary, intended to apply to all operations in this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-summary">spec.openapis.org</a>
     */
    String summary() default "";

    /**
     * An optional string description, intended to apply to all operations in this path.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * A definition of a GET operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-get">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] get() default {};

    /**
     * A definition of a PUT operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-put">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] put() default {};

    /**
     * A definition of a POST operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-post">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] post() default {};

    /**
     * A definition of a DELETE operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-delete">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] delete() default {};

    /**
     * A definition of a OPTIONS operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-options">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] options() default {};

    /**
     * A definition of a HEAD operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-head">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] head() default {};

    /**
     * A definition of a PATCH operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-patch">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] patch() default {};

    /**
     * A definition of a TRACE operation on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-trace">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] trace() default {};

    /**
     * A definition of a QUERY operation, as defined in the most recent IETF draft
     * (<a href="https://www.ietf.org/archive/id/draft-ietf-httpbis-safe-method-w-body-11.html">
     * draft-ietf-httpbis-safe-method-w-body-08</a> as of this writing) or its RFC successor, on this path.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-query">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOperation[] query() default {};

    /**
     * A map of additional operations on this path. The map key is the HTTP method with the same capitalization that is
     * to be sent in the request. This map <em>MUST NOT</em> contain any entry for the methods that can be defined by
     * other fixed fields with Operation Object values (e.g. no <code>POST</code> entry, as the <code>post</code> field
     * is used for this method).
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-additional-operations">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiOperation.MapEntry[] additionalOperations() default {};

    /**
     * Instead of using {@link #get()}, {@link #put()}, {@link #post()}, {@link #delete()}, {@link #options()},
     * {@link #head()}, {@link #patch()}, {@link #trace()}, or {@link #query()} directly, this uses the {@link Method}
     * enum mapped to a {@link OpenApiOperation}.
     */
    @AnnotationArrayIsMap
    @AnnotationJsonObjectInline
    ForEnum[] forEnum() default {};

    /**
     * {@link ForEnum} is an annotation for an entry in the {@link #forEnum()} map.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface ForEnum { //@formatter:on

        /**
         * The {@link #forEnum()} entry key.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        @AnnotationArrayIsNullableValue
        Method[] keyEnum() default {};

        /**
         * The {@link #forEnum()} entry value.
         */
        @AnnotationArrayIsNullableValue
        @AnnotationJsonObjectInline
        OpenApiOperation[] value() default {};
    }

    /**
     * An alternative <code>servers</code> array to service all operations in this path. If a <code>servers</code> array
     * is specified at the <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-servers">OpenAPI Object</a> level, it
     * will be overridden by this value.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-servers">spec.openapis.org</a>
     */
    OpenApiServer[] servers() default {};

    /**
     * A list of parameters that are applicable for all the operations described under this path. These parameters can
     * be overridden at the operation level, but cannot be removed there. The list <em>MUST NOT</em> include duplicated
     * parameters. A unique parameter is defined by a combination of a
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-name">name</a> and
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in">location</a>. The list can use the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-object">Reference Object</a> to link to parameters
     * that are defined in the <a href="https://spec.openapis.org/oas/v3.2.0.html#components-parameters">OpenAPI
     * Object’s <code>components.parameters</code></a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-parameters">spec.openapis.org</a>
     */
    OpenApiParameter[] parameters() default {};

    /**
     * @see #parameters()
     */
    @SerializedName("parameters")
    OpenApiReference[] parameterReferences() default {};
}
