package net.jacobpeterson.jet.openapiannotationsplugin.gson.serializer.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.jacobpeterson.jet.openapiannotations.meta.AnnotationJsonName;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * {@link AnnotationEnumJsonSerializer} is a {@link JsonSerializer} for {@link Enum}s declared inside
 * {@link Annotation}s that use {@link AnnotationJsonName}.
 */
@NullMarked
public class AnnotationEnumJsonSerializer implements JsonSerializer<Enum<?>> {

    @Override
    public JsonElement serialize(final Enum<?> src, final Type typeOfSrc, final JsonSerializationContext context) {
        try {
            return new JsonPrimitive(src.getDeclaringClass().getField(src.name())
                    .getAnnotation(AnnotationJsonName.class).value());
        } catch (final NoSuchFieldException noSuchFieldException) {
            throw new RuntimeException(noSuchFieldException);
        }
    }
}
