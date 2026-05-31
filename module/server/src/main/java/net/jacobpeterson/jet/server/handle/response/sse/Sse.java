package net.jacobpeterson.jet.server.handle.response.sse;

import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.server.handle.response.Response;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Throwables.throwIfUnchecked;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link Sse} represents a Server-Sent Events (SSE) {@link Response} and provides methods to send events and comments.
 * <p>
 * Note: this class is thread-safe.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events">
 * developer.mozilla.org</a>
 */
@RequiredArgsConstructor @Slf4j
@NullMarked
public class Sse {

    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');

    private final OutputStream bodyOutputStream;
    private final List<Runnable> onCloses = new ArrayList<>();
    private boolean closed;

    /**
     * Calls {@link #send(String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public void send(final String data) {
        send(data, true);
    }

    /**
     * Calls {@link #send(String, String, boolean)} with <code>event</code> set to <code>null</code>.
     */
    public void send(final String data, final boolean ignoreThrowable) {
        send(null, data, ignoreThrowable);
    }

    /**
     * Calls {@link #send(String, String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public void send(final @Nullable String event, final String data) {
        send(event, data, true);
    }

    /**
     * Calls {@link #send(String, String, String, boolean)} with <code>id</code> set to <code>null</code>.
     */
    public void send(final @Nullable String event, final String data, final boolean ignoreThrowable) {
        send(null, event, data, ignoreThrowable);
    }

    /**
     * Calls {@link #send(String, String, String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public void send(final @Nullable String id, final @Nullable String event, final String data) {
        send(id, event, data, true);
    }

    /**
     * Sends <code>data</code> with an optional <code>id</code> and <code>event</code>.
     *
     * @param id              the optional ID {@link String}
     * @param event           the optional event {@link String}
     * @param data            the data {@link String}
     * @param ignoreThrowable <code>true</code> if any {@link Throwable}s thrown internally should be ignored,
     *                        <code>false</code> otherwise. Note that {@link Throwable}s thrown internally will always
     *                        call {@link #close()}.
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#fields">
     * developer.mozilla.org</a>
     */
    public synchronized void send(final @Nullable String id, final @Nullable String event, final String data,
            final boolean ignoreThrowable) {
        try {
            if (id != null) {
                writeLine("id", sanitize(id));
            }
            if (event != null) {
                writeLine("event", sanitize(event));
            }
            for (final var dataLine : NEWLINE_SPLITTER.split(data)) {
                writeLine("data", dataLine);
            }
            writeFlush();
        } catch (final Throwable throwable) {
            closeForThrowable(ignoreThrowable, throwable);
        }
    }

    /**
     * Calls {@link #comment(String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public void comment(final String comment) {
        comment(comment, true);
    }

    /**
     * Sends a comment.
     *
     * @param comment         the comment {@link String}
     * @param ignoreThrowable <code>true</code> if any {@link Throwable}s thrown internally should be ignored,
     *                        <code>false</code> otherwise. Note that {@link Throwable}s thrown internally will always
     *                        call {@link #close()}.
     */
    public synchronized void comment(final String comment, final boolean ignoreThrowable) {
        try {
            writeLine("", sanitize(comment));
            writeFlush();
        } catch (final Throwable throwable) {
            closeForThrowable(ignoreThrowable, throwable);
        }
    }

    private String sanitize(final String input) {
        return input.replace("\n", "");
    }

    private void writeLine(final String beforeColon, final String afterColon) throws IOException {
        bodyOutputStream.write((beforeColon + ": " + afterColon + "\n").getBytes(UTF_8));
    }

    private void writeFlush() throws IOException {
        bodyOutputStream.write('\n');
        bodyOutputStream.flush();
    }

    private void closeForThrowable(final boolean ignoreThrowable, final Throwable throwable) {
        Throwable closeThrowable = null;
        try {
            close();
        } catch (final Throwable theCloseThrowable) {
            closeThrowable = theCloseThrowable;
        }
        if (ignoreThrowable) {
            LOGGER.debug("SSE ignored throwable", throwable);
            if (closeThrowable != null) {
                throwIfUnchecked(closeThrowable);
                throw new RuntimeException(closeThrowable);
            }
        } else {
            if (closeThrowable != null) {
                throwable.addSuppressed(throwable);
            }
            throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Adds the given {@link Runnable} to an internal list of {@link Runnable}s that all are guaranteed to run a single
     * time when {@link #close()} is called for the first time.
     *
     * @param onClose the {@link Runnable}
     */
    public synchronized void onClose(final Runnable onClose) {
        onCloses.add(onClose);
    }

    /**
     * @return <code>true</code> if {@link #close()} has been called, <code>false</code> otherwise
     */
    public synchronized boolean isClosed() {
        return closed;
    }

    /**
     * Closes this {@link Sse} {@link Response} and runs all {@link #onClose(Runnable)} {@link Runnable}s. Subsequent
     * calls to this method are ignored.
     */
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        Throwable multiThrowable = null;
        try {
            bodyOutputStream.close();
        } catch (final Throwable throwable) {
            multiThrowable = throwable;
        }
        for (final var onClose : onCloses) {
            try {
                onClose.run();
            } catch (final Throwable throwable) {
                if (multiThrowable == null) {
                    multiThrowable = throwable;
                } else {
                    multiThrowable.addSuppressed(throwable);
                }
            }
        }
        if (multiThrowable != null) {
            throwIfUnchecked(multiThrowable);
            throw new RuntimeException(multiThrowable);
        }
    }
}
