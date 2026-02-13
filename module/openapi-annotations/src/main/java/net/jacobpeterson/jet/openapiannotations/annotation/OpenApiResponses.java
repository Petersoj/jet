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
 * {@link OpenApiResponses} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#responses-object">OpenAPI Responses Object</a>.
 * <p>
 * A container for the expected responses of an operation. The container maps a HTTP response code to the expected
 * response.
 * <p>
 * The documentation is not necessarily expected to cover all possible HTTP response codes because they may not be
 * known in advance. However, documentation is expected to cover a successful operation response and any known errors.
 * <p>
 * The <code>default</code> <em>MAY</em> be used as a default Response Object for all HTTP codes that are not covered
 * individually by the Responses Object.
 * <p>
 * The Responses Object <em>MUST</em> contain at least one response code, and if only one response code is provided
 * it <em>SHOULD</em> be the response for a successful operation call.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#responses-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiResponses {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiResponses} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiResponses[] value() default {};
    }

    /**
     * Any <a href="https://spec.openapis.org/oas/v3.2.0.html#http-status-codes">HTTP status code</a> can be used as the
     * property name, but only one property per code, to describe the expected response for that HTTP status code. This
     * field <em>MUST</em> be enclosed in quotation marks (for example, “200”) for compatibility between JSON and YAML.
     * To define a range of response codes, this field <em>MAY</em> contain the uppercase wildcard character
     * <code>X</code>. For example, <code>2XX</code> represents all response codes between <code>200</code> and
     * <code>299</code>. Only the following range definitions are allowed: <code>1XX</code>, <code>2XX</code>,
     * <code>3XX</code>, <code>4XX</code>, and <code>5XX</code>. If a response is defined using an explicit code, the
     * explicit code definition takes precedence over the range definition for that code.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#responses-code">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    @AnnotationJsonObjectInline
    OpenApiResponse.MapEntry[] value() default {};

    /**
     * @see #value()
     */
    @AnnotationArrayIsMap
    @AnnotationJsonObjectInline
    @SerializedName("responses")
    OpenApiReference.MapEntry[] valueReferences() default {};
}
