package net.jacobpeterson.jet.server.handle;

import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.server.handle.request.Request;
import net.jacobpeterson.jet.server.handle.response.Response;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Handle} is a class that represents a web server request and response.
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
}
