package net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.annotation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMap;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsMapKey;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationArrayIsNullableValue;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonIgnore;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonObjectInline;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonRawString;
import net.jacobpeterson.jet.openapiannotations.annotation.meta.AnnotationJsonSerializeEmptyArray;
import net.jacobpeterson.jet.openapiannotations.plugin.util.gson.GsonUtil;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import static net.jacobpeterson.jet.openapiannotations.plugin.util.gson.GsonUtil.combine;
import static net.jacobpeterson.jet.openapiannotations.plugin.util.gson.GsonUtil.walk;
import static net.jacobpeterson.jet.openapiannotations.plugin.util.reflection.ReflectionUtil.getClassName;

/**
 * {@link AnnotationJsonSerializer} is a {@link JsonSerializer} for {@link Annotation} that uses reflection to invoke
 * the {@link Class#getDeclaredMethods()} of the {@link Annotation#annotationType()} and uses reflection to handle
 * {@link SerializedName}, {@link AnnotationJsonIgnore}, {@link AnnotationJsonSerializeEmptyArray},
 * {@link AnnotationJsonObjectInline}, {@link AnnotationArrayIsNullableValue}, {@link AnnotationJsonRawString},
 * {@link AnnotationArrayIsMap}, and {@link AnnotationArrayIsMapKey}.
 */
@NullMarked
@RequiredArgsConstructor
public final class AnnotationJsonSerializer implements JsonSerializer<Annotation> {

    /**
     * The JSON key name for {@link #getTracerClasses()}:
     * <code>"__AnnotationJsonSerializer.JSON_KEY_CLASS_TRACER__"</code>
     */
    public static final String JSON_KEY_CLASS_TRACER = "__AnnotationJsonSerializer.JSON_KEY_CLASS_TRACER__";

    /**
     * Removes all {@link #JSON_KEY_CLASS_TRACER} keys from the given {@link JsonObject}.
     *
     * @param jsonObject the {@link JsonObject}
     */
    public static void removeClassTracers(final JsonObject jsonObject) {
        walk(jsonObject, stack -> {
            final var top = requireNonNull(stack.peek()).getValue();
            if (top.isJsonObject()) {
                top.getAsJsonObject().remove(JSON_KEY_CLASS_TRACER);
            }
            return true;
        });
    }

    /**
     * The {@link Set} of {@link Annotation} {@link Class}es to insert a {@link #JSON_KEY_CLASS_TRACER} property into
     * the serialized {@link JsonObject} with the value of {@link Class#getCanonicalName()}.
     */
    private final @Getter Set<Class<? extends Annotation>> tracerClasses;

    @Override
    public JsonElement serialize(final Annotation src, final Type typeOfSrc, final JsonSerializationContext context) {
        var serialized = serialize(src, context);
        final var annotationType = src.annotationType();
        if (tracerClasses.contains(annotationType)) {
            checkArgument(serialized.isJsonObject() || serialized.isJsonNull(),
                    "Tracer class `%s` can only be added to JSON objects", annotationType);
            if (serialized.isJsonNull()) {
                serialized = new JsonObject();
            }
            serialized.getAsJsonObject().addProperty(JSON_KEY_CLASS_TRACER, annotationType.getCanonicalName());
        }
        return serialized;
    }

    private JsonElement serialize(final Annotation src, final JsonSerializationContext context) {
        final var methods = stream(src.annotationType().getDeclaredMethods())
                .filter(method -> !method.isAnnotationPresent(AnnotationJsonIgnore.class))
                .toList();
        final var valueInlinedMethods = methods.stream()
                .filter(method -> method.isAnnotationPresent(AnnotationJsonObjectInline.class))
                .toList();
        if (methods.size() == valueInlinedMethods.size()) {
            return valueInlinedMethods.stream()
                    .map(method -> getMethodJsonValue(context, src, method))
                    .reduce(GsonUtil::combine)
                    .orElse(JsonNull.INSTANCE);
        }
        var jsonObject = new JsonObject();
        for (final var method : methods) {
            final var methodJsonValue = getMethodJsonValue(context, src, method);
            if (method.isAnnotationPresent(AnnotationJsonObjectInline.class)) {
                try {
                    jsonObject = combine(jsonObject, methodJsonValue).getAsJsonObject();
                } catch (final Exception exception) {
                    throw new IllegalArgumentException(("`@%s` contains multiple methods annotated with `@%s`, " +
                            "but the serialized value of `@%s.%s` could not be combined").formatted(
                            getClassName(method.getDeclaringClass()),
                            getClassName(AnnotationJsonObjectInline.class),
                            getClassName(method.getDeclaringClass()), method.getName()),
                            exception);
                }
                continue;
            }
            final var methodJsonKey = method.isAnnotationPresent(SerializedName.class) ?
                    method.getAnnotation(SerializedName.class).value() : method.getName();
            var jsonValue = methodJsonValue;
            if (jsonObject.has(methodJsonKey)) {
                try {
                    jsonValue = combine(jsonValue, jsonObject.get(methodJsonKey));
                } catch (final Exception exception) {
                    throw new IllegalArgumentException(("`@%s` contains multiple methods with a serialized name of " +
                            "\"%s\", but their return types could not be combined").formatted(
                            getClassName(method.getDeclaringClass()), methodJsonKey),
                            exception);
                }
            }
            jsonObject.add(methodJsonKey, jsonValue);
        }
        if (jsonObject.isEmpty() && valueInlinedMethods.stream().noneMatch(method ->
                method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class))) {
            return JsonNull.INSTANCE;
        }
        return jsonObject;
    }

    private JsonElement getMethodJsonValue(final JsonSerializationContext context, final Object annotationObject,
            final Method method) {
        final Object value;
        try {
            value = method.invoke(annotationObject); // Never `null` since `Annotation` methods cannot return `null`
        } catch (final IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
        if (method.isAnnotationPresent(AnnotationJsonRawString.class)) {
            if (!(value instanceof final String valueString)) {
                throw new IllegalArgumentException("`@%s.%s` is annotated with `@%s`, but the return type is not `%s`"
                        .formatted(getClassName(method.getDeclaringClass()), method.getName(),
                                getClassName(AnnotationJsonRawString.class), getClassName(String.class)));
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
                        "than one element").formatted(getClassName(method.getDeclaringClass()), method.getName(),
                        getClassName(AnnotationArrayIsNullableValue.class)));
            }
            return context.serialize(Array.get(value, 0));
        }
        if (method.isAnnotationPresent(AnnotationArrayIsMap.class)) {
            if (length == 0) {
                return method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? new JsonObject() :
                        JsonNull.INSTANCE;
            }
            final var keyMethods = stream(method.getReturnType().getComponentType().getDeclaredMethods())
                    .filter(entryMethod -> entryMethod.isAnnotationPresent(AnnotationArrayIsMapKey.class))
                    .toList();
            final var map = new JsonObject();
            for (var index = 0; index < length; index++) {
                final var entry = Array.get(value, index);
                final var key = keyMethods.stream()
                        .map(keyMethod -> getMethodJsonValue(context, entry, keyMethod))
                        .filter(JsonElement::isJsonPrimitive).map(JsonElement::getAsJsonPrimitive)
                        .filter(JsonPrimitive::isString).map(JsonPrimitive::getAsString)
                        .collect(collectingAndThen(toUnmodifiableList(), keys -> {
                            if (keys.size() != 1) {
                                throw new IllegalArgumentException("Exactly one key must be set: " + keyMethods.stream()
                                        .map(keyMethod -> "`@%s.%s`".formatted(
                                                getClassName(keyMethod.getDeclaringClass()),
                                                keyMethod.getName()))
                                        .collect(joining(", ")));
                            }
                            return keys.getFirst();
                        }));
                if (map.asMap().put(key, context.serialize(entry)) != null) {
                    throw new IllegalArgumentException("`@%s.%s` duplicate key: %s".formatted(
                            getClassName(method.getDeclaringClass()), method.getName(), key));
                }
            }
            return map;
        }
        if (length == 0) {
            return method.isAnnotationPresent(AnnotationJsonSerializeEmptyArray.class) ? new JsonArray() :
                    JsonNull.INSTANCE;
        }
        return context.serialize(value);
    }
}
