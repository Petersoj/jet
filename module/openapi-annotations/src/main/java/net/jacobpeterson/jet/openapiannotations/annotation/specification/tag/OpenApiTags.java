package net.jacobpeterson.jet.openapiannotations.annotation.specification.tag;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiTags} is an annotation for repeated {@link OpenApiTag} annotations.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiTags {

    /**
     * The {@link OpenApiTag}s.
     */
    OpenApiTag[] value() default {};
}
