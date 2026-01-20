package net.jacobpeterson.jet.common.io.bounded;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class BoundedInputStreamTest {

    @Test
    public void read() {
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L)) {
                assertEquals('a', boundedInputStream.read());
                assertEquals('b', boundedInputStream.read());
                assertThrows(BoundException.class, boundedInputStream::read);
            }
        });
        assertDoesNotThrow(() -> {
            final var inputStream = new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8));
            inputStream.read();
            try (final var boundedInputStream = new BoundedInputStream(inputStream, 2L, 1L)) {
                assertEquals('b', boundedInputStream.read());
                assertThrows(BoundException.class, boundedInputStream::read);
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L, OnBoundCount.CLOSE)) {
                assertEquals('a', boundedInputStream.read());
                assertEquals('b', boundedInputStream.read());
                assertEquals(-1, boundedInputStream.read());
            }
        });
        assertDoesNotThrow(() -> {
            final var inputStream = new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8));
            inputStream.read();
            try (final var boundedInputStream = new BoundedInputStream(inputStream, 2L, 1L, OnBoundCount.CLOSE)) {
                assertEquals('b', boundedInputStream.read());
                assertEquals(-1, boundedInputStream.read());
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abc".getBytes(UTF_8)), null)) {
                assertEquals('a', boundedInputStream.read());
                assertEquals('b', boundedInputStream.read());
                assertEquals('c', boundedInputStream.read());
                assertEquals(-1, boundedInputStream.read());
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abc".getBytes(UTF_8)), -1L)) {
                assertEquals('a', boundedInputStream.read());
                assertEquals('b', boundedInputStream.read());
                assertEquals('c', boundedInputStream.read());
                assertEquals(-1, boundedInputStream.read());
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L, null)) {
                assertEquals('a', boundedInputStream.read());
                assertEquals('b', boundedInputStream.read());
                assertEquals(-1, boundedInputStream.read());
            }
        });
    }

    @Test
    public void readByteArray() {
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L)) {
                final var bytes = new byte[2];
                assertEquals(bytes.length, boundedInputStream.read(bytes));
                assertArrayEquals(new byte[]{'a', 'b'}, bytes);

                assertThrows(BoundException.class, () -> boundedInputStream.read(bytes));
            }
        });
        assertDoesNotThrow(() -> {
            final var inputStream = new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8));
            inputStream.read();
            try (final var boundedInputStream = new BoundedInputStream(inputStream, 3L, 1L)) {
                final var bytes = new byte[2];
                assertEquals(bytes.length, boundedInputStream.read(bytes));
                assertArrayEquals(new byte[]{'b', 'c'}, bytes);

                assertThrows(BoundException.class, () -> boundedInputStream.read(bytes));
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L, OnBoundCount.CLOSE)) {
                final var bytes = new byte[2];
                assertEquals(bytes.length, boundedInputStream.read(bytes));
                assertArrayEquals(new byte[]{'a', 'b'}, bytes);

                assertEquals(-1, boundedInputStream.read(bytes));
            }
        });
        assertDoesNotThrow(() -> {
            final var inputStream = new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8));
            inputStream.read();
            try (final var boundedInputStream = new BoundedInputStream(inputStream, 3L, 1L, OnBoundCount.CLOSE)) {
                final var bytes = new byte[2];
                assertEquals(bytes.length, boundedInputStream.read(bytes));
                assertArrayEquals(new byte[]{'b', 'c'}, bytes);

                assertEquals(-1, boundedInputStream.read(bytes));
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abc".getBytes(UTF_8)), null)) {
                final var bytes = new byte[3];
                assertEquals(bytes.length, boundedInputStream.read(bytes));
                assertArrayEquals(new byte[]{'a', 'b', 'c'}, bytes);
                assertEquals(-1, boundedInputStream.read(bytes));
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L, null)) {
                final var bytes = new byte[2];
                assertEquals(bytes.length, boundedInputStream.read(bytes));
                assertArrayEquals(new byte[]{'a', 'b'}, bytes);
                assertEquals(-1, boundedInputStream.read(bytes));
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L, null)) {
                assertArrayEquals(new byte[]{'a', 'b'}, boundedInputStream.readAllBytes());
                assertArrayEquals(new byte[]{}, boundedInputStream.readAllBytes());
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)))) {
                assertArrayEquals("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8), boundedInputStream.readAllBytes());
            }
        });
    }

    @Test
    public void skip() {
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 3L)) {
                assertEquals('a', boundedInputStream.read());
                assertEquals(1, boundedInputStream.skip(1));
                assertEquals('c', boundedInputStream.read());
                assertThrows(BoundException.class, boundedInputStream::read);
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L)) {
                assertEquals(2, boundedInputStream.skip(10));
                assertThrows(BoundException.class, boundedInputStream::read);
            }
        });
    }

    @Test
    public void availableBeforeBound() {
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L)) {
                assertEquals(2, boundedInputStream.availableBeforeBound());
                assertEquals('a', boundedInputStream.read());
                assertEquals(1, boundedInputStream.availableBeforeBound());
                assertEquals('b', boundedInputStream.read());
                assertEquals(0, boundedInputStream.availableBeforeBound());
            }
        });
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)))) {
                assertEquals(Long.MAX_VALUE, boundedInputStream.availableBeforeBound());
            }
        });
    }

    @Test
    public void available() {
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L)) {
                assertTrue(boundedInputStream.available() > 0);
                assertEquals('a', boundedInputStream.read());
                assertTrue(boundedInputStream.available() > 0);
                assertEquals('b', boundedInputStream.read());
                assertEquals(0, boundedInputStream.available());
            }
        });
    }

    @Test
    public void markAndReset() {
        assertDoesNotThrow(() -> {
            try (final var boundedInputStream = new BoundedInputStream(
                    new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes(UTF_8)), 2L)) {
                assertTrue(boundedInputStream.markSupported());
                assertEquals('a', boundedInputStream.read());
                boundedInputStream.mark(Integer.MAX_VALUE);
                assertFalse(boundedInputStream.isBound());
                assertEquals('b', boundedInputStream.read());
                boundedInputStream.reset();
                assertFalse(boundedInputStream.isBound());
                assertEquals('b', boundedInputStream.read());
                assertTrue(boundedInputStream.isBound());
                assertThrows(BoundException.class, boundedInputStream::read);
            }
        });
    }
}
