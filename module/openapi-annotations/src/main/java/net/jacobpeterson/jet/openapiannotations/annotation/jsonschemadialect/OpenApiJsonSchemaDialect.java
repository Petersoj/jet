package net.jacobpeterson.jet.openapiannotations.annotation.jsonschemadialect;

import net.jacobpeterson.jet.openapiannotations.OpenApiAnnotationsProcessor;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationMethodIsValue;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static net.jacobpeterson.jet.openapiannotations.OpenApiAnnotationsProcessor.DEFAULT_ANNOTATION_GROUP_NAME;

/**
 * {@link OpenApiJsonSchemaDialect} is an annotation for the
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-json-schema-dialect">OpenAPI $self</a>.
 * <p>
 * The default value for the <code>$schema</code> keyword within
 * <a href="https://spec.openapis.org/oas/v3.2.0.html#schema-object">Schema Objects</a> contained within this OAS
 * document. This <em>MUST</em> be in the form of a URI.
 *
 * @see <a href="https://spec.openapis.org/oas/v3.2.0.html#oas-json-schema-dialect">spec.openapis.org</a>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Repeatable(OpenApiJsonSchemaDialects.class)
@NullMarked
public @interface OpenApiJsonSchemaDialect {

    /**
     * The name of the {@link OpenApiAnnotationsProcessor#DEFAULT_ANNOTATION_GROUP_NAME annotation group} this
     * annotation should be assigned to.
     */
    @AnnotationJsonIgnore
    String annotationGroupName() default DEFAULT_ANNOTATION_GROUP_NAME;

    /**
     * The {@link OpenApiJsonSchemaDialect} value.
     */
    @AnnotationMethodIsValue
    String value() default "";
}
