package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module.enclosingclassname;

import com.github.victools.jsonschema.generator.SchemaGenerationContext;
import com.github.victools.jsonschema.generator.impl.DefinitionKey;
import com.github.victools.jsonschema.generator.naming.SchemaDefinitionNamingStrategy;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.openapiannotations.plugin.util.reflection.ReflectionUtil;
import org.jspecify.annotations.NullMarked;

import static net.jacobpeterson.jet.openapiannotations.plugin.util.reflection.ReflectionUtil.getEnclosingClassesName;

/**
 * {@link EnclosingClassNameDefinitionNamingStrategy} wraps an existing {@link SchemaDefinitionNamingStrategy}, but
 * prepends {@link ReflectionUtil#getEnclosingClassesName(Class, String)}.
 */
@NullMarked
@RequiredArgsConstructor
public class EnclosingClassNameDefinitionNamingStrategy implements SchemaDefinitionNamingStrategy {

    private final SchemaDefinitionNamingStrategy wrapped;

    @Override
    public String getDefinitionNameForKey(final DefinitionKey key, final SchemaGenerationContext generationContext) {
        return getEnclosingClassesName(key.getType().getErasedType(), "") +
                wrapped.getDefinitionNameForKey(key, generationContext);
    }
}
