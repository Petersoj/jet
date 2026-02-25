package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.schemaname;

import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.impl.DefinitionKey;
import com.github.victools.jsonschema.generator.naming.SchemaDefinitionNamingStrategy;
import net.jacobpeterson.jet.openapiannotations.annotation.schemaname.SchemaNameUtil;
import org.jspecify.annotations.NullMarked;

import static net.jacobpeterson.jet.openapiannotations.annotation.schemaname.SchemaNameUtil.getFullSchemaName;

/**
 * {@link SchemaNameDefinitionNamingStrategy} is a {@link SchemaDefinitionNamingStrategy} that uses
 * {@link SchemaNameUtil#getFullSchemaName(Class)}.
 */
@NullMarked
public class SchemaNameDefinitionNamingStrategy implements SchemaDefinitionNamingStrategy {

    @Override
    public String getDefinitionNameForKey(final DefinitionKey key, final SchemaGenerationContext generationContext) {
        return getFullSchemaName(key.getType().getErasedType(), "");
    }
}
