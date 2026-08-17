package net.jacobpeterson.jet.common.util.io;

import net.jacobpeterson.jet.common.io.function.IoConsumer;
import net.jacobpeterson.jet.common.io.function.IoFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Throwables.throwIfUnchecked;
import static com.google.common.util.concurrent.Futures.getUnchecked;
import static com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly;
import static net.jacobpeterson.jet.common.http.header.contentencoding.CompressionType.DEFAULT_BUFFER_SIZE;

/**
 * {@link IoUtil} is a utility class for I/O and streams.
 */
@NullMarked
public final class IoUtil {

    /**
     * Calls {@link #outputStreamToInputStreamResult(IoConsumer, IoFunction)} with no result.
     */
    public static void outputStreamToInputStream(final IoConsumer<OutputStream> outputStreamConsumer,
            final IoConsumer<InputStream> inputStreamConsumer) {
        outputStreamToInputStreamResult(outputStreamConsumer, inputStream -> {
            inputStreamConsumer.accept(inputStream);
            return null;
        });
    }

    /**
     * Streams the data from a given {@link OutputStream} to an {@link InputStream} using {@link PipedInputStream} and
     * {@link Thread#ofVirtual()}.
     *
     * @param <T>                  the result type
     * @param outputStreamConsumer the {@link OutputStream} {@link IoConsumer}
     * @param inputStreamFunction  the {@link InputStream} {@link IoFunction}
     *
     * @return the result
     */
    public static <T extends @Nullable Object> T outputStreamToInputStreamResult(
            final IoConsumer<OutputStream> outputStreamConsumer, final IoFunction<InputStream, T> inputStreamFunction) {
        final var future = new CompletableFuture<T>();
        final var pipedInputStream = new PipedInputStream(DEFAULT_BUFFER_SIZE);
        final PipedOutputStream pipedOutputStream;
        try {
            pipedOutputStream = new PipedOutputStream(pipedInputStream);
        } catch (final IOException ioException) { // Will never happen
            throw new UncheckedIOException(ioException);
        }
        Thread.ofVirtual().start(() -> {
            try (pipedInputStream) {
                future.complete(inputStreamFunction.apply(pipedInputStream));
            } catch (final Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        try (pipedOutputStream) {
            outputStreamConsumer.accept(pipedOutputStream);
        } catch (final Throwable throwable) {
            try {
                getUninterruptibly(future);
            } catch (final Throwable futureThrowable) {
                throwable.addSuppressed(futureThrowable);
            }
            throwIfUnchecked(throwable);
            throw new RuntimeException(throwable);
        }
        return getUnchecked(future);
    }

    private IoUtil() {}
}
