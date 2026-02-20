package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.schemaname;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link SchemaName} is an annotation for {@link SchemaNameDefinitionNamingStrategy} to specify a custom schema
 * definition name for a type (e.g. a class or enum).
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
