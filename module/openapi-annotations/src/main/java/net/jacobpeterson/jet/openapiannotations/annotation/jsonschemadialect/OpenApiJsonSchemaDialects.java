package net.jacobpeterson.jet.openapiannotations.annotation.jsonschemadialect;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiJsonSchemaDialects} is an annotation for repeated {@link OpenApiJsonSchemaDialect} annotations.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiJsonSchemaDialects {

    /**
     * The {@link OpenApiJsonSchemaDialect}s.
     */
    OpenApiJsonSchemaDialect[] value() default {};
}
