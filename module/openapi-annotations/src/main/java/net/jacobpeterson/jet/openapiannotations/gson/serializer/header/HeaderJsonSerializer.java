package net.jacobpeterson.jet.openapiannotations.gson.serializer.header;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.jacobpeterson.jet.common.http.header.Header;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;

/**
 * {@link HeaderJsonSerializer} is a {@link JsonSerializer} for {@link Header}.
 */
@NullMarked
public class HeaderJsonSerializer implements JsonSerializer<Header> {

    @Override
    public JsonElement serialize(final Header src, final Type typeOfSrc, final JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
