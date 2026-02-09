package net.jacobpeterson.jet.openapiannotations.annotation.specification.info.license;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiLicense} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#license-object">OpenAPI License Object</a>.
 * <p>
 * License information for the exposed API.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#license-object">spec.openapis.org</a>
 */
@Target({})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiLicense {

    /**
     * <strong><em>REQUIRED</em></strong>. The license name used for the API.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#license-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * An <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-spdx-licenses">SPDX License List</a> expression for the
     * API. The <code>identifier</code> field is mutually exclusive of the <code>url</code> field.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#license-identifier">spec.openapis.org</a>
     */
    String identifier() default "";

    /**
     * A URI for the license used for the API. This <em>MUST</em> be in the form of a URI. The <code>url</code> field
     * is mutually exclusive of the <code>identifier</code> field.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#license-url">spec.openapis.org</a>
     */
    String url() default "";
}
