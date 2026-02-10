package net.jacobpeterson.jet.openapiannotations.annotation.specification.component;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiComponents} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#components-object">OpenAPI Components Object</a>.
 * <p>
 * Holds a set of reusable objects for different aspects of the OAS. All objects defined within the Components Object
 * will have no effect on the API unless they are explicitly referenced from outside the Components Object.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#components-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiComponents {

    // TODO
}
