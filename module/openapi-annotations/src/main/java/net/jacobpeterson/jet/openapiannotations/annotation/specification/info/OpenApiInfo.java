package net.jacobpeterson.jet.openapiannotations.annotation.specification.info;

import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.contact.OpenApiContact;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.license.OpenApiLicense;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiInfo} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#info-object">OpenAPI Info Object</a>.
 * <p>
 * The object provides metadata about the API. The metadata <em>MAY</em> be used by the clients if needed, and
 * <em>MAY</em> be presented in editing or documentation generation tools for convenience.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiInfo {

    /**
     * <strong><em>REQUIRED</em></strong>. The title of the API.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-title">spec.openapis.org</a>
     */
    String title() default "";

    /**
     * A short summary of the API.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-summary">spec.openapis.org</a>
     */
    String summary() default "";

    /**
     * A description of the API. <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a>
     * syntax <em>MAY</em> be used for rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * A URI for the Terms of Service for the API. This <em>MUST</em> be in the form of a URI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-terms-of-service">spec.openapis.org</a>
     */
    String termsOfService() default "";

    /**
     * The contact information for the exposed API.
     * <p>
     * Note: this array must only contain one element (see {@link AnnotationArrayIsNullableValue}).
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-contact">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiContact[] contact() default {};

    /**
     * The license information for the exposed API.
     * <p>
     * Note: this array must only contain one element (see {@link AnnotationArrayIsNullableValue}).
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-license">spec.openapis.org</a>
     */
    @AnnotationArrayIsNullableValue
    OpenApiLicense[] license() default {};

    /**
     * <strong><em>REQUIRED</em></strong>. The version of the OpenAPI document (which is distinct from the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-version">OpenAPI Specification version</a> or the version
     * of the API being described or the version of the OpenAPI Description).
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#info-version">spec.openapis.org</a>
     */
    String version() default "";
}
