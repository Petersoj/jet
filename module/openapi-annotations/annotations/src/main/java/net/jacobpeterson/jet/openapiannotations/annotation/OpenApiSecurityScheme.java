package net.jacobpeterson.jet.openapiannotations.annotation;

import com.google.gson.annotations.SerializedName;
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
 * {@link OpenApiSecurityScheme} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-object">OpenAPI Security Scheme Object</a>.
 * <p>
 * Defines a security scheme that can be used by the operations.
 * <p>
 * Supported schemes are HTTP authentication, an API key (either as a header, a cookie parameter or as a query
 * parameter), mutual TLS (use of a client certificate), OAuth2’s common flows (implicit, password, client credentials
 * and authorization code) as defined in <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc6749">RFC6749</a>,
 * OAuth2 device authorization flow as defined in
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc8628">RFC8628</a>, and
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-openid-connect-core">OpenID-Connect-Core</a>. Please note that
 * as of 2020, the implicit flow is about to be deprecated by
 * <a href="https://tools.ietf.org/html/draft-ietf-oauth-security-topics">OAuth 2.0 Security Best Current Practice</a>.
 * Recommended for most use cases is Authorization Code Grant flow with PKCE.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiSecurityScheme {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiSecurityScheme} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiSecurityScheme[] value() default {};
    }

    /**
     * Applies to: <td>Any</td>
     * <p>
     * <strong><em>REQUIRED</em></strong>. The type of the security scheme. Valid values are <code>"apiKey"</code>,
     * <code>"http"</code>, <code>"mutualTLS"</code>, <code>"oauth2"</code>, <code>"openIdConnect"</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-type">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    Type[] type() default {};

    /**
     * {@link Type} is an enum for {@link #type()}.
     */
    enum Type {

        @SerializedName("apiKey")
        APIKEY,

        @SerializedName("http")
        HTTP,

        @SerializedName("mutualTLS")
        MUTUAL_TLS,

        @SerializedName("oauth2")
        OAUTH2,

        @SerializedName("openIdConnect")
        OPENID_CONNECT
    }

    /**
     * Applies to: <td>Any</td>
     * <p>
     * A description for security scheme.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * Applies to: <td><code>apiKey</code></td>
     * <p>
     * <strong><em>REQUIRED</em></strong>. The name of the header, query or cookie parameter to be used.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * Applies to: <td><code>apiKey</code></td>
     * <p>
     * <strong><em>REQUIRED</em></strong>. The location of the API key. Valid values are <code>"query"</code>,
     * <code>"header"</code>, or <code>"cookie"</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-in">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    In[] in() default {};

    /**
     * {@link In} is an enum for {@link #in()}.
     */
    enum In {

        @SerializedName("query")
        QUERY,

        @SerializedName("header")
        HEADER,

        @SerializedName("cookie")
        COOKIE
    }

    /**
     * Applies to: <td><code>http</code></td>
     * <p>
     * <strong><em>REQUIRED</em></strong>. The name of the HTTP Authentication scheme to be used in the Authorization
     * header as defined in <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc9110">RFC9110</a>
     * <a href="https://datatracker.ietf.org/doc/html/rfc9110#section-16.4.1">Section 16.4.1</a>. The values used
     * <em>SHOULD</em> be registered in the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-iana-http-authschemes">IANA Authentication Scheme
     * registry</a>. The value is case-insensitive, as defined in
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc9110">RFC9110</a>
     * <a href="https://datatracker.ietf.org/doc/html/rfc9110#section-11.1">Section 11.1</a>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-scheme">spec.openapis.org</a>
     */
    String scheme() default "";

    /**
     * Applies to: <td><code>http</code> (<code>"bearer"</code>)</td>
     * <p>
     * A hint to the client to identify how the bearer token is formatted. Bearer tokens are usually generated by an
     * authorization server, so this information is primarily for documentation purposes.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-bearer-format">spec.openapis.org</a>
     */
    String bearerFormat() default "";

    /**
     * Applies to: <td><code>oauth2</code></td>
     * <p>
     * <strong><em>REQUIRED</em></strong>. An object containing configuration information for the flow types supported.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-flows">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiOAuthFlows[] flows() default {};

    /**
     * Applies to: <td><code>openIdConnect</code></td>
     * <p>
     * <strong><em>REQUIRED</em></strong>.
     * <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig">Well-known URL</a> to
     * discover the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-openid-connect-discovery">OpenID-Connect-Discovery</a>
     * <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata">provider metadata</a>.
     *
     * @see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-open-id-connect-url">spec.openapis.org</a>
     */
    String openIdConnectUrl() default "";

    /**
     * Applies to: <td><code>oauth2</code></td>
     * <p>
     * URL to the OAuth2 authorization server metadata
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc8414">RFC8414</a>. TLS is required.
     *
     * @see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-oauth2-metadata-url">spec.openapis.org</a>
     */
    String oauth2MetadataUrl() default "";

    /**
     * Applies to: <td>Any</td>
     * <p>
     * Declares this security scheme to be deprecated. Consumers <em>SHOULD</em> refrain from usage of the declared
     * scheme. Default value is <code>false</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-scheme-deprecated">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    boolean[] deprecated() default {};
}
