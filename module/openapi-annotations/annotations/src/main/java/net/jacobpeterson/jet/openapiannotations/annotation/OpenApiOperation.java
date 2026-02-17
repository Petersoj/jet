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
 * {@link OpenApiOperation} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-object">OpenAPI Operation Object</a>.
 * <p>
 * Describes a single API operation on a path.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiOperation {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiOperation} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiOperation[] value() default {};
    }

    /**
     * A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or
     * any other qualifier.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-tags">spec.openapis.org</a>
     */
    String[] tags() default {};

    /**
     * A short summary of what the operation does.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-summary">spec.openapis.org</a>
     */
    String summary() default "";

    /**
     * A verbose explanation of the operation behavior.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * Additional external documentation for this operation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-external-docs">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiExternalDoc[] externalDocs() default {};

    /**
     * Unique string used to identify the operation. The id <em>MUST</em> be unique among all operations described in
     * the API. The operationId value is <strong>case-sensitive</strong>. Tools and libraries <em>MAY</em> use the
     * operationId to uniquely identify an operation, therefore, it is <em>RECOMMENDED</em> to follow common programming
     * naming conventions.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-id">spec.openapis.org</a>
     */
    String operationId() default "";

    /**
     * A list of parameters that are applicable for this operation. If a parameter is already defined at the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-parameters">Path Item</a>, the new definition will
     * override it but can never remove it. The list <em>MUST NOT</em> include duplicated parameters. A unique parameter
     * is defined by a combination of a <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-name">name</a> and
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-in">location</a>. The list can use the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#reference-object">Reference Object</a> to link to parameters
     * that are defined in the <a href="https://spec.openapis.org/oas/v3.2.0.html#components-parameters">OpenAPI
     * Object’s <code>components.parameters</code></a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-parameters">spec.openapis.org</a>
     */
    OpenApiParameter[] parameters() default {};

    /**
     * @see #parameters()
     */
    @SerializedName("parameters")
    OpenApiReference[] parameterReferences() default {};

    /**
     * The request body applicable for this operation. The <code>requestBody</code> is fully supported in HTTP methods
     * where the HTTP specification <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc9110">RFC9110</a>
     * <a href="https://datatracker.ietf.org/doc/html/rfc9110#section-9.3">Section 9.3</a> has explicitly defined
     * semantics for request bodies. In other cases where the HTTP spec discourages message content (such as
     * <a href="https://tools.ietf.org/html/rfc9110#section-9.3.1">GET</a> and
     * <a href="https://tools.ietf.org/html/rfc9110#section-9.3.5">DELETE</a>), <code>requestBody</code> is permitted
     * but does not have well-defined semantics and <em>SHOULD</em> be avoided if possible.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-request-body">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiRequestBody[] requestBody() default {};

    /**
     * @see #requestBody()
     */
    @AnnotationArrayIsNullableValue
    @SerializedName("requestBody")
    OpenApiReference[] requestBodyReference() default {};

    /**
     * The list of possible responses as they are returned from executing this operation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-responses">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiResponses[] responses() default {};

    /**
     * A map of possible out-of band callbacks related to the parent operation. The key is a unique identifier for the
     * Callback Object. Each value in the map is a
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#callback-object">Callback Object</a> that describes a request
     * that may be initiated by the API provider and the expected responses.
     * <p>
     * Note: {@link OpenApiOperation} has no <code>callbacks()</code> method because using {@link OpenApiCallback}
     * directly in {@link OpenApiOperation} causes a cyclic annotation compiler error.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-callbacks">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    @SerializedName("callbacks")
    OpenApiReference.MapEntry[] callbackReferences() default {};

    /**
     * Declares this operation to be deprecated. Consumers <em>SHOULD</em> refrain from usage of the declared operation.
     * Default value is <code>false</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-deprecated">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] deprecated() default {};

    /**
     * A declaration of which security mechanisms can be used for this operation. The list of values includes
     * alternative Security Requirement Objects that can be used. Only one of the Security Requirement Objects need to
     * be satisfied to authorize a request. To make security optional, an empty security requirement (<code>{}</code>)
     * can be included in the array. This definition overrides any declared top-level
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-security"><code>security</code></a>. To remove a top-level
     * security declaration, an empty array can be used.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-security">spec.openapis.org</a>
     */
    OpenApiSecurityRequirement[] security() default {};

    /**
     * An alternative <code>servers</code> array to service this operation. If a <code>servers</code> array is specified
     * at the <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-servers">Path Item Object</a> or
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-servers">OpenAPI Object</a> level, it will be overridden
     * by this value.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-servers">spec.openapis.org</a>
     */
    OpenApiServer[] servers() default {};

    /**
     * {@link OpenApiOperation} raw JSON.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
