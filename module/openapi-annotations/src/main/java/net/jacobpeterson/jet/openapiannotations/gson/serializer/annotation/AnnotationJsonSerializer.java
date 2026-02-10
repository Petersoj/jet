package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonSerializeEmptyArray;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationMethodIsValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.HashMap.newHashMap;

/**
 * {@link AnnotationJsonSerializer} is an {@link Annotation} {@link JsonSerializer} that uses reflection to invoke the
 * {@link Class#getDeclaredMethods()} of the {@link Annotation#annotationType()} and uses reflection to handle
 * {@link AnnotationJsonIgnore}, {@link AnnotationJsonSerializeEmptyArray}, {@link AnnotationMethodIsValue},
 * {@link AnnotationArrayIsNullableValue}, {@link AnnotationArrayIsMap}, and {@link AnnotationArrayIsMapKey}.
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
            return context.serialize(getMethodValue(src, methodIsValueMethod.get()));
        }
        final var jsonObject = new JsonObject();
        for (final var method : methods) {
            jsonObject.add(method.isAnnotationPresent(SerializedName.class) ?
                            method.getAnnotation(SerializedName.class).value() : method.getName(),
                    context.serialize(getMethodValue(src, method)));
        }
        return jsonObject;
    }

    private @Nullable Object getMethodValue(final Annotation src, final Method method) {
        if (method.isAnnotationPresent(AnnotationJsonIgnore.class)) {
            return null;
        }
        var value = invokeMethod(method, src);
        if (method.getReturnType().isArray()) {
            final var length = Array.getLength(value);
            if (method.isAnnotationPresent(AnnotationArrayIsNullableValue.class)) {
                if (length == 0) {
                    value = null;
                } else {
                    if (length != 1) {
                        throw new IllegalArgumentException(("`@%s.%s` is annotated with `@%s`, but the array " +
                                "contains more than one element").formatted(
                                method.getDeclaringClass().getSimpleName(), method.getName(),
                                AnnotationArrayIsNullableValue.class.getSimpleName()));
                    }
                    value = Array.get(value, 0);
                }
            } else if (method.isAnnotationPresent(AnnotationArrayIsMap.class)) {
                if (length == 0) {
                    value = method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? Map.of() : null;
                } else {
                    final var keyMethod = stream(method.getReturnType().getComponentType().getDeclaredMethods())
                            .filter(entryMethod -> entryMethod.isAnnotationPresent(AnnotationArrayIsMapKey.class))
                            .findFirst()
                            .orElseThrow();
                    final var map = newHashMap(length);
                    for (var index = 0; index < length; index++) {
                        final var entry = Array.get(value, index);
                        final var key = invokeMethod(keyMethod, entry);
                        if (map.put(key, entry) != null) {
                            throw new IllegalArgumentException("`@%s.%s` duplicate `@%s.%s`: %s".formatted(
                                    method.getDeclaringClass().getSimpleName(), method.getName(),
                                    method.getReturnType().getComponentType().getSimpleName(), keyMethod.getName(),
                                    key));
                        }
                    }
                    value = map;
                }
            } else if (length == 0) {
                value = method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? value : null;
            }
        }
        return value;
    }

    private Object invokeMethod(final Method method, final Object object) {
        try {
            return method.invoke(object);
        } catch (final IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}
