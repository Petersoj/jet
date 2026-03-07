package net.jacobpeterson.jet.server.handle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.server.Jet;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.jspecify.annotations.NullMarked;

/**
 * {@link HandleInternals} is a class to be internally instantiated by Jet and provided to {@link Handle} subclass
 * constructors.
 */
@NullMarked
@RequiredArgsConstructor @Getter
public final class HandleInternals {

    /** FOR INTERNAL USE ONLY. */
    private final Jet jet;

    /** FOR INTERNAL USE ONLY. */
    private final Request request;

    /** FOR INTERNAL USE ONLY. */
    private final Response response;
}
