package net.jacobpeterson.jet.common.util.io;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class IoUtilTest {

    @Test
    public void outputStreamToInputStream() {
        outputStreamToInputStreamSuccess(new byte[]{});
        outputStreamToInputStreamSuccess(new byte[1024 * 1024]);
        {
            final var testBytes = new byte[1024 * 1024];
            ThreadLocalRandom.current().nextBytes(testBytes);
            outputStreamToInputStreamSuccess(testBytes);
        }
        assertThrows(Exception.class, () -> IoUtil.outputStreamToInputStream(
                _ -> {}, _ -> { throw new IOException(); }));
        assertThrows(Exception.class, () -> IoUtil.outputStreamToInputStream(
                outputStream -> outputStream.write(0), _ -> { throw new IOException(); }));
        assertThrows(Exception.class, () -> IoUtil.outputStreamToInputStream(
                outputStream -> outputStream.write(new byte[1024 * 1024]), _ -> { throw new IOException(); }));
        assertThrows(Exception.class, () -> IoUtil.outputStreamToInputStream(
                _ -> { throw new IOException(); }, _ -> {}));
        assertThrows(Exception.class, () -> IoUtil.outputStreamToInputStream(
                _ -> { throw new IOException(); }, InputStream::readAllBytes));
        assertThrows(Exception.class, () -> IoUtil.outputStreamToInputStream(
                _ -> { throw new IOException(); }, _ -> { throw new IOException(); }));
    }

    private void outputStreamToInputStreamSuccess(final byte[] testBytes) {
        IoUtil.outputStreamToInputStream(outputStream -> outputStream.write(testBytes),
                inputStream -> assertArrayEquals(testBytes, inputStream.readAllBytes()));
    }
}
