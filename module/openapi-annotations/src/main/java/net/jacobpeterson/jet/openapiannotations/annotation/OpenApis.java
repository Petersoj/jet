package net.jacobpeterson.jet.openapiannotations.annotation;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApis} is an annotation for repeated {@link OpenApi} annotations.
 */
@NullMarked
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface OpenApis {

    /**
     * The {@link OpenApi}s.
     */
    OpenApi[] value() default {};
}
