package net.jacobpeterson.jet.openapiannotations.annotation.meta;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonIgnore} is an annotation for an {@link Annotation} method to be ignored by JSON serialization.
 */
@NullMarked
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationJsonIgnore {}
