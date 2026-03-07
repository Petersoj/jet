package net.jacobpeterson.jet.server.session.simple;

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
    public boolean has(final String key) {
        return map.containsKey(key);
    }

    @Override
    public <T> @Nullable T get(final String key, final Class<T> clazz) {
        return clazz.cast(map.get(key));
    }

    @Override
    public <T> @Nullable T set(final String key, @Nullable final Object value, final Class<T> clazz) {
        if (value == null) {
            return remove(key, clazz);
        }
        return clazz.cast(map.put(key, value));
    }

    @Override
    public <T> @Nullable T remove(final String key, final Class<T> clazz) {
        return clazz.cast(map.remove(key));
    }
}
