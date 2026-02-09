package net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation;

import net.jacobpeterson.jet.openapiannotations.OpenApiAnnotationsProcessor;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static net.jacobpeterson.jet.openapiannotations.OpenApiAnnotationsProcessor.DEFAULT_ANNOTATION_GROUP_NAME;
import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.ERROR;

/**
 * {@link OpenApiAnnotationsValidation} is an annotation to set the {@link AnnotationsValidationLevel} for the OpenAPI
 * annotations of an annotation group, which determines if {@link OpenApiAnnotationsProcessor} validates the generated
 * OpenAPI specification JSON results.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiAnnotationsValidation {

    /**
     * The name of the {@link OpenApiAnnotationsProcessor#DEFAULT_ANNOTATION_GROUP_NAME annotation group} this
     * annotation should be assigned to.
     */
    @AnnotationJsonIgnore
    String annotationGroupName() default DEFAULT_ANNOTATION_GROUP_NAME;

    /**
     * The {@link AnnotationsValidationLevel}.
     */
    AnnotationsValidationLevel level() default ERROR;
}
