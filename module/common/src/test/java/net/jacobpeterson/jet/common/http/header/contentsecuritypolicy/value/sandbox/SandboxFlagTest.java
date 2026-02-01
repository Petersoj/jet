package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.sandbox;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class SandboxFlagTest {

    @Test
    public void forString() {
        assertEquals(SandboxFlag.ALLOW_DOWNLOADS, SandboxFlag.forString("allow-downloads"));
        assertEquals(SandboxFlag.ALLOW_DOWNLOADS, SandboxFlag.forString("ALLOW-DOWNLOADS"));
        assertEquals(SandboxFlag.ALLOW_DOWNLOADS, SandboxFlag.forString("Allow-Downloads"));
        assertNull(SandboxFlag.forString("allow downloads"));
    }
}
