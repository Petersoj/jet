package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation;

import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationJsonSerializer;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonSerializeEmptyArray} is an annotation for an {@link Annotation} method to denote that the array
 * return value should be serialized by {@link AnnotationJsonSerializer} even if it is empty.
 */
@NullMarked
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationJsonSerializeEmptyArray {}
