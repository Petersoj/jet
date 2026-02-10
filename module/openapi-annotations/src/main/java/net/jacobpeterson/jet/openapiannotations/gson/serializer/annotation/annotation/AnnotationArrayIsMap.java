package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationArrayIsMap} is an annotation for an {@link Annotation} method to denote that the array return type
 * should act as a {@link Map} and not an array, with the key being the {@link Annotation} method annotated with
 * {@link AnnotationArrayIsMapKey}.
 */
@NullMarked
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationArrayIsMap {}
