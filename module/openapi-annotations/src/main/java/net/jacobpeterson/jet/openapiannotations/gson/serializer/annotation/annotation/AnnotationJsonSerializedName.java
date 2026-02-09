package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.AnnotationJsonSerializer;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AnnotationJsonSerializedName} is an annotation for an {@link Annotation} method to define the JSON property
 * key name used by {@link AnnotationJsonSerializer}. Effectively the same as {@link SerializedName}, but for an
 * {@link Annotation} method.
 */
@Target(METHOD)
@Retention(RUNTIME)
@NullMarked
public @interface AnnotationJsonSerializedName {

    /**
     * The {@link AnnotationJsonSerializedName} value.
     */
    String value();
}
