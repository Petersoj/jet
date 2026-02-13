package net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonRawString;
import net.jacobpeterson.jet.openapiannotations.gson.serializer.annotation.annotation.AnnotationJsonSerializeEmptyArray;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static java.util.Arrays.stream;
import static java.util.HashMap.newHashMap;
import static net.jacobpeterson.jet.openapiannotations.util.reflection.ReflectionUtil.getFullClassName;

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
            return getMethodJsonValue(context, src, valueInlinedMethods.getFirst());
        }
        final var jsonObject = new JsonObject();
        for (final var method : methods) {
            final var jsonValue = getMethodJsonValue(context, src, method);
            if (method.isAnnotationPresent(AnnotationJsonObjectInline.class)) {
                if (jsonValue.isJsonNull()) {
                    continue;
                }
                if (!jsonValue.isJsonObject()) {
                    throw new IllegalArgumentException(("`@%s` contains multiple methods annotated with `@%s`, but " +
                            "the serialized value of `@%s.%s` is not a JSON object").formatted(
                            getFullClassName(method.getDeclaringClass()),
                            getFullClassName(AnnotationJsonObjectInline.class),
                            getFullClassName(method.getDeclaringClass()), method.getName()));
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
                            getFullClassName(method.getDeclaringClass()), jsonKey));
                }
                if (existingJsonValue.isJsonObject()) {
                    jsonValue.getAsJsonObject().asMap().forEach((key, value) ->
                            existingJsonValue.getAsJsonObject().add(key, value));
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
        if (jsonObject.isEmpty() && valueInlinedMethods.stream().noneMatch(method ->
                method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class))) {
            return JsonNull.INSTANCE;
        }
        return jsonObject;
    }

    private JsonElement getMethodJsonValue(final JsonSerializationContext context, final Annotation src,
            final Method method) {
        final var value = invokeAnnotationMethod(method, src);
        if (method.isAnnotationPresent(AnnotationJsonRawString.class)) {
            if (!(value instanceof final String valueString)) {
                throw new IllegalArgumentException("`@%s.%s` is annotated with `@%s`, but the return type is not `%s`"
                        .formatted(getFullClassName(method.getDeclaringClass()), method.getName(),
                                getFullClassName(AnnotationJsonRawString.class), getFullClassName(String.class)));
            }
            if (valueString.isEmpty()) {
                return JsonNull.INSTANCE;
            }
            return JsonParser.parseString(valueString);
        }
        if (!method.getReturnType().isArray()) {
            return context.serialize(value);
        }
        final var length = Array.getLength(value);
        if (method.isAnnotationPresent(AnnotationArrayIsNullableValue.class)) {
            if (length == 0) {
                return method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? new JsonObject() :
                        JsonNull.INSTANCE;
            }
            if (length != 1) {
                throw new IllegalArgumentException(("`@%s.%s` is annotated with `@%s`, but the array contains more " +
                        "than one element").formatted(getFullClassName(method.getDeclaringClass()), method.getName(),
                        getFullClassName(AnnotationArrayIsNullableValue.class)));
            }
            return context.serialize(Array.get(value, 0));
        }
        if (method.isAnnotationPresent(AnnotationArrayIsMap.class)) {
            if (length == 0) {
                return method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? new JsonObject() :
                        JsonNull.INSTANCE;
            }
            final var keyMethod = stream(method.getReturnType().getComponentType().getDeclaredMethods())
                    .filter(entryMethod -> entryMethod.isAnnotationPresent(AnnotationArrayIsMapKey.class))
                    .findFirst()
                    .orElseThrow();
            final var map = newHashMap(length); // Use `Map` in case of `enableComplexMapKeySerialization()`
            for (var index = 0; index < length; index++) {
                final var entry = Array.get(value, index);
                final var key = invokeAnnotationMethod(keyMethod, entry);
                if (map.put(key, entry) != null) {
                    throw new IllegalArgumentException("`@%s.%s` duplicate `@%s.%s`: %s".formatted(
                            getFullClassName(method.getDeclaringClass()), method.getName(),
                            getFullClassName(method.getReturnType().getComponentType()), keyMethod.getName(), key));
                }
            }
            return context.serialize(map);
        }
        if (length == 0) {
            return method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? new JsonArray() :
                    JsonNull.INSTANCE;
        }
        return context.serialize(value);
    }

    private Object invokeAnnotationMethod(final Method method, final Object object) {
        try {
            return method.invoke(object); // Never `null` since `Annotation` methods cannot return `null`
        } catch (final IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }
}
