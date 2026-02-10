package net.jacobpeterson.jet.openapiannotations.annotation.specification.path;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiPathItem} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object">OpenAPI Path Item Object</a>.
 * <p>
 * Describes the operations available on a single path. A Path Item <em>MAY</em> be empty, due to
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#security-filtering">ACL constraints</a>. The path itself is still
 * exposed to the documentation viewer but they will not know which operations and parameters are available.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#path-item-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiPathItem {

    // TODO
}
