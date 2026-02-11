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
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonSerializeEmptyArray;
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
 * {@link SerializedName}, {@link AnnotationJsonIgnore}, {@link AnnotationJsonSerializeEmptyArray},
 * {@link AnnotationJsonObjectInline}, {@link AnnotationArrayIsNullableValue}, {@link AnnotationArrayIsMap}, and
 * {@link AnnotationArrayIsMapKey}.
 */
@NullMarked
public final class AnnotationJsonSerializer implements JsonSerializer<Annotation> {

    @Override
    public JsonElement serialize(final Annotation src, final Type typeOfSrc, final JsonSerializationContext context) {
        final var methods = stream(src.annotationType().getDeclaredMethods())
                .filter(method -> !method.isAnnotationPresent(AnnotationJsonIgnore.class))
                .toList();
        final var valueInlinedMethods = methods.stream()
                .filter(method -> method.isAnnotationPresent(AnnotationJsonObjectInline.class))
                .toList();
        if (valueInlinedMethods.size() == 1) {
            return context.serialize(getMethodValue(src, valueInlinedMethods.getFirst()));
        }
        final var jsonObject = new JsonObject();
        for (final var method : methods) {
            final var jsonValue = context.serialize(getMethodValue(src, method));
            if (method.isAnnotationPresent(AnnotationJsonObjectInline.class)) {
                if (jsonValue.isJsonNull()) {
                    continue;
                }
                if (!jsonValue.isJsonObject()) {
                    final var className = method.getDeclaringClass().getSimpleName();
                    throw new IllegalArgumentException(("`@%s` contains multiple methods annotated with `@%s`, but " +
                            "the serialized value of `@%s.%s` is not a JSON object").formatted(className,
                            AnnotationJsonObjectInline.class.getSimpleName(), className, method.getName()));
                }
                jsonValue.getAsJsonObject().asMap().forEach(jsonObject::add);
                continue;
            }
            final var jsonKey = method.isAnnotationPresent(SerializedName.class) ?
                    method.getAnnotation(SerializedName.class).value() : method.getName();
            final var existingJsonValue = jsonObject.get(jsonKey);
            if (existingJsonValue != null && !existingJsonValue.isJsonNull()) {
                if (!existingJsonValue.getClass().equals(jsonValue.getClass()) || existingJsonValue.isJsonPrimitive()) {
                    throw new IllegalArgumentException(("`@%s` contains multiple methods with a serialized name of " +
                            "\"%s\", but their return types cannot be combined").formatted(
                            method.getDeclaringClass().getSimpleName(), jsonKey));
                }
                if (existingJsonValue.isJsonObject()) {
                    final var existingJsonValueObject = existingJsonValue.getAsJsonObject();
                    jsonValue.getAsJsonObject().asMap().forEach(existingJsonValueObject::add);
                    continue;
                }
                if (existingJsonValue.isJsonArray()) {
                    existingJsonValue.getAsJsonArray().addAll(jsonValue.getAsJsonArray());
                    continue;
                }
                throw new IllegalStateException();
            }
            jsonObject.add(jsonKey, jsonValue);
        }
        return jsonObject;
    }

    private @Nullable Object getMethodValue(final Annotation src, final Method method) {
        final var value = invokeMethod(method, src);
        if (method.getReturnType().isArray()) {
            final var length = Array.getLength(value);
            if (method.isAnnotationPresent(AnnotationArrayIsNullableValue.class)) {
                if (length == 0) {
                    return null;
                } else {
                    if (length != 1) {
                        throw new IllegalArgumentException(("`@%s.%s` is annotated with `@%s`, but the array " +
                                "contains more than one element").formatted(method.getDeclaringClass().getSimpleName(),
                                method.getName(), AnnotationArrayIsNullableValue.class.getSimpleName()));
                    }
                    return Array.get(value, 0);
                }
            } else if (method.isAnnotationPresent(AnnotationArrayIsMap.class)) {
                if (length == 0) {
                    return method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? Map.of() : null;
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
                    return map;
                }
            } else if (length == 0) {
                return method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? value : null;
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
