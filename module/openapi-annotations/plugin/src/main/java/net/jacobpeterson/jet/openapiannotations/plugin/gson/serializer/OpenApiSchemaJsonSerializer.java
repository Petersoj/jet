package net.jacobpeterson.jet.openapiannotations.plugin.gson.serializer;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link OpenApiSchemaJsonSerializer} is a {@link JsonSerializer} for {@link OpenApiSchema}.
 */
@NullMarked
@RequiredArgsConstructor
public class OpenApiSchemaJsonSerializer implements JsonSerializer<OpenApiSchema> {

    private static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();

    private final SchemaGenerator schemaGenerator;

    @Override
    public JsonElement serialize(final OpenApiSchema src, final Type typeOfSrc,
            final JsonSerializationContext context) {
        final var from = src.from();
        if (from.length == 0) {
            return serializeAsAnnotationClass(context, src);
        }
        checkArgument(from.length == 1, "`@OpenApiSchema.from` cannot contain more than one `Class`");
        final var generatedSchema = schemaGenerator.generateSchema(from[0]);
        final var rawJson = src.rawJson();
        if (!rawJson.isEmpty()) {
            final var valueJsonNode = JACKSON_OBJECT_MAPPER.readTree(rawJson);
            checkArgument(valueJsonNode.isObject(), "`@OpenApiSchema.rawJson` must be a JSON object");
            generatedSchema.setAll((ObjectNode) valueJsonNode);
        }
        return serializeAsAnnotationClass(context, new OpenApiSchemaWrapper(generatedSchema.toString()));
    }

    private JsonElement serializeAsAnnotationClass(final JsonSerializationContext context, final OpenApiSchema src) {
        return context.serialize(src, Annotation.class); // Prevent recursion by specifying `Annotation.class` as type
    }

    @Value
    @SuppressWarnings({"ClassExplicitlyAnnotation", "ImmutableAnnotationChecker"})
    private static class OpenApiSchemaWrapper implements OpenApiSchema {

        String rawJson;

        @Override
        public Class<?>[] from() {
            return new Class[]{};
        }

        @Override
        public String rawJson() {
            return rawJson;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return OpenApiSchema.class;
        }
    }
}
