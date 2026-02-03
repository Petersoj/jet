package net.jacobpeterson.jet.common.http.header.authorization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@NullMarked
public final class BasicAuthenticationTest {

    @Test
    public void parse() {
        {
            final var basicAuthentication = BasicAuthentication.parse("Basic YWxhZGRpbjpvcGVuc2VzYW1l");
            assertEquals("aladdin", basicAuthentication.getUsername());
            assertEquals("opensesame", basicAuthentication.getPassword());
            assertEquals(UTF_8, basicAuthentication.getCharset());
        }
        {
            final var basicAuthentication = BasicAuthentication.parse("     Basic   YWxhZGRpbjpvcGVuc2VzYW1l  ");
            assertEquals("aladdin", basicAuthentication.getUsername());
            assertEquals("opensesame", basicAuthentication.getPassword());
            assertEquals(UTF_8, basicAuthentication.getCharset());
        }
        assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.parse(""));
        assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.parse(" Basic "));
        assertThrows(IllegalArgumentException.class, () -> BasicAuthentication.parse(" Basic YQ=="));
    }

    public static final class BuilderTest {

        @Test
        public void username() {
            assertEquals("username", BasicAuthentication.builder()
                    .username("username")
                    .password("password")
                    .build().getUsername());
        }

        @Test
        public void password() {
            assertEquals("password", BasicAuthentication.builder()
                    .username("username")
                    .password("password")
                    .build().getPassword());
        }

        @Test
        public void charset() {
            assertEquals(UTF_8, BasicAuthentication.builder()
                    .username("username")
                    .password("password")
                    .build().getCharset());
            assertEquals(US_ASCII, BasicAuthentication.builder()
                    .username("username")
                    .password("password")
                    .charset(US_ASCII)
                    .build().getCharset());
        }
    }

    @Test
    public void _toString() {
        assertEquals("Basic YWxhZGRpbjpvcGVuc2VzYW1l",
                BasicAuthentication.parse("Basic YWxhZGRpbjpvcGVuc2VzYW1l").toString());
        assertEquals("Basic YTpi", BasicAuthentication.builder()
                .username("a")
                .password("b")
                .build().toString());
    }
}
