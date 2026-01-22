package net.jacobpeterson.jet.common.io.replacing;

import lombok.Generated;
import org.jspecify.annotations.NullMarked;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.copyOf;
import static java.util.Objects.checkFromIndexSize;

/**
 * {@link ReplacingInputStream} is a {@link FilterInputStream} for finding and replacing bytes as an
 * {@link InputStream} is read from. This class does not use an internal buffer, which helps performance and reduces
 * memory usage, but the find and replace logic is only implemented in the single-byte {@link #read()} method, meaning
 * the byte-array {@link #read(byte[], int, int)} method uses the single-byte {@link #read()}, method which may decrease
 * performance.
 */
@NullMarked
public class ReplacingInputStream extends FilterInputStream {

    /**
     * @return {@link #forStrings(InputStream, List)} with {@link ArrayList#ArrayList(Collection)}
     * {@link Map#entrySet()}
     */
    public static ReplacingInputStream forStrings(final InputStream inputStream,
            final Map<String, String> replacementsOfFinds) {
        return forStrings(inputStream, new ArrayList<>(replacementsOfFinds.entrySet()));
    }

    /**
     * Composes a {@link ReplacingInputStream} of each {@link Entry} in the given <code>replacementsOfFinds</code>.
     *
     * @param inputStream         the {@link InputStream}
     * @param replacementsOfFinds the {@link Entry} {@link List} of {@link String}s to find and their mapped
     *                            replacements
     *
     * @return the composed {@link ReplacingInputStream}
     */
    public static ReplacingInputStream forStrings(final InputStream inputStream,
            final List<Entry<String, String>> replacementsOfFinds) {
        return forStringsOrByteArrays(inputStream, replacementsOfFinds, false);
    }

    /**
     * @return {@link #forByteArrays(InputStream, List)} with {@link ArrayList#ArrayList(Collection)}
     * {@link Map#entrySet()}
     */
    public static ReplacingInputStream forByteArrays(final InputStream inputStream,
            final Map<byte[], byte[]> replacementsOfFinds) {
        return forByteArrays(inputStream, new ArrayList<>(replacementsOfFinds.entrySet()));
    }

    /**
     * Composes a {@link ReplacingInputStream} of each {@link Entry} in the given <code>replacementsOfFinds</code>.
     *
     * @param inputStream         the {@link InputStream}
     * @param replacementsOfFinds the {@link Entry} {@link List} of byte arrays to find and their mapped replacements
     *
     * @return the composed {@link ReplacingInputStream}
     */
    public static ReplacingInputStream forByteArrays(final InputStream inputStream,
            final List<Entry<byte[], byte[]>> replacementsOfFinds) {
        return forStringsOrByteArrays(inputStream, replacementsOfFinds, true);
    }

    private static ReplacingInputStream forStringsOrByteArrays(final InputStream inputStream,
            final List<? extends Entry<?, ?>> replacementsOfFinds, final boolean strings) {
        if (replacementsOfFinds.isEmpty()) {
            return new ReplacingInputStream(inputStream, new byte[]{}, new byte[]{});
        }
        var composition = inputStream;
        for (final var entry : replacementsOfFinds) {
            composition = strings ?
                    new ReplacingInputStream(composition, (String) entry.getKey(), (String) entry.getValue()) :
                    new ReplacingInputStream(composition, (byte[]) entry.getKey(), (byte[]) entry.getValue());
        }
        return (ReplacingInputStream) composition;
    }

    private final byte[] find;
    private final int find0;
    private final byte[] replace;
    private final int replace0;
    private int replaceIndex;
    private int findFalseIndex;
    private int findFalseLastIndex;
    private int findFalseLastRead;
    private boolean findFalseLastReadEqualsFind0;

    /**
     * Calls {@link #ReplacingInputStream(InputStream, byte[], byte[])} with {@link String#getBytes()}
     * {@link StandardCharsets#UTF_8}.
     */
    public ReplacingInputStream(final InputStream inputStream, final String find, final String replace) {
        this(inputStream, find.getBytes(UTF_8), replace.getBytes(UTF_8));
    }

    /**
     * Instantiates a new {@link ReplacingInputStream}.
     *
     * @param inputStream the {@link InputStream}
     * @param find        the bytes to find
     * @param replace     the replacement bytes
     */
    public ReplacingInputStream(final InputStream inputStream, final byte[] find, final byte[] replace) {
        super(inputStream);
        this.find = copyOf(find, find.length);
        find0 = this.find.length == 0 ? -1 : Byte.toUnsignedInt(find[0]);
        this.replace = copyOf(replace, replace.length);
        replace0 = this.replace.length == 0 ? -1 : Byte.toUnsignedInt(replace[0]);
        replaceIndex = -1;
        findFalseIndex = -1;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public int read() throws IOException {
        if (find.length == 0) {
            return super.read();
        }
        if (replaceIndex != -1) {
            final var replacement = replace[replaceIndex++];
            if (replaceIndex == replace.length) {
                replaceIndex = -1;
            }
            return Byte.toUnsignedInt(replacement);
        }
        if (find.length == 1) {
            final var read = super.read();
            if (read == find0) {
                if (replace.length == 0) {
                    int removeRead;
                    while ((removeRead = super.read()) == find0) {}
                    return removeRead;
                }
                if (replace.length > 1) {
                    replaceIndex = 1;
                }
                return replace0;
            }
            return read;
        }
        if (findFalseIndex != -1) {
            if (findFalseLastReadEqualsFind0) {
                final var findFalseIndexValue = Byte.toUnsignedInt(find[findFalseIndex++]);
                if (findFalseIndex == findFalseLastIndex - 1) {
                    findFalseIndex = -1;
                }
                return findFalseIndexValue;
            } else {
                if (findFalseIndex < findFalseLastIndex) {
                    return Byte.toUnsignedInt(find[findFalseIndex++]);
                }
                findFalseIndex = -1;
                return findFalseLastRead;
            }
        }
        if (!find(findFalseLastReadEqualsFind0 ? 1 : 0)) {
            if (findFalseLastReadEqualsFind0) {
                if (findFalseLastIndex > 1) {
                    findFalseIndex = 1;
                }
                return findFalseLastIndex == 0 ? findFalseLastRead : find0;
            } else {
                if (findFalseLastIndex > 0) {
                    findFalseIndex = 1;
                }
                return findFalseLastIndex == 0 ? findFalseLastRead : find0;
            }
        }
        if (replace.length == 0) {
            while (find(0)) {}
            if (findFalseLastReadEqualsFind0) {
                if (findFalseLastIndex > 1) {
                    findFalseIndex = 1;
                }
                return findFalseLastIndex == 0 ? findFalseLastRead : find0;
            } else {
                if (findFalseLastIndex > 0) {
                    findFalseIndex = 1;
                }
                return findFalseLastIndex == 0 ? findFalseLastRead : find0;
            }
        }
        if (replace.length > 1) {
            replaceIndex = 1;
        }
        return replace0;
    }

    private boolean find(final int startIndex) throws IOException {
        for (var index = startIndex; index < find.length; index++) {
            final var read = super.read();
            if (read != Byte.toUnsignedInt(find[index])) {
                findFalseLastIndex = index;
                findFalseLastRead = read;
                findFalseLastReadEqualsFind0 = read == find0;
                return false;
            }
        }
        return true;
    }

    // This method is copied directly from `InputStream.read(byte[], int, int)` because the `read()` method of this
    // class must be used instead of the `read()` method of the underlying `InputStream`s class.
    @SuppressWarnings("all")
    @Generated
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkFromIndexSize(off, len, b.length);
        if (len == 0) {
            return 0;
        }
        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;
        int i = 1;
        try {
            for (; i < len; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte) c;
            }
        } catch (IOException ee) {}
        return i;
    }

    @Override
    public long skip(final long n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mark(final int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }
}
