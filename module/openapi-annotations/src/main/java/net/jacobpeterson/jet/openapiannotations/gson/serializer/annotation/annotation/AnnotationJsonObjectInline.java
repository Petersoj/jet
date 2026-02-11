package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation;

import com.google.gson.JsonObject;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationJsonSerializer;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonObjectInline} is an annotation for an {@link Annotation} method to denote that the method's
 * return value serializes by {@link AnnotationJsonSerializer} to a {@link JsonObject} and its entries should be added
 * directly to the enclosing {@link JsonObject}.
 */
@NullMarked
@Target(METHOD)
@Retention(RUNTIME)
public @interface AnnotationJsonObjectInline {}
