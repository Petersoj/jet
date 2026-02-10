package net.jacobpeterson.jet.openapiannotations.annotation.specification.securityrequirement;

import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonSerializeEmptyArray;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationMethodIsValue;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiSecurityRequirement} is an annotation for an entry in {@link OpenApiSecurityRequirements#value()}.
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiSecurityRequirement {

    /**
     * Each name or URI <em>MUST</em> correspond to a security scheme as described above. If the security scheme is
     * of type <code>"oauth2"</code> or <code>"openIdConnect"</code>, then the value is a list of scope names
     * required for the execution, and the list <em>MAY</em> be empty if authorization does not require a specified
     * scope. For other security scheme types, the array <em>MAY</em> contain a list of role names which are required
     * for the execution, but are not otherwise defined or exchanged in-band.
     */
    @AnnotationJsonIgnore
    @AnnotationArrayIsMapKey
    String name() default "";

    /**
     * Each name or URI <em>MUST</em> correspond to a security scheme as described above. If the security scheme is
     * of type <code>"oauth2"</code> or <code>"openIdConnect"</code>, then the value is a list of scope names
     * required for the execution, and the list <em>MAY</em> be empty if authorization does not require a specified
     * scope. For other security scheme types, the array <em>MAY</em> contain a list of role names which are required
     * for the execution, but are not otherwise defined or exchanged in-band.
     */
    @AnnotationJsonSerializeEmptyArray
    @AnnotationMethodIsValue
    String[] scopes() default {};
}
