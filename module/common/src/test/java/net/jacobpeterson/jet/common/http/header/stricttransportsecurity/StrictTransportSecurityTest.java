package net.jacobpeterson.jet.common.http.header.stricttransportsecurity;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class StrictTransportSecurityTest {

    @Test
    public void parse() {
        {
            final var strictTransportSecurity = StrictTransportSecurity.parse("max-age=1");
            assertEquals(1, strictTransportSecurity.getMaxAge());
            assertFalse(strictTransportSecurity.isIncludeSubDomains());
            assertFalse(strictTransportSecurity.isPreload());
        }
        {
            final var strictTransportSecurity = StrictTransportSecurity.parse(" max-age  =  1 ; includesubdomains ;;;");
            assertEquals(1, strictTransportSecurity.getMaxAge());
            assertTrue(strictTransportSecurity.isIncludeSubDomains());
            assertFalse(strictTransportSecurity.isPreload());
        }
        {
            final var strictTransportSecurity = StrictTransportSecurity.parse("max-age=1;includeSubDomains;preload");
            assertEquals(1, strictTransportSecurity.getMaxAge());
            assertTrue(strictTransportSecurity.isIncludeSubDomains());
            assertTrue(strictTransportSecurity.isPreload());
        }
        assertThrows(IllegalArgumentException.class, () -> StrictTransportSecurity.parse(""));
        assertThrows(IllegalArgumentException.class, () -> StrictTransportSecurity.parse("max-age"));
        assertThrows(IllegalArgumentException.class, () -> StrictTransportSecurity.parse("max-age="));
        assertThrows(IllegalArgumentException.class, () -> StrictTransportSecurity.parse(" max-age= "));
        assertThrows(IllegalArgumentException.class, () -> StrictTransportSecurity.parse("max-age=a"));
    }

    public static final class BuilderTest {

        @Test
        public void maxAge() {
            assertEquals(1, StrictTransportSecurity.builder()
                    .maxAge(1)
                    .build().getMaxAge());
        }

        @Test
        public void includeSubDomains() {
            assertTrue(StrictTransportSecurity.builder()
                    .maxAge(1)
                    .includeSubDomains(true)
                    .build().isIncludeSubDomains());
            assertFalse(StrictTransportSecurity.builder().build().isIncludeSubDomains());
        }

        @Test
        public void preload() {
            assertTrue(StrictTransportSecurity.builder()
                    .maxAge(1)
                    .preload(true)
                    .build().isPreload());
            assertFalse(StrictTransportSecurity.builder().build().isPreload());
        }
    }

    @Test
    public void _toString() {
        assertEquals("max-age=1", StrictTransportSecurity.builder()
                .maxAge(1)
                .build().toString());
        assertEquals("max-age=1; includeSubDomains", StrictTransportSecurity.builder()
                .maxAge(1)
                .includeSubDomains(true)
                .build().toString());
        assertEquals("max-age=1; preload", StrictTransportSecurity.builder()
                .maxAge(1)
                .preload(true)
                .build().toString());
        assertEquals("max-age=1; includeSubDomains; preload", StrictTransportSecurity.builder()
                .maxAge(1)
                .includeSubDomains(true)
                .preload(true)
                .build().toString());
    }
}
