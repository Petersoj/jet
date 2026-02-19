package net.jacobpeterson.jet.openapiannotations.plugin.util.reflection;

import org.jspecify.annotations.NullMarked;

/**
 * {@link ReflectionUtil} is a utility class for Java reflection.
 */
@NullMarked
public final class ReflectionUtil {

    /**
     * Gets the name of the given {@link Class} with enclosing {@link Class}es prepended.
     *
     * @param clazz the {@link Class}
     *
     * @return the class name {@link String}
     */
    public static String getClassName(final Class<?> clazz) {
        final var className = new StringBuilder(clazz.getSimpleName());
        for (var enclosingClass = clazz.getEnclosingClass(); enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            className.insert(0, '.').insert(0, enclosingClass.getSimpleName());
        }
        return className.toString();
    }

    private ReflectionUtil() {}
}
