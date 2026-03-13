package net.jacobpeterson.jet.common.io.bounded;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * {@link BoundedInputStream} is a {@link FilterInputStream} that limits the number of bytes that can be read from a
 * given {@link InputStream}. This is useful for reading {@link InputStream}s of an unknown length, such as a chunked
 * file upload from a web client or a chunked file download from a web server. {@link BoundedInputStream} can also be
 * used to only count the number of bytes read instead of also imposing a bound.
 *
 * @see
 * <a href="https://github.com/apache/commons-io/blob/5e39dd4778d58bf0c6314eae3da3781b809f5285/src/main/java/org/apache/commons/io/input/BoundedInputStream.java">
 * github.com/apache/commons-io/.../BoundedInputStream.java</a>
 * @see ByteStreams#limit(InputStream, long)
 */
@NullMarked
public class BoundedInputStream extends FilterInputStream {

    /**
     * The maximum number of bytes that can be read, or <code>null</code> for no bound.
     */
    private final @Getter @Nullable Long boundCount;

    /**
     * <code>true</code> to throw {@link BoundException} on the first call to {@link #close()} if {@link #isBound()},
     * <code>false</code> to not throw.
     */
    private final @Getter boolean throwOnClose;

    /**
     * The current number of bytes read.
     */
    private @Getter long currentCount;

    private boolean thrownOnClose;
    private long mark;

    /**
     * Calls {@link #BoundedInputStream(InputStream, Long, boolean)} with <code>throwOnClose</code> set to
     * <code>true</code>.
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount) {
        this(inputStream, boundCount, true);
    }

    /**
     * Calls {@link #BoundedInputStream(InputStream, Long, long, boolean)} with <code>throwOnClose</code> set to
     * <code>true</code>.
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount, final long initialCount) {
        this(inputStream, boundCount, initialCount, true);
    }

    /**
     * Calls {@link #BoundedInputStream(InputStream, Long, long, boolean)} with <code>initialCount</code> set to
     * <code>0</code>.
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount,
            final boolean throwOnClose) {
        this(inputStream, boundCount, 0, throwOnClose);
    }

    /**
     * Instantiates a new {@link BoundedInputStream}.
     *
     * @param inputStream  the {@link InputStream}
     * @param boundCount   the {@link #getBoundCount()} (a negative number is equivalent to <code>null</code>)
     * @param initialCount the initial count to set {@link #getCurrentCount()} to
     * @param throwOnClose the {@link #isThrowOnClose()}
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount, final long initialCount,
            final boolean throwOnClose) {
        super(inputStream);
        this.boundCount = boundCount != null && boundCount >= 0 ? boundCount : null;
        this.throwOnClose = throwOnClose;
        currentCount = initialCount;
    }

    /**
     * @return <code>true</code> if the maximum number of bytes have been read (<code>{@link #getBoundCount()} != null
     * &amp;&amp; {@link #getCurrentCount()} &gt;= {@link #getBoundCount()}</code>), <code>false</code> otherwise
     */
    public boolean isBound() {
        return boundCount != null && currentCount >= boundCount;
    }

    @Override
    public int read() throws IOException {
        if (isBound()) {
            return -1;
        }
        final var read = super.read();
        if (read != -1) {
            currentCount += 1;
        }
        return read;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (isBound()) {
            return -1;
        }
        final var read = super.read(b, off, (int) lengthToRead(len));
        if (read != -1) {
            currentCount += read;
        }
        return read;
    }

    @Override
    public long skip(final long n) throws IOException {
        final var skipped = super.skip(lengthToRead(n));
        currentCount += skipped;
        return skipped;
    }

    private long lengthToRead(final long length) {
        return boundCount == null ? length : min(length, boundCount - currentCount);
    }

    /**
     * @return the number of bytes that can be read before {@link #isBound()} becomes <code>true</code>, or
     * {@link Long#MAX_VALUE} if {@link #getBoundCount()} is <code>null</code>
     *
     * @see #available()
     */
    public long availableBeforeBound() {
        return boundCount == null ? Long.MAX_VALUE : max(0, boundCount - currentCount);
    }

    @Override
    public int available() throws IOException {
        return min(super.available(), (int) min(availableBeforeBound(), Integer.MAX_VALUE));
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (throwOnClose && isBound() && !thrownOnClose) {
            thrownOnClose = true;
            throw new BoundException(this);
        }
    }

    @Override
    public void mark(final int readlimit) {
        super.mark(readlimit);
        mark = currentCount;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        currentCount = mark;
    }
}
