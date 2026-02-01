package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.nonce;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class NonceSourceExpressionTest {

    @Test
    public void generate() {
        assertEquals(8, NonceSourceExpression.generate(8).getNonce().length());
    }

    @Test
    public void generateInt() {
        assertEquals(16, NonceSourceExpression.generate().getNonce().length());
    }

    @Test
    public void create() {
        assertEquals(NonceSourceExpression.create("123"), NonceSourceExpression.create("123"));
    }

    @Test
    public void _toString() {
        assertEquals("'nonce-123'", NonceSourceExpression.create("123").toString());
    }
}
