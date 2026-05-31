package net.jacobpeterson.jet.server.handle.response.sse;

import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.server.handle.response.Response;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;

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

    /**
     * Calls {@link #send(String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public boolean send(final String data) {
        return send(data, true);
    }

    /**
     * Calls {@link #send(String, String, boolean)} with <code>event</code> set to <code>null</code>.
     */
    public boolean send(final String data, final boolean ignoreThrowable) {
        return send(null, data, ignoreThrowable);
    }

    /**
     * Calls {@link #send(String, String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public boolean send(final @Nullable String event, final String data) {
        return send(event, data, true);
    }

    /**
     * Calls {@link #send(String, String, String, boolean)} with <code>id</code> set to <code>null</code>.
     */
    public boolean send(final @Nullable String event, final String data, final boolean ignoreThrowable) {
        return send(null, event, data, ignoreThrowable);
    }

    /**
     * Calls {@link #send(String, String, String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public boolean send(final @Nullable String id, final @Nullable String event, final String data) {
        return send(id, event, data, true);
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
     * @return <code>true</code> if sent successfully, <code>false</code> otherwise
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#fields">
     * developer.mozilla.org</a>
     */
    public synchronized boolean send(final @Nullable String id, final @Nullable String event, final String data,
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
            return true;
        } catch (final Throwable throwable) {
            closeForThrowable(ignoreThrowable, throwable);
            return false;
        }
    }

    /**
     * Calls {@link #comment(String, boolean)} with <code>ignoreThrowable</code> set to <code>true</code>.
     */
    public boolean comment(final String comment) {
        return comment(comment, true);
    }

    /**
     * Sends a comment.
     *
     * @param comment         the comment {@link String}
     * @param ignoreThrowable <code>true</code> if any {@link Throwable}s thrown internally should be ignored,
     *                        <code>false</code> otherwise. Note that {@link Throwable}s thrown internally will always
     *                        call {@link #close()}.
     *
     * @return <code>true</code> if sent successfully, <code>false</code> otherwise
     */
    public synchronized boolean comment(final String comment, final boolean ignoreThrowable) {
        try {
            writeLine("", sanitize(comment));
            writeFlush();
            return true;
        } catch (final Throwable throwable) {
            closeForThrowable(ignoreThrowable, throwable);
            return false;
        }
    }

    private String sanitize(final String input) {
        return input.replace('\n', ' ');
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
     * Closes this {@link Sse} {@link Response}
     */
    public synchronized void close() {
        try {
            bodyOutputStream.close();
        } catch (final IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }
}
