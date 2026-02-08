package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonSerializerExclude} is an annotation for an {@link Annotation} method to be excluded by
 * {@link AnnotationJsonSerializer}.
 */
@Target(METHOD)
@Retention(RUNTIME)
@NullMarked
public @interface AnnotationJsonSerializerExclude {}
