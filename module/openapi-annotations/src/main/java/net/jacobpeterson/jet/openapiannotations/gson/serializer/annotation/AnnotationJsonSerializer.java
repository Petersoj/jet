package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;

import static java.util.Arrays.stream;

/**
 * {@link AnnotationJsonSerializer} is an {@link Annotation} {@link JsonSerializer} that uses reflection to invoke the
 * {@link Class#getDeclaredMethods()} of the {@link Annotation#annotationType()} and uses reflection to handle
 * {@link AnnotationJsonSerializerExclude}, {@link AnnotationArrayIsNullableValue}, {@link AnnotationArrayIsMap}, and
 * {@link AnnotationArrayIsMapKey}.
 */
@NullMarked
public final class AnnotationJsonSerializer implements JsonSerializer<Annotation> {

    @Override
    public JsonElement serialize(final Annotation src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var jsonObject = new JsonObject();
        for (final var method : src.annotationType().getDeclaredMethods()) {
            if (method.isAnnotationPresent(AnnotationJsonSerializerExclude.class)) {
                continue;
            }
            Object value;
            try {
                value = method.invoke(src);
            } catch (final IllegalAccessException | InvocationTargetException exception) {
                throw new RuntimeException(exception);
            }
            if (method.getReturnType().isArray()) {
                final var length = Array.getLength(value);
                if (length == 0) {
                    value = null;
                } else if (method.isAnnotationPresent(AnnotationArrayIsNullableValue.class)) {
                    value = Array.get(value, 0);
                } else if (method.isAnnotationPresent(AnnotationArrayIsMap.class)) {
                    final var keyMethod = stream(method.getReturnType().getComponentType().getDeclaredMethods())
                            .filter(entryMethod -> entryMethod.isAnnotationPresent(AnnotationArrayIsMapKey.class))
                            .findFirst()
                            .orElseThrow();
                    final var map = new HashMap<>();
                    for (var index = 0; index < length; index++) {
                        final var entry = Array.get(value, index);
                        try {
                            map.put(keyMethod.invoke(entry), entry);
                        } catch (final IllegalAccessException | InvocationTargetException exception) {
                            throw new RuntimeException(exception);
                        }
                    }
                    value = map;
                }
            }
            jsonObject.add(method.getName(), context.serialize(value));
        }
        return jsonObject;
    }
}
