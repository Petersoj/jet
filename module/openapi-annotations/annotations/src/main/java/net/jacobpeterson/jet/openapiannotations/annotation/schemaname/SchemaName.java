package net.jacobpeterson.jet.openapiannotations.annotation.schemaname;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link SchemaName} is an annotation for a type (e.g. a class or enum) to specify a custom schema definition name
 * when used with {@link OpenApiSchema#fromClass()}.
 */
@NullMarked
@Target(TYPE)
@Retention(RUNTIME)
public @interface SchemaName {

    /**
     * The {@link SchemaName} value.
     */
    String value();
}
