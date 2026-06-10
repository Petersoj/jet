package net.jacobpeterson.jet.openapiannotations.meta;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonName} is an annotation for an {@link Annotation} method to indicate that {@link #value()} should
 * be used instead of the method name for the serialized JSON key name.
 */
@NullMarked
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface AnnotationJsonName {

    /**
     * The {@link AnnotationJsonName} value.
     */
    String value();
}
