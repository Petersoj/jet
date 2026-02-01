package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class HashSourceExpressionAlgorithmTest {

    @Test
    public void hashBytes() {
        assertEquals("A5BYxvLAy0ksUzsKTRTvd8wPeKvMztUofYShogEc+4E=",
                HashSourceExpressionAlgorithm.SHA_256.hashBytes(new byte[]{1, 2, 3}));
    }

    @Test
    public void hashString() {
        assertEquals("pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM=",
                HashSourceExpressionAlgorithm.SHA_256.hashString("123"));
    }

    @Test
    public void _toString() {
        assertEquals("sha256", HashSourceExpressionAlgorithm.SHA_256.toString());
    }

    @Test
    public void forString() {
        assertEquals(HashSourceExpressionAlgorithm.SHA_256, HashSourceExpressionAlgorithm.forString("sha256"));
        assertEquals(HashSourceExpressionAlgorithm.SHA_256, HashSourceExpressionAlgorithm.forString("SHA256"));
        assertEquals(HashSourceExpressionAlgorithm.SHA_256, HashSourceExpressionAlgorithm.forString("Sha256"));
        assertNull(HashSourceExpressionAlgorithm.forString("sha257"));
    }
}
