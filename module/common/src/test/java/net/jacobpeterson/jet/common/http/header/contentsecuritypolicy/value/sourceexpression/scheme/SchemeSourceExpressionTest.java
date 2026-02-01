package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.scheme;

import net.jacobpeterson.jet.common.http.url.Scheme;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class SchemeSourceExpressionTest {

    @Test
    public void forScheme() {
        assertEquals(SchemeSourceExpression.create("https"), SchemeSourceExpression.forScheme(Scheme.HTTPS));
    }

    @Test
    public void create() {
        assertEquals(SchemeSourceExpression.create("https"), SchemeSourceExpression.create("https"));
    }

    @Test
    public void toScheme() {
        assertEquals(Scheme.HTTPS, SchemeSourceExpression.create("https").toScheme());
        assertNull(SchemeSourceExpression.create("a").toScheme());
    }

    @Test
    public void _toString() {
        assertEquals("https:", SchemeSourceExpression.create("https").toString());
        assertEquals("a:", SchemeSourceExpression.create("a").toString());
    }
}
