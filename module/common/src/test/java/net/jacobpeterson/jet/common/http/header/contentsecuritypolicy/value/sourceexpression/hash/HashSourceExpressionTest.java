package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@NullMarked
public final class HashSourceExpressionTest {

    @Test
    public void forBytesSource() {
        assertEquals(HashSourceExpression.create(HashAlgorithm.SHA_256,
                        "A5BYxvLAy0ksUzsKTRTvd8wPeKvMztUofYShogEc+4E="),
                HashSourceExpression.forBytesSource(HashAlgorithm.SHA_256,
                        new byte[]{1, 2, 3}));
    }

    @Test
    public void forStringSource() {
        assertEquals(HashSourceExpression.create(HashAlgorithm.SHA_256,
                        "pmWkWSBCL51Bfkhn79xPuKBKHz//H6B+mY6G9/eieuM="),
                HashSourceExpression.forStringSource(HashAlgorithm.SHA_256,
                        "123"));
    }

    @Test
    public void create() {
        assertEquals(HashSourceExpression.create(HashAlgorithm.SHA_256, "123"),
                HashSourceExpression.create(HashAlgorithm.SHA_256, "123"));
        assertNotEquals(HashSourceExpression.create(HashAlgorithm.SHA_512, "123"),
                HashSourceExpression.create(HashAlgorithm.SHA_256, "123"));
        assertNotEquals(HashSourceExpression.create(HashAlgorithm.SHA_256, "123"),
                HashSourceExpression.create(HashAlgorithm.SHA_256, "1234"));
    }

    @Test
    public void getPrefix() {
        assertEquals("sha256-",
                HashSourceExpression.create(HashAlgorithm.SHA_256, "123").getPrefix());
    }

    @Test
    public void _toString() {
        assertEquals("'sha256-123'",
                HashSourceExpression.create(HashAlgorithm.SHA_256, "123").toString());
    }
}
