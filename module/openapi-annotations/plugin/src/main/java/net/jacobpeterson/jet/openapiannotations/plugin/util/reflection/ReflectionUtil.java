package net.jacobpeterson.jet.openapiannotations.plugin.util.reflection;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;

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
     * @return the concatenation of {@link #getEnclosingClassesName(Class, String)}, <code>delimiter</code>, and
     * {@link Class#getSimpleName()}
     */
    public static String getClassName(final Class<?> clazz, final String delimiter) {
        final var enclosingClassesName = getEnclosingClassesName(clazz, delimiter);
        return (!enclosingClassesName.isEmpty() ? enclosingClassesName + delimiter : "") + clazz.getSimpleName();
    }

    /**
     * @return {@link #getEnclosingClassesName(Class, String)} with <code>delimiter</code> set to <code>"."</code>
     */
    public static String getEnclosingClassesName(final Class<?> clazz) {
        return getEnclosingClassesName(clazz, ".");
    }

    /**
     * Gets the {@link Class#getSimpleName()} of the enclosing {@link Class}es of the given {@link Class}, joined by
     * the given <code>delimiter</code>.
     *
     * @param clazz     the {@link Class}
     * @param delimiter the delimiter {@link String}
     *
     * @return the enclosing class name {@link String}, or an empty {@link String}
     */
    public static String getEnclosingClassesName(final Class<?> clazz, final String delimiter) {
        final var enclosingClassNames = new ArrayList<String>();
        for (var enclosingClass = clazz.getEnclosingClass(); enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            enclosingClassNames.add(enclosingClass.getSimpleName());
        }
        return String.join(delimiter, enclosingClassNames.reversed());
    }

    private ReflectionUtil() {}
}
