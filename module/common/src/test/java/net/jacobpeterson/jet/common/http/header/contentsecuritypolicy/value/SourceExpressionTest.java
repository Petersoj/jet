package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value;

import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.SourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash.HashSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.host.HostSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.nonce.NonceSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.predefined.PredefinedSourceExpression;
import net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.scheme.SchemeSourceExpression;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.hash.HashSourceExpressionAlgorithm.SHA_256;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class SourceExpressionTest {

    @Test
    public void parse() {
        assertEquals(NonceSourceExpression.create("123"), SourceExpression.parse("'nonce-123'"));
        assertEquals(HashSourceExpression.create(SHA_256, "123"), SourceExpression.parse("'sha256-123'"));
        assertEquals(HostSourceExpression.create("https://a.com"), SourceExpression.parse("https://a.com"));
        assertEquals(SchemeSourceExpression.create("https"), SourceExpression.parse("https:"));
        assertEquals(PredefinedSourceExpression.UNSAFE_INLINE, SourceExpression.parse("'unsafe-inline'"));
        assertThrows(IllegalArgumentException.class, () -> SourceExpression.parse("'a-123'"));
        assertThrows(IllegalArgumentException.class, () -> SourceExpression.parse("'a-'"));
        assertThrows(IllegalArgumentException.class, () -> SourceExpression.parse("'a'"));
    }
}
