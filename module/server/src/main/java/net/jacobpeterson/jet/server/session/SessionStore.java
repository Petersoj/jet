package net.jacobpeterson.jet.server.session;

import net.jacobpeterson.jet.server.handle.Handle;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@link SessionStore} is an interface that represents a store for {@link Session} instances.
 */
@NullMarked
public interface SessionStore {

    /**
     * @return {@link #get(Handle)}, or if <code>null</code>, creates a new {@link Session} for the given {@link Handle}
     */
    Session getOrCreate(final Handle handle);

    /**
     * @return the {@link Session} for the given {@link Handle}, or <code>null</code> if there is no existing
     * {@link Session}
     */
    @Nullable Session get(final Handle handle);

    /**
     * @return the {@link Session} for the given {@link Session#getId()}, or <code>null</code> if there is no existing
     * {@link Session}
     */
    @Nullable Session get(final String id);
}
