package net.jacobpeterson.jet.openapiannotationsplugin.schemagenerator.module.gson;

import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.NullMarked;

/**
 * {@link GsonSchemaModule} is a {@link Module} that supports the Gson {@link Expose} and {@link SerializedName}
 * annotations.
 */
// TODO remove once https://github.com/victools/jsonschema-generator/pull/448 is merged
@NullMarked
public class GsonSchemaModule implements Module {

    @SuppressWarnings("ConstantValue")
    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder builder) {
        builder.forFields()
                .withIgnoreCheck(fieldScope -> {
                    final var expose = fieldScope.getAnnotationConsideringFieldAndGetter(Expose.class);
                    return expose != null && !expose.serialize();
                }).withPropertyNameOverrideResolver(fieldScope -> {
                    final var serializedName = fieldScope.getAnnotationConsideringFieldAndGetter(SerializedName.class);
                    return serializedName == null ? null : serializedName.value();
                });
        builder.forTypesInGeneral().withCustomDefinitionProvider(new EnumSerializedNameCustomDefinitionProvider());
    }
}
