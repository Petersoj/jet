package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.gson;

import com.fasterxml.classmate.ResolvedType;
import com.github.victools.jsonschema.generator.CustomDefinition;
import com.github.victools.jsonschema.generator.CustomDefinitionProviderV2;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.node.JsonNodeFactory;

import java.util.ArrayList;

import static com.github.victools.jsonschema.generator.SchemaKeyword.TAG_ENUM;

/**
 * {@link EnumSerializedNameCustomDefinitionProvider} is a {@link CustomDefinitionProviderV2} to support
 * {@link SerializedName} on {@link Enum} fields.
 */
@NullMarked
public class EnumSerializedNameCustomDefinitionProvider implements CustomDefinitionProviderV2 {

    @Override
    public @Nullable CustomDefinition provideCustomSchemaDefinition(final ResolvedType javaType,
            final SchemaGenerationContext context) {
        final var enumConstants = javaType.getErasedType().getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            return null;
        }
        var hasSerializedName = false;
        final var serializedNames = new ArrayList<String>(enumConstants.length);
        for (final var enumConstant : enumConstants) {
            final var enumName = ((Enum<?>) enumConstant).name();
            final SerializedName serializedName;
            try {
                serializedName = javaType.getErasedType().getDeclaredField(enumName)
                        .getAnnotation(SerializedName.class);
            } catch (final NoSuchFieldException noSuchFieldException) {
                throw new RuntimeException(noSuchFieldException);
            }
            if (serializedName != null) {
                hasSerializedName = true;
            }
            serializedNames.add(serializedName != null ? serializedName.value() : enumName);
        }
        if (!hasSerializedName) {
            return null;
        }
        final var standardDefinition = context.createStandardDefinition(javaType, this);
        standardDefinition.putArray(TAG_ENUM.forVersion(context.getGeneratorConfig().getSchemaVersion()))
                .addAll(serializedNames.stream()
                        .map(JsonNodeFactory.instance::stringNode)
                        .toList());
        return new CustomDefinition(standardDefinition, false);
    }
}
