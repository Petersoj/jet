package net.jacobpeterson.jet.openapiannotations.annotation.specification.server.variable;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiServerVariable} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#server-variable-object">OpenAPI Server Variable Object</a>.
 * <p>
 * An object representing a Server Variable for server URL template substitution.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-variable-object">spec.openapis.org</a>
 */
@Target({})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiServerVariable {

    /**
     * The variable name.
     */
    @AnnotationJsonIgnore
    @AnnotationArrayIsMapKey
    String name() default "";

    /**
     * An enumeration of string values to be used if the substitution options are from a limited set. The array
     * <em>MUST NOT</em> be empty.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-variable-enum">spec.openapis.org</a>
     */
    @SerializedName("enum")
    @SuppressWarnings("IdentifierName")
    String[] enum_() default {};

    /**
     * <strong><em>REQUIRED</em></strong>. The default value to use for substitution, which <em>SHALL</em> be sent if
     * an alternate value is <em>not</em> supplied. If the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#server-variable-enum"><code>enum</code></a>
     * is defined, the value <em>MUST</em> exist in the enum’s values. Note that this behavior is different from the
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">Schema Object's</a> <code>default</code>
     * keyword, which documents the receiver’s behavior rather than inserting the value into the data.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-variable-default">spec.openapis.org</a>
     */
    @SerializedName("default")
    @SuppressWarnings("IdentifierName")
    String default_() default "";

    /**
     * An optional description for the server variable.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a>
     * syntax <em>MAY</em> be used for rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-variable-description">spec.openapis.org</a>
     */
    String description() default "";
}
