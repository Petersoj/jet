package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.schemaname;

import com.fasterxml.classmate.ResolvedType;
import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.impl.DefinitionKey;
import com.github.victools.jsonschema.generator.naming.SchemaDefinitionNamingStrategy;
import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

/**
 * {@link SchemaNameDefinitionNamingStrategy} is a {@link SchemaDefinitionNamingStrategy} that uses
 * {@link SchemaName#value()} for type names along with all {@link Class#getEnclosingClass()} type names prepended.
 */
@NullMarked
public class SchemaNameDefinitionNamingStrategy implements SchemaDefinitionNamingStrategy {

    private static final Pattern TYPE_PARAMETERS_DELIMITERS_PATTERN = Pattern.compile("[, <>]");

    @Override
    public String getDefinitionNameForKey(final DefinitionKey key, final SchemaGenerationContext generationContext) {
        final var name = new StringBuilder(getTypeSchemaName(generationContext, key.getType()));
        for (var enclosingClass = key.getType().getErasedType().getEnclosingClass(); enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            name.insert(0, getTypeSchemaName(generationContext,
                    generationContext.getTypeContext().resolve(enclosingClass)));
        }
        return name.toString();
    }

    private String getTypeSchemaName(final SchemaGenerationContext generationContext, final ResolvedType type) {
        final var schemaName = type.getErasedType().getDeclaredAnnotation(SchemaName.class);
        return schemaName != null ? schemaName.value() : TYPE_PARAMETERS_DELIMITERS_PATTERN.matcher(
                generationContext.getTypeContext().getSimpleTypeDescription(type)).replaceAll("");
    }
}
