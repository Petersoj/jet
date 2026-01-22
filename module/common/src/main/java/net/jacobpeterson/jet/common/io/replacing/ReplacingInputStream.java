package net.jacobpeterson.jet.common.io.replacing;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link ReplacingInputStream} is an {@link InputStream} for finding and replacing bytes in an {@link InputStream} as
 * it is read from.
 */
@NullMarked
@SuppressWarnings("InputStreamSlowMultibyteRead")
public class ReplacingInputStream extends InputStream {

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
        return forStringsOrByteArrays(inputStream, replacementsOfFinds, true);
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
        return forStringsOrByteArrays(inputStream, replacementsOfFinds, false);
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

    private final InputStream inputStream;
    private final byte[] find;
    private final int find0;
    private final byte[] replace;
    private final int replace0;
    private final @Nullable Deque<Integer> buffer;
    private int replaceIndex;

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
        this.inputStream = inputStream;
        this.find = Arrays.copyOf(find, find.length);
        find0 = this.find.length == 0 ? -1 : Byte.toUnsignedInt(find[0]);
        this.replace = Arrays.copyOf(replace, replace.length);
        replace0 = this.replace.length == 0 ? -1 : Byte.toUnsignedInt(replace[0]);
        buffer = this.find.length > 1 ? new ArrayDeque<>(this.find.length) : null;
        replaceIndex = -1;
    }

    @SuppressWarnings({"NullAway", "StatementWithEmptyBody", "DataFlowIssue"})
    @Override
    public int read() throws IOException {
        if (find.length == 0) {
            return inputStream.read();
        }
        if (replaceIndex != -1) {
            final var replacement = Byte.toUnsignedInt(replace[replaceIndex++]);
            if (replaceIndex == replace.length) {
                replaceIndex = -1;
            }
            return replacement;
        }
        if (find.length == 1) {
            final var read = inputStream.read();
            if (read == find0) {
                if (replace.length == 0) {
                    int removeRead;
                    while ((removeRead = inputStream.read()) == find0) {}
                    return removeRead;
                }
                if (replace.length > 1) {
                    replaceIndex = 1;
                }
                return replace0;
            }
            return read;
        }
        if (!find()) {
            return eofOrRemoveFirst();
        }
        buffer.clear();
        if (replace.length == 0) {
            while (find()) { buffer.clear(); }
            return eofOrRemoveFirst();
        }
        if (replace.length > 1) {
            replaceIndex = 1;
        }
        return replace0;
    }

    @SuppressWarnings({"NullAway", "DataFlowIssue"})
    private boolean find() throws IOException {
        int read;
        while (buffer.size() < find.length && (read = inputStream.read()) != -1) {
            buffer.add(read);
        }
        if (buffer.size() != find.length) {
            return false;
        }
        if (buffer.getFirst() != find0) {
            return false;
        }
        var index = 0;
        for (final var bufferRead : buffer) { // TODO https://bugs.openjdk.org/browse/JDK-8356821
            if (index == 0) {
                index = 1;
                continue;
            }
            if (bufferRead != Byte.toUnsignedInt(find[index++])) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"NullAway", "DataFlowIssue"})
    private int eofOrRemoveFirst() {
        return buffer.isEmpty() ? -1 : buffer.removeFirst();
    }
}
