package net.jacobpeterson.jet.openapiannotations.annotation;

import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationsValidationLevel;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.component.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.externaldoc.OpenApiExternalDoc;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.path.OpenApiPath;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.securityrequirement.OpenApiSecurityRequirements;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.tag.OpenApiTag;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.webhook.OpenApiWebhook;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationsValidationLevel.ERROR;

/**
 * {@link OpenApi} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#openapi-object">OpenAPI Object</a>.
 * <p>
 * This is the root object of the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#openapi-description-structure#openapi-description-structure">
 * OpenAPI Description</a>.
 * <p>
 * In addition to the required fields, at least one of the <code>components</code>, <code>paths</code>, or
 * <code>webhooks</code> fields <em>MUST</em> be present.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#openapi-object">spec.openapis.org</a>
 */
@NullMarked
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Repeatable(OpenApis.class)
public @interface OpenApi {

    /**
     * OpenAPI annotations can be grouped into different OpenAPI specification JSON outputs according to their
     * <em>annotation group name</em>. This {@link String} constant acts as a reference for the default annotation
     * group, which is just an empty {@link String}.
     */
    String DEFAULT_ANNOTATION_GROUP_NAME = "";

    /**
     * The default value for {@link #$schema()}.
     *
     * @see <a href="https://spec.openapis.org/oas/3.2/schema/2025-09-17.html">spec.openapis.org</a>
     */
    String DEFAULT_SCHEMA = "https://spec.openapis.org/oas/3.2/schema/2025-09-17";

    /**
     * The default value for {@link #openapi()}.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html">spec.openapis.org</a>
     */
    String DEFAULT_OPENAPI = "3.2.0";

    /**
     * The name of the {@link #DEFAULT_ANNOTATION_GROUP_NAME annotation group} this annotation should be assigned to.
     * <p>
     * Defaults to {@link #DEFAULT_ANNOTATION_GROUP_NAME}.
     */
    @AnnotationJsonIgnore
    String annotationGroupName() default DEFAULT_ANNOTATION_GROUP_NAME;

    /**
     * The {@link AnnotationsValidationLevel}.
     * <p>
     * Defaults to {@link AnnotationsValidationLevel#ERROR}.
     */
    @AnnotationJsonIgnore
    AnnotationsValidationLevel annotationsValidationLevel() default ERROR;

    /**
     * The URL of the OpenAPI Specification JSON schema.
     * <p>
     * Defaults to {@link #DEFAULT_SCHEMA}.
     */
    String $schema() default DEFAULT_SCHEMA;

    /**
     * <strong><em>REQUIRED</em></strong>. This string <em>MUST</em> be the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#versions-and-deprecation">version number</a> of the OpenAPI
     * Specification that the OpenAPI document uses. The <code>openapi</code> field <em>SHOULD</em> be used by tooling
     * to interpret the OpenAPI document. This is <em>not</em> related to the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#info-version"><code>info.version</code></a> string, which
     * describes the OpenAPI document’s version.
     * <p>
     * Defaults to {@link #DEFAULT_OPENAPI}.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-version">spec.openapis.org</a>
     */
    String openapi() default DEFAULT_OPENAPI;

    /**
     * This string <em>MUST</em> be in the form of a URI reference as defined by
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc3986">RFC3986</a>
     * <a href="https://tools.ietf.org/html/rfc3986#section-4.1">Section 4.1</a>. The <code>$self</code> field provides
     * the self-assigned URI of this document, which also serves as its base URI in accordance with
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc3986">RFC3986</a>
     * <a href="https://tools.ietf.org/html/rfc3986#section-5.1.1">Section 5.1.1</a>. Implementations <em>MUST</em>
     * support identifying the targets of
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#relative-references-in-api-description-uris">API description
     * URIs</a> using the URI defined by this field when it is present. See
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#establishing-the-base-uri">Establishing the Base URI</a> for
     * the base URI behavior when <code>$self</code> is absent or relative, and see
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#appendix-f-examples-of-base-uri-determination-and-reference-resolution">
     * Appendix F</a> for examples of using <code>$self</code> to resolve references.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-self">spec.openapis.org</a>
     */
    String $self() default "";

    /**
     * <strong><em>REQUIRED</em></strong>. Provides metadata about the API. The metadata <em>MAY</em> be used by tooling
     * as required.
     * <p>
     * Note: this array must only contain one element (see {@link AnnotationArrayIsNullableValue}).
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-info">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiInfo[] info() default {};

    /**
     * The default value for the <code>$schema</code> keyword within
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">Schema Objects</a> contained within this OAS
     * document. This <em>MUST</em> be in the form of a URI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-json-schema-dialect">spec.openapis.org</a>
     */
    String jsonSchemaDialect() default "";

    /**
     * An array of Server Objects, which provide connectivity information to a target server. If the
     * <code>servers</code> field is not provided, or is an empty array, the default value would be an array consisting
     * of a single <a href="https://spec.openapis.org/oas/v3.2.0.html#server-object">Server Object</a> with a
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#server-url">url</a> value of <code>/</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-servers">spec.openapis.org</a>
     */
    OpenApiServer[] servers() default {};

    /**
     * The available paths and operations for the API.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-paths">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiPath[] paths() default {};

    /**
     * The incoming webhooks that <em>MAY</em> be received as part of this API and that the API consumer <em>MAY</em>
     * choose to implement. Closely related to the <code>callbacks</code> feature, this section describes requests
     * initiated other than by an API call, for example by an out of band registration. The key name is a unique string
     * to refer to each webhook, while the (optionally referenced) Path Item Object describes a request that may be
     * initiated by the API provider and the expected responses. An
     * <a href="https://learn.openapis.org/examples/v3.1/webhook-example.html">example</a> is available.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-webhooks">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiWebhook[] webhooks() default {};

    /**
     * An element to hold various Objects for the OpenAPI Description.
     * <p>
     * Note: this array must only contain one element (see {@link AnnotationArrayIsNullableValue}).
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-components">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiComponents[] components() default {};

    /**
     * A declaration of which security mechanisms can be used across the API. The list of values includes alternative
     * Security Requirement Objects that can be used. Only one of the Security Requirement Objects need to be satisfied
     * to authorize a request. Individual operations can override this definition. The list can be incomplete, up to
     * being empty or absent. To make security explicitly optional, an empty security requirement (<code>{}</code>) can
     * be included in the array.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-security">spec.openapis.org</a>
     */
    OpenApiSecurityRequirements[] security() default {};

    /**
     * A list of tags used by the OpenAPI Description with additional metadata. The order of the tags can be used to
     * reflect on their order by the parsing tools. Not all tags that are used by the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#operation-object">Operation Object</a> must be declared. The
     * tags that are not declared <em>MAY</em> be organized randomly or based on the tools’ logic. Each tag name in the
     * list <em>MUST</em> be unique.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-tags">spec.openapis.org</a>
     */
    OpenApiTag[] tags() default {};

    /**
     * Additional external documentation.
     * <p>
     * Note: this array must only contain one element (see {@link AnnotationArrayIsNullableValue}).
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-external-docs">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiExternalDoc[] externalDocs() default {};
}
