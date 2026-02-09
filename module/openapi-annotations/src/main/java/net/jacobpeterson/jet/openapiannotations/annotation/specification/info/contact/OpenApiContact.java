package net.jacobpeterson.jet.openapiannotations.annotation.specification.info.contact;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiContact} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-object">OpenAPI Contact Object</a>.
 * <p>
 * Contact information for the exposed API.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-object">spec.openapis.org</a>
 */
@Target({})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiContact {

    /**
     * The identifying name of the contact person/organization.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * The URI for the contact information. This <em>MUST</em> be in the form of a URI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-url">spec.openapis.org</a>
     */
    String url() default "";

    /**
     * The email address of the contact person/organization. This <em>MUST</em> be in the form of an email address.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#contact-email">spec.openapis.org</a>
     */
    String email() default "";
}
