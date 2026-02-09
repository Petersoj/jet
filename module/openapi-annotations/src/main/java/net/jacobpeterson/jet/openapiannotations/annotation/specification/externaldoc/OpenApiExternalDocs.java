package net.jacobpeterson.jet.openapiannotations.annotation.specification.externaldoc;

import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiExternalDocs} is an annotation for repeated {@link OpenApiExternalDoc} annotations.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@NullMarked
public @interface OpenApiExternalDocs {

    /**
     * The {@link OpenApiExternalDoc}s.
     */
    OpenApiExternalDoc[] value() default {};
}
