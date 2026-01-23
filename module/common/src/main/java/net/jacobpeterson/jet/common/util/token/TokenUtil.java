package net.jacobpeterson.jet.common.util.token;

import org.jspecify.annotations.NullMarked;

import java.security.SecureRandom;

/**
 * {@link TokenUtil} is a utility class for tokens.
 */
@NullMarked
public final class TokenUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final char[] TOKEN_CHARACTER_POOL = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * Generates a secure, random, alphanumeric token {@link String} with the given <code>length</code>.
     *
     * @param length the token length
     *
     * @return the token {@link String}
     */
    public static String generateToken(final int length) {
        final var token = new StringBuilder();
        for (var index = 0; index < length; index++) {
            token.append(TOKEN_CHARACTER_POOL[SECURE_RANDOM.nextInt(TOKEN_CHARACTER_POOL.length)]);
        }
        return token.toString();
    }

    private TokenUtil() {}
}
