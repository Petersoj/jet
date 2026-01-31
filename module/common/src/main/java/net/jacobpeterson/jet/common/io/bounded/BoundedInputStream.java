package net.jacobpeterson.jet.common.io.bounded;

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
 * used to count the number of bytes read instead of imposing a bound.
 *
 * @see
 * <a href="https://github.com/apache/commons-io/blob/5e39dd4778d58bf0c6314eae3da3781b809f5285/src/main/java/org/apache/commons/io/input/BoundedInputStream.java">
 * github.com/apache/commons-io/.../BoundedInputStream.java</a>
 */
@NullMarked
public class BoundedInputStream extends FilterInputStream {

    /** The maximum number of bytes that can be read, or <code>null</code> for no bound. */
    private final @Getter @Nullable Long boundCount;
    /** The current number of bytes read. */
    private @Getter long currentCount;
    private final @Nullable OnBoundCount onBoundCount;
    private long mark;

    /**
     * Calls {@link #BoundedInputStream(InputStream, Long, OnBoundCount)} with <code>boundCount</code> set to
     * <code>null</code> and <code>onBoundCount</code> set to <code>null</code>.
     */
    public BoundedInputStream(final InputStream inputStream) {
        this(inputStream, null, null);
    }

    /**
     * Calls {@link #BoundedInputStream(InputStream, Long, OnBoundCount)} with <code>onBoundCount</code> set to
     * {@link OnBoundCount#THROW}.
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount) {
        this(inputStream, boundCount, OnBoundCount.THROW);
    }

    /**
     * Calls {@link #BoundedInputStream(InputStream, Long, long, OnBoundCount)} with <code>onBoundCount</code> set to
     * {@link OnBoundCount#THROW}.
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount, final long initialCount) {
        this(inputStream, boundCount, initialCount, OnBoundCount.THROW);
    }

    /**
     * Calls {@link #BoundedInputStream(InputStream, Long, long, OnBoundCount)} with <code>initialCount</code> set to
     * <code>0</code>.
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount,
            final @Nullable OnBoundCount onBoundCount) {
        this(inputStream, boundCount, 0, onBoundCount);
    }

    /**
     * Instantiates a new {@link BoundedInputStream}.
     *
     * @param inputStream  the {@link InputStream}
     * @param boundCount   the {@link #getBoundCount()} (a negative number is equivalent to <code>null</code>)
     * @param initialCount the initial count to set {@link #getCurrentCount()} to
     * @param onBoundCount the {@link OnBoundCount} or <code>null</code>
     */
    public BoundedInputStream(final InputStream inputStream, final @Nullable Long boundCount, final long initialCount,
            final @Nullable OnBoundCount onBoundCount) {
        super(inputStream);
        this.boundCount = boundCount != null && boundCount >= 0 ? boundCount : null;
        this.onBoundCount = onBoundCount;
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
            if (onBoundCount != null) {
                onBoundCount.onBoundCount(this);
            }
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
            if (onBoundCount != null) {
                onBoundCount.onBoundCount(this);
            }
            return -1;
        }
        final var count = super.read(b, off, (int) lengthToRead(len));
        if (count != -1) {
            currentCount += count;
        }
        return count;
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
     * {@link Long#MAX_VALUE} for no bound
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
