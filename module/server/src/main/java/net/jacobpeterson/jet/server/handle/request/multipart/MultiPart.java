package net.jacobpeterson.jet.server.handle.request.multipart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.io.ByteStreams;
import com.google.errorprone.annotations.concurrent.LazyInit;
import lombok.RequiredArgsConstructor;
import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.common.http.header.contentdisposition.ContentDisposition;
import net.jacobpeterson.jet.common.http.header.contenttype.ContentType;
import net.jacobpeterson.jet.server.handle.response.exception.StatusException;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.MultiPart.Part;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.io.ByteStreams.readFully;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_DISPOSITION;
import static net.jacobpeterson.jet.common.http.header.Header.CONTENT_TYPE;
import static net.jacobpeterson.jet.common.http.status.Status.BAD_REQUEST_400;
import static net.jacobpeterson.jet.common.http.status.Status.CONTENT_TOO_LARGE_413;
import static org.eclipse.jetty.io.Content.Source.asInputStream;

/**
 * {@link MultiPart} is a class that represents a part in a multipart web server request.
 */
@NullMarked
@RequiredArgsConstructor
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class MultiPart {

    private final Part part;
    private @LazyInit @Nullable ImmutableListMultimap<String, String> headers;
    private @LazyInit @Nullable Optional<ContentDisposition> contentDisposition;
    private @LazyInit @Nullable Optional<ContentType> contentType;

    /**
     * @return the size (in bytes)
     */
    public long getSize() {
        final var length = part.getLength();
        checkState(length >= 0); // Should always be positive or zero
        return length;
    }

    /**
     * @return internally-cached headers {@link String} {@link ImmutableListMultimap}
     */
    public ImmutableListMultimap<String, String> getHeaders() {
        if (headers == null) {
            final var headers = ImmutableListMultimap.<String, String>builder();
            for (final var headerValues : HttpFields.asMap(part.getHeaders()).entrySet()) {
                for (final var headerValue : headerValues.getValue()) {
                    headers.put(headerValues.getKey(), headerValue);
                }
            }
            this.headers = headers.build();
        }
        return headers;
    }

    /**
     * @return {@link #getHeader(String)} with {@link Header#toString()}
     */
    public @Nullable String getHeader(final Header header) {
        return getHeader(header.toString());
    }

    /**
     * @return {@link #getHeaders()} {@link ImmutableListMultimap#get(Object)} {@link ImmutableList#getFirst()} or
     * <code>null</code>
     */
    public @Nullable String getHeader(final String header) {
        final var headers = getHeaders().get(header);
        return headers.isEmpty() ? null : headers.getFirst();
    }

    /**
     * @return internally-cached {@link ContentDisposition#parse(String)} {@link #getHeader(Header)}
     * {@link Header#CONTENT_DISPOSITION}
     */
    public @Nullable ContentDisposition getContentDisposition() throws StatusException {
        if (contentDisposition == null) {
            final var contentDisposition = getHeader(CONTENT_DISPOSITION);
            try {
                this.contentDisposition = Optional.ofNullable(contentDisposition == null ? null :
                        ContentDisposition.parse(contentDisposition));
            } catch (final IllegalArgumentException illegalArgumentException) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(CONTENT_DISPOSITION),
                        illegalArgumentException);
            }
        }
        return contentDisposition.orElse(null);
    }

    /**
     * @return internally-cached {@link ContentType#parse(String)} {@link #getHeader(Header)}
     * {@link Header#CONTENT_TYPE}
     */
    public @Nullable ContentType getContentType() throws StatusException {
        if (contentType == null) {
            final var contentType = getHeader(CONTENT_TYPE);
            try {
                this.contentType = Optional.ofNullable(contentType == null ? null : ContentType.parse(contentType));
            } catch (final IllegalArgumentException illegalArgumentException) {
                throw new StatusException(BAD_REQUEST_400, "Failed to parse `%s` header".formatted(CONTENT_TYPE),
                        illegalArgumentException);
            }
        }
        return contentType.orElse(null);
    }

    /**
     * @return {@link #getContentType()} {@link ContentType#getCharset()}, defaulting to
     * {@link StandardCharsets#UTF_8}
     */
    public Charset getCharset() {
        final var contentType = getContentType();
        if (contentType == null) {
            return UTF_8;
        }
        final var charset = contentType.getCharset();
        return charset != null ? charset : UTF_8;
    }

    /**
     * @return the part content {@link InputStream}
     */
    public InputStream getInputStream() {
        return asInputStream(part.getContentSource());
    }

    /**
     * @return {@link #getInputStream()} {@link ByteStreams#readFully(InputStream, byte[])}
     */
    public byte[] getBytes() {
        try (final var inputStream = getInputStream()) {
            final var size = getSize();
            if (size > Integer.MAX_VALUE - 8) {
                throw new StatusException(CONTENT_TOO_LARGE_413, "Part size is larger than byte array maximum size");
            }
            final var bytes = new byte[(int) size];
            readFully(inputStream, bytes);
            return bytes;
        } catch (final IOException ioException) {
            throw new StatusException(BAD_REQUEST_400, ioException);
        }
    }

    /**
     * @return {@link String#String(byte[], Charset)} with {@link #getBytes()} and {@link #getCharset()}
     */
    public String getString() throws StatusException {
        final var bytes = getBytes();
        final var charset = getCharset();
        try {
            return new String(bytes, charset);
        } catch (final Exception exception) {
            throw new StatusException(BAD_REQUEST_400, exception);
        }
    }
}
