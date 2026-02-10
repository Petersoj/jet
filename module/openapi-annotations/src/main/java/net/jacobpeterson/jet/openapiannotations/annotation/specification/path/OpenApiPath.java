package net.jacobpeterson.jet.openapiannotations.annotation.specification.path;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationMethodIsValue;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiPath} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#paths-object">OpenAPI Paths Object</a>.
 * <p>
 * Holds the relative paths to the individual endpoints and their operations. The path is appended to the URL from the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#server-object">Server Object</a> in order to construct the full
 * URL. The Paths Object <em>MAY</em> be empty, due to
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-filtering">Access Control List (ACL) constraints</a>.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#paths-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiPath {

    /**
     * The {@link OpenApi#paths()} entry key.
     * <p>
     * A relative path to an individual endpoint. The field name <em>MUST</em> begin with a forward slash
     * (<code>/</code>). The URL from the <a href="https://spec.openapis.org/oas/v3.2.0.html#server-object">Server
     * Object</a>’s <code>url</code> field, resolved and with template variables substituted, has the path
     * <strong>appended</strong> (no relative URL resolution) to it in order to construct the full URL.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-templating">Path templating</a> is allowed. When matching
     * URLs, concrete (non-templated) paths would be matched before their templated counterparts. Templated paths with
     * the same hierarchy but different templated names <em>MUST NOT</em> exist as they are identical. In case of
     * ambiguous matching, it’s up to the tooling to decide which one to use.
     */
    @AnnotationJsonIgnore
    @AnnotationArrayIsMapKey
    String path() default "";

    /**
     * The {@link OpenApi#paths()} entry value {@link OpenApiPathItem}.
     * <p>
     * Note: this array must only contain one element (see {@link AnnotationArrayIsNullableValue}).
     */
    @AnnotationArrayIsNullableValue
    @AnnotationMethodIsValue
    OpenApiPathItem[] item() default {};
}
