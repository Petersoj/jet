package net.jacobpeterson.jet.openapiannotations.util.reflection;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class ReflectionUtilTest {

    public static final class Inner {}

    @Test
    public void getFullClassName() {
        assertEquals("ReflectionUtilTest", ReflectionUtil.getFullClassName(ReflectionUtilTest.class));
        assertEquals("ReflectionUtilTest.Inner", ReflectionUtil.getFullClassName(Inner.class));
    }
}
