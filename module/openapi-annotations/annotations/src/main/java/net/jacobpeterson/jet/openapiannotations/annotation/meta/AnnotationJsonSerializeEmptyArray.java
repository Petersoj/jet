package net.jacobpeterson.jet.openapiannotations.annotation.meta;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonSerializeEmptyArray} is an annotation for an {@link Annotation} method to denote that the array
 * return value should be serialized, even if it evaluates to an empty JSON array or object.
 */
@NullMarked
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationJsonSerializeEmptyArray {}
