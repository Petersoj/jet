package net.jacobpeterson.jet.openapiannotations.annotation;

import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonRawString;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiServer} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#server-object">OpenAPI Server Object</a>.
 * <p>
 * An object representing a Server.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-object">spec.openapis.org</a>
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiServer {

    /**
     * {@link MapEntry} is an annotation for an {@link OpenApiServer} entry in an {@link AnnotationArrayIsMap}
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
        OpenApiServer[] value() default {};
    }

    /**
     * <strong><em>REQUIRED</em></strong>. A URL to the target host. This URL supports Server Variables and <em>MAY</em>
     * be relative, to indicate that the host location is relative to the location where the document containing the
     * Server Object is being served. Query and fragment <em>MUST NOT</em> be part of this URL. Variable substitutions
     * will be made when a variable is named in <code>{</code>braces<code>}</code>.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-url">spec.openapis.org</a>
     */
    String url() default "";

    /**
     * An optional string describing the host designated by the URL.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * An optional unique string to refer to the host designated by the URL.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-name">spec.openapis.org</a>
     */
    String name() default "";

    /**
     * A map between a variable name and its value. The value is used for substitution in the server’s URL template.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-variables">spec.openapis.org</a>
     */
    @AnnotationArrayIsMap
    OpenApiServerVariable.MapEntry[] variables() default {};

    /**
     * {@link OpenApiServer} raw JSON.
     */
    @AnnotationJsonRawString
    @AnnotationJsonObjectInline
    String rawJson() default "";
}
