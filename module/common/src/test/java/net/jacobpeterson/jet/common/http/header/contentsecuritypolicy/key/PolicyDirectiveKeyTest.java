package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.key;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class PolicyDirectiveKeyTest {

    @Test
    public void forString() {
        assertEquals(PolicyDirectiveKey.DEFAULT_SRC, PolicyDirectiveKey.forString("default-src"));
        assertEquals(PolicyDirectiveKey.DEFAULT_SRC, PolicyDirectiveKey.forString("DEFAULT-SRC"));
        assertEquals(PolicyDirectiveKey.DEFAULT_SRC, PolicyDirectiveKey.forString("Default-Src"));
        assertNull(PolicyDirectiveKey.forString("default src"));
    }
}
