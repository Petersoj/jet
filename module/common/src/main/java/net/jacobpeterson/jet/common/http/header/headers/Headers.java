package net.jacobpeterson.jet.common.http.header.headers;

import org.jspecify.annotations.NullMarked;

/**
 * {@link Headers} is the mutable {@link AbstractHeaders} implementation.
 */
@NullMarked
public final class Headers extends AbstractHeaders {

    /**
     * Create a new {@link Headers} instance.
     *
     * @return the new {@link Headers} instance
     */
    public static Headers create() {
        return new Headers();
    }

    private Headers() {
        super(null);
    }

    /**
     * Calls {@link #removeAll(Object)} with the given <code>key</code>, then calls {@link #put(Object, Object)}.
     */
    public void set(final String key, final String value) {
        removeAll(key);
        put(key, value);
    }

    /**
     * Calls {@link #put(Object, Object)} if {@link #containsEntry(Object, Object)} is <code>false</code>.
     */
    public void ensureEntry(final String key, final String value) {
        if (!containsEntry(key, value)) {
            put(key, value);
        }
    }

    /**
     * Calls {@link #put(Object, Object)} if {@link #containsEntryIgnoreCase(String, String)} is <code>false</code>.
     */
    public void ensureEntryIgnoreCase(final String key, final String value) {
        if (!containsEntryIgnoreCase(key, value)) {
            put(key, value);
        }
    }

    /**
     * Calls {@link #put(Object, Object)} if {@link #containsEntryContaining(String, String)} is <code>false</code>.
     */
    public void ensureEntryContaining(final String key, final String value) {
        if (!containsEntryContaining(key, value)) {
            put(key, value);
        }
    }

    /**
     * Calls {@link #put(Object, Object)} if {@link #containsEntryContainingIgnoreCase(String, String)} is
     * <code>false</code>.
     */
    public void ensureEntryContainingIgnoreCase(final String key, final String value) {
        if (!containsEntryContainingIgnoreCase(key, value)) {
            put(key, value);
        }
    }
}
