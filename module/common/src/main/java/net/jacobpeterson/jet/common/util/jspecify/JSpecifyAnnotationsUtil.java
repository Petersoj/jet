package net.jacobpeterson.jet.common.util.jspecify;

import com.google.common.collect.ImmutableSet;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.synchronizedMap;

/**
 * {@link JSpecifyAnnotationsUtil} is a utility class for {@link org.jspecify.annotations}.
 */
@NullMarked
public class JSpecifyAnnotationsUtil {

    /**
     * A {@link Function} that returns a {@link NullPointerException} for the first entry in the given
     * {@link ImmutableSet}, and {@link Exception#addSuppressed(Throwable)} used for subsequent entries.
     */
    public static Function<ImmutableSet<Field>, @Nullable RuntimeException> NESTING_NULL_POINTER_EXCEPTION =
            nonNullFieldsUnset -> {
                NullPointerException nullPointerException = null;
                for (final var nonNullFieldUnset : nonNullFieldsUnset) {
                    final var nestedNullPointerException = new NullPointerException(nonNullFieldUnset.toString());
                    if (nullPointerException == null) {
                        nullPointerException = nestedNullPointerException;
                    } else {
                        nullPointerException.addSuppressed(nestedNullPointerException);
                    }
                }
                return nullPointerException;
            };

    private static final Map<Field, Boolean> NULLABILITY_OF_FIELDS = synchronizedMap(new HashMap<>());

    /**
     * Calls {@link #requireNonNullFieldsSet(Object, Function)} with {@link #NESTING_NULL_POINTER_EXCEPTION}.
     */
    public static void requireNonNullFieldsSet(final @Nullable Object object) {
        requireNonNullFieldsSet(object, NESTING_NULL_POINTER_EXCEPTION);
    }

    /**
     * Throws the {@link RuntimeException} created from the given <code>exceptionSupplier</code> {@link Function} if
     * {@link #getNonNullFieldsUnset(Object)} for the given {@link Object} is non-empty.
     *
     * @param object            the {@link Object} to inspect
     * @param exceptionSupplier the {@link RuntimeException} supplier {@link Function} (may return <code>null</code> to
     *                          not throw)
     */
    public static void requireNonNullFieldsSet(final @Nullable Object object,
            final Function<ImmutableSet<Field>, @Nullable RuntimeException> exceptionSupplier) {
        final var nonNullFieldsUnset = getNonNullFieldsUnset(object);
        if (!nonNullFieldsUnset.isEmpty()) {
            final var exception = exceptionSupplier.apply(nonNullFieldsUnset);
            if (exception != null) {
                throw exception;
            }
        }
    }

    /**
     * Gets an {@link ImmutableSet} of {@link Field}s that are unset (set to <code>null</code>) and have a
     * non-<code>null</code> designation from a {@link org.jspecify.annotations} annotation: {@link Nullable} or
     * {@link NonNull} on the {@link Field}, {@link NullMarked} or {@link NullUnmarked} on the enclosing
     * {@link Class} or {@link Package}.
     *
     * @param object the {@link Object} to inspect
     *
     * @return the {@link ImmutableSet} of non-<code>null</code> {@link Field}s that are unset
     */
    public static ImmutableSet<Field> getNonNullFieldsUnset(final @Nullable Object object) {
        if (object == null) {
            return ImmutableSet.of();
        }
        final var nonNullFieldsSetToNull = ImmutableSet.<Field>builderWithExpectedSize(0);
        for (var clazz = object.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final var field : clazz.getDeclaredFields()) {
                if (!isFieldNullable(field)) {
                    field.setAccessible(true);
                    try {
                        if (field.get(object) == null) {
                            nonNullFieldsSetToNull.add(field);
                        }
                    } catch (final IllegalAccessException illegalAccessException) {
                        throw new RuntimeException(illegalAccessException);
                    }
                }
            }
        }
        return nonNullFieldsSetToNull.build();
    }

    /**
     * Gets the nullability designation from a {@link org.jspecify.annotations} annotation: {@link Nullable} or
     * {@link NonNull} on the {@link Field}, {@link NullMarked} or {@link NullUnmarked} on the enclosing {@link Class}
     * or {@link Package}.
     *
     * @param field the {@link Field}
     *
     * @return internally-cached <code>true</code> if the given {@link Field} is {@link Nullable}, <code>false</code>
     * otherwise
     */
    public static boolean isFieldNullable(final Field field) {
        return NULLABILITY_OF_FIELDS.computeIfAbsent(field, _ -> {
            if (field.getType().isPrimitive() || field.isSynthetic()) {
                return false;
            }
            final var annotatedType = field.getAnnotatedType();
            if (annotatedType.isAnnotationPresent(Nullable.class)) {
                return true;
            }
            if (annotatedType.isAnnotationPresent(NonNull.class)) {
                return false;
            }
            for (var enclosingClass = field.getDeclaringClass(); enclosingClass != null;
                    enclosingClass = enclosingClass.getEnclosingClass()) {
                if (enclosingClass.isAnnotationPresent(NullMarked.class)) {
                    return false;
                }
                if (enclosingClass.isAnnotationPresent(NullUnmarked.class)) {
                    return true;
                }
            }
            final var classPackage = field.getDeclaringClass().getPackage();
            if (classPackage != null) {
                if (classPackage.isAnnotationPresent(NullMarked.class)) {
                    return false;
                }
                if (classPackage.isAnnotationPresent(NullUnmarked.class)) {
                    return true;
                }
            }
            return true;
        });
    }

    private JSpecifyAnnotationsUtil() {}
}
