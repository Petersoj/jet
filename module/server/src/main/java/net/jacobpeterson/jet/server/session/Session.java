package net.jacobpeterson.jet.server.session;

import com.google.common.collect.ImmutableSet;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * {@link Session} is an interface representing a web session.
 * <p>
 * Note: implementations must be thread-safe.
 */
@NullMarked
public interface Session {

    /**
     * @return the session ID {@link String}
     */
    String getId();

    /**
     * @return a copied {@link String} {@link Set} of keys stored in this {@link Session}
     */
    ImmutableSet<String> keys();

    /**
     * Gets a value for the given <code>key</code>.
     *
     * @param <T> the existing value type
     * @param key the key {@link String}
     *
     * @return the value for the given <code>key</code>, or <code>null</code> if the given <code>key</code> doesn't
     * exist
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    <T> @Nullable T get(final String key);

    /**
     * Sets the given <code>value</code> for the given <code>key</code>.
     *
     * @param <T>   the existing value type
     * @param key   the key {@link String}
     * @param value the value, or <code>null</code> to {@link #remove(String)}
     *
     * @return the existing value for the given <code>key</code>, or <code>null</code> if there was no existing value
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    <T> @Nullable T set(final String key, final @Nullable Object value);

    /**
     * Removes the <code>value</code> for the given <code>key</code>.
     *
     * @param <T> the existing value type
     * @param key the key {@link String}
     *
     * @return the existing value for the given <code>key</code>, or <code>null</code> if there was no existing value
     */
    @SuppressWarnings("TypeParameterUnusedInFormals")
    <T> @Nullable T remove(final String key);
}
