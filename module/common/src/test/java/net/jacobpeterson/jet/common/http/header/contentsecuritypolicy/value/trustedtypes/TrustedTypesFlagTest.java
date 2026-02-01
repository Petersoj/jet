package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.trustedtypes;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public final class TrustedTypesFlagTest {

    @Test
    public void forString() {
        assertEquals(TrustedTypesFlag.ALLOW_DUPLICATES, TrustedTypesFlag.forString("'allow-duplicates'"));
        assertEquals(TrustedTypesFlag.ALLOW_DUPLICATES, TrustedTypesFlag.forString("'ALLOW-DUPLICATES'"));
        assertEquals(TrustedTypesFlag.ALLOW_DUPLICATES, TrustedTypesFlag.forString("'Allow-Duplicates'"));
        assertNull(TrustedTypesFlag.forString("allow duplicates"));
    }
}
