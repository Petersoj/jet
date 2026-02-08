package net.jacobpeterson.jet.openapiannotations.annotation.self;

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
 * {@link OpenApiSelf} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-self">OpenAPI $self</a>.
 * <p>
 * This string <em>MUST</em> be in the form of a URI reference as defined by
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc3986">RFC3986</a>
 * <a href="https://tools.ietf.org/html/rfc3986#section-4.1">Section 4.1</a>. The <code>$self</code> field provides the
 * self-assigned URI of this document, which also serves as its base URI in accordance with
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#bib-rfc3986">RFC3986</a>
 * <a href="https://tools.ietf.org/html/rfc3986#section-5.1.1">Section 5.1.1</a>. Implementations <em>MUST</em> support
 * identifying the targets of
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#relative-references-in-api-description-uris">API description
 * URIs</a> using the URI defined by this field when it is present. See
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#establishing-the-base-uri">Establishing the Base URI</a> for the
 * base URI behavior when <code>$self</code> is absent or relative, and see
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#appendix-f-examples-of-base-uri-determination-and-reference-resolution">
 * Appendix F</a> for examples of using <code>$self</code> to resolve references.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-self">spec.openapis.org</a>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Repeatable(OpenApiSelfs.class)
@NullMarked
public @interface OpenApiSelf {

    /**
     * The name of the {@link OpenApiAnnotationsProcessor#DEFAULT_ANNOTATION_GROUP_NAME annotation group} this
     * annotation should be assigned to.
     */
    @AnnotationJsonSerializerExclude
    String annotationGroupName() default DEFAULT_ANNOTATION_GROUP_NAME;

    /**
     * The {@link OpenApiSelf} value.
     */
    String value() default "";
}
