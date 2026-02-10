package net.jacobpeterson.jet.openapiannotations.annotation.specification.webhook;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.path.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationMethodIsValue;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link OpenApiWebhook} is an annotation for an entry in {@link OpenApi#webhooks()}.
 */
@NullMarked
@Target({})
@Retention(RUNTIME)
public @interface OpenApiWebhook {

    /**
     * The {@link OpenApi#webhooks()} entry key.
     */
    @AnnotationJsonIgnore
    @AnnotationArrayIsMapKey
    String name() default "";

    /**
     * The {@link OpenApi#webhooks()} entry value {@link OpenApiPathItem}.
     * <p>
     * Note: this array must only contain one element (see {@link AnnotationArrayIsNullableValue}).
     */
    @AnnotationArrayIsNullableValue
    @AnnotationMethodIsValue
    OpenApiPathItem[] item() default {};
}
