package net.jacobpeterson.jet.openapiannotations.annotation.server;

import net.jacobpeterson.jet.openapiannotations.OpenApiAnnotationsProcessor;
import net.jacobpeterson.jet.openapiannotations.annotation.server.variable.OpenApiServerVariable;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationJsonSerializerExclude;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static net.jacobpeterson.jet.openapiannotations.OpenApiAnnotationsProcessor.DEFAULT_ANNOTATION_GROUP_NAME;

/**
 * {@link OpenApiServer} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#server-object">OpenAPI Server Object</a>.
 * <p>
 * An object representing a Server.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#server-object">spec.openapis.org</a>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Repeatable(OpenApiServers.class)
@NullMarked
public @interface OpenApiServer {

    /**
     * The name of the {@link OpenApiAnnotationsProcessor#DEFAULT_ANNOTATION_GROUP_NAME annotation group} this
     * annotation should be assigned to.
     */
    @AnnotationJsonSerializerExclude
    String annotationGroupName() default DEFAULT_ANNOTATION_GROUP_NAME;

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
    OpenApiServerVariable[] variables() default {};
}
