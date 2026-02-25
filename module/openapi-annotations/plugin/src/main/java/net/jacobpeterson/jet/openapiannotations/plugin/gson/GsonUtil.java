package net.jacobpeterson.jet.openapiannotations.plugin.gson;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jspecify.annotations.NullMarked;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Map.entry;

/**
 * {@link GsonUtil} is a utility class for {@link Gson}.
 */
@NullMarked
public final class GsonUtil {

    /**
     * Recursively combines the given <code>a</code> {@link JsonObject} or {@link JsonArray} with the given
     * <code>b</code> {@link JsonObject} or {@link JsonArray} into a new {@link JsonObject} or {@link JsonArray}.
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

    /**
     * Recursively walks the given {@link JsonElement} tree.
     *
     * @param root   the root {@link JsonElement}
     * @param walker the walker {@link Function} that receives the current tree stack and returns a <code>boolean</code>
     *               of whether to walk the entry at the top of the stack
     */
    @SuppressWarnings({"NonApiType", "JdkObsolete"})
    public static void walk(final JsonElement root,
            // TODO replace `LinkedList` with `ArrayDeque`: https://bugs.openjdk.org/browse/JDK-8356821
            final Function<LinkedList<Entry<String, JsonElement>>, Boolean> walker) {
        walkRecursively(entry("root", root), new LinkedList<>(), walker);
    }

    @SuppressWarnings("NonApiType")
    private static void walkRecursively(final Entry<String, JsonElement> entry,
            final LinkedList<Entry<String, JsonElement>> stack,
            final Function<LinkedList<Entry<String, JsonElement>>, Boolean> walker) {
        stack.push(entry);
        try {
            if (!walker.apply(stack)) {
                return;
            }
            if (entry.getValue().isJsonObject()) {
                for (final var entryValueEntry : entry.getValue().getAsJsonObject().entrySet()) {
                    walkRecursively(entryValueEntry, stack, walker);
                }
            } else if (entry.getValue().isJsonArray()) {
                final var array = entry.getValue().getAsJsonArray();
                for (var index = 0; index < array.size(); index++) {
                    walkRecursively(entry(String.valueOf(index), array.get(index)), stack, walker);
                }
            }
        } finally {
            stack.pop();
        }
    }

    private GsonUtil() {}
}
