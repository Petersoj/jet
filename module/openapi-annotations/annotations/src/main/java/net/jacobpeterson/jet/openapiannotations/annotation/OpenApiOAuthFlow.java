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
 * {@link OpenApiOAuthFlow} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flow-object">OpenAPI OAuth Flow Object</a>.
 * <p>
 * Configuration details for a supported OAuth Flow.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flow-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiOAuthFlow {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiOAuthFlow} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiOAuthFlow[] value() default {};
    }

    /**
     * Applies to: <code>oauth2</code> (<code>"implicit"</code>, <code>"authorizationCode"</code>)
     * <p>
     * <strong><em>REQUIRED</em></strong>. The authorization URL to be used for this flow. This <em>MUST</em> be in the
     * form of a URL. The OAuth2 standard requires the use of TLS.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flow-authorization-url">spec.openapis.org/</a>
     */
    String authorizationUrl() default "";

    /**
     * Applies to: <code>oauth2</code> (<code>"deviceAuthorization"</code>)
     * <p>
     * <strong><em>REQUIRED</em></strong>. The device authorization URL to be used for this flow. This <em>MUST</em> be
     * in the form of a URL. The OAuth2 standard requires the use of TLS.
     *
     * @see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flow-device-authorization-url">spec.openapis.org/</a>
     */
    String deviceAuthorizationUrl() default "";

    /**
     * Applies to: <code>oauth2</code> (<code>"password"</code>, <code>"clientCredentials"</code>,
     * <code>"authorizationCode"</code>, <code>"deviceAuthorization"</code>)
     * <p>
     * <strong><em>REQUIRED</em></strong>. The token URL to be used for this flow. This <em>MUST</em> be in the form of
     * a URL. The OAuth2 standard requires the use of TLS.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flow-token-url">spec.openapis.org/</a>
     */
    String tokenUrl() default "";

    /**
     * Applies to: <code>oauth2</code>
     * <p>
     * The URL to be used for obtaining refresh tokens. This <em>MUST</em> be in the form of a URL. The OAuth2 standard
     * requires the use of TLS.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flow-refresh-url">spec.openapis.org/</a>
     */
    String refreshUrl() default "";

    /**
     * Applies to: <code>oauth2</code>
     * <p>
     * <strong><em>REQUIRED</em></strong>. The available scopes for the OAuth2 security scheme. A map between the scope
     * name and a short description for it. The map <em>MAY</em> be empty.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oauth-flow-scopes">spec.openapis.org/</a>
     */
    @AnnotationArrayIsMap
    Scope[] scopes() default {};

    /**
     * {@link Scope} is an annotation an entry in the {@link #scopes()} map.
     */
    @Target({})
    @Retention(RUNTIME) //@formatter:off
    @interface Scope { //@formatter:on

        /**
         * The {@link #scopes()} map entry key.
         */
        @AnnotationJsonIgnore
        @AnnotationArrayIsMapKey
        String key() default "";

        /**
         * The {@link #scopes()} map entry value.
         */
        @AnnotationJsonObjectInline
        String value() default "";
    }

    /**
     * {@link OpenApiOAuthFlow} raw JSON object {@link String}, merged with the existing JSON object created from the
     * serialization of this {@link Annotation}.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
