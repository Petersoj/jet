package net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer.annotation;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static net.jacobpeterson.jet.openapiannotations.plugin.gson.GsonUtil.combine;
import static net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.SchemaGeneratorUtil.generateSchemaToGsonAndInlineSingleSimpleTypeDef;

/**
 * {@link OpenApiSchemaJsonSerializer} is a {@link JsonSerializer} for {@link OpenApiSchema}.
 */
@NullMarked
@RequiredArgsConstructor
public class OpenApiSchemaJsonSerializer implements JsonSerializer<OpenApiSchema> {

    private final SchemaGenerator schemaGenerator;

    @Override
    public JsonElement serialize(final OpenApiSchema src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final var serialized = context.serialize(src, Annotation.class);
        final var fromClass = src.fromClass();
        if (fromClass.length == 0) {
            return serialized;
        }
        checkArgument(fromClass.length == 1, "`@OpenApiSchema.fromClass` cannot contain more than one `Class`");
        return combine(serialized, generateSchemaToGsonAndInlineSingleSimpleTypeDef(schemaGenerator, fromClass[0]));
    }
}
