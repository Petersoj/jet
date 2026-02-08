package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationArrayIsNullableValue} is an annotation for an {@link Annotation} method to denote that the array
 * return type should act as a single nullable value and not an array: if the array is empty, the value is considered
 * <code>null</code>, and if the array is non-empty, the value is equal to the first element in the array.
 */
@Target(METHOD)
@Retention(RUNTIME)
@NullMarked
public @interface AnnotationArrayIsNullableValue {}
