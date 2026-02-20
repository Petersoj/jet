package net.jacobpeterson.jet.openapiannotations.plugin.util.reflection;

import org.jspecify.annotations.NullMarked;

/**
 * {@link ReflectionUtil} is a utility class for Java reflection.
 */
@NullMarked
public final class ReflectionUtil {

    /**
     * @return {@link #getClassName(Class, String)} with <code>delimiter</code> set to <code>"."</code>
     */
    public static String getClassName(final Class<?> clazz) {
        return getClassName(clazz, ".");
    }

    /**
     * @return the concatenation of {@link #getEnclosingClassName(Class, String)}, <code>delimiter</code>, and
     * {@link Class#getSimpleName()}
     */
    public static String getClassName(final Class<?> clazz, final String delimiter) {
        return getEnclosingClassName(clazz, delimiter) + delimiter + clazz.getSimpleName();
    }

    /**
     * @return {@link #getEnclosingClassName(Class, String)} with <code>delimiter</code> set to <code>"."</code>
     */
    public static String getEnclosingClassName(final Class<?> clazz) {
        return getEnclosingClassName(clazz, ".");
    }

    /**
     * Gets the name of the enclosing {@link Class}es of the given {@link Class}, using the given
     * <code>delimiter</code>.
     *
     * @param clazz     the {@link Class}
     * @param delimiter the delimiter {@link String}
     *
     * @return the enclosing class name {@link String}, or an empty {@link String}
     */
    public static String getEnclosingClassName(final Class<?> clazz, final String delimiter) {
        final var name = new StringBuilder();
        for (var enclosingClass = clazz.getEnclosingClass(); enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            name.insert(0, delimiter).insert(0, enclosingClass.getSimpleName());
        }
        return name.toString();
    }

    private ReflectionUtil() {}
}
