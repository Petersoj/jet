package net.jacobpeterson.jet.server.handle;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import net.jacobpeterson.jet.server.JetServer;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import net.jacobpeterson.jet.server.session.Session;
import net.jacobpeterson.jet.server.session.SessionStore;
import org.jspecify.annotations.NullMarked;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * {@link Handle} is a class that represents a web server request and response.
 * <p>
 * Note: this class is not thread-safe.
 */
@NullMarked
public class Handle {

    /**
     * FOR INTERNAL USE ONLY.
     */
    private final @Getter HandleInternals internals;

    /**
     * The {@link Request}.
     */
    private final @Getter Request request;

    /**
     * The {@link Response}.
     */
    private final @Getter Response response;

    /**
     * A {@link Stopwatch} to measure the request and response lifecycle elapsed time.
     */
    private final @Getter Stopwatch stopwatch = Stopwatch.createStarted();

    /**
     * Instantiates a new {@link Handle}.
     *
     * @param internals the {@link #getInternals()}
     */
    public Handle(final HandleInternals internals) {
        this.internals = internals;
        request = new Request(this);
        response = new Response(this);
    }

    /**
     * Same as {@link #withPausedStopwatch(Supplier)}, but with {@link Runnable#run()}.
     */
    public void withPausedStopwatch(final Runnable runnable) {
        stopwatch.stop();
        try {
            runnable.run();
        } finally {
            stopwatch.start();
        }
    }

    /**
     * Calls {@link Stopwatch#stop()}, {@link Supplier#get()}, and finally {@link Stopwatch#start()}.
     * <p>
     * This is useful for situations where you want to pause the {@link #getStopwatch()} while running code that should
     * not influence the {@link Stopwatch#elapsed()} time, such as when reading the request body.
     *
     * @param <T>      the {@link Supplier} type
     * @param supplier the {@link Supplier}
     *
     * @return {@link Supplier#get()}
     */
    public <T> T withPausedStopwatch(final Supplier<T> supplier) {
        stopwatch.stop();
        try {
            return supplier.get();
        } finally {
            stopwatch.start();
        }
    }

    /**
     * @return {@link JetServer#getSessionStore()} {@link SessionStore#getOrCreate(Handle)}
     */
    public Session getSession() {
        return requireNonNull(internals.getJetServer().getSessionStore(),
                "`JetServer.Builder.sessionStore()` must be called to enable sessions").getOrCreate(this);
    }
}
