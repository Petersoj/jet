package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationMethodIsValue} is an annotation for an {@link Annotation} method to denote that the method's return
 * value is the value for the entire {@link Annotation} object, as opposed to the method's return value being a member
 * of the {@link Annotation} object value.
 */
@Target(METHOD)
@Retention(RUNTIME)
@NullMarked
public @interface AnnotationMethodIsValue {}
