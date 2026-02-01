package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.predefined;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class PredefinedSourceExpressionTest {

    @Test
    public void forString() {
        assertEquals(PredefinedSourceExpression.UNSAFE_INLINE, PredefinedSourceExpression.forString("'unsafe-inline'"));
        assertEquals(PredefinedSourceExpression.UNSAFE_INLINE, PredefinedSourceExpression.forString("'UNSAFE-INLINE'"));
        assertEquals(PredefinedSourceExpression.UNSAFE_INLINE, PredefinedSourceExpression.forString("'Unsafe-Inline'"));
        assertNull(PredefinedSourceExpression.forString("'unsafe inline'"));
    }
}
