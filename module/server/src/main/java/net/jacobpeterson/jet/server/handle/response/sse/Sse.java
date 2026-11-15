package net.jacobpeterson.jet.server.handle.response.sse;

import com.google.common.base.Splitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.jet.server.handle.response.Response;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.jacobpeterson.jet.common.util.throwable.ThrowableUtil.throwCheckedOrUnchecked;

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
     * Calls {@link #send(String, boolean)} with <code>logThrowable</code> set to <code>true</code>.
     */
    public boolean send(final String data) {
        return send(data, true);
    }

    /**
     * Calls {@link #send(String, String, boolean)} with <code>event</code> set to <code>null</code>.
     */
    public boolean send(final String data, final boolean logThrowable) {
        return send(null, data, logThrowable);
    }

    /**
     * Calls {@link #send(String, String, boolean)} with <code>logThrowable</code> set to <code>true</code>.
     */
    public boolean send(final @Nullable String event, final String data) {
        return send(event, data, true);
    }

    /**
     * Calls {@link #send(String, String, String, boolean)} with <code>id</code> set to <code>null</code>.
     */
    public boolean send(final @Nullable String event, final String data, final boolean logThrowable) {
        return send(null, event, data, logThrowable);
    }

    /**
     * Calls {@link #send(String, String, String, boolean)} with <code>logThrowable</code> set to <code>true</code>.
     */
    public boolean send(final @Nullable String id, final @Nullable String event, final String data) {
        return send(id, event, data, true);
    }

    /**
     * Sends <code>data</code> with an optional <code>id</code> and <code>event</code>.
     *
     * @param id           the optional ID {@link String}
     * @param event        the optional event {@link String}
     * @param data         the data {@link String}
     * @param logThrowable <code>true</code> if any {@link Throwable}s thrown internally should be logged instead of
     *                     thrown. Note that {@link Throwable}s thrown internally will always call {@link #close()}.
     *
     * @return <code>true</code> if sent successfully, <code>false</code> otherwise
     *
     * @see
     * <a href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#fields">
     * developer.mozilla.org</a>
     */
    public synchronized boolean send(final @Nullable String id, final @Nullable String event, final String data,
            final boolean logThrowable) {
        try {
            final var line = new StringBuilder();
            if (id != null) {
                line.append("id: ").append(sanitize(id)).append('\n');
            }
            if (event != null) {
                line.append("event: ").append(sanitize(event)).append('\n');
            }
            for (final var dataLine : NEWLINE_SPLITTER.split(data)) {
                line.append("data: ").append(dataLine).append('\n');
            }
            bodyOutputStream.write(line.append('\n').toString().getBytes(UTF_8));
            bodyOutputStream.flush();
            return true;
        } catch (final Throwable throwable) {
            closeForThrowable(logThrowable, throwable);
            return false;
        }
    }

    /**
     * Calls {@link #comment(String, boolean)} with <code>logThrowable</code> set to <code>true</code>.
     */
    public boolean comment(final String comment) {
        return comment(comment, true);
    }

    /**
     * Sends a comment.
     *
     * @param comment      the comment {@link String}
     * @param logThrowable <code>true</code> if any {@link Throwable}s thrown internally should be logged instead of
     *                     thrown. Note that {@link Throwable}s thrown internally will always call {@link #close()}.
     *
     * @return <code>true</code> if sent successfully, <code>false</code> otherwise
     */
    public synchronized boolean comment(final String comment, final boolean logThrowable) {
        try {
            bodyOutputStream.write((": " + sanitize(comment) + "\n\n").getBytes(UTF_8));
            bodyOutputStream.flush();
            return true;
        } catch (final Throwable throwable) {
            closeForThrowable(logThrowable, throwable);
            return false;
        }
    }

    private String sanitize(final String input) {
        return input.replace('\n', ' ');
    }

    private void closeForThrowable(final boolean logThrowable, final Throwable throwable) {
        try {
            close(false);
        } catch (final Throwable closeThrowable) {
            throwable.addSuppressed(throwable);
        }
        handleIgnorableThrowable(logThrowable, throwable);
    }

    /**
     * Calls {@link #close(boolean)} with <code>logThrowable</code> set to <code>true</code>.
     */
    public void close() {
        close(true);
    }

    /**
     * Closes this {@link Sse} {@link Response}.
     *
     * @param logThrowable <code>true</code> if any {@link Throwable}s thrown internally should be logged instead of
     *                     thrown. Note that {@link Throwable}s thrown internally will always call {@link #close()}.
     */
    public void close(final boolean logThrowable) {
        try {
            bodyOutputStream.close();
        } catch (final Throwable throwable) {
            handleIgnorableThrowable(logThrowable, throwable);
        }
    }

    private void handleIgnorableThrowable(final boolean logThrowable, final Throwable throwable) {
        if (logThrowable) {
            LOGGER.debug("", throwable);
        } else {
            throwCheckedOrUnchecked(throwable);
        }
    }
}
