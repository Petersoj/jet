package net.jacobpeterson.jet.openapiannotations;

import net.jacobpeterson.jet.openapiannotations.meta.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationJsonRawString;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiOAuthFlows} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flows-object">OpenAPI OAuth Flows Object</a>.
 * <p>
 * Allows configuration of the supported OAuth Flows.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flows-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiOAuthFlows {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiOAuthFlows} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiOAuthFlows[] value() default {};
    }

    /**
     * Configuration for the OAuth Implicit flow
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flows-implicit">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOAuthFlow[] implicit() default {};

    /**
     * Configuration for the OAuth Resource Owner Password flow
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flows-password">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOAuthFlow[] password() default {};

    /**
     * Configuration for the OAuth Client Credentials flow. Previously called <code>application</code> in OpenAPI 2.0.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flows-client-credentials">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOAuthFlow[] clientCredentials() default {};

    /**
     * Configuration for the OAuth Authorization Code flow. Previously called <code>accessCode</code> in OpenAPI 2.0.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flows-authorization-code">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOAuthFlow[] authorizationCode() default {};

    /**
     * Configuration for the OAuth Device Authorization flow.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flows-device-authorization">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOAuthFlow[] deviceAuthorization() default {};

    /**
     * {@link OpenApiOAuthFlows} raw JSON object {@link String}, merged with the existing JSON object created from
     * the serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
