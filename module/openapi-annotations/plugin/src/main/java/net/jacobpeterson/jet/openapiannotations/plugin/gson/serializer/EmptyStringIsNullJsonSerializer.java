package net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;

/**
 * {@link EmptyStringIsNullJsonSerializer} is a {@link String} {@link JsonSerializer} that treats
 * {@link String#isEmpty()} as {@link JsonNull}.
 */
@NullMarked
public final class EmptyStringIsNullJsonSerializer implements JsonSerializer<String> {

    @Override
    public JsonElement serialize(final String src, final Type typeOfSrc, final JsonSerializationContext context) {
        return src.isEmpty() ? JsonNull.INSTANCE : new JsonPrimitive(src);
    }
}
