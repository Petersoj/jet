package net.jacobpeterson.jet.common.util.jspecify;

import com.google.common.collect.ImmutableSet;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Arrays.stream;
import static java.util.Collections.disjoint;
import static java.util.Collections.synchronizedMap;

/**
 * {@link JSpecifyAnnotationsUtil} is a utility class for {@link org.jspecify.annotations}.
 */
@NullMarked
public class JSpecifyAnnotationsUtil {

    /**
     * A {@link Function} for {@link #requireNonNullFieldsSet(Object, Function)} that returns a
     * {@link NullPointerException} for the first entry in the given {@link ImmutableSet}, and
     * {@link Exception#addSuppressed(Throwable)} used for subsequent entries.
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

    /**
     * The {@link String} {@link ImmutableSet} of <code>null</code>-able annotation class names from well-known
     * nullability annotation libraries.
     *
     * @see #isFieldNullable(Field)
     */
    public static final ImmutableSet<String> NULL_ABLE_ANNOTATION_CLASS_NAMES = ImmutableSet.of("Nullable");

    /**
     * The {@link String} {@link ImmutableSet} of non-<code>null</code> annotation class names from well-known
     * nullability annotation libraries.
     *
     * @see #isFieldNullable(Field)
     */
    public static final ImmutableSet<String> NON_NULL_ANNOTATION_CLASS_NAMES = ImmutableSet.of("NonNull", "NotNull");

    /**
     * The {@link String} {@link ImmutableSet} of <code>null</code>-marked annotation class names from well-known
     * nullability annotation libraries.
     *
     * @see #isFieldNullable(Field)
     */
    public static final ImmutableSet<String> NULL_MARKED_ANNOTATION_CLASS_NAMES = ImmutableSet.of("NullMarked",
            "NotNullByDefault");

    /**
     * The {@link String} {@link ImmutableSet} of <code>null</code>-unmarked annotation class names from well-known
     * nullability annotation libraries.
     *
     * @see #isFieldNullable(Field)
     */
    public static final ImmutableSet<String> NULL_UNMARKED_ANNOTATION_CLASS_NAMES = ImmutableSet.of("NullUnmarked",
            "NullByDefault");

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
     * Gets an {@link ImmutableSet} of non-{@link #isFieldNullable(Field)} {@link Field}s that are unset (set to
     * <code>null</code>) in the given {@link Object}.
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
     * Gets the {@link Field} nullability status as designated from a well-known nullability annotation library:
     * {@link #NULL_ABLE_ANNOTATION_CLASS_NAMES} or {@link #NON_NULL_ANNOTATION_CLASS_NAMES} on the {@link Field},
     * {@link #NULL_MARKED_ANNOTATION_CLASS_NAMES} or {@link #NULL_UNMARKED_ANNOTATION_CLASS_NAMES} on the enclosing
     * {@link Class} or {@link Package}.
     *
     * @param field the {@link Field} to inspect
     *
     * @return internally-cached <code>true</code> if the given {@link Field} is <code>null</code>-able,
     * <code>false</code> otherwise
     */
    public static boolean isFieldNullable(final Field field) {
        return NULLABILITY_OF_FIELDS.computeIfAbsent(field, _ -> {
            if (field.getType().isPrimitive() || field.isSynthetic()) {
                return false;
            }
            final var fieldAnnotationClassNames = getAnnotationClassNames(field.getAnnotatedType().getAnnotations());
            if (!disjoint(fieldAnnotationClassNames, NULL_ABLE_ANNOTATION_CLASS_NAMES)) {
                return true;
            }
            if (!disjoint(fieldAnnotationClassNames, NON_NULL_ANNOTATION_CLASS_NAMES)) {
                return false;
            }
            for (var enclosingClass = field.getDeclaringClass(); enclosingClass != null;
                    enclosingClass = enclosingClass.getEnclosingClass()) {
                final var enclosingClassAnnotationClassNames = getAnnotationClassNames(enclosingClass.getAnnotations());
                if (!disjoint(enclosingClassAnnotationClassNames, NULL_MARKED_ANNOTATION_CLASS_NAMES)) {
                    return false;
                }
                if (!disjoint(enclosingClassAnnotationClassNames, NULL_UNMARKED_ANNOTATION_CLASS_NAMES)) {
                    return true;
                }
            }
            final var classPackage = field.getDeclaringClass().getPackage();
            if (classPackage != null) {
                final var classPackageAnnotationClassNames = getAnnotationClassNames(classPackage.getAnnotations());
                if (!disjoint(classPackageAnnotationClassNames, NULL_MARKED_ANNOTATION_CLASS_NAMES)) {
                    return false;
                }
                if (!disjoint(classPackageAnnotationClassNames, NULL_UNMARKED_ANNOTATION_CLASS_NAMES)) {
                    return true;
                }
            }
            return true;
        });
    }

    private static ImmutableSet<String> getAnnotationClassNames(final Annotation[] annotations) {
        return stream(annotations)
                .map(annotation -> annotation.annotationType().getSimpleName())
                .collect(toImmutableSet());
    }

    private JSpecifyAnnotationsUtil() {}
}
