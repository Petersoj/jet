package net.jacobpeterson.jet.openapiannotationsplugin.schemagenerator.module.nullable;

import com.github.victools.jsonschema.generator.ConfigFunction;
import com.github.victools.jsonschema.generator.Module;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigPart;
import net.jacobpeterson.jet.common.util.nullable.NullableUtil;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import static net.jacobpeterson.jet.common.util.nullable.NullableUtil.isFieldNullable;

/**
 * {@link NullableSchemaModule} is a {@link Module} that uses {@link NullableUtil#isFieldNullable(Field)} for
 * {@link SchemaGeneratorConfigPart#withRequiredCheck(Predicate)}.
 * <p>
 * Note: {@link SchemaGeneratorConfigPart#withNullableCheck(ConfigFunction)} is not applied because
 * {@link SchemaGenerator} uses <code>anyOf</code> for all nullable properties, which adds unnecessary complexity to
 * the schema, and many OpenAPI generators reference <code>required</code> for nullability.
 */
@NullMarked
public class NullableSchemaModule implements Module {

    @Override
    public void applyToConfigBuilder(final SchemaGeneratorConfigBuilder builder) {
        builder.forFields().withRequiredCheck(fieldScope -> !isFieldNullable(fieldScope.getRawMember()));
    }
}
