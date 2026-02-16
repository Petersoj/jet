package net.jacobpeterson.jet.openapiannotations.annotation.meta;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationArrayIsMapKey} is an annotation for an {@link Annotation} method to denote the {@link Map} key for
 * {@link AnnotationArrayIsMap}
 */
@NullMarked
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationArrayIsMapKey {}
