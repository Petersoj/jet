package net.jacobpeterson.jet.common.io.replacing;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class ReplacingInputStreamTest {

    @Test
    public void forStrings() throws IOException {
        assertEquals("", replacedStrings(""));
        assertEquals("ab", replacedStrings("ab"));
        assertEquals("b", replacedStrings("ab", entry("a", "")));
        assertEquals("", replacedStrings("ab", entry("a", ""), entry("b", "")));
        assertEquals("", replacedStrings("aabb", entry("a", ""), entry("b", "")));
        assertEquals("a", replacedStrings("aabb", entry("ab", ""), entry("b", "")));
        assertEquals("b", replacedStrings("abba", entry("ab", "b"), entry("ba", "")));
        assertEquals("zzzzz", replacedStrings("zazazbzbz", entry("a", ""), entry("b", "")));
        assertEquals("zzz", replacedStrings("zzz", entry("a", ""), entry("b", "")));
    }

    @SafeVarargs
    private String replacedStrings(final String string, final Entry<String, String>... replacementsOfFinds)
            throws IOException {
        final var replacementsOfFindsMap = new LinkedHashMap<String, String>();
        for (final var entry : replacementsOfFinds) {
            replacementsOfFindsMap.put(entry.getKey(), entry.getValue());
        }
        return new String(ReplacingInputStream.forStrings(new ByteArrayInputStream(string.getBytes(UTF_8)),
                replacementsOfFindsMap).readAllBytes(), UTF_8);
    }

    @Test
    public void forByteArrays() throws IOException {
        assertArrayEquals(bytes(), replacedBytesArrays(bytes()));
        assertArrayEquals(bytes(0, 1), replacedBytesArrays(bytes(0, 1)));
        assertArrayEquals(bytes(1), replacedBytesArrays(bytes(0, 1), entry(bytes(0), bytes())));
        assertArrayEquals(bytes(1, 1), replacedBytesArrays(bytes(0, 1), entry(bytes(0), bytes(1))));
        assertArrayEquals(bytes(1, 1, 1), replacedBytesArrays(bytes(0, 1), entry(bytes(0), bytes(1, 1))));
        assertArrayEquals(bytes(1, 0, 1, 1), replacedBytesArrays(bytes(0, 1), entry(bytes(0), bytes(1, 0, 1))));
        assertArrayEquals(bytes(1, 1, 0, 1, 1, 1), replacedBytesArrays(bytes(0, 1),
                entry(bytes(0), bytes(1, 0, 1)), entry(bytes(0), bytes(1, 0, 1))));
    }

    @SuppressWarnings("ArrayAsKeyOfSetOrMap")
    @SafeVarargs
    private byte[] replacedBytesArrays(final byte[] bytes, final Entry<byte[], byte[]>... replacementsOfFinds)
            throws IOException {
        final var replacementsOfFindsMap = new LinkedHashMap<byte[], byte[]>();
        for (final var entry : replacementsOfFinds) {
            replacementsOfFindsMap.put(entry.getKey(), entry.getValue());
        }
        return ReplacingInputStream.forByteArrays(new ByteArrayInputStream(bytes),
                replacementsOfFindsMap).readAllBytes();
    }

    @Test
    public void read() throws Exception {
        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(), bytes()));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(), bytes()));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(), bytes()));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(), bytes()));
        assertArrayEquals(bytes(0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(), bytes()));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(), bytes()));
        assertArrayEquals(bytes(-1, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(), bytes()));
        assertArrayEquals(bytes(0, 0, 0), replacedBytes(bytes(0, 0, 0), bytes(), bytes()));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0), bytes()));
        assertArrayEquals(bytes(), replacedBytes(bytes(0), bytes(0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0), bytes()));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0), bytes()));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(0, 0, -1), bytes(0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(0, -1, 0), bytes(0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1, 0, 0), bytes(0), bytes()));
        assertArrayEquals(bytes(), replacedBytes(bytes(0, 0, 0), bytes(0), bytes()));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0), bytes(0)));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0), bytes(0)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0), bytes(0)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0), bytes(0)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0), bytes(0)));
        assertArrayEquals(bytes(0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(0), bytes(0)));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0), bytes(0)));
        assertArrayEquals(bytes(-1, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(0), bytes(0)));
        assertArrayEquals(bytes(0, 0, 0), replacedBytes(bytes(0, 0, 0), bytes(0), bytes(0)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(0, 0), replacedBytes(bytes(0), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(0, 0, 0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(0, 0, -1, 0, 0), replacedBytes(bytes(0, -1, 0), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(-1, 0, 0, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(0), bytes(0, 0)));
        assertArrayEquals(bytes(0, 0, 0, 0, 0, 0), replacedBytes(bytes(0, 0, 0), bytes(0), bytes(0, 0)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(0, 0, 0), replacedBytes(bytes(0), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(0, 0, 0, 0, 0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(0, 0, 0, -1, 0, 0, 0), replacedBytes(bytes(0, -1, 0), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(-1, 0, 0, 0, 0, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(0), bytes(0, 0, 0)));
        assertArrayEquals(bytes(0, 0, 0, 0, 0, 0, 0, 0, 0), replacedBytes(bytes(0, 0, 0), bytes(0), bytes(0, 0, 0)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(0), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(0, 0, -1), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(0, -1, 0), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, 0, 0), bytes(0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(0, 0, 0), bytes(0), bytes(-1)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(0, 0, -1), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1, 0, 0), bytes(0, 0), bytes()));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0, 0, 0), bytes(0, 0), bytes()));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(0, -1), replacedBytes(bytes(0, 0, -1), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(-1, 0), replacedBytes(bytes(-1, 0, 0), bytes(0, 0), bytes(0)));
        assertArrayEquals(bytes(0, 0), replacedBytes(bytes(0, 0, 0), bytes(0, 0), bytes(0)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(-1, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(0, 0), bytes(0, 0)));
        assertArrayEquals(bytes(0, 0, 0), replacedBytes(bytes(0, 0, 0), bytes(0, 0), bytes(0, 0)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(0, 0, -1), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, 0, 0), bytes(0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, 0), replacedBytes(bytes(0, 0, 0), bytes(0, 0), bytes(-1)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(-1, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(0, 0, 0), bytes()));
        assertArrayEquals(bytes(), replacedBytes(bytes(0, 0, 0), bytes(0, 0, 0), bytes()));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(-1, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(0, 0, 0), bytes(0)));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0, 0, 0), bytes(0, 0, 0), bytes(0)));

        assertArrayEquals(bytes(), replacedBytes(bytes(), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(0), replacedBytes(bytes(0), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(-1), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1), replacedBytes(bytes(-1, -1), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, -1, -1), replacedBytes(bytes(-1, -1, -1), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(0, 0, -1), replacedBytes(bytes(0, 0, -1), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(0, -1, 0), replacedBytes(bytes(0, -1, 0), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1, 0, 0), replacedBytes(bytes(-1, 0, 0), bytes(0, 0, 0), bytes(-1)));
        assertArrayEquals(bytes(-1), replacedBytes(bytes(0, 0, 0), bytes(0, 0, 0), bytes(-1)));

        assertEquals("", replacedString("", "", ""));
        assertEquals("a", replacedString("a", "", ""));
        assertEquals("aa", replacedString("aa", "", ""));
        assertEquals("aaa", replacedString("aaa", "", ""));
        assertEquals("ab", replacedString("ab", "", ""));
        assertEquals("aba", replacedString("aba", "", ""));
        assertEquals("abab", replacedString("abab", "", ""));
        assertEquals("ababa", replacedString("ababa", "", ""));
        assertEquals("aabaa", replacedString("aabaa", "", ""));
        assertEquals("aabaabbaa", replacedString("aabaabbaa", "", ""));
        assertEquals("👍🙂👍", replacedString("👍🙂👍", "", ""));
        assertEquals("👍👍🙂👍👍", replacedString("👍👍🙂👍👍", "", ""));

        assertEquals("", replacedString("", "a", ""));
        assertEquals("", replacedString("a", "a", ""));
        assertEquals("", replacedString("aa", "a", ""));
        assertEquals("", replacedString("aaa", "a", ""));
        assertEquals("b", replacedString("ab", "a", ""));
        assertEquals("b", replacedString("aba", "a", ""));
        assertEquals("bb", replacedString("abab", "a", ""));
        assertEquals("bb", replacedString("ababa", "a", ""));
        assertEquals("b", replacedString("aabaa", "a", ""));
        assertEquals("bbb", replacedString("aabaabbaa", "a", ""));
        assertEquals("👍🙂👍", replacedString("👍🙂👍", "a", ""));
        assertEquals("👍👍🙂👍👍", replacedString("👍👍🙂👍👍", "a", ""));

        assertEquals("", replacedString("", "a", "baba"));
        assertEquals("baba", replacedString("a", "a", "baba"));
        assertEquals("babababa", replacedString("aa", "a", "baba"));
        assertEquals("babababababa", replacedString("aaa", "a", "baba"));
        assertEquals("babab", replacedString("ab", "a", "baba"));
        assertEquals("bababbaba", replacedString("aba", "a", "baba"));
        assertEquals("bababbabab", replacedString("abab", "a", "baba"));
        assertEquals("bababbababbaba", replacedString("ababa", "a", "baba"));
        assertEquals("bababababbabababa", replacedString("aabaa", "a", "baba"));
        assertEquals("bababababbababababbbabababa", replacedString("aabaabbaa", "a", "baba"));
        assertEquals("👍🙂👍", replacedString("👍🙂👍", "a", "baba"));
        assertEquals("👍👍🙂👍👍", replacedString("👍👍🙂👍👍", "a", "baba"));

        assertEquals("", replacedString("", "a", "👍"));
        assertEquals("👍", replacedString("a", "a", "👍"));
        assertEquals("👍👍", replacedString("aa", "a", "👍"));
        assertEquals("👍👍👍", replacedString("aaa", "a", "👍"));
        assertEquals("👍b", replacedString("ab", "a", "👍"));
        assertEquals("👍b👍", replacedString("aba", "a", "👍"));
        assertEquals("👍b👍b", replacedString("abab", "a", "👍"));
        assertEquals("👍b👍b👍", replacedString("ababa", "a", "👍"));
        assertEquals("👍👍b👍👍", replacedString("aabaa", "a", "👍"));
        assertEquals("👍👍b👍👍bb👍👍", replacedString("aabaabbaa", "a", "👍"));
        assertEquals("👍🙂👍", replacedString("👍🙂👍", "a", "👍"));
        assertEquals("👍👍🙂👍👍", replacedString("👍👍🙂👍👍", "a", "👍"));

        assertEquals("", replacedString("", "👍", ""));
        assertEquals("a", replacedString("a", "👍", ""));
        assertEquals("aa", replacedString("aa", "👍", ""));
        assertEquals("aaa", replacedString("aaa", "👍", ""));
        assertEquals("ab", replacedString("ab", "👍", ""));
        assertEquals("aba", replacedString("aba", "👍", ""));
        assertEquals("abab", replacedString("abab", "👍", ""));
        assertEquals("ababa", replacedString("ababa", "👍", ""));
        assertEquals("aabaa", replacedString("aabaa", "👍", ""));
        assertEquals("aabaabbaa", replacedString("aabaabbaa", "👍", ""));
        assertEquals("🙂", replacedString("👍🙂👍", "👍", ""));
        assertEquals("🙂", replacedString("👍👍🙂👍👍", "👍", ""));

        assertEquals("", replacedString("", "👍", "🙂"));
        assertEquals("a", replacedString("a", "👍", "🙂"));
        assertEquals("aa", replacedString("aa", "👍", "🙂"));
        assertEquals("aaa", replacedString("aaa", "👍", "🙂"));
        assertEquals("ab", replacedString("ab", "👍", "🙂"));
        assertEquals("aba", replacedString("aba", "👍", "🙂"));
        assertEquals("abab", replacedString("abab", "👍", "🙂"));
        assertEquals("ababa", replacedString("ababa", "👍", "🙂"));
        assertEquals("aabaa", replacedString("aabaa", "👍", "🙂"));
        assertEquals("aabaabbaa", replacedString("aabaabbaa", "👍", "🙂"));
        assertEquals("🙂🙂🙂", replacedString("👍🙂👍", "👍", "🙂"));
        assertEquals("🙂🙂🙂🙂🙂", replacedString("👍👍🙂👍👍", "👍", "🙂"));

        assertEquals("", replacedString("", "👍🙂", ""));
        assertEquals("a", replacedString("a", "👍🙂", ""));
        assertEquals("aa", replacedString("aa", "👍🙂", ""));
        assertEquals("aaa", replacedString("aaa", "👍🙂", ""));
        assertEquals("ab", replacedString("ab", "👍🙂", ""));
        assertEquals("aba", replacedString("aba", "👍🙂", ""));
        assertEquals("abab", replacedString("abab", "👍🙂", ""));
        assertEquals("ababa", replacedString("ababa", "👍🙂", ""));
        assertEquals("aabaa", replacedString("aabaa", "👍🙂", ""));
        assertEquals("aabaabbaa", replacedString("aabaabbaa", "👍🙂", ""));
        assertEquals("👍", replacedString("👍🙂👍", "👍🙂", ""));
        assertEquals("👍👍👍", replacedString("👍👍🙂👍👍", "👍🙂", ""));

        assertEquals("", replacedString("", "👍🙂", "🙂"));
        assertEquals("a", replacedString("a", "👍🙂", "🙂"));
        assertEquals("aa", replacedString("aa", "👍🙂", "🙂"));
        assertEquals("aaa", replacedString("aaa", "👍🙂", "🙂"));
        assertEquals("ab", replacedString("ab", "👍🙂", "🙂"));
        assertEquals("aba", replacedString("aba", "👍🙂", "🙂"));
        assertEquals("abab", replacedString("abab", "👍🙂", "🙂"));
        assertEquals("ababa", replacedString("ababa", "👍🙂", "🙂"));
        assertEquals("aabaa", replacedString("aabaa", "👍🙂", "🙂"));
        assertEquals("aabaabbaa", replacedString("aabaabbaa", "👍🙂", "🙂"));
        assertEquals("🙂👍", replacedString("👍🙂👍", "👍🙂", "🙂"));
        assertEquals("👍🙂👍👍", replacedString("👍👍🙂👍👍", "👍🙂", "🙂"));

        assertEquals("", replacedString("", "👍🙂", "🙂👍"));
        assertEquals("a", replacedString("a", "👍🙂", "🙂👍"));
        assertEquals("aa", replacedString("aa", "👍🙂", "🙂👍"));
        assertEquals("aaa", replacedString("aaa", "👍🙂", "🙂👍"));
        assertEquals("ab", replacedString("ab", "👍🙂", "🙂👍"));
        assertEquals("aba", replacedString("aba", "👍🙂", "🙂👍"));
        assertEquals("abab", replacedString("abab", "👍🙂", "🙂👍"));
        assertEquals("ababa", replacedString("ababa", "👍🙂", "🙂👍"));
        assertEquals("aabaa", replacedString("aabaa", "👍🙂", "🙂👍"));
        assertEquals("aabaabbaa", replacedString("aabaabbaa", "👍🙂", "🙂👍"));
        assertEquals("🙂👍👍", replacedString("👍🙂👍", "👍🙂", "🙂👍"));
        assertEquals("👍🙂👍👍👍", replacedString("👍👍🙂👍👍", "👍🙂", "🙂👍"));

        assertEquals("", replacedString("", "🙂👍", "👍🙂"));
        assertEquals("a", replacedString("a", "🙂👍", "👍🙂"));
        assertEquals("aa", replacedString("aa", "🙂👍", "👍🙂"));
        assertEquals("aaa", replacedString("aaa", "🙂👍", "👍🙂"));
        assertEquals("ab", replacedString("ab", "🙂👍", "👍🙂"));
        assertEquals("aba", replacedString("aba", "🙂👍", "👍🙂"));
        assertEquals("abab", replacedString("abab", "🙂👍", "👍🙂"));
        assertEquals("ababa", replacedString("ababa", "🙂👍", "👍🙂"));
        assertEquals("aabaa", replacedString("aabaa", "🙂👍", "👍🙂"));
        assertEquals("aabaabbaa", replacedString("aabaabbaa", "🙂👍", "👍🙂"));
        assertEquals("👍👍🙂", replacedString("👍🙂👍", "🙂👍", "👍🙂"));
        assertEquals("👍👍👍🙂👍", replacedString("👍👍🙂👍👍", "🙂👍", "👍🙂"));
    }

    private byte[] replacedBytes(final byte[] bytes, final byte[] find, final byte[] replace) throws IOException {
        return new ReplacingInputStream(new ByteArrayInputStream(bytes), find, replace).readAllBytes();
    }

    private String replacedString(final String string, final String find, final String replace) throws IOException {
        return new String(new ReplacingInputStream(new ByteArrayInputStream(string.getBytes(UTF_8)), find, replace)
                .readAllBytes(), UTF_8);
    }

    private byte[] bytes(final int... bytes) {
        final var byteArray = new byte[bytes.length];
        for (var index = 0; index < bytes.length; index++) {
            byteArray[index] = (byte) bytes[index];
        }
        return byteArray;
    }
}
