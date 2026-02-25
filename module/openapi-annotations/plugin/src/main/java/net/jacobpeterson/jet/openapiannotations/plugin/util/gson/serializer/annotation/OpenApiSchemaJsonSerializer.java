package net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer.annotation;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static net.jacobpeterson.jet.openapiannotations.plugin.util.gson.GsonUtil.combine;

/**
 * {@link OpenApiSchemaJsonSerializer} is a {@link JsonSerializer} for {@link OpenApiSchema}.
 * <p>
 * Note: {@link OpenApiSchemaJsonSerializer} <strong>MUST</strong> be used with {@link AnnotationJsonSerializer}
 * registered using {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}, otherwise a recursive infinite loop
 * will occur.
 */
@NullMarked
@RequiredArgsConstructor
public class OpenApiSchemaJsonSerializer implements JsonSerializer<OpenApiSchema> {

    private final SchemaGenerator schemaGenerator;

    @Override
    public JsonElement serialize(final OpenApiSchema src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        // Prevent recursive infinite loop by using `Annotation.class`
        final var serialized = context.serialize(src, Annotation.class);
        final var fromClass = src.fromClass();
        if (fromClass.length == 0) {
            return serialized;
        }
        checkArgument(fromClass.length == 1, "`@OpenApiSchema.fromClass` cannot contain more than one `Class`");
        return combine(serialized, JsonParser.parseString(schemaGenerator.generateSchema(fromClass[0]).toString()));
    }
}
