package net.jacobpeterson.jet.openapiannotations.plugin.util.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jspecify.annotations.NullMarked;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link GsonUtil} is a utility class for {@link Gson}.
 */
@NullMarked
public final class GsonUtil {

    /**
     * Combines the given <code>a</code> {@link JsonObject} or {@link JsonArray} with the given <code>b</code>
     * {@link JsonObject} or {@link JsonArray} into a new {@link JsonObject} or {@link JsonArray}.
     *
     * @param a a {@link JsonElement}
     * @param b a {@link JsonElement}
     *
     * @return the combined {@link JsonElement}
     *
     * @throws IllegalArgumentException thrown for illegal combinations (e.g. duplicate keys with {@link JsonPrimitive}
     *                                  values, or if <code>a</code> is a {@link JsonArray} and <code>b</code> is a
     *                                  {@link JsonObject})
     */
    public static JsonElement combine(final JsonElement a, final JsonElement b) throws IllegalArgumentException {
        if (a.isJsonNull()) {
            return b.deepCopy();
        }
        if (b.isJsonNull()) {
            return a.deepCopy();
        }
        checkArgument((a.isJsonArray() && b.isJsonArray()) || (a.isJsonObject() && b.isJsonObject()),
                "Cannot combine `%s` and `%s`", a, b);
        if (a.isJsonArray()) {
            final var aArray = a.getAsJsonArray();
            final var bArray = b.getAsJsonArray();
            final var combined = new JsonArray(aArray.size() + bArray.size());
            combined.addAll(aArray.deepCopy());
            combined.addAll(bArray.deepCopy());
            return combined;
        }
        final var combined = a.getAsJsonObject().deepCopy();
        for (final var bEntry : b.getAsJsonObject().entrySet()) {
            final var bKey = bEntry.getKey();
            final var bValue = bEntry.getValue();
            combined.add(bKey, combined.has(bKey) ? combine(combined.get(bKey), bValue) : bValue);
        }
        return combined;
    }

    private GsonUtil() {}
}
