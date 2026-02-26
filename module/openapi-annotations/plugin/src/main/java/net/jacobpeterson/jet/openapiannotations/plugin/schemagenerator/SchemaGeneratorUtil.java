package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaKeyword.SchemaType;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.node.ObjectNode;

import java.lang.reflect.Type;

import static com.github.victools.jsonschema.generator.SchemaKeyword.SchemaType.ARRAY;
import static com.github.victools.jsonschema.generator.SchemaKeyword.SchemaType.OBJECT;
import static com.github.victools.jsonschema.generator.SchemaKeyword.TAG_DEFINITIONS;
import static com.github.victools.jsonschema.generator.SchemaKeyword.TAG_REF;
import static com.github.victools.jsonschema.generator.SchemaKeyword.TAG_REF_MAIN;
import static com.github.victools.jsonschema.generator.SchemaKeyword.TAG_TYPE;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Arrays.stream;
import static java.util.Locale.ROOT;
import static java.util.function.Function.identity;

/**
 * {@link SchemaGeneratorUtil} is a utility class for {@link SchemaGenerator}.
 */
@NullMarked
public final class SchemaGeneratorUtil {

    private static final ImmutableMap<String, SchemaType> SCHEMA_TYPES_OF_LOWERCASED_KEYWORDS =
            stream(SchemaType.values()).collect(toImmutableMap(value ->
                    value.getSchemaKeywordValue().toLowerCase(ROOT), identity()));

    /**
     * @return {@link #generateSchemaToGson(SchemaGenerator, Type, Type...)}, but with simple type definitions (e.g.
     * <code>$ref</code> is to single <code>$defs</code> entry with non-object and non-array <code>type</code>) inlined
     */
    public static JsonObject generateSchemaToGsonAndInlineSingleSimpleTypeDef(final SchemaGenerator schemaGenerator,
            final Type mainTargetType, final Type... typeParameters) {
        final var schema = generateSchemaToGson(schemaGenerator, mainTargetType, typeParameters);
        final var schemaVersion = schemaGenerator.getConfig().getSchemaVersion();
        final var ref = schema.get(TAG_REF.forVersion(schemaVersion));
        if (ref == null || ref.isJsonNull()) {
            return schema;
        }
        final var defs = schema.getAsJsonObject(TAG_DEFINITIONS.forVersion(schemaVersion));
        if (defs == null || defs.size() != 1) {
            return schema;
        }
        final var defEntry = defs.asMap().entrySet().iterator().next();
        if (!ref.getAsString().equals(TAG_REF_MAIN.forVersion(schemaVersion) + "/" +
                TAG_DEFINITIONS.forVersion(schemaVersion) + "/" + defEntry.getKey())) {
            return schema;
        }
        final var defObject = defEntry.getValue().getAsJsonObject();
        final var defType = defObject.get(TAG_TYPE.forVersion(schemaVersion));
        if (defType == null || defType.isJsonNull()) {
            return schema;
        }
        final var defSchemaType = SCHEMA_TYPES_OF_LOWERCASED_KEYWORDS.get(defType.getAsString().toLowerCase(ROOT));
        if (defSchemaType == null || defSchemaType == OBJECT || defSchemaType == ARRAY) {
            return schema;
        }
        schema.remove(TAG_REF.forVersion(schemaVersion));
        schema.remove(TAG_DEFINITIONS.forVersion(schemaVersion));
        schema.asMap().putAll(defObject.asMap());
        return schema;
    }

    /**
     * @return {@link SchemaGenerator#generateSchema(Type, Type...)} with {@link ObjectNode#toString()} and
     * {@link JsonParser#parseString(String)}
     */
    public static JsonObject generateSchemaToGson(final SchemaGenerator schemaGenerator,
            final Type mainTargetType, final Type... typeParameters) {
        return JsonParser.parseString(schemaGenerator.generateSchema(mainTargetType, typeParameters).toString())
                .getAsJsonObject();
    }

    private SchemaGeneratorUtil() {}
}
