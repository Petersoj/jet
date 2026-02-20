package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.enclosingclassname;

import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaGeneratorGeneralConfigPart;
import com.github.victools.jsonschema.generator.naming.DefaultSchemaDefinitionNamingStrategy;
import com.github.victools.jsonschema.generator.naming.SchemaDefinitionNamingStrategy;
import org.jspecify.annotations.NullMarked;

/**
 * {@link EnclosingClassNameSchemaModule} is a {@link Module} that sets
 * {@link SchemaGeneratorGeneralConfigPart#withDefinitionNamingStrategy(SchemaDefinitionNamingStrategy)} to
 * {@link EnclosingClassNameDefinitionNamingStrategy}.
 */
@NullMarked
public class EnclosingClassNameSchemaModule implements Module {

    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder builder) {
        final var existing = builder.forTypesInGeneral().getDefinitionNamingStrategy();
        builder.forTypesInGeneral().withDefinitionNamingStrategy(new EnclosingClassNameDefinitionNamingStrategy(
                existing != null ? existing : new DefaultSchemaDefinitionNamingStrategy()));
    }
}
