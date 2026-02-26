package net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.jacobpeterson.jet.common.http.method.Method;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Locale.ROOT;

/**
 * {@link OpenApiPathItemJsonSerializer} is a {@link JsonSerializer} for {@link OpenApiPathItem}.
 */
@NullMarked
public class OpenApiPathItemJsonSerializer implements JsonSerializer<OpenApiPathItem> {

    @Override
    public JsonElement serialize(final OpenApiPathItem src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final var lowercased = new JsonObject();
        for (final var entry : context.serialize(src, Annotation.class).getAsJsonObject().entrySet()) {
            final var key = entry.getKey();
            checkArgument(lowercased.asMap().put(Method.VALUES_OF_UPPERCASED_STRINGS.containsKey(key) ?
                            key.toLowerCase(ROOT) : key, entry.getValue()) == null,
                    "Duplicate key due to lowercasing: \"%s\"", key);
        }
        return lowercased;
    }
}
