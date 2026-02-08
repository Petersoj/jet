package net.jacobpeterson.jet.openapiannotations.annotation.externaldoc;

import net.jacobpeterson.jet.openapiannotations.OpenApiAnnotationsProcessor;
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
 * {@link OpenApiExternalDoc} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#external-documentation-object">OpenAPI External Documentation
 * Object</a>.
 * <p>
 * Allows referencing an external resource for extended documentation.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#external-documentation-object">spec.openapis.org</a>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Repeatable(OpenApiExternalDocs.class)
@NullMarked
public @interface OpenApiExternalDoc {

    /**
     * The name of the {@link OpenApiAnnotationsProcessor#DEFAULT_ANNOTATION_GROUP_NAME annotation group} this
     * annotation should be assigned to.
     */
    @AnnotationJsonSerializerExclude
    String annotationGroupName() default DEFAULT_ANNOTATION_GROUP_NAME;

    /**
     * A description of the target documentation.
     * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-commonmark">CommonMark</a> syntax <em>MAY</em> be used for
     * rich text representation.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#external-doc-description">spec.openapis.org</a>
     */
    String description() default "";

    /**
     * <strong><em>REQUIRED</em></strong>. The URI for the target documentation. This <em>MUST</em> be in the form of a
     * URI.
     *
     * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#external-doc-url">spec.openapis.org</a>
     */
    String url() default "";
}
