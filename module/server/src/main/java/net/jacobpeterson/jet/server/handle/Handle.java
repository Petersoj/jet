package net.jacobpeterson.jet.server.handle;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.session.Session;
import net.jacobpeterson.jet.server.session.SessionStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * {@link Handle} is a class that represents a web server request and response.
 * <p>
 * Note: this class is not thread-safe.
 */
@NullMarked
@RequiredArgsConstructor
public class Handle {

    /** FOR INTERNAL USE ONLY. */
    private final @Getter HandleInternals internals;

    /** A {@link Stopwatch} to measure the request and response lifecycle elapsed time. */
    private final @Getter Stopwatch stopwatch = Stopwatch.createStarted();

    /** The {@link Request}. */
    private final @Getter Request request = new Request(this);

    /** The {@link Response}. */
    private final @Getter Response response = new Response(this);

    /**
     * Calls {@link #withPausedStopwatch(Supplier)} with {@link Runnable#run()}.
     */
    public void withPausedStopwatch(final Runnable runnable) {
        withPausedStopwatch(new Supplier<>() {
            @Override
            public @Nullable Object get() {
                runnable.run();
                return null;
            }
        });
    }

    /**
     * Calls {@link Stopwatch#stop()}, {@link Supplier#get()}, and finally {@link Stopwatch#start()}.
     *
     * @param <T>      the {@link Supplier} type
     * @param supplier the {@link Supplier}
     *
     * @return {@link Supplier#get()}
     */
    public <T> T withPausedStopwatch(final Supplier<T> supplier) {
        try {
            stopwatch.stop();
            return supplier.get();
        } finally {
            stopwatch.start();
        }
    }

    /**
     * @return {@link JetServer#getSessionStore()} {@link SessionStore#getOrCreate(Handle)}
     */
    public Session getSession() {
        return internals.getJetServer().getSessionStore().getOrCreate(this);
    }
}
