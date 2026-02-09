package net.jacobpeterson.jet.openapiannotations.annotation.specification.server;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiServers} is an annotation for repeated {@link OpenApiServer} annotations.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiServers {

    /**
     * The {@link OpenApiServer}s.
     */
    OpenApiServer[] value() default {};
}
