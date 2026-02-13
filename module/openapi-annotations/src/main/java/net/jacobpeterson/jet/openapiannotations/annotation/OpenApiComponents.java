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
 * {@link OpenApiComponents} is an annotation for the
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
public @interface OpenApiComponents {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiComponents} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiComponents[] value() default {};
    }

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">Schema Objects</a>.
     * <p>
     * Note: {@link OpenApiComponents} has no <code>schemaReferences()</code> method because {@link OpenApiSchema}
     * already has a <code>$ref()</code> method.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-schemas">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiSchema.MapEntry[] schemas() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#response-object">Response Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-responses">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiResponse.MapEntry[] responses() default {};

    /**
     * @see #responses()
     */
    @AnnotationArrayIsMap
    @SerializedName("responses")
    OpenApiReference.MapEntry[] responseReferences() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#parameter-object">Parameter Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-parameters">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiParameter.MapEntry[] parameters() default {};

    /**
     * @see #parameters()
     */
    @AnnotationArrayIsMap
    @SerializedName("parameters")
    OpenApiReference.MapEntry[] parameterReferences() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#example-object">Example Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-examples">spec.openapis.org</a>
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
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#request-body-object">Request Body Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-request-bodies">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiRequestBody.MapEntry[] requestBodies() default {};

    /**
     * @see #requestBodies()
     */
    @AnnotationArrayIsMap
    @SerializedName("requestBodies")
    OpenApiReference.MapEntry[] requestBodyReferences() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#header-object">Header Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-headers">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiHeader.MapEntry[] headers() default {};

    /**
     * @see #headers()
     */
    @AnnotationArrayIsMap
    @SerializedName("headers")
    OpenApiReference.MapEntry[] headerReferences() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-object">Security Scheme Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-security-schemes">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiSecurityScheme.MapEntry[] securitySchemes() default {};

    /**
     * @see #securitySchemes()
     */
    @AnnotationArrayIsMap
    @SerializedName("securitySchemes")
    OpenApiReference.MapEntry[] securitySchemeReferences() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#link-object">Link Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-links">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiLink.MapEntry[] links() default {};

    /**
     * @see #links()
     */
    @AnnotationArrayIsMap
    @SerializedName("links")
    OpenApiReference.MapEntry[] linkReferences() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#callback-object">Callback Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-callbacks">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiCallback.MapEntry[] callbacks() default {};

    /**
     * @see #callbacks()
     */
    @AnnotationArrayIsMap
    @SerializedName("callbacks")
    OpenApiReference.MapEntry[] callbackReferences() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object">Path Item Objects</a>.
     * <p>
     * Note: {@link OpenApiComponents} has no <code>pathReferences()</code> method because {@link OpenApiPathItem}
     * already has a <code>$ref()</code> method.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-path-items">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiPathItem.MapEntry[] pathItems() default {};

    /**
     * An object to hold reusable
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#media-type-object">Media Type Objects</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-media-types">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiMediaType.MapEntry[] mediaTypes() default {};

    /**
     * @see #mediaTypes()
     */
    @AnnotationArrayIsMap
    @SerializedName("mediaTypes")
    OpenApiReference.MapEntry[] mediaTypeReferences() default {};
}
