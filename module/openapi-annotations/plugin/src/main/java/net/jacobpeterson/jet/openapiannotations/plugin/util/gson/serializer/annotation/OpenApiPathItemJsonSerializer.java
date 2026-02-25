package net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.annotation;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static java.util.Locale.ROOT;

/**
 * {@link OpenApiPathItemJsonSerializer} is a {@link JsonSerializer} for {@link OpenApiPathItem}.
 * <p>
 * Note: {@link OpenApiPathItemJsonSerializer} <strong>MUST</strong> be used with {@link AnnotationJsonSerializer}
 * registered using {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}, otherwise a recursive infinity loop
 * will occur.
 */
@NullMarked
@RequiredArgsConstructor
public class OpenApiPathItemJsonSerializer implements JsonSerializer<OpenApiPathItem> {

    @Override
    public JsonElement serialize(final OpenApiPathItem src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        // Prevent recursive infinity loop by using `Annotation.class`
        final var serialized = context.serialize(src, Annotation.class).getAsJsonObject();
        final var serializedLowercased = new JsonObject();
        for (final var entry : serialized.entrySet()) {
            final var key = entry.getKey();
            serializedLowercased.add(Method.VALUES_OF_UPPERCASED_STRINGS.containsKey(key) ? key.toLowerCase(ROOT) :
                    key, entry.getValue());
        }
        return serializedLowercased;
    }
}
