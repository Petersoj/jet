package net.jacobpeterson.jet.common.http.header.contentsecuritypolicy.value.requiretrustedtypesfor;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class RequireTrustedTypesForTest {

    @Test
    public void forString() {
        assertEquals(RequireTrustedTypesFor.SCRIPT, RequireTrustedTypesFor.forString("'script'"));
        assertEquals(RequireTrustedTypesFor.SCRIPT, RequireTrustedTypesFor.forString("'SCRIPT'"));
        assertEquals(RequireTrustedTypesFor.SCRIPT, RequireTrustedTypesFor.forString("'Script'"));
        assertNull(RequireTrustedTypesFor.forString("script"));
    }
}
