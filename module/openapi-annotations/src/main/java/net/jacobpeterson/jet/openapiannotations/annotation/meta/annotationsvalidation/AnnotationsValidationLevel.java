package net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation;

import org.jspecify.annotations.NullMarked;

/**
 * {@link AnnotationsValidationLevel} is an enum for {@link OpenApiAnnotationsValidation#level()}.
 */
@NullMarked
public enum AnnotationsValidationLevel {

    /**
     * Do not perform any validation of the OpenAPI specification JSON generated from OpenAPI annotations.
     */
    NONE,

    /**
     * Perform validation of the OpenAPI specification JSON generated from OpenAPI annotations and report schema
     * offenses as warnings.
     */
    WARNING,

    /**
     * Perform validation of the OpenAPI specification JSON generated from OpenAPI annotations and report schema
     * offenses as errors.
     */
    ERROR
}
