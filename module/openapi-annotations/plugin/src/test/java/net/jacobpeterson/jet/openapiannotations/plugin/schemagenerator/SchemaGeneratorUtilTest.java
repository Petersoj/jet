package net.jacobpeterson.jet.openapiannotations.plugin.schemagenerator;

import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.github.victools.jsonschema.generator.Option.DEFINITIONS_FOR_ALL_OBJECTS;
import static com.github.victools.jsonschema.generator.Option.DEFINITIONS_FOR_MEMBER_SUPERTYPES;
import static com.github.victools.jsonschema.generator.Option.DEFINITION_FOR_MAIN_SCHEMA;
import static com.github.victools.jsonschema.generator.OptionPreset.PLAIN_JSON;
import static com.github.victools.jsonschema.generator.SchemaVersion.DRAFT_2020_12;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class SchemaGeneratorUtilTest {

    @RequiredArgsConstructor
    @SuppressWarnings("all")
    private static final class NotInline {

        private final String test;
    }

    @Test
    public void generateSchemaToGsonAndInlineSingleSimpleTypeDef() {
        final var schemaGenerator = new SchemaGenerator(new SchemaGeneratorConfigBuilder(DRAFT_2020_12, PLAIN_JSON)
                .with(DEFINITION_FOR_MAIN_SCHEMA)
                .with(DEFINITIONS_FOR_ALL_OBJECTS)
                .with(DEFINITIONS_FOR_MEMBER_SUPERTYPES)
                .build());
        assertEquals(JsonParser.parseString("""
                        {
                          "$schema": "https://json-schema.org/draft/2020-12/schema",
                          "type": "string"
                        }
                        """),
                SchemaGeneratorUtil.generateSchemaToGsonAndInlineSingleSimpleTypeDef(schemaGenerator, String.class));
        assertEquals(JsonParser.parseString("""
                        {
                          "$schema": "https://json-schema.org/draft/2020-12/schema",
                          "type": "string",
                          "format": "uuid"
                        }
                        """),
                SchemaGeneratorUtil.generateSchemaToGsonAndInlineSingleSimpleTypeDef(schemaGenerator, UUID.class));
        assertEquals(JsonParser.parseString("""
                        {
                          "$schema": "https://json-schema.org/draft/2020-12/schema",
                          "$ref": "#/$defs/NotInline",
                          "$defs": {
                            "NotInline": {
                              "type": "object",
                              "properties": {
                                "test": {
                                  "type": "string"
                                }
                              }
                            }
                          }
                        }
                        """),
                SchemaGeneratorUtil.generateSchemaToGsonAndInlineSingleSimpleTypeDef(schemaGenerator, NotInline.class));
    }
}
