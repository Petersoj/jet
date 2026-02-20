package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.schemaname;

import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.impl.DefinitionKey;
import com.github.victools.jsonschema.generator.naming.SchemaDefinitionNamingStrategy;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;

/**
 * {@link SchemaNameDefinitionNamingStrategy} wraps an existing {@link SchemaDefinitionNamingStrategy}, but uses
 * {@link SchemaName#value()} if present.
 */
@NullMarked
@RequiredArgsConstructor
public class SchemaNameDefinitionNamingStrategy implements SchemaDefinitionNamingStrategy {

    private final SchemaDefinitionNamingStrategy wrapped;

    @Override
    public String getDefinitionNameForKey(final DefinitionKey key, final SchemaGenerationContext generationContext) {
        final var schemaName = key.getType().getErasedType().getDeclaredAnnotation(SchemaName.class);
        return schemaName != null ? schemaName.value() : wrapped.getDefinitionNameForKey(key, generationContext);
    }
}
