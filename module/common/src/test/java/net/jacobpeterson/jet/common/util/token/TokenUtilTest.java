package net.jacobpeterson.jet.common.util.token;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class TokenUtilTest {

    @Test
    public void generateToken() {
        assertTrue(TokenUtil.generateToken(1024).matches("^[a-zA-Z\\d]+$"));
        assertTrue(TokenUtil.generateToken(0).isEmpty());
        assertTrue(TokenUtil.generateToken(-1).isEmpty());
        assertEquals(1, TokenUtil.generateToken(1).length());
        assertEquals(64, TokenUtil.generateToken(64).length());
    }
}
