package net.jacobpeterson.jet.openapiannotations.annotation;

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
 * {@link OpenApiCallback} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#callback-object">OpenAPI Callback Object</a>.
 * <p>
 * A map of possible out-of band callbacks related to the parent operation. Each value in the map is a
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object#path-item-object">Path Item Object</a> that
 * describes a set of requests that may be initiated by the API provider and the expected responses. The key value used
 * to identify the Path Item Object is an expression, evaluated at runtime, that identifies a URL to use for the
 * callback operation.
 * <p>
 * To describe incoming requests from the API provider independent from another API call, use the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-webhooks"><code>webhooks</code></a> field.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#callback-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiCallback {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiCallback} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiCallback[] value() default {};
    }

    /**
     * A Path Item Object used to define a callback request and expected responses. A
     * <a href="https://learn.openapis.org/examples/v3.0/callback-example.html">complete example</a> is available.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#callback-expression">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    @AnnotationJsonObjectInline
    OpenApiPathItem.MapEntry[] value() default {};

    /**
     * {@link OpenApiCallback} raw JSON object {@link String}, merged with the existing JSON object created from the
     * serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
