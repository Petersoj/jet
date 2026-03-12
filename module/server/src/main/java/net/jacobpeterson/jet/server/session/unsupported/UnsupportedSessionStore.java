package net.jacobpeterson.jet.server.session.unsupported;

import net.jacobpeterson.jet.server.handle.Handle;
import net.jacobpeterson.jet.server.session.Session;
import net.jacobpeterson.jet.server.session.SessionStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@link UnsupportedSessionStore} is a {@link SessionStore} that always throws {@link UnsupportedSessionStore}.
 */
@NullMarked
public final class UnsupportedSessionStore implements SessionStore {

    /**
     * {@link UnsupportedSessionStore} singleton instance.
     */
    public static final UnsupportedSessionStore INSTANCE = new UnsupportedSessionStore();

    private UnsupportedSessionStore() {}

    @Override
    public Session getOrCreate(final Handle handle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Session get(final Handle handle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Session get(final String id) {
        throw new UnsupportedOperationException();
    }
}
