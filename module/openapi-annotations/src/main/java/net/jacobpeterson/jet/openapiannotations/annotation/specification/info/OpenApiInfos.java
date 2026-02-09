package net.jacobpeterson.jet.openapiannotations.annotation.specification.info;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiInfos} is an annotation for repeated {@link OpenApiInfo} annotations.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiInfos {

    /**
     * The {@link OpenApiInfo}s.
     */
    OpenApiInfo[] value() default {};
}
