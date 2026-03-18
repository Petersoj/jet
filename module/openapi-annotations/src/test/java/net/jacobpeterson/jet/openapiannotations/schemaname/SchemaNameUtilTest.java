package net.jacobpeterson.jet.openapiannotations.schemaname;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class SchemaNameUtilTest {

    private static final class Outer {

        private static final class Inner {}
    }

    @SchemaName("Outer")
    private static final class OuterAnnotated {

        private static final class Inner {}
    }

    private static final class Outer2 {

        @SchemaName("Inner")
        private static final class InnerAnnotated {}
    }

    @Test
    public void getFullSchemaName() {
        assertEquals("String", SchemaNameUtil.getFullSchemaName(String.class));
        assertEquals("Map.Entry", SchemaNameUtil.getFullSchemaName(Map.Entry.class));
        assertEquals("SchemaNameUtilTest.Outer", SchemaNameUtil.getFullSchemaName(Outer.class));
        assertEquals("SchemaNameUtilTest.Outer.Inner", SchemaNameUtil.getFullSchemaName(Outer.Inner.class));
        assertEquals("SchemaNameUtilTest.Outer", SchemaNameUtil.getFullSchemaName(OuterAnnotated.class));
        assertEquals("SchemaNameUtilTest.Outer.Inner", SchemaNameUtil.getFullSchemaName(OuterAnnotated.Inner.class));
        assertEquals("SchemaNameUtilTest.Outer2", SchemaNameUtil.getFullSchemaName(Outer2.class));
        assertEquals("SchemaNameUtilTest.Outer2.Inner", SchemaNameUtil.getFullSchemaName(Outer2.InnerAnnotated.class));
    }
}
