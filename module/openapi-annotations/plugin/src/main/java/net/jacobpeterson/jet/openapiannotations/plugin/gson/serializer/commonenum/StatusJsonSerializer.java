package net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.commonenum;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.jacobpeterson.jet.common.http.status.Status;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;

/**
 * {@link StatusJsonSerializer} is a {@link JsonSerializer} for {@link Status}.
 */
@NullMarked
public class StatusJsonSerializer implements JsonSerializer<Status> {

    @Override
    public JsonElement serialize(final Status src, final Type typeOfSrc, final JsonSerializationContext context) {
        return new JsonPrimitive(String.valueOf(src.getCode()));
    }
}
