package net.jacobpeterson.jet.openapiannotations.plugin.util.reflection;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class ReflectionUtilTest {

    private static final class Outer {

        private static final class Inner {}
    }

    @Test
    public void getClassName() {
        assertEquals("String", ReflectionUtil.getClassName(String.class));
        assertEquals("Map.Entry", ReflectionUtil.getClassName(Map.Entry.class));
        assertEquals("ReflectionUtilTest.Outer.Inner", ReflectionUtil.getClassName(Outer.Inner.class));
    }
}
