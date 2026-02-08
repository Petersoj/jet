package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationMethodIsValue;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;

import static java.util.Arrays.stream;

/**
 * {@link AnnotationJsonSerializer} is an {@link Annotation} {@link JsonSerializer} that uses reflection to invoke the
 * {@link Class#getDeclaredMethods()} of the {@link Annotation#annotationType()} and uses reflection to handle
 * {@link AnnotationJsonIgnore}, {@link AnnotationMethodIsValue}, {@link AnnotationArrayIsNullableValue},
 * {@link AnnotationArrayIsMap}, and {@link AnnotationArrayIsMapKey}.
 */
@NullMarked
public final class AnnotationJsonSerializer implements JsonSerializer<Annotation> {

    @Override
    public JsonElement serialize(final Annotation src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var methods = src.annotationType().getDeclaredMethods();
        final var methodIsValueMethod = stream(methods)
                .filter(method -> method.isAnnotationPresent(AnnotationMethodIsValue.class))
                .findFirst();
        if (methodIsValueMethod.isPresent()) {
            return context.serialize(invokeMethod(methodIsValueMethod.get(), src));
        }
        final var jsonObject = new JsonObject();
        for (final var method : methods) {
            if (method.isAnnotationPresent(AnnotationJsonIgnore.class)) {
                continue;
            }
            var value = invokeMethod(method, src);
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
                        map.put(invokeMethod(keyMethod, entry), entry);
                    }
                    value = map;
                }
            }
            jsonObject.add(method.getName(), context.serialize(value));
        }
        return jsonObject;
    }

    private Object invokeMethod(final Method method, final Object object) {
        try {
            return method.invoke(object);
        } catch (final IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}
