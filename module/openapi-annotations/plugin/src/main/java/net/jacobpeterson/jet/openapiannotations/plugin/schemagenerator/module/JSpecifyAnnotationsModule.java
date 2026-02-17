package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator.module;

import com.github.victools.jsonschema.generator.ConfigFunction;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigPart;
import net.jacobpeterson.jet.common.util.jspecify.JSpecifyAnnotationsUtil;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import static net.jacobpeterson.jet.common.util.jspecify.JSpecifyAnnotationsUtil.isFieldNullable;

/**
 * {@link JSpecifyAnnotationsModule} is a {@link Module} that uses
 * {@link JSpecifyAnnotationsUtil#isFieldNullable(Field)} for
 * {@link SchemaGeneratorConfigPart#withRequiredCheck(Predicate)} and
 * {@link SchemaGeneratorConfigPart#withNullableCheck(ConfigFunction)}.
 */
@NullMarked
public class JSpecifyAnnotationsModule implements Module {

    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder builder) {
        builder.forFields()
                .withRequiredCheck(fieldScope -> !isFieldNullable(fieldScope.getRawMember()))
                .withNullableCheck(fieldScope -> isFieldNullable(fieldScope.getRawMember()));
    }
}
