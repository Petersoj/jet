package net.jacobpeterson.jet.openapiannotations.plugin.util.gson.serializer;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import org.jspecify.annotations.NullMarked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static net.jacobpeterson.jet.openapiannotations.plugin.util.gson.GsonUtil.combine;

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
        final var from = src.from();
        if (from.length == 0) {
            return serializeAsAnnotationClass(context, src);
        }
        checkArgument(from.length == 1, "`@OpenApiSchema.from` cannot contain more than one `Class`");
        final var generatedSchemaJson = schemaGenerator.generateSchema(from[0]).toString();
        final String wrapperRawJson;
        final var rawJson = src.rawJson();
        if (!rawJson.isEmpty()) {
            try {
                wrapperRawJson = combine(JsonParser.parseString(generatedSchemaJson), JsonParser.parseString(rawJson))
                        .toString();
            } catch (final Exception exception) {
                throw new IllegalArgumentException(
                        "`@OpenApiSchema.from` could not be combined with `@OpenApiSchema.rawJson`", exception);
            }
        } else {
            wrapperRawJson = generatedSchemaJson;
        }
        return serializeAsAnnotationClass(context, new OpenApiSchemaWrapper(wrapperRawJson));
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
