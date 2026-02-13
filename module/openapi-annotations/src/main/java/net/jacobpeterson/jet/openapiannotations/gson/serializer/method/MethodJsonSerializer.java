package net.jacobpeterson.jet.openapiannotations.gson.serializer.method;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.jacobpeterson.jet.common.http.method.Method;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;

import static java.util.Locale.ROOT;

/**
 * {@link MethodJsonSerializer} is a {@link JsonSerializer} for {@link Method}.
 */
@NullMarked
public class MethodJsonSerializer implements JsonSerializer<Method> {

    @Override
    public JsonElement serialize(final Method src, final Type typeOfSrc, final JsonSerializationContext context) {
        return new JsonPrimitive(src.toString().toLowerCase(ROOT));
    }
}
