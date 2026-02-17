package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module;

import com.github.victools.jsonschema.generator.ConfigFunction;
import com.github.victools.jsonschema.generator.FieldScope;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigPart;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.function.Predicate;

/**
 * {@link GsonModule} is a {@link Module} that uses {@link FieldScope#getAnnotationConsideringFieldAndGetter(Class)}
 * with {@link Expose} for {@link SchemaGeneratorConfigPart#withIgnoreCheck(Predicate)} and uses
 * {@link FieldScope#getAnnotationConsideringFieldAndGetter(Class)} with {@link SerializedName} for
 * {@link SchemaGeneratorConfigPart#withPropertyNameOverrideResolver(ConfigFunction)}.
 *
 * @see
 * <a href="https://github.com/victools/jsonschema-generator/pull/448">github.com/victools/jsonschema-generator/pull/448</a>
 */
public class GsonModule implements Module {

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
    }
}
