package net.jacobpeterson.jet.openapiannotations.annotation.specification.securityrequirement;

import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationMethodIsValue;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiSecurityRequirements} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-requirement-object">OpenAPI Security Requirement
 * Object</a>.
 * <p>
 * Lists the required security schemes to execute this operation.
 * <p>
 * The name used for each property <em>MUST</em> either correspond to a security scheme declared in the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#components-security-schemes">Security Schemes</a> under the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#components-object">Components Object</a>, or be the URI of a
 * Security Scheme Object. Property names that are identical to a component name under the Components Object
 * <em>MUST</em> be treated as a component name. To reference a Security Scheme with a single-segment relative URI
 * reference (e.g. <code>foo</code>) that collides with a component name
 * (e.g. <code>#/components/securitySchemes/foo</code>), use the <code>.</code> path segment (e.g. <code>./foo</code>).
 * <p>
 * Using a Security Scheme component name that appears to be a URI is <em>NOT RECOMMENDED</em>, as the precedence of
 * component-name-matching over URI resolution, which is necessary to maintain compatibility with prior OAS versions,
 * is counter-intuitive. See also <a href="https://spec.openapis.org/oas/v3.2.0.html#security-considerations">Security
 * Considerations</a>.
 * <p>
 * A Security Requirement Object <em>MAY</em> refer to multiple security schemes in which case all schemes <em>MUST</em>
 * be satisfied for a request to be authorized. This enables support for scenarios where multiple query parameters or
 * HTTP headers are required to convey security information.
 * <p>
 * When the <code>security</code> field is defined on the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#openapi-object">OpenAPI Object</a> or
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-object">Operation Object</a> and contains multiple
 * Security Requirement Objects, only one of the entries in the list needs to be satisfied to authorize the request.
 * This enables support for scenarios where the API allows multiple, independent security schemes.
 * <p>
 * An empty Security Requirement Object (<code>{}</code>) indicates anonymous access is supported.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#security-requirement-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiSecurityRequirements {

    /**
     * The {@link OpenApiSecurityRequirements} value.
     */
    @AnnotationArrayIsMap
    @AnnotationMethodIsValue
    OpenApiSecurityRequirement[] value() default {};
}
