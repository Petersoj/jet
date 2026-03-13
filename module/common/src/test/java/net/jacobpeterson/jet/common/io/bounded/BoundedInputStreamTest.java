package net.jacobpeterson.jet.common.io.bounded;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class BoundedInputStreamTest {

    @Test
    public void read() throws IOException {
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L);
            assertEquals('a', boundedInputStream.read());
            assertEquals('b', boundedInputStream.read());
            assertEquals(-1, boundedInputStream.read());
            assertThrows(BoundException.class, boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abc"), null);
            assertEquals('a', boundedInputStream.read());
            assertEquals('b', boundedInputStream.read());
            assertEquals('c', boundedInputStream.read());
            assertEquals(-1, boundedInputStream.read());
            assertDoesNotThrow(boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abc"), -1L);
            assertEquals('a', boundedInputStream.read());
            assertEquals('b', boundedInputStream.read());
            assertEquals('c', boundedInputStream.read());
            assertEquals(-1, boundedInputStream.read());
            assertDoesNotThrow(boundedInputStream::close);
        }
        {
            final var inputStream = stringStream("abcdefg");
            assertEquals('a', inputStream.read());
            final var boundedInputStream = new BoundedInputStream(inputStream, 2L, 1L);
            assertEquals('b', boundedInputStream.read());
            assertEquals(-1, boundedInputStream.read());
            assertThrows(BoundException.class, boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L, false);
            assertEquals('a', boundedInputStream.read());
            assertEquals('b', boundedInputStream.read());
            assertEquals(-1, boundedInputStream.read());
            assertDoesNotThrow(boundedInputStream::close);
        }
        {
            final var inputStream = stringStream("abcdefg");
            assertEquals('a', inputStream.read());
            final var boundedInputStream = new BoundedInputStream(inputStream, 2L, 1L, false);
            assertEquals('b', boundedInputStream.read());
            assertEquals(-1, boundedInputStream.read());
            assertDoesNotThrow(boundedInputStream::close);
        }
    }

    @Test
    public void readByteArray() throws IOException {
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L);
            final var bytes = new byte[3];
            assertEquals(2, boundedInputStream.read(bytes));
            assertArrayEquals(new byte[]{'a', 'b', 0}, bytes);
            assertThrows(BoundException.class, boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abc"), null);
            final var bytes = new byte[3];
            assertEquals(3, boundedInputStream.read(bytes));
            assertArrayEquals(new byte[]{'a', 'b', 'c'}, bytes);
            assertDoesNotThrow(boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abc"), -1L);
            final var bytes = new byte[3];
            assertEquals(3, boundedInputStream.read(bytes));
            assertArrayEquals(new byte[]{'a', 'b', 'c'}, bytes);
            assertDoesNotThrow(boundedInputStream::close);
        }
        {
            final var inputStream = stringStream("abcdefg");
            assertEquals('a', inputStream.read());
            final var boundedInputStream = new BoundedInputStream(inputStream, 2L, 1L);
            final var bytes = new byte[2];
            assertEquals(1, boundedInputStream.read(bytes));
            assertArrayEquals(new byte[]{'b', 0}, bytes);
            assertThrows(BoundException.class, boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L, false);
            final var bytes = new byte[3];
            assertEquals(2, boundedInputStream.read(bytes));
            assertArrayEquals(new byte[]{'a', 'b', 0}, bytes);
            assertDoesNotThrow(boundedInputStream::close);
        }
        {
            final var inputStream = stringStream("abcdefg");
            assertEquals('a', inputStream.read());
            final var boundedInputStream = new BoundedInputStream(inputStream, 2L, 1L, false);
            final var bytes = new byte[2];
            assertEquals(1, boundedInputStream.read(bytes));
            assertArrayEquals(new byte[]{'b', 0}, bytes);
            assertDoesNotThrow(boundedInputStream::close);
        }
    }

    @Test
    public void skip() throws IOException {
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 3L);
            assertEquals('a', boundedInputStream.read());
            assertEquals(1, boundedInputStream.skip(1));
            assertEquals('c', boundedInputStream.read());
            assertEquals(-1, boundedInputStream.read());
            assertThrows(BoundException.class, boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L);
            assertEquals(2, boundedInputStream.skip(10));
            assertEquals(-1, boundedInputStream.read());
            assertThrows(BoundException.class, boundedInputStream::close);
        }
    }

    @Test
    public void availableBeforeBound() throws IOException {
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L);
            assertEquals(2, boundedInputStream.availableBeforeBound());
            assertEquals('a', boundedInputStream.read());
            assertEquals(1, boundedInputStream.availableBeforeBound());
            assertEquals('b', boundedInputStream.read());
            assertEquals(0, boundedInputStream.availableBeforeBound());
            assertEquals(-1, boundedInputStream.read());
            assertThrows(BoundException.class, boundedInputStream::close);
        }
        {
            final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), null);
            assertEquals(Long.MAX_VALUE, boundedInputStream.availableBeforeBound());
            assertDoesNotThrow(boundedInputStream::close);
        }
    }

    @Test
    public void available() throws IOException {
        final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L);
        assertTrue(boundedInputStream.available() > 0);
        assertEquals('a', boundedInputStream.read());
        assertTrue(boundedInputStream.available() > 0);
        assertEquals('b', boundedInputStream.read());
        assertEquals(0, boundedInputStream.available());
        assertEquals(-1, boundedInputStream.read());
        assertThrows(BoundException.class, boundedInputStream::close);
    }

    @Test
    public void markAndReset() throws IOException {
        final var boundedInputStream = new BoundedInputStream(stringStream("abcdefg"), 2L);
        assertTrue(boundedInputStream.markSupported());
        assertEquals('a', boundedInputStream.read());
        boundedInputStream.mark(Integer.MAX_VALUE);
        assertFalse(boundedInputStream.isBound());
        assertEquals('b', boundedInputStream.read());
        boundedInputStream.reset();
        assertFalse(boundedInputStream.isBound());
        assertEquals('b', boundedInputStream.read());
        assertTrue(boundedInputStream.isBound());
        assertEquals(-1, boundedInputStream.read());
        assertThrows(BoundException.class, boundedInputStream::close);
    }

    private ByteArrayInputStream stringStream(final String string) {
        return new ByteArrayInputStream(string.getBytes(UTF_8));
    }
}
