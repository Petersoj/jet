package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonRawString} is an annotation for an {@link Annotation} method with a {@link String} return type
 * to denote that the {@link String} return value is raw JSON.
 */
@NullMarked
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationJsonRawString {}
