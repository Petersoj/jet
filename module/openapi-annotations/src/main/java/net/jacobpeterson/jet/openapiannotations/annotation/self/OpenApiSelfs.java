package net.jacobpeterson.jet.openapiannotations.annotation.self;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiSelfs} is an annotation for repeated {@link OpenApiSelf} annotations.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiSelfs {

    /**
     * The {@link OpenApiSelf}s.
     */
    OpenApiSelf[] value() default {};
}
