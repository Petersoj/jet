package net.jacobpeterson.jet.server.exception;

import lombok.Getter;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.server.handle.Handler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.TEXT_PLAIN_UTF_8;

/**
 * {@link StatusException} is a {@link RuntimeException} for {@link Handler}s to respond with a given {@link Status} and
 * body.
 */
@NullMarked
@Getter
public class StatusException extends RuntimeException {

    /** The response {@link Status}. */
    private final Status status;

    /** The response body bytes. */
    private final byte[] body;

    /** The {@link ContentType} of {@link #getBody()}. */
    private final ContentType contentType;

    /**
     * Calls {@link #StatusException(Status, String)} with <code>body</code> set to <code>null</code>.
     */
    public StatusException(final Status status) {
        this(status, (String) null);
    }

    /**
     * Calls {@link #StatusException(Status, String, Throwable)} with <code>cause</code> set to <code>null</code>.
     */
    public StatusException(final Status status, final @Nullable String body) {
        this(status, body, null);
    }

    /**
     * Calls {@link #StatusException(Status, String, Throwable)} with <code>body</code> set to <code>null</code>.
     */
    public StatusException(final Status status, final @Nullable Throwable cause) {
        this(status, null, cause);
    }

    /**
     * Calls {@link #StatusException(Status, byte[], ContentType, Throwable)} with <code>body</code> set to
     * {@link String#getBytes()} {@link StandardCharsets#UTF_8} and <code>contentType</code> set to <code>null</code>.
     */
    public StatusException(final Status status, final @Nullable String body, final @Nullable Throwable cause) {
        this(status, body == null ? null : body.getBytes(UTF_8), null, cause);
    }

    /**
     * Calls {@link #StatusException(Status, byte[], ContentType, Throwable)} with <code>cause</code> set to
     * <code>null</code>.
     */
    public StatusException(final Status status, final byte @Nullable [] body, final @Nullable ContentType contentType) {
        this(status, body, contentType, null);
    }

    /**
     * Instantiates a new {@link StatusException}.
     *
     * @param status      the {@link #getStatus()}
     * @param body        the {@link #getBody()}, or <code>null</code> to use {@link Status#toString()}
     * @param contentType the {@link #getContentType()}, or <code>null</code> to use
     *                    {@link ContentType#TEXT_PLAIN_UTF_8}
     * @param cause       the {@link #getCause()}
     */
    public StatusException(final Status status, final byte @Nullable [] body,
            final @Nullable ContentType contentType, final @Nullable Throwable cause) {
        super(cause);
        this.status = status;
        this.body = body != null ? body : status.toString().getBytes(UTF_8);
        this.contentType = contentType != null ? contentType : TEXT_PLAIN_UTF_8;
    }
}
