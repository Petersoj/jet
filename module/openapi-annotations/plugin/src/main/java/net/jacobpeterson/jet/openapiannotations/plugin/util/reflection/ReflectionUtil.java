package net.jacobpeterson.jet.openapiannotations.plugin.util.reflection;

import org.jspecify.annotations.NullMarked;

/**
 * {@link ReflectionUtil} is a utility class for Java reflection.
 */
@NullMarked
public final class ReflectionUtil {

    /**
     * Gets the name of the given {@link Class} by removing {@link Class#getPackageName()} from
     * {@link Class#getCanonicalName()}.
     *
     * @param clazz the {@link Class}
     *
     * @return the class name {@link String}
     */
    public static String getClassName(final Class<?> clazz) {
        final var canonicalName = clazz.getCanonicalName();
        return (canonicalName == null ? clazz.getName().replace('$', '.') : canonicalName)
                .replace(clazz.getPackageName() + ".", "");
    }

    private ReflectionUtil() {}
}
