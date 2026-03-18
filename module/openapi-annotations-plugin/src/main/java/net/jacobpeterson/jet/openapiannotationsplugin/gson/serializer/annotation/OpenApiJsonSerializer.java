package net.jacobpeterson.jet.openapiannotationsplugin.gson.serializer.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.openapiannotations.OpenApi;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import static net.jacobpeterson.jet.openapiannotationsplugin.gson.serializer.annotation.AnnotationJsonSerializer.CURRENT_ANNOTATION_METHOD;

/**
 * {@link OpenApiJsonSerializer} is a {@link JsonSerializer} for {@link OpenApi}.
 */
@NullMarked
@RequiredArgsConstructor
public class OpenApiJsonSerializer implements JsonSerializer<OpenApi> {

    /**
     * A {@link Map} of {@link OpenApi} annotations mapped to their annotated {@link Method} used to set
     * {@link AnnotationJsonSerializer#CURRENT_ANNOTATION_METHOD}.
     */
    private final @Getter Map<OpenApi, Method> openApisOfMethods;

    @Override
    public JsonElement serialize(final OpenApi src, final Type typeOfSrc, final JsonSerializationContext context) {
        CURRENT_ANNOTATION_METHOD.set(openApisOfMethods.get(src));
        final var serialized = context.serialize(src, Annotation.class);
        CURRENT_ANNOTATION_METHOD.remove();
        return serialized;
    }
}
