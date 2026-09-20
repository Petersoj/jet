package net.jacobpeterson.jet.server.session.simple;

import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.jacobpeterson.jet.server.session.Session;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

/**
 * {@link SimpleSession} is a simple {@link Session} implementation that uses a {@link Collections#synchronizedMap(Map)}
 * {@link HashMap} to store session data.
 */
@NullMarked
@EqualsAndHashCode @ToString
public class SimpleSession implements Session {

    private final String id;
    private final Map<String, Object> map = synchronizedMap(new HashMap<>());

    /**
     * Instantiates a new {@link SimpleSession}.
     *
     * @param id the {@link #getId()}
     */
    public SimpleSession(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ImmutableSet<String> keys() {
        return ImmutableSet.copyOf(map.keySet());
    }

    @SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals"})
    @Override
    public <T> @Nullable T get(final String key) {
        return (T) map.get(key);
    }

    @SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals"})
    @Override
    public <T> @Nullable T set(final String key, @Nullable final Object value) {
        if (value == null) {
            return remove(key);
        }
        return (T) map.put(key, value);
    }

    @SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals"})
    @Override
    public <T> @Nullable T remove(final String key) {
        return (T) map.remove(key);
    }
}
