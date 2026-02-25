package net.jacobpeterson.jet.openapiannotations.annotation.schemaname;

import org.jspecify.annotations.NullMarked;

/**
 * {@link SchemaNameUtil} is a utility class for {@link SchemaName}.
 */
@NullMarked
public final class SchemaNameUtil {

    /**
     * @return {@link #getFullSchemaName(Class, String)} with <code>delimiter</code> set to <code>"."</code>
     */
    public static String getFullSchemaName(final Class<?> clazz) {
        return getFullSchemaName(clazz, ".");
    }

    /**
     * Gets the {@link #getSchemaName(Class)} of the enclosing {@link Class}es and the given {@link Class}, joined by
     * the given <code>delimiter</code>.
     *
     * @param clazz     the {@link Class}
     * @param delimiter the delimiter {@link String}
     *
     * @return the full schema name {@link String}
     */
    public static String getFullSchemaName(final Class<?> clazz, final String delimiter) {
        final var name = new StringBuilder(getSchemaName(clazz));
        for (var enclosingClass = clazz.getEnclosingClass(); enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            name.insert(0, delimiter).insert(0, getSchemaName(enclosingClass));
        }
        return name.toString();
    }

    /**
     * If the given {@link Class} is annotated with {@link SchemaName}, then {@link SchemaName#value()} is returned,
     * otherwise {@link Class#getSimpleName()} is returned.
     *
     * @param clazz the {@link Class}
     *
     * @return the {@link Class} schema name {@link String}
     */
    public static String getSchemaName(final Class<?> clazz) {
        final var schemaName = clazz.getDeclaredAnnotation(SchemaName.class);
        return schemaName != null ? schemaName.value() : clazz.getSimpleName();
    }

    private SchemaNameUtil() {}
}
