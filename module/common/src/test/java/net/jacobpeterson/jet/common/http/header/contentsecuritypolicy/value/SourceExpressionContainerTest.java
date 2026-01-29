package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value;

import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpressionContainer;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.predefined.PredefinedSourceExpression;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class SourceExpressionContainerTest {

    @Test
    public void wrap() {
        assertEquals(SourceExpressionContainer.wrap(SourceExpression.parse("'none'")),
                SourceExpressionContainer.wrap(PredefinedSourceExpression.NONE));
    }

    @Test
    public void unwrap() {
        assertEquals(PredefinedSourceExpression.NONE,
                SourceExpressionContainer.wrap(SourceExpression.parse("'none'")).unwrap());
    }

    @Test
    public void isNonce() {
        assertTrue(SourceExpressionContainer.wrap(SourceExpression.parse("'nonce-123'")).isNonce());
    }

    @Test
    public void asNonce() {
        assertDoesNotThrow(() -> SourceExpressionContainer.wrap(SourceExpression.parse("'nonce-123'")).asNonce());
    }

    @Test
    public void isHash() {
        assertTrue(SourceExpressionContainer.wrap(SourceExpression.parse("'sha256-123'")).isHash());
    }

    @Test
    public void asHash() {
        assertDoesNotThrow(() -> SourceExpressionContainer.wrap(SourceExpression.parse("'sha256-123'")).asHash());
    }

    @Test
    public void isHost() {
        assertTrue(SourceExpressionContainer.wrap(SourceExpression.parse("https://a.com")).isHost());
    }

    @Test
    public void asHost() {
        assertDoesNotThrow(() -> SourceExpressionContainer.wrap(SourceExpression.parse("https://a.com")).asHost());
    }

    @Test
    public void isScheme() {
        assertTrue(SourceExpressionContainer.wrap(SourceExpression.parse("https:")).isScheme());
    }

    @Test
    public void asScheme() {
        assertDoesNotThrow(() -> SourceExpressionContainer.wrap(SourceExpression.parse("https:")).asScheme());
    }

    @Test
    public void isPredefined() {
        assertTrue(SourceExpressionContainer.wrap(SourceExpression.parse("'none'")).isPredefined());
    }

    @Test
    public void asPredefined() {
        assertDoesNotThrow(() -> SourceExpressionContainer.wrap(SourceExpression.parse("'none'")).asPredefined());
    }

    @Test
    public void _toString() {
        assertEquals(PredefinedSourceExpression.NONE.toString(),
                SourceExpressionContainer.wrap(PredefinedSourceExpression.NONE).toString());
    }
}
