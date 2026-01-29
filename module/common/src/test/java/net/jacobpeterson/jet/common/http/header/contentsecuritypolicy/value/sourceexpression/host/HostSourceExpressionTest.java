package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sourceexpression.host;

import net.jacobpeterson.jet.common.http.url.Url;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public class HostSourceExpressionTest {

    @Test
    public void create() {
        assertEquals(HostSourceExpression.create("https://a.com"), HostSourceExpression.create("https://a.com"));
        assertEquals(HostSourceExpression.create("https://a.com/"), HostSourceExpression.create("https://a.com/"));
        assertEquals(HostSourceExpression.create("https://a.com/a"), HostSourceExpression.create("https://a.com/a"));
        assertEquals(HostSourceExpression.create("https://*.com"), HostSourceExpression.create("https://*.com"));
        assertEquals(HostSourceExpression.create("https://a.com:*/a/b/"),
                HostSourceExpression.create("https://a.com:*/a/b/"));
        assertEquals(HostSourceExpression.WILDCARD, HostSourceExpression.create("*"));
    }

    @Test
    public void isWildcard() {
        assertTrue(HostSourceExpression.WILDCARD.isWildcard());
        assertTrue(HostSourceExpression.create("*").isWildcard());
        assertFalse(HostSourceExpression.create("https://a.com").isWildcard());
        assertFalse(HostSourceExpression.create("https://*.com").isWildcard());
    }

    @Test
    public void hasWildcard() {
        assertTrue(HostSourceExpression.WILDCARD.hasWildcard());
        assertTrue(HostSourceExpression.create("*").hasWildcard());
        assertFalse(HostSourceExpression.create("https://a.com").hasWildcard());
        assertTrue(HostSourceExpression.create("https://*.com").hasWildcard());
    }

    @Test
    public void toUrl() {
        assertEquals(Url.parse("https://a.com"), HostSourceExpression.create("https://a.com").toUrl());
        assertThrows(IllegalArgumentException.class, () -> HostSourceExpression.create("https://*.com").toUrl());
        assertThrows(IllegalArgumentException.class, HostSourceExpression.WILDCARD::toUrl);
    }

    @Test
    public void _toString() {
        assertEquals("https://a.com", HostSourceExpression.create("https://a.com").toString());
        assertEquals("https://*.com", HostSourceExpression.create("https://*.com").toString());
        assertEquals("*", HostSourceExpression.WILDCARD.toString());
    }
}
